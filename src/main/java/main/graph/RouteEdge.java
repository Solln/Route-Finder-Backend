package main.graph;

import main.ConverterWorkshop;
import main.HgtReader;

import java.util.ArrayList;

public class RouteEdge {

    private MarkerNode start;
    private MarkerNode end;

    HgtReader hgt = new HgtReader();

    private double distance;
    private double elevationChange;

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

    public ArrayList<RouteEdge> split(char mode) {

        ConverterWorkshop converter = new ConverterWorkshop();

        MarkerNode newPoint;

        RouteEdge halfOne;
        RouteEdge halfTwo;

        double spreadDistance = converter.getDistance(start.getLat(), start.getLng(), end.getLat(), end.getLng()) / 8;

        double[] midPoint = converter.getMidPoint(start.getLat(), start.getLng(), end.getLat(), end.getLng());
        double bearing = converter.getBearing(start.getLat(), start.getLng(), end.getLat(), end.getLng());
        double[] newBearings = converter.getNewBearings(bearing);

        if (mode == 'M') {
            newPoint = new MarkerNode(midPoint[0], midPoint[1], hgt.getElevation(midPoint[0], midPoint[1]));

            halfOne = new RouteEdge(start, newPoint);
            halfTwo = new RouteEdge(newPoint, end);

        } else if (mode == 'L') {
            double[] leftPoint = converter.newPoint(midPoint[0], midPoint[1], newBearings[0], spreadDistance);
            MarkerNode left = new MarkerNode(leftPoint[0], leftPoint[1], hgt.getElevation(leftPoint[0], leftPoint[1]));

            halfOne = new RouteEdge(start, left);
            halfTwo = new RouteEdge(left, end);

        } else if (mode == 'R') {
            double[] rightPoint = converter.newPoint(midPoint[0], midPoint[1], newBearings[1], spreadDistance);
            MarkerNode right = new MarkerNode(rightPoint[0], rightPoint[1], hgt.getElevation(rightPoint[0], rightPoint[1]));

            halfOne = new RouteEdge(start, right);
            halfTwo = new RouteEdge(right, end);
        }
        else{
            System.out.println("Couldn't Generate wut");
            halfOne = null;
            halfTwo = null;
        }


        ArrayList<RouteEdge> newHalves = new ArrayList<>();

        newHalves.add(halfOne);
        newHalves.add(halfTwo);

        return newHalves;
    }

}
