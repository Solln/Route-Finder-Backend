package main.Algorithms.MidPointAlgoGraphElements;

import main.GraphElements.MarkerNode;

import java.util.*;

public class Graph {

    private Map<MarkerNode, LinkedHashSet<MarkerNode>> map = new HashMap();

    public void addEdge(MarkerNode node1, MarkerNode node2) {
        LinkedHashSet<MarkerNode> adjacent = map.get(node1);
        if(adjacent==null) {
            adjacent = new LinkedHashSet();
            map.put(node1, adjacent);
        }
        adjacent.add(node2);
    }

    public void addTwoWayVertex(MarkerNode node1, MarkerNode node2) {
        addEdge(node1, node2);
        addEdge(node2, node1);
    }

    public boolean isConnected(MarkerNode node1, MarkerNode node2) {
        Set adjacent = map.get(node1);
        if(adjacent==null) {
            return false;
        }
        return adjacent.contains(node2);
    }

    public LinkedList<MarkerNode> adjacentNodes(MarkerNode last) {
        LinkedHashSet<MarkerNode> adjacent = map.get(last);
        if(adjacent==null) {
            return new LinkedList();
        }
        return new LinkedList<MarkerNode>(adjacent);
    }

}
