package main;

import main.graph.MarkerNode;

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

//            newCoordsString = newCoordsString + lat + "," + lng + "," + elevation + "/";
        }

        //Run Algorithm 1

        MidPointAlgo algo1 = new MidPointAlgo();

        List<MarkerNode> newMarkers = algo1.MidPointAlgo(markers);

        for (MarkerNode node : newMarkers){
            newCoordsString = newCoordsString + node.lat + "," + node.lng + "," + node.elevation + "/";
        }

        return newCoordsString;
    }


}
