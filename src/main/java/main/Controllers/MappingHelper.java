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
    private NaismithConverter nConv;

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

        nConv = new NaismithConverter(Integer.parseInt(settings[2]));

        int slope = Integer.parseInt(settings[0]);
        int fitness = Integer.parseInt(settings[2]);

        // Select Algorithm
        switch (settings[1]) {
            case "1":
                System.out.println("Running Midpoint");
                return runMidpointAlgo(sets, slope, fitness);
            case "2":
                System.out.println("Running Tree");
                return runTreeAlgo(sets, slope, fitness);
            case "3":
                System.out.println("Running Tree Plus");
                return runTreePlusAlgo(sets, slope, fitness);
            case "4":
                System.out.println("Running Multi");
                return runMultiRunTree(sets, slope, fitness);
            case "5":
                System.out.println("Running Mesh");
                return runMeshAlgo(sets, slope, fitness);
            case "6":
                System.out.println("Running All");
                return runAllAlgos(sets, slope, fitness);
        }

        System.out.println("Deferring to Default...");
        return runMeshAlgo(sets, Integer.parseInt(settings[0]), Integer.parseInt(settings[2]));
    }

    private String runMeshAlgo(ArrayList<ArrayList<MarkerNode>> sets, int slopeLimit, int fitness) {
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

                MeshAlgo algo = new MeshAlgo(slopeLimit, fitness);
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

        return maxSlope + "/" + (int) actDistance + "/" + fourDecimal(nConv.calcSingleRouteTime(newSets.get(newSets.size() - 1))) + ";" + convertToMarkerString(newSets.get(newSets.size() - 1));
    }

    private String runMidpointAlgo(ArrayList<ArrayList<MarkerNode>> sets, int slopeLimit, int fitness) {
        ArrayList<ArrayList<MarkerNode>> newSets = new ArrayList<>();
        double actDistance = 0;

        for (ArrayList<MarkerNode> set : sets) {

            //Run Algorithm 1
            Algorithm algo = new MidPointAlgo(slopeLimit, fitness);
            List<MarkerNode> newMarkers = algo.runAlgo(set);
            actDistance = algo.getTotalDistance();

            newSets.add((ArrayList<MarkerNode>) newMarkers);

        }

        return 0 + "/" + actDistance + "/" + fourDecimal(nConv.calcSingleRouteTime(combineSets(newSets))) + ";" + convertToMarkerString(combineSets(newSets));
    }

    private String runTreeAlgo(ArrayList<ArrayList<MarkerNode>> sets, int slopeLimit, int fitness) {
        ArrayList<ArrayList<MarkerNode>> newSets = new ArrayList<>();
        double actDistance = 0;

        for (ArrayList<MarkerNode> set : sets) {

            //Run Algorithm 1
            Algorithm algo = new TreeAlgo(slopeLimit, fitness);
            List<MarkerNode> newMarkers = algo.runAlgo(set);
            actDistance = algo.getTotalDistance();

            newSets.add((ArrayList<MarkerNode>) newMarkers);

        }

        return 0 + "/" + actDistance + "/" + fourDecimal(nConv.calcSingleRouteTime(combineSets(newSets))) + ";" + convertToMarkerString(combineSets(newSets));
    }

    private String runTreePlusAlgo(ArrayList<ArrayList<MarkerNode>> sets, int slopeLimit, int fitness) {
        ArrayList<ArrayList<MarkerNode>> newSets = new ArrayList<>();
        double actDistance = 0;

        for (ArrayList<MarkerNode> set : sets) {

            //Run Algorithm 1
            Algorithm algo = new TreeAlgoPlus(slopeLimit, fitness);
            List<MarkerNode> newMarkers = algo.runAlgo(set);
            actDistance = algo.getTotalDistance();

            newSets.add((ArrayList<MarkerNode>) newMarkers);

        }

        return 0 + "/" + actDistance + "/" + fourDecimal(nConv.calcSingleRouteTime(combineSets(newSets))) + ";" + convertToMarkerString(combineSets(newSets));


    }

    private String runMultiRunTree(ArrayList<ArrayList<MarkerNode>> sets, int slopeLimit, int fitness) {
        ArrayList<ArrayList<MarkerNode>> newSets = new ArrayList<>();
        double actDistance = 0;

        for (ArrayList<MarkerNode> set : sets) {

            //Run Algorithm 1
            Algorithm algo = new MultiRunTree(slopeLimit, fitness);
            List<MarkerNode> newMarkers = algo.runAlgo(set);
            actDistance = algo.getTotalDistance();

            newSets.add((ArrayList<MarkerNode>) newMarkers);

        }

        return 0 + "/" + actDistance + "/" + fourDecimal(nConv.calcSingleRouteTime(combineSets(newSets))) + ";" + convertToMarkerString(combineSets(newSets));
    }

    private String runAllAlgos(ArrayList<ArrayList<MarkerNode>> sets, int slopeLimit, int fitness) {
        String mesh = runMeshAlgo(sets, slopeLimit, fitness);
        String mid = runMidpointAlgo(sets, slopeLimit, fitness);
        String tree = runTreeAlgo(sets, slopeLimit, fitness);
        String treeP = runTreePlusAlgo(sets, slopeLimit, fitness);
        String multi = runMultiRunTree(sets, slopeLimit, fitness);

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

    private double fourDecimal(double value) {
        df.setRoundingMode(RoundingMode.CEILING);
        return Double.parseDouble(df.format(value));
    }

}
