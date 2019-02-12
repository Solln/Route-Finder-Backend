package main;

import main.graph.MarkerNode;
import main.graph.Route;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TreeAlgoPlus {

    private ConverterWorkshop converter = new ConverterWorkshop();
    private HgtReader hgt = new HgtReader();

    private MarkerNode START, END;

    private double baseBearing;

    // Num of Markers between Start and End
    private int numOfMidPoints = 10;
    private double edgeDist = 0;

    private ArrayList<Route> finalRoutes = new ArrayList<>();

    private NaismithConverter NConverter = new NaismithConverter();

    public List<MarkerNode> runTreeAlgoAlgo(ArrayList<MarkerNode> markers) {

        START = markers.get(0);
        END = markers.get(1);

        baseBearing = converter.getBearing(START.getLat(), START.getLng(), END.getLat(), END.getLng());

        edgeDist = converter.getDistance(START.getLat(), START.getLng(), END.getLat(), END.getLng());


        // Midpoint out at normal bearing for X distance
        // Use this point with perp bearing for X distance for side points

        //Get bearing between new points and end point, if > 45 degrees then scrap the route?

        //if last section then link straight to end point

        System.out.println("Total Distance is: " + edgeDist);
        System.out.println("Splitting into : " + (numOfMidPoints) + " Sections");
        System.out.println("Total of : " + (numOfMidPoints + 1) + " Markers");

        MarkerNode completedTree = addChildren(START, numOfMidPoints, 0, edgeDist);

        printPaths(completedTree);

        finalRoutes = NConverter.calcRouteTimes(finalRoutes);

        Route selected = finalRoutes.stream().min(Comparator.comparing(route -> route.getRouteTime())).get();

        System.out.println("Selected Time for Route: " + selected.getRouteTime() + " Minutes");

        // Testing

//        testPrintElevations();
        int counter = 0;
        boolean isMin = true;
        for (Route testRoute : finalRoutes) {
            if (testRoute.getRouteTime() == selected.getRouteTime()) {
                counter++;
            } else if (testRoute.getRouteTime() < selected.getRouteTime()) {
                isMin = false;
            }
        }

        System.out.println("Total number of routes: " + finalRoutes.size());
        System.out.println("Number of routes with same time: " + counter);
        System.out.println("Selected Route is the min time: " + isMin);

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
        if (node.getChildren().size() == 1)
            printArray(path, pathLen);
        else {
            /* otherwise try both subtrees  */
            printPathsRecur(node.getChildren().get(0), path, pathLen);
            printPathsRecur(node.getChildren().get(1), path, pathLen);
            printPathsRecur(node.getChildren().get(2), path, pathLen);
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
        double spllitDist = dist/newDiv;

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
            MarkerNode leftNode = new MarkerNode(left[0], left[1], hgt.getElevation(left[0], left[1]));
            leftNode.setParent(currentNode);
            children.add(leftNode);

            //RIGHT
            double[] right = converter.newPoint(mid[0], mid[1], newMidBearings[1], spllitDist);
            MarkerNode rightNode = new MarkerNode(right[0], right[1], hgt.getElevation(right[0], right[1]));
            rightNode.setParent(currentNode);
            children.add(rightNode);
        }

        currentNode.setChildren(children);

        counter += 1;

        for (MarkerNode node : currentNode.getChildren()) {
            if (counter <= limit) {
                newChildren.add(addChildren(node, limit, counter, (dist-spllitDist)));
            }
        }
        nodeToReturn.setChildren(newChildren);

        return nodeToReturn;

    }

}
