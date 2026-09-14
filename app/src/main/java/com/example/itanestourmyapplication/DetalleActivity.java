package com.example.itanestourmyapplication;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class DetalleActivity extends AppCompatActivity {
    private boolean esFavorito;
    private DBHelper dbHelper;

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
        String nombre = intent.getStringExtra("nombre");
        String descripcion = intent.getStringExtra("descripcion");
        double latitud = intent.getDoubleExtra("latitud", 0.0);
        double longitud = intent.getDoubleExtra("longitud", 0.0);
        int imagenRes = intent.getIntExtra("imagen", 0);
        esFavorito = intent.getBooleanExtra("favorito", false);

        tvNombre.setText(nombre);
        tvDescripcion.setText(descripcion);
        ivFoto.setImageResource(imagenRes);
        actualizarTextoFavorito(btnFavorito);

        // Ruta en Google Maps
        btnRuta.setOnClickListener(v -> {
            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitud + "," + longitud + "&mode=d");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");

            if (mapIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Uri fallback = Uri.parse("https://www.google.com/maps/dir/?api=1&destination=" + latitud + "," + longitud);
                startActivity(new Intent(Intent.ACTION_VIEW, fallback));
            }
        });

        // Alternar favorito
        btnFavorito.setOnClickListener(v -> {
            esFavorito = !esFavorito;
            dbHelper.actualizarFavorito(id, esFavorito);
            actualizarTextoFavorito(btnFavorito);
            Toast.makeText(this, esFavorito ? "Añadido a favoritos" : "Eliminado de favoritos", Toast.LENGTH_SHORT).show();
        });
    }

    private void actualizarTextoFavorito(Button btn) {
        btn.setText(esFavorito ? "★ Quitar de Favoritos" : "☆ Guardar en Favoritos");
    }
}