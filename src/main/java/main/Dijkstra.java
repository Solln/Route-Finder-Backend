package main;

import main.GraphElements.MarkerNode;

import java.util.*;

public class Dijkstra {

    public List<MarkerNode> calculateShortestPathFromSource(ArrayList<MarkerNode> graph, MarkerNode source) {

        source.setDistance(0);

        Set<MarkerNode> settledNodes = new HashSet<>();
        Set<MarkerNode> unsettledNodes = new HashSet<>();

        unsettledNodes.add(source);

        while (unsettledNodes.size() != 0) {
            MarkerNode currentNode = getLowestDistanceNode(unsettledNodes);
            unsettledNodes.remove(currentNode);
            for (MarkerNode child : currentNode.getChildren()) {
                double edgeWeight = child.getElevation() - currentNode.getElevation();
                if (!settledNodes.contains(child)) {
                    calculateMinimumDistance(child, edgeWeight, currentNode);
                    unsettledNodes.add(child);
                }
            }
            settledNodes.add(currentNode);
        }
        return graph;
    }

    private MarkerNode getLowestDistanceNode(Set < MarkerNode > unsettledNodes) {
        MarkerNode lowestDistanceNode = null;
        double lowestDistance = Integer.MAX_VALUE;
        for (MarkerNode node: unsettledNodes) {
            double nodeDistance = node.getDistance();
            if (nodeDistance < lowestDistance) {
                lowestDistance = nodeDistance;
                lowestDistanceNode = node;
            }
        }
        return lowestDistanceNode;
    }

    private void calculateMinimumDistance(MarkerNode evaluationNode,
                                                 double edgeWeigh, MarkerNode sourceNode) {
        double sourceDistance = sourceNode.getDistance();
        if (sourceDistance + edgeWeigh < evaluationNode.getDistance()) {
            evaluationNode.setDistance(sourceDistance + edgeWeigh);
            LinkedList<MarkerNode> shortestPath = new LinkedList<>(sourceNode.getShortestPath());
            shortestPath.add(sourceNode);
            evaluationNode.setShortestPath(shortestPath);
        }
    }

}
