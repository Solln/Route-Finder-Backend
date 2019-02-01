package main.graph;

import java.util.ArrayList;
import java.util.List;

public class RouteGraph {

    List<RouteEdge> edges = new ArrayList<>();

    public RouteGraph() {

    }

    public void addEdge(MarkerNode adjNode1, MarkerNode adjNode2) {

        RouteEdge e = new RouteEdge(adjNode1, adjNode2);
        edges.add(e);
    }

    public List<RouteEdge> getEdges() {
        return edges;
    }

    public void setEdges(List<RouteEdge> edges) {
        this.edges = edges;
    }
}
