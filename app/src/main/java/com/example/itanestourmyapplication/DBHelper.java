package com.example.itanestourmyapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "itanes_tour.db";
    private static final int DATABASE_VERSION = 4;

    public static final String TABLE_PUNTOS = "puntos_turisticos";
    public static final String COL_ID = "id";
    public static final String COL_NOMBRE = "nombre";
    public static final String COL_DESCRIPCION = "descripcion";
    public static final String COL_LATITUD = "latitud";
    public static final String COL_LONGITUD = "longitud";
    public static final String COL_IMAGEN_RES = "imagen_res";
    public static final String COL_FAVORITO = "es_favorito";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_PUNTOS + " (" +
                COL_ID + " INTEGER PRIMARY KEY, " +
                COL_NOMBRE + " TEXT, " +
                COL_DESCRIPCION + " TEXT, " +
                COL_LATITUD + " REAL, " +
                COL_LONGITUD + " REAL, " +
                COL_IMAGEN_RES + " INTEGER, " +
                COL_FAVORITO + " INTEGER DEFAULT 0)";
        db.execSQL(sql);

        insertarPuntoInicial(db, 1, "Plaza Mayor de Lima",
                "Centro histórico, rodeado por la Catedral de Lima y el Palacio de Gobierno.",
                -12.0453, -77.0311, R.drawable.foto_plaza);

        insertarPuntoInicial(db, 2, "Huaca Pucllana",
                "Gran centro ceremonial de adobe edificado por la cultura Lima en Miraflores.",
                -12.1111, -77.0336, R.drawable.foto_huaca);

        insertarPuntoInicial(db, 3, "Parque del Amor",
                "Ubicado en el malecón de Miraflores con vista al Océano Pacífico y la escultura El Beso.",
                -12.1249, -77.0375, R.drawable.foto_parque);

        insertarPuntoInicial(db, 4, "Barranco Tradicional",
                "Distrito bohemio conocido por el Puente de los Suspiros y su arquitectura colonial.",
                -12.1486, -77.0219, R.drawable.foto_barranco);

        insertarPuntoInicial(db, 5, "Circuito Mágico del Agua",
                "Complejo de fuentes cibernéticas con espectáculos de luces y sonido en el Parque de la Reserva.",
                -12.0694, -77.0336, R.drawable.foto_circuito);
    }

    private void insertarPuntoInicial(SQLiteDatabase db, int id, String nombre, String desc, double lat, double lng, int resId) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ID, id);
        cv.put(COL_NOMBRE, nombre);
        cv.put(COL_DESCRIPCION, desc);
        cv.put(COL_LATITUD, lat);
        cv.put(COL_LONGITUD, lng);
        cv.put(COL_IMAGEN_RES, resId);
        cv.put(COL_FAVORITO, 0);
        db.insert(TABLE_PUNTOS, null, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PUNTOS);
        onCreate(db);
    }

    public List<PuntoTuristico> obtenerTodosLosPuntos() {
        List<PuntoTuristico> lista = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_PUNTOS, null);

        if (cursor.moveToFirst()) {
            do {
                PuntoTuristico p = new PuntoTuristico();
                p.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID)));
                p.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE)));
                p.setDescripcion(cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPCION)));
                p.setLatitud(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LATITUD)));
                p.setLongitud(cursor.getDouble(cursor.getColumnIndexOrThrow(COL_LONGITUD)));
                p.setImagenResId(cursor.getInt(cursor.getColumnIndexOrThrow(COL_IMAGEN_RES)));
                p.setEsFavorito(cursor.getInt(cursor.getColumnIndexOrThrow(COL_FAVORITO)) == 1);
                lista.add(p);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return lista;
    }

    public void actualizarFavorito(int id, boolean esFavorito) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_FAVORITO, esFavorito ? 1 : 0);
        db.update(TABLE_PUNTOS, cv, COL_ID + " = ?", new String[]{String.valueOf(id)});
    }
}