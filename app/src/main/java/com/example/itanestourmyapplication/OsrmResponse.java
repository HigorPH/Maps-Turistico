package com.example.itanestourmyapplication;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OsrmResponse {
    @SerializedName("routes")
    public List<OsrmRoute> routes;

    public static class OsrmRoute {
        @SerializedName("geometry")
        public OsrmGeometry geometry;

        @SerializedName("distance")
        public double distance; // en metros

        @SerializedName("duration")
        public double duration; // en segundos
    }

    public static class OsrmGeometry {
        @SerializedName("coordinates")
        public List<List<Double>> coordinates; // [ [lng, lat], [lng, lat], ... ]
    }
}
