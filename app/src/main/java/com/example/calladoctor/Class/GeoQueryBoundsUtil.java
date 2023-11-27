package com.example.calladoctor.Class;

import org.osmdroid.util.GeoPoint;

public class GeoQueryBoundsUtil {
    public static GeoPoint[] getBoundingBox(GeoPoint center, double distanceKm) {
        double lat = center.getLatitude();
        double lon = center.getLongitude();

        // Radius of the Earth in kilometers
        double radius = 6371.0;

        // Angular distance covered by the radius
        double deltaLat = distanceKm / radius * (180.0 / Math.PI);
        double deltaLon = distanceKm / (radius * Math.cos(Math.PI * lat / 180.0)) * (180.0 / Math.PI);

        // Calculate bounding box coordinates
        double minLat = lat - deltaLat;
        double maxLat = lat + deltaLat;
        double minLon = lon - deltaLon;
        double maxLon = lon + deltaLon;

        return new GeoPoint[] {
                new GeoPoint(minLat, minLon), // Southwest
                new GeoPoint(maxLat, maxLon)  // Northeast
        };
    }
}