package com.example.itanestourmyapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
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

import java.util.Locale;

public class Maps_Activity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private double latitudDestino = -12.0453; // Valor por defecto (Plaza Mayor)
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

        // Botón para iniciar navegación GPS guiada externa con voz y giros
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

        // Añadir marcador del destino
        mMap.addMarker(new MarkerOptions().position(destino).title(nombreLugar));

        // Estilo Waze / GPS: Vista 3D (tilt 45°)
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(destino)
                .zoom(16f)
                .tilt(45f)
                .bearing(30f)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        // Capas adicionales
        mMap.setTrafficEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);

        // Obtener ubicación y trazar ruta
        obtenerUbicacionYTrazarRuta();
    }

    private void obtenerUbicacionYTrazarRuta() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);

            fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
                if (location != null) {
                    LatLng origen = new LatLng(location.getLatitude(), location.getLongitude());
                    LatLng destino = new LatLng(latitudDestino, longitudDestino);

                    // Detectar si es la ubicación por defecto del emulador (Mountain View, CA)
                    if (Math.abs(origen.latitude - 37.422) < 0.01 && Math.abs(origen.longitude - (-122.084)) < 0.01) {
                        tvDistancia.setText("⚠️ Estás en Mountain View (Emulador). Cambia tu ubicación GPS en los controles del emulador a Lima.");
                    } else {
                        // Trazar línea de ruta (Polyline)
                        if (routePolyline != null) {
                            routePolyline.remove();
                        }
                        routePolyline = mMap.addPolyline(new PolylineOptions()
                                .add(origen, destino)
                                .width(12f)
                                .color(0xFF1565C0)); // Azul

                        // Calcular distancia aproximada en kilómetros
                        float[] results = new float[1];
                        Location.distanceBetween(origen.latitude, origen.longitude, destino.latitude, destino.longitude, results);
                        float distanciaKm = results[0] / 1000f;

                        tvDistancia.setText(String.format(Locale.getDefault(), "Distancia directa: %.2f km", distanciaKm));
                    }
                } else {
                    tvDistancia.setText("Esperando señal GPS...");
                }
            });
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
