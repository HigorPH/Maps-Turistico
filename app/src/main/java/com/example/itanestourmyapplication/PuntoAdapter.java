package com.example.itanestourmyapplication;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class PuntoAdapter extends RecyclerView.Adapter<PuntoAdapter.ViewHolder> {
    private final List<PuntoTuristico> lista;
    private final Context context;

    public PuntoAdapter(Context context, List<PuntoTuristico> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_punto, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PuntoTuristico punto = lista.get(position);
        holder.tvNombre.setText(punto.getNombre());
        holder.ivFoto.setImageResource(punto.getImagenResId());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleActivity.class);
            intent.putExtra("id", punto.getId());
            intent.putExtra("nombre", punto.getNombre());
            intent.putExtra("descripcion", punto.getDescripcion());
            intent.putExtra("latitud", punto.getLatitud());
            intent.putExtra("longitud", punto.getLongitud());
            intent.putExtra("imagen", punto.getImagenResId());
            intent.putExtra("favorito", punto.isEsFavorito());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivFoto;
        TextView tvNombre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivFoto = itemView.findViewById(R.id.ivLugar);
            tvNombre = itemView.findViewById(R.id.tvNombreLugar);
        }
    }
}