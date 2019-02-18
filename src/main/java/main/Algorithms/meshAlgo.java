package main.Algorithms;

import main.ConverterWorkshop;
import main.Dijkstra;
import main.GraphElements.MarkerNode;
import main.HgtReader;

import java.util.*;

public class meshAlgo {

    private List<MarkerNode> markerBank = new ArrayList<>();
    private ConverterWorkshop converter = new ConverterWorkshop();
    private HgtReader hgt = new HgtReader();

    private MarkerNode START;
    private MarkerNode END;

    // 88.9km per 1    0.001 = 90m
    double resolution = 0.001;

    private double totalDistance = 0;

    public List<MarkerNode> runMultiAlgo(ArrayList<MarkerNode> markers) {

        START = markers.get(0);
        END = markers.get(1);

        totalDistance = converter.getDistance(START.getLat(), START.getLng(), END.getLat(), END.getLng());

        double bearing = converter.getBearing(START.getLat(), START.getLng(), END.getLat(), END.getLng());

        double[] newBearings = converter.getNewBearings(bearing);

        // Create the mesh of points around START and END
        List<List<MarkerNode>> rows = createMesh(newBearings);

        // Add all the Neighbours to nodes
        addNeighbours(rows);

        System.out.println("Removed links due to +/- 30 degrees: " + removalCount);

        // 2.5mil markers - 15 seconds
        // Used to detect Water hazards
        markerBank = fetchFrequency((ArrayList<MarkerNode>) markerBank);

        // Run Dijkstras and returns a route to END
        return runDijkstras();
    }

    private List<List<MarkerNode>> createMesh(double[] newBearings) {
        double[] returnedPoint;
        double[] x_array = new double[4];
        double[] y_array = new double[4];

        returnedPoint = converter.newPoint(START.getLat(), START.getLng(), newBearings[0], totalDistance / 4);
        x_array[0] = returnedPoint[1];
        y_array[0] = returnedPoint[0];

        returnedPoint = converter.newPoint(START.getLat(), START.getLng(), newBearings[1], totalDistance / 4);
        x_array[1] = returnedPoint[1];
        y_array[1] = returnedPoint[0];

        returnedPoint = converter.newPoint(END.getLat(), END.getLng(), newBearings[1], totalDistance / 4);
        x_array[2] = returnedPoint[1];
        y_array[2] = returnedPoint[0];

        returnedPoint = converter.newPoint(END.getLat(), END.getLng(), newBearings[0], totalDistance / 4);
        x_array[3] = returnedPoint[1];
        y_array[3] = returnedPoint[0];

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

//        Path2D.Double p = new Path2D.Double();
//
//        p.moveTo(minX, minY);
//        p.lineTo(maxX, minY);
//        p.lineTo(maxX, maxY);
//        p.lineTo(minX, maxY);
//        p.closePath();

//        if (p.contains(x, y)) {

        List<List<MarkerNode>> rows = new ArrayList<>();

        for (double y = minY; y <= maxY; ) {

            ArrayList<MarkerNode> row = new ArrayList<>();

            for (double x = minX; x <= maxX; ) {

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

//        System.out.println("DIJ START NODE = " + START.getLat() + ", " + START.getLng());
//        System.out.println("DIJ END NODE = " + END.getLat() + ", " + END.getLng());

        Dijkstra dij = new Dijkstra();

        ArrayList<MarkerNode> dijMarkers = (ArrayList<MarkerNode>) dij.calculateShortestPathFromSource((ArrayList<MarkerNode>) markerBank, markerBank.get(markerBank.indexOf(START)));

        MarkerNode newEnd = dijMarkers.get(dijMarkers.indexOf(END));

//        System.out.println("newEnd: " + newEnd.getLat() + ", " + newEnd.getLng());

        LinkedList<MarkerNode> routeList = newEnd.getShortestPath();

        ArrayList<MarkerNode> convertedList = new ArrayList<>(routeList);

        convertedList.add(END);

//        System.out.println("First Node: " + convertedList.get(0).getLat() + ", "+ convertedList.get(0).getLng() + "|| Last Node: " + convertedList.get(convertedList.size()-1).getLat() + ", "+ convertedList.get(convertedList.size()-1).getLng());
//        System.out.println("------------------------------");

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

        for (MarkerNode node : list) {
            for (Double val : onesToRemove) {
                if (node.getElevation() == val) {
                    node.setElevation(99999);
                }
            }
        }

        return list;

    }

    int removalCount = 0;

    private boolean checkGradient(MarkerNode start, MarkerNode end) {

        double eleChange = end.getElevation() - start.getElevation();

        if (eleChange > 0) {
            double slope = Math.toDegrees(Math.atan(eleChange / 90));
            if (slope > 30) {
                removalCount++;
                return false;
            } else {
                return true;
            }
        } else if (eleChange < 0) {
            double slope = Math.toDegrees(Math.atan(eleChange / 90));
            if (slope < -30) {
                removalCount++;
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }


    }

}
