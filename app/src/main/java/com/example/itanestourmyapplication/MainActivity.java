package com.example.itanestourmyapplication;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private RecyclerView rvPuntos;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);

        rvPuntos = findViewById(R.id.rvPuntos);
        rvPuntos.setLayoutManager(new LinearLayoutManager(this));

        bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                cargarPuntos(dbHelper.obtenerTodosLosPuntos());
                return true;
            } else if (id == R.id.nav_favoritos) {
                cargarPuntos(dbHelper.obtenerPuntosFavoritos());
                return true;
            } else if (id == R.id.nav_mapa) {
                startActivity(new Intent(MainActivity.this, Maps_Activity.class));
                return true;
            }
            return false;
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (bottomNavigationView.getSelectedItemId() == R.id.nav_favoritos) {
            cargarPuntos(dbHelper.obtenerPuntosFavoritos());
        } else {
            cargarPuntos(dbHelper.obtenerTodosLosPuntos());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Asegurar que al volver al MainActivity mantenga la pestaña actual
        bottomNavigationView.setSelectedItemId(R.id.nav_home);
    }

    private void cargarPuntos(List<PuntoTuristico> lista) {
        PuntoAdapter adapter = new PuntoAdapter(this, lista);
        rvPuntos.setAdapter(adapter);
    }
}
