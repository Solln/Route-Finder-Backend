package main.Algorithms;

import main.Helpers.ConverterWorkshop;
import main.Helpers.HgtReader;
import main.Helpers.NaismithConverter;
import main.GraphElements.MarkerNode;
import main.GraphElements.Route;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TreeAlgoPlus implements Algorithm{

    private ConverterWorkshop converter = new ConverterWorkshop();
    private HgtReader hgt = new HgtReader();

    private MarkerNode START, END;

    private double baseBearing;

    // Num of Markers between Start and End
    private int numOfMidPoints = 10;
    private double edgeDist = 0;

    private ArrayList<Route> finalRoutes = new ArrayList<>();

    private NaismithConverter NConverter = new NaismithConverter();

    public List<MarkerNode> runAlgo(ArrayList<MarkerNode> markers) {

        START = markers.get(0);
        END = markers.get(1);

        baseBearing = converter.getBearing(START.getLat(), START.getLng(), END.getLat(), END.getLng());

        edgeDist = converter.getDistance(START.getLat(), START.getLng(), END.getLat(), END.getLng());


        // Midpoint out at normal bearing for X distance
        // Use this point with perp bearing for X distance for side points

        //Get bearing between new points and end point, if > 45 degrees then scrap the route?

        //if last section then link straight to end point

        MarkerNode completedTree = addChildren(START, numOfMidPoints, 0, edgeDist);

        printPaths(completedTree);

        finalRoutes = NConverter.calcRouteTimes(finalRoutes);

        Route selected = finalRoutes.stream().min(Comparator.comparing(route -> route.getRouteTime())).get();

        return selected.getMarkers();
    }

    private void printPaths(MarkerNode node) {
        MarkerNode path[] = new MarkerNode[1000];
        printPathsRecur(node, path, 0);
    }

    private void printPathsRecur(MarkerNode node, MarkerNode path[], int pathLen) {
        if (node == null)
            return;

        /* append this node to the path array */
        path[pathLen] = node;
        pathLen++;

        /* it's a leaf, so print the path that led to here  */
        if (node.getChildren().size() == 1 && node.getChildren().get(0) == END)
            printArray(path, pathLen);
        else {
            /* otherwise try both subtrees  */
            for (MarkerNode childNode : node.getChildren()){
                printPathsRecur(childNode, path, pathLen);
            }


        }
    }

    private void printArray(MarkerNode markers[], int len) {
        Route newRoute = new Route();
        for (int i = 0; i < len; i++) {
            newRoute.addMarker(markers[i]);
        }
        newRoute.addMarker(END);
        finalRoutes.add(newRoute);
    }

    private MarkerNode addChildren(MarkerNode currentNode, int limit, int counter, double dist) {

        ArrayList<MarkerNode> children = new ArrayList<>();

        MarkerNode nodeToReturn = currentNode;
        ArrayList<MarkerNode> newChildren = new ArrayList<>();

        int newDiv = limit - counter;
        double spllitDist = dist / newDiv;

        if (limit == counter) {
            newChildren.add(END);
        } else {
            // MID
            double midBearing = converter.getBearing(currentNode.getLat(), currentNode.getLng(), END.getLat(), END.getLng());

            double[] newMidBearings = converter.getNewBearings(midBearing);

            double[] mid = converter.newPoint(currentNode.getLat(), currentNode.getLng(), midBearing, spllitDist);

            MarkerNode midNode = new MarkerNode(mid[0], mid[1], hgt.getElevation(mid[0], mid[1]));
            midNode.setParent(currentNode);
            children.add(midNode);

            //LEFT
            double[] left = converter.newPoint(mid[0], mid[1], newMidBearings[0], spllitDist);
            double leftCheckBearing = converter.getBearing(left[0], left[1], END.getLat(), END.getLng());
            double leftAngle = (baseBearing - leftCheckBearing + 180 + 360) % 360 - 180;
            if (leftAngle <= 90 && leftAngle >= -90) {
                MarkerNode leftNode = new MarkerNode(left[0], left[1], hgt.getElevation(left[0], left[1]));
                leftNode.setParent(currentNode);
                children.add(leftNode);
            }

            //RIGHT
            double[] right = converter.newPoint(mid[0], mid[1], newMidBearings[1], spllitDist);
            double rightCheckBearing = converter.getBearing(right[0], right[1], END.getLat(), END.getLng());
            double rightAngle = (baseBearing - rightCheckBearing + 180 + 360) % 360 - 180;
            if (rightAngle <= 90 && rightAngle >= -90) {
                MarkerNode rightNode = new MarkerNode(right[0], right[1], hgt.getElevation(right[0], right[1]));
                rightNode.setParent(currentNode);
                children.add(rightNode);
            }
        }

        currentNode.setChildren(children);

        counter += 1;

        for (MarkerNode node : currentNode.getChildren()) {
            if (counter <= limit) {
                newChildren.add(addChildren(node, limit, counter, (dist - spllitDist)));
            }
        }
        nodeToReturn.setChildren(newChildren);

        return nodeToReturn;

    }

    public double getTotalDistance() {
        return 0;
    }

}
