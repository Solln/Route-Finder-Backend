package main.Controllers;

import main.Algorithms.*;
import main.GraphElements.MarkerNode;
import main.HgtReader;
import org.slf4j.Marker;

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

        // Convert into sets of coords
        ArrayList<ArrayList<MarkerNode>> sets = new ArrayList<>();

        for (int i = 0; i < markers.size() - 1; i++) {
            ArrayList<MarkerNode> set = new ArrayList<>();
            set.add(markers.get(i));
            set.add(markers.get(i + 1));
            sets.add(set);
        }

        return runMeshAlgo(sets);
    }

    private String runMeshAlgo(ArrayList<ArrayList<MarkerNode>> sets) {
        //Run Algorithm 5

        ArrayList<ArrayList<MarkerNode>> newSets = new ArrayList<>();

        for (ArrayList<MarkerNode> set : sets) {

            meshAlgo algo5A = new meshAlgo();
            List<MarkerNode> newMarkers = algo5A.runMultiAlgo(set);

            newSets.add((ArrayList<MarkerNode>) newMarkers);
        }
        return convertToMarkerString(newSets.get(newSets.size()-1));
    }

    private String runMidpointAlgo(ArrayList<ArrayList<MarkerNode>> sets) {
        ArrayList<ArrayList<MarkerNode>> newSets = new ArrayList<>();

        for (ArrayList<MarkerNode> set : sets) {

            //Run Algorithm 1
            MidPointAlgo algo1 = new MidPointAlgo();
            List<MarkerNode> newMarkers = algo1.runMidPointAlgo(set);

            newSets.add((ArrayList<MarkerNode>) newMarkers);

        }
        return convertToMarkerString(combineSets(newSets));
    }

    private String runTreeAlgo(ArrayList<ArrayList<MarkerNode>> sets) {
        ArrayList<ArrayList<MarkerNode>> newSets = new ArrayList<>();

        for (ArrayList<MarkerNode> set : sets) {

            //Run Algorithm 1
            TreeAlgo algo = new TreeAlgo();
            List<MarkerNode> newMarkers = algo.runTreeAlgo(set);

            newSets.add((ArrayList<MarkerNode>) newMarkers);

        }
        return convertToMarkerString(combineSets(newSets));

    }

    private String runTreePlusAlgo(ArrayList<ArrayList<MarkerNode>> sets) {
        ArrayList<ArrayList<MarkerNode>> newSets = new ArrayList<>();

        for (ArrayList<MarkerNode> set : sets) {

            //Run Algorithm 1
            TreeAlgoPlus algo = new TreeAlgoPlus();
            List<MarkerNode> newMarkers = algo.runTreeAlgo(set);

            newSets.add((ArrayList<MarkerNode>) newMarkers);

        }
        return convertToMarkerString(combineSets(newSets));
    }

    private String runMultiRunTree(ArrayList<ArrayList<MarkerNode>> sets) {
        ArrayList<ArrayList<MarkerNode>> newSets = new ArrayList<>();

        for (ArrayList<MarkerNode> set : sets) {

            //Run Algorithm 1
            multiRunTree algo = new multiRunTree();
            List<MarkerNode> newMarkers = algo.runMultiAlgo(set);

            newSets.add((ArrayList<MarkerNode>) newMarkers);

        }
        return convertToMarkerString(combineSets(newSets));
    }

    private ArrayList<MarkerNode> combineSets(ArrayList<ArrayList<MarkerNode>> sets) {

        ArrayList<MarkerNode> finalMarkerList = new ArrayList<>();

        if (sets.size() > 1) {

            for (int i = 0; i < sets.size(); i++) {

                if (i == 0) {
                    finalMarkerList.addAll(sets.get(i));
                } else {
                    sets.get(i).remove(0);
                    finalMarkerList.addAll(sets.get(i));
                }
            }
        } else {
            finalMarkerList.addAll(sets.get(0));
        }

        return finalMarkerList;

    }

    private String convertToMarkerString(List<MarkerNode> newMarkers) {
        StringBuilder newCoordsString = new StringBuilder();

        for (MarkerNode node : newMarkers) {
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

            double elevation = new HgtReader().getElevation(lat, lng);

            markers.add(new MarkerNode(lat, lng, elevation));
        }
    }


}
