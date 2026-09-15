package com.example.itanestourmyapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Maps_Activity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "Maps_Activity";
    private GoogleMap mMap;
    private double latitudDestino = -12.0453;
    private double longitudDestino = -77.0311;
    private String nombreLugar = "Destino";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private FusedLocationProviderClient fusedLocationClient;
    private Polyline routePolyline;
    private TextView tvDestinoTitulo, tvDistancia;
    private Button btnIniciarNavegacion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        tvDestinoTitulo = findViewById(R.id.tvDestinoTitulo);
        tvDistancia = findViewById(R.id.tvDistancia);
        btnIniciarNavegacion = findViewById(R.id.btnIniciarNavegacion);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("latitud") && intent.hasExtra("longitud")) {
            latitudDestino = intent.getDoubleExtra("latitud", -12.0453);
            longitudDestino = intent.getDoubleExtra("longitud", -77.0311);
            String nombreExtra = intent.getStringExtra("nombre");
            if (nombreExtra != null) {
                nombreLugar = nombreExtra;
            }
        }

        tvDestinoTitulo.setText(nombreLugar);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Botón para iniciar navegación GPS guiada externa con voz
        btnIniciarNavegacion.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitudDestino + "," + longitudDestino + "&mode=d");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Uri fallback = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + latitudDestino + "," + longitudDestino);
                startActivity(new Intent(Intent.ACTION_VIEW, fallback));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng destino = new LatLng(latitudDestino, longitudDestino);
        mMap.addMarker(new MarkerOptions().position(destino).title(nombreLugar));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(destino)
                .zoom(16f)
                .tilt(45f)
                .bearing(30f)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        mMap.setTrafficEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        obtenerUbicacionYTrazarRutaOSRM(destino);
    }

    private void obtenerUbicacionYTrazarRutaOSRM(LatLng destino) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                LatLng origen;
                if (location != null) {
                    origen = new LatLng(location.getLatitude(), location.getLongitude());
                    if (Math.abs(origen.latitude - 37.422) < 0.01 && Math.abs(origen.longitude - (-122.084)) < 0.01) {
                        origen = new LatLng(-12.1214, -77.0305); // Parque Kennedy, Miraflores por defecto
                    }
                } else {
                    origen = new LatLng(-12.1214, -77.0305);
                }

                solicitarRutaOSRM(origen, destino);
            });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void solicitarRutaOSRM(LatLng origen, LatLng destino) {
        // OSRM usa formato: {longitud},{latitud};{longitud},{latitud}
        String coords = origen.longitude + "," + origen.latitude + ";" + destino.longitude + "," + destino.latitude;

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://router.project-osrm.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        OsrmApiService service = retrofit.create(OsrmApiService.class);
        service.getRoute(coords, "full", "geojson").enqueue(new Callback<OsrmResponse>() {
            @Override
            public void onResponse(@NonNull Call<OsrmResponse> call, @NonNull Response<OsrmResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().routes != null && !response.body().routes.isEmpty()) {
                    OsrmResponse.OsrmRoute route = response.body().routes.get(0);
                    List<LatLng> decodedPath = new ArrayList<>();
                    for (List<Double> coord : route.geometry.coordinates) {
                        // OSRM devuelve [longitude, latitude]
                        decodedPath.add(new LatLng(coord.get(1), coord.get(0)));
                    }

                    if (routePolyline != null) {
                        routePolyline.remove();
                    }
                    routePolyline = mMap.addPolyline(new PolylineOptions()
                            .addAll(decodedPath)
                            .width(12f)
                            .color(0xFF1565C0)); // Azul

                    double distanciaKm = route.distance / 1000.0;
                    int minutos = (int) (route.duration / 60.0);
                    tvDistancia.setText(String.format(Locale.getDefault(), "Ruta: %.2f km (Aprox. %d min)", distanciaKm, minutos));
                } else {
                    Log.e(TAG, "Error OSRM: " + response.code());
                    trazarLineaRecta(origen, destino);
                }
            }

            @Override
            public void onFailure(@NonNull Call<OsrmResponse> call, @NonNull Throwable t) {
                Log.e(TAG, "Fallo en llamada OSRM: " + t.getMessage(), t);
                trazarLineaRecta(origen, destino);
            }
        });
    }

    private void trazarLineaRecta(LatLng origen, LatLng destino) {
        if (routePolyline != null) {
            routePolyline.remove();
        }
        routePolyline = mMap.addPolyline(new PolylineOptions()
                .add(origen, destino)
                .width(10f)
                .color(0xFF1565C0));

        float[] results = new float[1];
        Location.distanceBetween(origen.latitude, origen.longitude, destino.latitude, destino.longitude, results);
        float distanciaKm = results[0] / 1000f;
        tvDistancia.setText(String.format(Locale.getDefault(), "Distancia directa: %.2f km", distanciaKm));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                LatLng destino = new LatLng(latitudDestino, longitudDestino);
                obtenerUbicacionYTrazarRutaOSRM(destino);
            }
        }
    }
}
