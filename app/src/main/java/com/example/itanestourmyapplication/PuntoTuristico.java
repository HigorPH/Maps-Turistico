package com.example.itanestourmyapplication;

public class PuntoTuristico {
    private int id;
    private String nombre;
    private String descripcion;
    private double latitud;
    private double longitud;
    private int imagenResId; // ID del drawable local
    private boolean esFavorito;

    public PuntoTuristico() {}

    public PuntoTuristico(int id, String nombre, String descripcion, double latitud, double longitud, int imagenResId, boolean esFavorito) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.latitud = latitud;
        this.longitud = longitud;
        this.imagenResId = imagenResId;
        this.esFavorito = esFavorito;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }

    public double getLatitud() { return latitud; }
    public void setLatitud(double latitud) { this.latitud = latitud; }

    public double getLongitud() { return longitud; }
    public void setLongitud(double longitud) { this.longitud = longitud; }

    public int getImagenResId() { return imagenResId; }
    public void setImagenResId(int imagenResId) { this.imagenResId = imagenResId; }

    public boolean isEsFavorito() { return esFavorito; }
    public void setEsFavorito(boolean esFavorito) { this.esFavorito = esFavorito; }
}