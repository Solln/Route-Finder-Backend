package main.Controllers;

import main.Algorithms.*;
import main.GraphElements.MarkerNode;
import main.Helpers.HgtReader;
import main.Helpers.NaismithConverter;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MappingHelper {

    private String coords;
    public ArrayList<MarkerNode> markers = new ArrayList<>();
    private NaismithConverter nConv = new NaismithConverter();

    private double defaultDist = 999;

    private DecimalFormat df = new DecimalFormat("#.####");

    public MappingHelper(String coords) {
        this.coords = coords;
    }


    public String createMarkerObjects() {

        // Getting the passed settings from the start
        String[] split = coords.split(";");

        // 0 is slope, 1 is algorithm
        String[] settings = split[0].split("/");

        // Extracts from String into MarkerNode Objects (markers)
        extractMarkers(split[1]);

        // Convert into sets of coords
        ArrayList<ArrayList<MarkerNode>> sets = new ArrayList<>();

        for (int i = 0; i < markers.size() - 1; i++) {
            ArrayList<MarkerNode> set = new ArrayList<>();
            set.add(markers.get(i));
            set.add(markers.get(i + 1));
            sets.add(set);
        }

        // Select Algorithm
        switch (settings[1]) {
            case "1":
                System.out.println("Running Midpoint");
                return runMidpointAlgo(sets);
            case "2":
                System.out.println("Running Tree");
                return runTreeAlgo(sets);
            case "3":
                System.out.println("Running Tree Plus");
                return runTreePlusAlgo(sets);
            case "4":
                System.out.println("Running Multi");
                return runMultiRunTree(sets);
            case "5":
                System.out.println("Running Mesh");
                return runMeshAlgo(sets, Integer.parseInt(settings[0]));
            case "6":
                System.out.println("Running All");
                return runAllAlgos(sets);
        }

        System.out.println("Deferring to Default...");
        return runMeshAlgo(sets, Integer.parseInt(settings[0]));
    }

    private String runMeshAlgo(ArrayList<ArrayList<MarkerNode>> sets, int slopeLimit) {
        //Run Algorithm 5

        ArrayList<ArrayList<MarkerNode>> newSets = new ArrayList<>();

        double maxSlope = 0;
        double actDistance = 0;

        for (ArrayList<MarkerNode> set : sets) {

            List<MarkerNode> newMarkers = new ArrayList<>();

            // Expand the slope size
            while (newMarkers.size() < 3) {

                if (slopeLimit == 60) {
                    break;
                }

                System.out.println("Slope size: " + slopeLimit);

                MeshAlgo algo = new MeshAlgo();
                algo.setSlopeLimit(slopeLimit);
                newMarkers = algo.runAlgo(set);
                actDistance = algo.getTotalDistance();
                if (newMarkers.size() > 3) {
                    if (maxSlope < slopeLimit) {
                        maxSlope = slopeLimit;
                    }
                } else {
                    slopeLimit = slopeLimit + 10;
                }
            }

            // Catch if both fails
            if (newMarkers.size() < 3) {
                return "No Route Found";
            }

            newSets.add((ArrayList<MarkerNode>) newMarkers);
        }

        String test = maxSlope + "/" + (int)actDistance + "/" + fourDecimal(nConv.calcSingleRouteTime(newSets.get(newSets.size() - 1))) + ";" + convertToMarkerString(newSets.get(newSets.size() - 1));

        return test;
    }

    private String runMidpointAlgo(ArrayList<ArrayList<MarkerNode>> sets) {
        ArrayList<ArrayList<MarkerNode>> newSets = new ArrayList<>();
        double actDistance = 0;

        for (ArrayList<MarkerNode> set : sets) {

            //Run Algorithm 1
            Algorithm algo = new MidPointAlgo();
            List<MarkerNode> newMarkers = algo.runAlgo(set);
            actDistance = algo.getTotalDistance();

            newSets.add((ArrayList<MarkerNode>) newMarkers);

        }

        String test = 0 + "/" + actDistance + "/" + fourDecimal(nConv.calcSingleRouteTime(combineSets(newSets))) + ";" + convertToMarkerString(combineSets(newSets));

        return test;
    }

    private String runTreeAlgo(ArrayList<ArrayList<MarkerNode>> sets) {
        ArrayList<ArrayList<MarkerNode>> newSets = new ArrayList<>();
        double actDistance = 0;

        for (ArrayList<MarkerNode> set : sets) {

            //Run Algorithm 1
            Algorithm algo = new TreeAlgo();
            List<MarkerNode> newMarkers = algo.runAlgo(set);
            actDistance = algo.getTotalDistance();

            newSets.add((ArrayList<MarkerNode>) newMarkers);

        }

        String test = 0 + "/" + actDistance + "/" + fourDecimal(nConv.calcSingleRouteTime(combineSets(newSets))) + ";" + convertToMarkerString(combineSets(newSets));

        return test;
    }

    private String runTreePlusAlgo(ArrayList<ArrayList<MarkerNode>> sets) {
        ArrayList<ArrayList<MarkerNode>> newSets = new ArrayList<>();
        double actDistance = 0;

        for (ArrayList<MarkerNode> set : sets) {

            //Run Algorithm 1
            Algorithm algo = new TreeAlgoPlus();
            List<MarkerNode> newMarkers = algo.runAlgo(set);
            actDistance = algo.getTotalDistance();

            newSets.add((ArrayList<MarkerNode>) newMarkers);

        }

        String test = 0 + "/" + actDistance + "/" + fourDecimal(nConv.calcSingleRouteTime(combineSets(newSets))) + ";" + convertToMarkerString(combineSets(newSets));

        return test;    }

    private String runMultiRunTree(ArrayList<ArrayList<MarkerNode>> sets) {
        ArrayList<ArrayList<MarkerNode>> newSets = new ArrayList<>();
        double actDistance = 0;

        for (ArrayList<MarkerNode> set : sets) {

            //Run Algorithm 1
            Algorithm algo = new MultiRunTree();
            List<MarkerNode> newMarkers = algo.runAlgo(set);
            actDistance = algo.getTotalDistance();

            newSets.add((ArrayList<MarkerNode>) newMarkers);

        }

        String test = 0 + "/" + actDistance + "/" + fourDecimal(nConv.calcSingleRouteTime(combineSets(newSets))) + ";"  + convertToMarkerString(combineSets(newSets));

        return test;    }

    private String runAllAlgos(ArrayList<ArrayList<MarkerNode>> sets){
        String mesh = runMeshAlgo(sets, 40);
        String mid = runMidpointAlgo(sets);
        String tree = runTreeAlgo(sets);
        String treeP = runTreePlusAlgo(sets);
        String multi = runMultiRunTree(sets);

        return mesh + "+" + mid + "+" + tree + "+" + treeP + "+" + multi;
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

    public void extractMarkers(String coordSet) {
        String[] splitCoords;

        splitCoords = coordSet.split("\\/");


        for (String latlng : splitCoords) {
            String[] indivEntries;
            indivEntries = latlng.split("\\,");

            double lat = fourDecimal(Double.parseDouble(indivEntries[0]));
            double lng = fourDecimal(Double.parseDouble(indivEntries[1]));

            double elevation = new HgtReader().getElevation(lat, lng);

            markers.add(new MarkerNode(lat, lng, elevation));
        }
    }

    private double fourDecimal(double value){
        df.setRoundingMode(RoundingMode.CEILING);
        return Double.parseDouble(df.format(value));
    }

}
