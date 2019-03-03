package main.Algorithms;

import main.GraphElements.MarkerNode;
import main.Helpers.ConverterWorkshop;
import main.Helpers.Dijkstra;
import main.Helpers.HgtReader;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class MeshAlgo implements Algorithm{

    public List<MarkerNode> markerBank = new ArrayList<>();
    private ConverterWorkshop converter = new ConverterWorkshop();
    private HgtReader hgt = new HgtReader();

    private DecimalFormat df = new DecimalFormat("#.####");


    private MarkerNode START;
    private MarkerNode END;

    // 88.9km per 1    0.001 = 70m - 110m   is the correct unit of measurement for the elevation data capped at 90m
    double resolution = 0.001;

    private double minDistance = 0;
    private double totalDistance = 0;
    private double splitDist = 0.01;


    public List<MarkerNode> runAlgo(ArrayList<MarkerNode> markers) {

        START = markers.get(0);
        END = markers.get(1);

        minDistance = converter.getDistance(START.getLat(), START.getLng(), END.getLat(), END.getLng());

        // Create the mesh of points around START and END
        List<List<MarkerNode>> rows = createMesh();

        // Add all the Neighbours to nodes
        addNeighbours(rows);

        // 2.5mil markers - 15 seconds
        // Used to detect Water hazards
        markerBank = fetchFrequency((ArrayList<MarkerNode>) markerBank);

        // Run Dijkstras and returns a route to END
        return runDijkstras();
    }

    private List<List<MarkerNode>> createMesh() {
        double[] x_array = new double[4];
        double[] y_array = new double[4];

        x_array[0] = START.getLng() + splitDist;
        y_array[0] = START.getLat() + splitDist;

        x_array[1] = START.getLng() - splitDist;
        y_array[1] = START.getLat() - splitDist;

        x_array[2] = END.getLng() + splitDist;
        y_array[2] = END.getLat() + splitDist;

        x_array[3] = END.getLng() - splitDist;
        y_array[3] = END.getLat() - splitDist;

        double minX = 100, maxX = -100, minY = 100, maxY = -100;

        for (double val : x_array) {
            if (val < minX) {
                minX = val;
            }
            if (val > maxX) {
                maxX = val;
            }
        }

        for (double val : y_array) {
            if (val < minY) {
                minY = val;
            }
            if (val > maxY) {
                maxY = val;
            }
        }

        List<List<MarkerNode>> rows = new ArrayList<>();

        for (double y = minY; y <= maxY; ) {

            y = fourDecimal(y);

            ArrayList<MarkerNode> row = new ArrayList<>();

            for (double x = minX; x <= maxX; ) {

                x = fourDecimal(x);

                MarkerNode newMarker;

                if (START.getLng() >= x && START.getLng() <= (x + resolution) && START.getLat() >= y && START.getLat() <= (y + resolution)) {
                    row.add(START);
                } else if (END.getLng() >= x && END.getLng() <= (x + resolution) && END.getLat() >= y && END.getLat() <= (y + resolution)) {
                    row.add(END);
                } else {
                    newMarker = new MarkerNode(y, x, hgt.getElevation(y, x));
                    row.add(newMarker);
                }

                x = x + resolution;
            }
            rows.add(row);
            y = y + resolution;
        }
        return rows;
    }

    private void addNeighbours(List<List<MarkerNode>> rows) {

        // TODO Add water check here? if previous 4 is same value then elevation == 999999 ?

        // i is the ROWS, j is the Columns

        for (int i = 0; i < rows.size(); i++) {

            for (int j = 0; j < rows.get(i).size(); j++) {

                // Adding the relevant children, Checking each ones gradient and not adding if too steep > or < 30

                // START -----------------------------------------------------------------------------
                if (j == 0) {
                    ArrayList<MarkerNode> startChildren = new ArrayList<>();
                    // Nodes that always go on START/LEFT //  X -> x
                    if (checkGradient(rows.get(i).get(j), rows.get(i).get(j + 1))) {
                        startChildren.add(rows.get(i).get(j + 1));
                    }
                    // BOTTOM LEFT CORNER
                    if (i == 0) {
                        if (checkGradient(rows.get(i).get(j), rows.get(i + 1).get(j))) {
                            startChildren.add(rows.get(i + 1).get(j));
                        }
                        if (checkGradient(rows.get(i).get(j), rows.get(i + 1).get(j + 1))) {
                            startChildren.add(rows.get(i + 1).get(j + 1));
                        }
                    }
                    // TOP LEFT CORNER
                    else if (i == rows.size() - 1) {
                        if (checkGradient(rows.get(i).get(j), rows.get(i - 1).get(j))) {
                            startChildren.add(rows.get(i - 1).get(j));
                        }
                        if (checkGradient(rows.get(i).get(j), rows.get(i - 1).get(j + 1))) {
                            startChildren.add(rows.get(i - 1).get(j + 1));
                        }
                    }
                    // LEFT MIDDLE
                    else {
                        if (checkGradient(rows.get(i).get(j), rows.get(i + 1).get(j))) {
                            startChildren.add(rows.get(i + 1).get(j));
                        }
                        if (checkGradient(rows.get(i).get(j), rows.get(i + 1).get(j + 1))) {
                            startChildren.add(rows.get(i + 1).get(j + 1));
                        }
                        if (checkGradient(rows.get(i).get(j), rows.get(i - 1).get(j))) {
                            startChildren.add(rows.get(i - 1).get(j));
                        }
                        if (checkGradient(rows.get(i).get(j), rows.get(i - 1).get(j + 1))) {
                            startChildren.add(rows.get(i - 1).get(j + 1));
                        }
                    }
                    rows.get(i).get(j).setChildren(startChildren);
                }
                // END -----------------------------------------------------------------------------
                else if (j == rows.get(i).size() - 1) {
                    ArrayList<MarkerNode> endChildren = new ArrayList<>();
                    // Nodes that always go on END/RIGHT //  x <- X
                    if (checkGradient(rows.get(i).get(j), rows.get(i).get(j - 1))) {
                        endChildren.add(rows.get(i).get(j - 1));
                    }

                    // Bottom Right Corner
                    if (i == 0) {
                        if (checkGradient(rows.get(i).get(j), rows.get(i + 1).get(j))) {
                            endChildren.add(rows.get(i + 1).get(j));
                        }
                        if (checkGradient(rows.get(i).get(j), rows.get(i + 1).get(j - 1))) {
                            endChildren.add(rows.get(i + 1).get(j - 1));
                        }
                    }
                    // Top Right Corner
                    else if (i == rows.size() - 1) {
                        if (checkGradient(rows.get(i).get(j), rows.get(i - 1).get(j))) {
                            endChildren.add(rows.get(i - 1).get(j));
                        }
                        if (checkGradient(rows.get(i).get(j), rows.get(i - 1).get(j - 1))) {
                            endChildren.add(rows.get(i - 1).get(j - 1));
                        }
                    }
                    // Right Middle
                    else {
                        if (checkGradient(rows.get(i).get(j), rows.get(i + 1).get(j))) {
                            endChildren.add(rows.get(i + 1).get(j));
                        }
                        if (checkGradient(rows.get(i).get(j), rows.get(i + 1).get(j - 1))) {
                            endChildren.add(rows.get(i + 1).get(j - 1));
                        }
                        if (checkGradient(rows.get(i).get(j), rows.get(i - 1).get(j))) {
                            endChildren.add(rows.get(i - 1).get(j));
                        }
                        if (checkGradient(rows.get(i).get(j), rows.get(i - 1).get(j - 1))) {
                            endChildren.add(rows.get(i - 1).get(j - 1));
                        }
                    }
                    rows.get(i).get(j).setChildren(endChildren);
                }
                // MIDDLE -----------------------------------------------------------------------------
                else {
                    ArrayList<MarkerNode> midChildren = new ArrayList<>();
                    // Nodes that always go on middle //  x <- X -> x
                    if (checkGradient(rows.get(i).get(j), rows.get(i).get(j + 1))) {
                        midChildren.add(rows.get(i).get(j + 1));
                    }
                    if (checkGradient(rows.get(i).get(j), rows.get(i).get(j - 1))) {
                        midChildren.add(rows.get(i).get(j - 1));
                    }

                    // Bottom Middle
                    if (i == 0) {
                        if (checkGradient(rows.get(i).get(j), rows.get(i + 1).get(j))) {
                            midChildren.add(rows.get(i + 1).get(j));
                        }
                        if (checkGradient(rows.get(i).get(j), rows.get(i + 1).get(j + 1))) {
                            midChildren.add(rows.get(i + 1).get(j + 1));
                        }
                        if (checkGradient(rows.get(i).get(j), rows.get(i + 1).get(j - 1))) {
                            midChildren.add(rows.get(i + 1).get(j - 1));
                        }
                    }
                    // Top Middle
                    else if (i == rows.size() - 1) {
                        if (checkGradient(rows.get(i).get(j), rows.get(i - 1).get(j))) {
                            midChildren.add(rows.get(i - 1).get(j));
                        }
                        if (checkGradient(rows.get(i).get(j), rows.get(i - 1).get(j + 1))) {
                            midChildren.add(rows.get(i - 1).get(j + 1));
                        }
                        if (checkGradient(rows.get(i).get(j), rows.get(i - 1).get(j - 1))) {
                            midChildren.add(rows.get(i - 1).get(j - 1));
                        }
                    }
                    // All Inner Nodes
                    else {
                        if (checkGradient(rows.get(i).get(j), rows.get(i + 1).get(j))) {
                            midChildren.add(rows.get(i + 1).get(j));
                        }
                        if (checkGradient(rows.get(i).get(j), rows.get(i + 1).get(j + 1))) {
                            midChildren.add(rows.get(i + 1).get(j + 1));
                        }
                        if (checkGradient(rows.get(i).get(j), rows.get(i + 1).get(j - 1))) {
                            midChildren.add(rows.get(i + 1).get(j - 1));
                        }
                        if (checkGradient(rows.get(i).get(j), rows.get(i - 1).get(j))) {
                            midChildren.add(rows.get(i - 1).get(j));
                        }
                        if (checkGradient(rows.get(i).get(j), rows.get(i - 1).get(j + 1))) {
                            midChildren.add(rows.get(i - 1).get(j + 1));
                        }
                        if (checkGradient(rows.get(i).get(j), rows.get(i - 1).get(j - 1))) {
                            midChildren.add(rows.get(i - 1).get(j - 1));
                        }

                    }
                    rows.get(i).get(j).setChildren(midChildren);
                }
            }
        }

        for (List<MarkerNode> row : rows) {
            markerBank.addAll(row);
        }
    }

    private ArrayList<MarkerNode> runDijkstras() {

        Dijkstra dij = new Dijkstra();

        ArrayList<MarkerNode> dijMarkers = (ArrayList<MarkerNode>) dij.calculateShortestPathFromSource((ArrayList<MarkerNode>) markerBank, markerBank.get(markerBank.indexOf(START)));

        MarkerNode newEnd = dijMarkers.get(dijMarkers.indexOf(END));

        LinkedList<MarkerNode> routeList = newEnd.getShortestPath();

        ArrayList<MarkerNode> convertedList = new ArrayList<>(routeList);

        convertedList.add(END);

        // TESTING -- Used to count the amount of total 0 Path routes
//        int count = 0;
//
//        for (MarkerNode node : dijMarkers){
//            if (node.getShortestPath().size() == 0){
//                count++;
//            }
//        }

        for (int i = 1; i < convertedList.size(); i++) {

            double fLat = convertedList.get(i - 1).getLat();
            double fLng = convertedList.get(i - 1).getLng();
            double sLat = convertedList.get(i).getLat();
            double sLng = convertedList.get(i).getLng();

            double indivDist = converter.getDistance(fLat, fLng, sLat, sLng);

            totalDistance = totalDistance + indivDist;
        }

        return convertedList;
    }

    private ArrayList<MarkerNode> fetchFrequency(ArrayList<MarkerNode> list) {

        HashMap<Double, Integer> sets = new HashMap<>();

        for (MarkerNode node : list) {
            if (sets.containsKey(node.getElevation())) {
                int value = sets.get(node.getElevation());
                value++;
                sets.put(node.getElevation(), value);
            } else {
                sets.put(node.getElevation(), 1);
            }
        }

        double total = 0;
        for (Map.Entry<Double, Integer> set : sets.entrySet()) {
            total = total + set.getValue();
        }

        double average = total / sets.size();

        ArrayList<Double> onesToRemove = new ArrayList<>();

        for (Map.Entry<Double, Integer> set : sets.entrySet()) {
            if (set.getValue() > average * 5) {
                onesToRemove.add(set.getKey());
            }
        }

        // Remove links to "Lake" Areas
        ArrayList<MarkerNode> nodesToRemove = new ArrayList<>();

        for (MarkerNode node : list) {
            for (Double val : onesToRemove) {
                if (node.getElevation() == val) {
                    nodesToRemove.add(node);
                }
            }
        }

        for (MarkerNode node : list){
            for (MarkerNode removal : nodesToRemove) {
                node.getChildren().remove(removal);
            }
        }

        return list;
    }

    private int slopeLimit = 30;

    private boolean checkGradient(MarkerNode start, MarkerNode end) {

        double eleChange = end.getElevation() - start.getElevation();

        if (eleChange > 0) {
            double slope = Math.toDegrees(Math.atan(eleChange / 90));
            if (slope > slopeLimit) {
                return false;
            } else {
                return true;
            }
        } else if (eleChange < 0) {
            double slope = Math.toDegrees(Math.atan(eleChange / 90));
            if (slope < -slopeLimit) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void setSlopeLimit(int slopeLimit) {
        this.slopeLimit = slopeLimit;
    }

    public double getTotalDistance() {
        return totalDistance;
    }

    private double fourDecimal(double value) {
        df.setRoundingMode(RoundingMode.CEILING);
        return Double.parseDouble(df.format(value));
    }
}
