package main.graph;

import java.util.ArrayList;

public class MarkerNode {

    private double lat, lng, elevation;

    private MarkerNode parent;

    // MID, LEFT, RIGHT
    private ArrayList<MarkerNode> children = null;

    public MarkerNode(double lat, double lng, double elevation) {
        this.lat = lat;
        this.lng = lng;
        this.elevation = elevation;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getElevation() {
        return elevation;
    }

    public void setElevation(double elevation) {
        this.elevation = elevation;
    }

    public ArrayList<MarkerNode> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<MarkerNode> children) {
        this.children = children;
    }

    public MarkerNode getParent() {
        return parent;
    }

    public void setParent(MarkerNode parent) {
        this.parent = parent;
    }
}
