package com.example.itanestourmyapplication;

import android.os.Bundle;
<<<<<<< Updated upstream
=======
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import androidx.appcompat.app.AlertDialog;
>>>>>>> Stashed changes
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private DBHelper dbHelper;
<<<<<<< Updated upstream
    private RecyclerView rvPuntos;
    private PuntoAdapter adapter;
=======
    private RecyclerView rvPuntos, rvFavoritos;
    private LinearLayout seccionFavoritos;
>>>>>>> Stashed changes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

<<<<<<< Updated upstream
        rvPuntos = findViewById(R.id.rvPuntos);
        rvPuntos.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new DBHelper(this);
=======
        dbHelper = new DBHelper(this);

        rvPuntos = findViewById(R.id.rvPuntos);
        rvPuntos.setLayoutManager(new LinearLayoutManager(this));

        rvFavoritos = findViewById(R.id.rvFavoritos);
        rvFavoritos.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        seccionFavoritos = findViewById(R.id.seccionFavoritos);
        ImageButton btnPerfil = findViewById(R.id.btnPerfil);

        btnPerfil.setOnClickListener(v -> mostrarDialogoPerfil());
>>>>>>> Stashed changes
    }

    @Override
    protected void onResume() {
        super.onResume();
<<<<<<< Updated upstream
        List<PuntoTuristico> lista = dbHelper.obtenerTodosLosPuntos();
        adapter = new PuntoAdapter(this, lista);
        rvPuntos.setAdapter(adapter);
        //d
=======
        cargarDatos();
    }

    private void cargarDatos() {
        // Carga la lista completa
        List<PuntoTuristico> listaGeneral = dbHelper.obtenerTodosLosPuntos();
        PuntoAdapter adapterGeneral = new PuntoAdapter(this, listaGeneral);
        rvPuntos.setAdapter(adapterGeneral);

        // Carga la sección exclusiva de favoritos
        List<PuntoTuristico> listaFavoritos = dbHelper.obtenerPuntosFavoritos();
        if (listaFavoritos.isEmpty()) {
            seccionFavoritos.setVisibility(View.GONE);
        } else {
            seccionFavoritos.setVisibility(View.VISIBLE);
            PuntoAdapter adapterFavoritos = new PuntoAdapter(this, listaFavoritos);
            rvFavoritos.setAdapter(adapterFavoritos);
        }
    }

    private void mostrarDialogoPerfil() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_perfil, null);
        builder.setView(view);

        AlertDialog dialog = builder.create();

        // Elimina el marco cuadrado para respetar los bordes redondeados
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        Button btnCerrar = view.findViewById(R.id.btnCerrarDialog);
        btnCerrar.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
>>>>>>> Stashed changes
    }
}