package main.graph;

public class RouteEdge {

    public MarkerNode start;
    public MarkerNode end;

    public double distance;
    public double elevationChange;

    public RouteEdge(MarkerNode start, MarkerNode end) {
        this.start = start;
        this.end = end;
    }

    public MarkerNode getStart() {
        return start;
    }

    public void setStart(MarkerNode start) {
        this.start = start;
    }

    public MarkerNode getEnd() {
        return end;
    }

    public void setEnd(MarkerNode end) {
        this.end = end;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getElevationChange() {
        return elevationChange;
    }

    public void setElevationChange(double elevationChange) {
        this.elevationChange = elevationChange;
    }
}
