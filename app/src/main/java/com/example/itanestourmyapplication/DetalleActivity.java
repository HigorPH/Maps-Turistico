package com.example.itanestourmyapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class DetalleActivity extends AppCompatActivity implements OnMapReadyCallback {
    private boolean esFavorito;
    private DBHelper dbHelper;
    private double latitud;
    private double longitud;
    private String nombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        dbHelper = new DBHelper(this);

        ImageView ivFoto = findViewById(R.id.ivDetalleFoto);
        TextView tvNombre = findViewById(R.id.tvDetalleNombre);
        TextView tvDescripcion = findViewById(R.id.tvDetalleDescripcion);
        Button btnRuta = findViewById(R.id.btnRutaAuto);
        Button btnFavorito = findViewById(R.id.btnFavorito);

        Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        nombre = intent.getStringExtra("nombre");
        String descripcion = intent.getStringExtra("descripcion");
        latitud = intent.getDoubleExtra("latitud", 0.0);
        longitud = intent.getDoubleExtra("longitud", 0.0);
        int imagenRes = intent.getIntExtra("imagen", 0);
        esFavorito = intent.getBooleanExtra("favorito", false);

        tvNombre.setText(nombre);
        tvDescripcion.setText(descripcion);
        ivFoto.setImageResource(imagenRes);
        actualizarTextoFavorito(btnFavorito);

        // Inicializar el mini mapa incrustado
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Abrir la actividad de mapas estilo Waze al hacer clic en Ver Ruta
        btnRuta.setOnClickListener(v -> {
            Intent mapIntent = new Intent(DetalleActivity.this, Maps_Activity.class);
            mapIntent.putExtra("nombre", nombre);
            mapIntent.putExtra("latitud", latitud);
            mapIntent.putExtra("longitud", longitud);
            startActivity(mapIntent);
        });

        // Alternar favorito
        btnFavorito.setOnClickListener(v -> {
            esFavorito = !esFavorito;
            dbHelper.actualizarFavorito(id, esFavorito);
            actualizarTextoFavorito(btnFavorito);
            Toast.makeText(this, esFavorito ? "Añadido a favoritos" : "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng coordenadas = new LatLng(latitud, longitud);
        googleMap.addMarker(new MarkerOptions().position(coordenadas).title(nombre));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadas, 15f));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    private void actualizarTextoFavorito(Button btn) {
        btn.setText(esFavorito ? "★ Quitar de Favoritos" : "☆ Guardar en Favoritos");
    }
}
