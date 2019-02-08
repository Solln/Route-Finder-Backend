package main.graph;

import java.util.ArrayList;

public class Route {

    private ArrayList<RouteEdge> edges = new ArrayList<>();

    private ArrayList<MarkerNode> markers = new ArrayList<>();

    private double elevationChange = 0;

    public void addEdge(MarkerNode adjNode1, MarkerNode adjNode2) {
        RouteEdge e = new RouteEdge(adjNode1, adjNode2);
        edges.add(e);
    }

    public void addMarker(MarkerNode marker) {
        markers.add(marker);
    }

    public ArrayList<RouteEdge> getEdges() {
        return edges;
    }

    public void setEdges(ArrayList<RouteEdge> edges) {
        this.edges = edges;
    }

    public ArrayList<MarkerNode> getMarkers() {
        return markers;
    }

    public void setMarkers(ArrayList<MarkerNode> markers) {
        this.markers = markers;
    }

    public double getElevationChange() {
        return elevationChange;
    }

    public void setElevationChange(double elevationChange) {
        this.elevationChange = elevationChange;
    }
}
