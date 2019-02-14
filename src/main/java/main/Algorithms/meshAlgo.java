package main.Algorithms;

import main.ConverterWorkshop;
import main.Dijkstra;
import main.GraphElements.MarkerNode;
import main.HgtReader;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class meshAlgo {

    List<MarkerNode> markerBank = new ArrayList<>();
    ConverterWorkshop converter = new ConverterWorkshop();
    HgtReader hgt = new HgtReader();

    MarkerNode START, END;

    double totalDistance = 0;

    double resolution = 0.001;

    public List<MarkerNode> runMultiAlgo(ArrayList<MarkerNode> markers) {

        START = markers.get(0);
        END = markers.get(1);

        totalDistance = converter.getDistance(START.getLat(), START.getLng(), END.getLat(), END.getLng());

        double bearing = converter.getBearing(START.getLat(), START.getLng(), END.getLat(), END.getLng());

        double[] newBearings = converter.getNewBearings(bearing);

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

        for (int i = 0; i < rows.size(); i++) {

            for (int j = 0; j < rows.get(i).size(); j++) {

                // START
                if (j == 0) {
                    ArrayList<MarkerNode> startChildren = new ArrayList();
                    startChildren.add(rows.get(i).get(j + 1));

                    if (i == 0) {
                        startChildren.add(rows.get(i + 1).get(j));
                    } else if (i == rows.size() - 1) {
                        startChildren.add(rows.get(i - 1).get(j));
                    } else {
                        startChildren.add(rows.get(i + 1).get(j));
                        startChildren.add(rows.get(i - 1).get(j));
                    }
                    rows.get(i).get(j).setChildren(startChildren);
                }
                // END
                else if (j == rows.get(i).size() - 1) {
                    ArrayList<MarkerNode> endChildren = new ArrayList();
                    endChildren.add(rows.get(i).get(j - 1));

                    if (i == 0) {
                        endChildren.add(rows.get(i + 1).get(j));
                    } else if (i == rows.size() - 1) {
                        endChildren.add(rows.get(i - 1).get(j));
                    } else {
                        endChildren.add(rows.get(i + 1).get(j));
                        endChildren.add(rows.get(i - 1).get(j));
                    }
                    rows.get(i).get(j).setChildren(endChildren);
                }
                // MIDDLE
                else {
                    ArrayList<MarkerNode> midChildren = new ArrayList();
                    midChildren.add(rows.get(i).get(j + 1));
                    midChildren.add(rows.get(i).get(j - 1));

                    if (i == 0) {
                        midChildren.add(rows.get(i + 1).get(j));
                    } else if (i == rows.size() - 1) {
                        midChildren.add(rows.get(i - 1).get(j));
                    } else {
                        midChildren.add(rows.get(i + 1).get(j));
                        midChildren.add(rows.get(i - 1).get(j));
                    }
                    rows.get(i).get(j).setChildren(midChildren);
                }
            }
        }

        for (List<MarkerNode> row : rows) {
            markerBank.addAll(row);
        }

        // 2.5mil markers - 15 seconds
        System.out.println("Total markers considered - " + markerBank.size());

        Dijkstra dij = new Dijkstra();

        ArrayList<MarkerNode> dijMarkers = (ArrayList<MarkerNode>) dij.calculateShortestPathFromSource((ArrayList<MarkerNode>) markerBank, markerBank.get(markerBank.indexOf(START)));

        MarkerNode newEnd = dijMarkers.get(dijMarkers.indexOf(END));

        LinkedList<MarkerNode> routeList = newEnd.getShortestPath();

        ArrayList<MarkerNode> convertedList = new ArrayList<>(routeList);

        convertedList.add(END);

        return convertedList;
    }


}
