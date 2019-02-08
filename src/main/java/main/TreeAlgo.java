package main;

import main.graph.MarkerNode;
import main.graph.Route;

import java.util.ArrayList;
import java.util.List;

public class TreeAlgo {

    ConverterWorkshop converter = new ConverterWorkshop();
    HgtReader hgt = new HgtReader();

    MarkerNode START, END;

    double baseBearing;
    double[] newBearings;
    double distance;

    ArrayList<Route> finalRoutes = new ArrayList<>();

    public List<MarkerNode> runTreeAlgoAlgo(ArrayList<MarkerNode> markers) {

        START = markers.get(0);
        END = markers.get(1);

        baseBearing = converter.getBearing(START.getLat(), START.getLng(), END.getLat(), END.getLng());
        newBearings = converter.getNewBearings(baseBearing);

        double edgeDist = converter.getDistance(START.getLat(), START.getLng(), END.getLat(), END.getLng());

        int numOfSplits = 12;

        // Dynamic Number here please   EG 5 edges / 6 total markers / 4 inner markers
        distance = edgeDist / (numOfSplits + 1);

        // Midpoint out at normal bearing for X distance
        // Use this point with perp bearing for X distance for side points

        //Get bearing between new points and end point, if > 45 degrees then scrap the route?

        //if last section then link straight to end point

        System.out.println("Total Distance is: " + edgeDist);
        System.out.println("Splitting into : " + numOfSplits+1 + " Sections");
        System.out.println("Total of : " + (numOfSplits+2) + " Markers");
        System.out.println("Distance of each split: " + distance);

        MarkerNode completedTree = addChildren(START, numOfSplits, 0);

        printPaths(completedTree);

        return finalRoutes.get(9).getMarkers();
    }




    void printPaths(MarkerNode node)
    {
        MarkerNode path[] = new MarkerNode[1000];
        printPathsRecur(node, path, 0);
    }

    /* Recursive helper function -- given a node, and an array
       containing the path from the root node up to but not
       including this node, print out all the root-leaf paths.*/
    void printPathsRecur(MarkerNode node, MarkerNode path[], int pathLen)
    {
        if (node == null)
            return;

        /* append this node to the path array */
        path[pathLen] = node;
        pathLen++;

        /* it's a leaf, so print the path that led to here  */
        if (node.getChildren().size() == 1)
            printArray(path, pathLen);
        else
        {
            /* otherwise try both subtrees */
            printPathsRecur(node.getChildren().get(0), path, pathLen);
            printPathsRecur(node.getChildren().get(1), path, pathLen);
            printPathsRecur(node.getChildren().get(2), path, pathLen);
        }
    }


    void printArray(MarkerNode markers[], int len)
    {
        Route newRoute = new Route();
        for (int i = 0; i < len; i++)
        {
            newRoute.addMarker(markers[i]);
        }
        newRoute.addMarker(END);
        finalRoutes.add(newRoute);
    }




    private MarkerNode addChildren(MarkerNode currentNode, int limit, int counter) {

        ArrayList<MarkerNode> children = new ArrayList<>();

        MarkerNode nodeToReturn = currentNode;
        ArrayList<MarkerNode> newChildren = new ArrayList<>();

        if (limit == counter) {
            newChildren.add(END);
        } else {
            // MID
            double[] mid = converter.newPoint(currentNode.getLat(), currentNode.getLng(), baseBearing, distance);
            MarkerNode midNode = new MarkerNode(mid[0], mid[1], hgt.getElevation(mid[0], mid[1]));
            midNode.setParent(currentNode);
            children.add(midNode);

            //LEFT
            double[] left = converter.newPoint(mid[0], mid[1], newBearings[0], distance/4);
            MarkerNode leftNode = new MarkerNode(left[0], left[1], hgt.getElevation(left[0], left[1]));
            leftNode.setParent(currentNode);
            children.add(leftNode);

            //RIGHT
            double[] right = converter.newPoint(mid[0], mid[1], newBearings[1], distance/4);
            MarkerNode rightNode = new MarkerNode(right[0], right[1], hgt.getElevation(right[0], right[1]));
            rightNode.setParent(currentNode);
            children.add(rightNode);
        }

        currentNode.setChildren(children);

        counter += 1;

        for (MarkerNode node : currentNode.getChildren()) {
            if (counter <= limit) {
                newChildren.add(addChildren(node, limit, counter));
            }
        }
        nodeToReturn.setChildren(newChildren);

        return nodeToReturn;

    }

}
