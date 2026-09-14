package com.example.itanestourmyapplication;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DBHelper dbHelper;
    private RecyclerView rvPuntos;
    private PuntoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvPuntos = findViewById(R.id.rvPuntos);
        rvPuntos.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DBHelper(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<PuntoTuristico> lista = dbHelper.obtenerTodosLosPuntos();
        adapter = new PuntoAdapter(this, lista);
        rvPuntos.setAdapter(adapter);
    }
}