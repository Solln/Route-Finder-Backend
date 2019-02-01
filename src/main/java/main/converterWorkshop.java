package main;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class converterWorkshop {

    static DecimalFormat df = new DecimalFormat("#.####");

    public static void main(String[] args) {

        double lat1 = 56.8;
        double lon1 = -3.6;
        double lat2 = 57;
        double lon2 = -3.3;

        df.setRoundingMode(RoundingMode.CEILING);

        getDistance(lat1, lon1, lat2, lon2);

        System.out.println("Original Coords: (" + lat1 + ", " + lon1 + "), (" + lat2 + ", " + lon2 + ")");

        double bearing = getBearing(lat1, lon1, lat2, lon2);

        System.out.println("Original Bearing is: " + convertSingleDMS(bearing));

        double[] newBearings = getNewBearings(bearing);

        double[] midpoint = getMidPoint(lat1, lon1, lat2, lon2);

        System.out.println("Midpoint is: " + midpoint[0] + ", " + midpoint[1]);

        convertToDMS(midpoint[0], midpoint[1]);

        System.out.println("---------BACK----------");
        System.out.println("Bearing going Back: " + convertSingleDMS(newBearings[0]));
        double[] pointA = newPoint(midpoint[0], midpoint[1], newBearings[0] , 1000);
        System.out.println("Moving back for 1000m");
        System.out.println(pointA[0] + ", " + pointA[1]);

        System.out.println("---------FORWARD----------");
        System.out.println("Bearing going Forward: " + convertSingleDMS(newBearings[1]));
        double[] pointB = newPoint(midpoint[0], midpoint[1], newBearings[1] , 1000);
        System.out.println("Moving Forward for 1000m");
        System.out.println(pointB[0] + ", " + pointB[1]);



    }

    //All Coordinates and Distances return to 4 decimal places

    // Returns new bearings +/- 90 Degrees from bearing
    // Returns Array [0] - Back / [1] - Forward. In Decimal format
    private static double[] getNewBearings(double bearing) {

        double newBearings[] = new double[2];

        // [0] is Back 90   /   [1] is Forward 90

        if (bearing < 90) {
            newBearings[0] = 360 - 90 + bearing;
        } else {
            newBearings[0] = bearing - 90;
        }

        if (bearing > 270) {
            newBearings[1] = bearing - 360 + 90;
        } else {
            newBearings[1] = bearing + 90;
        }

        return newBearings;
    }

    // Converts a Lat/Lon to DMS
    // Returns String of conversions in DMS format
    private static String convertToDMS(double lat, double lon) {

        boolean N = true;
        boolean E = true;

        if (lat < 0) {
            N = false;
            lat = lat * -1;
        }

        if (lon < 0) {
            E = false;
            lon = lon * -1;
        }

        StringBuilder result = new StringBuilder();

        // LAT
        if (N == true) {
            result.append(convertSingleDMS(lat) + " " + "N");
        } else {
            result.append(convertSingleDMS(lat) + " " + "S");
        }


        // LON
        if (E == true) {
            result.append(convertSingleDMS(lon) + " " + "E");
        } else {
            result.append(convertSingleDMS(lon) + " " + "W");
        }

        return result.toString();

    }

    // Converts a single value to DMS
    // Returns String of conversion in DMS format
    private static String convertSingleDMS(double value) {
        int d = (int) value;
        double t1 = (value - d) * 60;
        int m = (int) t1;
        int s = (int) ((t1 - m) * 60);

        return (d + " " + m + " " + s);
    }

    // Gets the distance between 2 Lat/Lon points
    // Returns distance, units in Meters. In Decimal format
    private static double getDistance(double lat1, double lon1, double lat2, double lon2) {

        double radius = 6371e3;

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);

        double vectLat = Math.toRadians(lat2 - lat1);
        double vectLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(vectLat / 2) * Math.sin(vectLat / 2) +
                Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                        Math.sin(vectLon / 2) * Math.sin(vectLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double d = radius * c;

        //Its in Meters
        return Double.parseDouble(df.format(d));
    }

    // Gets Bearing between 2 Lat/Lon points
    // Returns Bearing in Decimal format
    private static double getBearing(double lat1, double lon1, double lat2, double lon2) {

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double lon1Rad = Math.toRadians(lon1);
        double lon2Rad = Math.toRadians(lon2);

        double y = Math.sin(lon2Rad - lon1Rad) * Math.cos(lat2Rad);
        double x = Math.cos(lat1Rad) * Math.sin(lat2Rad) - Math.sin(lat1Rad) * Math.cos(lat2Rad) * Math.cos(lon2Rad - lon1Rad);

        double brng = Math.toDegrees(Math.atan2(y, x));

        return brng;

    }

    // Gets the midpoint between 2 Lat/Lon points
    // Returns double[] where [0] = new Lat and [1] = new Lon . In Decimal format
    private static double[] getMidPoint(double lat1, double lon1, double lat2, double lon2) {

        double lat1Rad = Math.toRadians(lat1);
        double lat2Rad = Math.toRadians(lat2);
        double lon1Rad = Math.toRadians(lon1);
        double lon2Rad = Math.toRadians(lon2);

        double bX = Math.cos(lat2Rad) * Math.cos(lon2Rad - lon1Rad);
        double bY = Math.cos(lat2Rad) * Math.sin(lon2Rad - lon1Rad);

        double lat3 = Math.atan2(Math.sin(lat1Rad) + Math.sin(lat2Rad),
                Math.sqrt(
                        ((Math.cos(lat1Rad) + bX) *
                                (Math.cos(lat1Rad) + bX) +
                                (bY * bY)
                        )
                )
        );


        double lon3 = lon1Rad + Math.atan2(bY, Math.cos(lat1Rad) + bX);

        double[] midpoint = new double[2];

        midpoint[0] = Double.parseDouble(df.format(Math.toDegrees(lat3)));
        midpoint[1] = Double.parseDouble(df.format(Math.toDegrees(lon3)));

        return midpoint;

    }

    // Gets the new point based off a starting point / bearing / distance (in Meters)
    // Returns double[] where [0] = new Lat and [1] = new Lon . In Decimal format
    private static double[] newPoint(double lat1, double lon1, double bearing, double d) {

        double lat1Rad = Math.toRadians(lat1);
        double lon1Rad = Math.toRadians(lon1);

        double brng = Math.toRadians(bearing);

        double radius = 6371e3;

        double ang = d / radius;

        double lat2 = Math.asin(Math.sin(lat1Rad) * Math.cos(d / radius) +
                Math.cos(lat1Rad) * Math.sin(d / radius) * Math.cos(brng));

        double lon2 = lon1Rad + Math.atan2(
                Math.sin(brng) *
                        Math.sin(ang) *
                        Math.cos(lat1Rad),
                Math.cos(ang) -
                        Math.sin(lat1Rad) *
                                Math.sin(lat2)
        );

        double[] newPoint = new double[2];

        newPoint[0] = Double.parseDouble(df.format(Math.toDegrees(lat2)));
        newPoint[1] = Double.parseDouble(df.format(Math.toDegrees(lon2)));

        return newPoint;

    }

}
