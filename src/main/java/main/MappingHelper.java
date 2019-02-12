package main;

import main.graph.MarkerNode;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MappingHelper {

    private String coords;
    private ArrayList<MarkerNode> markers = new ArrayList<>();

    private DecimalFormat df = new DecimalFormat("#.####");

    public MappingHelper(String coords) {
        this.coords = coords;
    }


    public String createMarkerObjects() {

        // Extracts from String into MarkerNode Objects (markers)
        extractMarkers();

        //Run Algorithm 1
        MidPointAlgo algo1 = new MidPointAlgo();
        //List<MarkerNode> newMarkers = algo1.runMidPointAlgo(markers);

        //Run Algorithm 2
        TreeAlgo algo2 = new TreeAlgo();
//        List<MarkerNode> newMarkers = algo2.runTreeAlgoAlgo(markers);

        //Run Algorithm 3
        TreeAlgoPlus algo3 = new TreeAlgoPlus();
        List<MarkerNode> newMarkers = algo3.runTreeAlgoAlgo(markers);

        return convertToMarkerString(newMarkers);
    }

    private String convertToMarkerString(List<MarkerNode> newMarkers) {
        StringBuilder newCoordsString = new StringBuilder();

        for (MarkerNode node : newMarkers){
            newCoordsString.append(node.getLat()).append(",").append(node.getLng()).append(",").append(node.getElevation()).append("/");
        }

        return newCoordsString.toString();
    }

    private void extractMarkers() {
        String[] splitCoords;

        splitCoords = coords.split("\\/");

        df.setRoundingMode(RoundingMode.CEILING);

        for (String latlng : splitCoords) {
            String[] indivEntries;
            indivEntries = latlng.split("\\,");

            double lat = Double.parseDouble(df.format(Double.parseDouble(indivEntries[0])));
            double lng = Double.parseDouble(df.format(Double.parseDouble(indivEntries[1])));

            double elevation = new HgtReader().getElevation(lat,lng);

            markers.add(new MarkerNode(lat, lng, elevation));
        }
    }


}
