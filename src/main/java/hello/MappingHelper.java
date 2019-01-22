package hello;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

public class MappingHelper {

    private String coords;
    private ArrayList<Marker> markers = new ArrayList<>();

    DecimalFormat df = new DecimalFormat("#.####");

    public MappingHelper(String coords) {
        this.coords = coords;
    }


    public String createMarkerObjects() {

        String[] splitCoords;
        String newCoordsString = "";

        splitCoords = coords.split("\\/");

        df.setRoundingMode(RoundingMode.CEILING);

        AtomicLong counter = new AtomicLong();

        for (String latlng : splitCoords) {
            String[] indivEntries;
            indivEntries = latlng.split("\\,");

            double lat = Double.parseDouble(df.format(Double.parseDouble(indivEntries[0])));
            double lng = Double.parseDouble(df.format(Double.parseDouble(indivEntries[1])));

            double elevation = new HgtReader().getElevation(lat,lng);

            markers.add(new Marker(counter.getAndIncrement(), lat, lng, elevation));

            newCoordsString = newCoordsString + lat + "," + lng + "/";
        }

        System.out.println(coords);

        for (Marker mark : markers){
            System.out.println("ID: " + mark.getId());
            System.out.println("Lat: " + mark.getlat());
            System.out.println("Lng: " + mark.getlng());
            System.out.println("Elevation: " + mark.getElevation());
        }

        return newCoordsString;
    }


    public String testAddMethod() {

        String[] splitCoords;
        String newCoordsString = "";

        splitCoords = coords.split("\\/");

        df.setRoundingMode(RoundingMode.CEILING);

        for (String latlng : splitCoords) {
            String[] indivEntries;
            indivEntries = latlng.split("\\,");

            double lat = Double.parseDouble(df.format(Double.parseDouble(indivEntries[0]) + 0.001));
            double lng = Double.parseDouble(df.format(Double.parseDouble(indivEntries[1]) + 0.001));

            newCoordsString = newCoordsString + lat + "," + lng + "/";

        }

//        System.out.println(coords);
//        System.out.println(newCoordsString);

        return newCoordsString;

    }


}
