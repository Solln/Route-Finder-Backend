package main;

import main.graph.MarkerNode;
import main.graph.RouteEdge;
import main.graph.RouteGraph;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MidPointAlgo {

    private ArrayList<Marker> markers = new ArrayList<>();

    private int[] numMarkers = new int[]{3, 5, 9, 17, 33, 65, 129, 257, 513, 1025};

    private RouteGraph route = new RouteGraph();

    // EVERY X.XX(X)X IS 130M


    public List<MarkerNode> MidPointAlgo(ArrayList<Marker> markers) {
        this.markers = markers;

        plotMidPoints();

        ArrayList<MarkerNode> newMarkerList = new ArrayList<>();

        for (RouteEdge edge : route.getEdges()){
            if (edge.getStart() != null && !newMarkerList.contains(edge.getStart())){
                newMarkerList.add(edge.getStart());
            }
            if (edge.getEnd() != null && !newMarkerList.contains(edge.getEnd())){
                newMarkerList.add(edge.getEnd());
            }
        }

        return newMarkerList;
    }

    private void plotMidPoints() {

        double dist = distance(markers.get(0).getlat(), markers.get(0).getlng(),
                markers.get(1).getlat(), markers.get(1).getlng());
        int markersThatCanBePlaced = (int) (dist * 10);
        System.out.println(dist + " Kilometers\n");
        System.out.println("Markers to be placed: " + markersThatCanBePlaced);

        int distance = Math.abs(numMarkers[0] - markersThatCanBePlaced);
        int idx = 0;
        for (int c = 1; c < numMarkers.length; c++) {
            int cdistance = Math.abs(numMarkers[c] - markersThatCanBePlaced);
            if (cdistance < distance) {
                idx = c;
                distance = cdistance;
            }
        }
        int markersToBePlaced = numMarkers[idx];

        System.out.println("Closest Value is: " + markersToBePlaced);

        // Add first edge

        MarkerNode nodeA = new MarkerNode(markers.get(0).getlat(), markers.get(0).getlng(), markers.get(0).getElevation());
        MarkerNode nodeB = new MarkerNode(markers.get(1).getlat(), markers.get(1).getlng(), markers.get(1).getElevation());

        route.addEdge(nodeA, nodeB);

        // Add midpoints

        while (markersToBePlaced > 2) {

            List<RouteEdge> newEdges = new ArrayList<>();

            for (RouteEdge edge : route.getEdges()) {

                double midpoint[] = midPoint(edge.getStart().lat, edge.getStart().lng, edge.getEnd().lat, edge.getEnd().lng);

                double elevation = new HgtReader().getElevation(midpoint[0],midpoint[1]);

                MarkerNode nodeC = new MarkerNode(midpoint[0], midpoint[1], elevation);

                newEdges.add(new RouteEdge(edge.getStart(), nodeC));
                newEdges.add(new RouteEdge(nodeC, edge.getEnd()));
            }

            route.setEdges(newEdges);
            markersToBePlaced = ((markersToBePlaced + 1) / 2);

        }

        System.out.println("Route contains: " + route.getEdges().size());

    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return dist;
    }

    //  This function converts decimal degrees to radians
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public double[] midPoint(double lat1, double lon1, double lat2, double lon2) {

        DecimalFormat df = new DecimalFormat("#.####");

        double dLon = Math.toRadians(lon2 - lon1);

        //convert to radians
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);
        lon1 = Math.toRadians(lon1);

        double Bx = Math.cos(lat2) * Math.cos(dLon);
        double By = Math.cos(lat2) * Math.sin(dLon);
        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + Bx) * (Math.cos(lat1) + Bx) + By * By));
        double lon3 = lon1 + Math.atan2(By, Math.cos(lat1) + Bx);

        // Parsing these values to 4 decimal points
        double[] values = new double[2];
        values[0] = Double.parseDouble(df.format(Math.toDegrees(lat3)));
        values[1] = Double.parseDouble(df.format(Math.toDegrees(lon3)));

        return values;
    }


}
