package main.Algorithms.MidPointAlgoGraphElements;

import main.GraphElements.MarkerNode;
import main.GraphElements.Route;
import main.GraphElements.RouteEdge;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class Search {

    private static MarkerNode START = new MarkerNode(1, 1, 1);
    private static MarkerNode END = new MarkerNode(10, 10, 10);
    private static List routes = new ArrayList();

    public static void main(String[] args) {
        // this GraphElements is directional
        routes = search();
    }

    private static List<ArrayList> search() {
        Graph graph = new Graph();

        // Generate Edges (Don't care about duplicates or replacing)

        RouteEdge baseEdge = new RouteEdge(START, END);
        graph.addEdge(baseEdge.getStart(), baseEdge.getEnd());

        ArrayList<RouteEdge> splitEdges = new ArrayList();

        splitEdges.addAll(baseEdge.split('L'));
        splitEdges.addAll(baseEdge.split('M'));
        splitEdges.addAll(baseEdge.split('R'));

        for (RouteEdge element : new ArrayList<>(splitEdges)) {
            splitEdges.addAll(element.split('L'));
            splitEdges.addAll(element.split('M'));
            splitEdges.addAll(element.split('R'));
        }

        for (RouteEdge element : new ArrayList<>(splitEdges)) {
            splitEdges.addAll(element.split('L'));
            splitEdges.addAll(element.split('M'));
            splitEdges.addAll(element.split('R'));
        }

        for (RouteEdge edge : splitEdges) {
            graph.addEdge(edge.getStart(), edge.getEnd());
        }


//            GraphElements.addEdge(START, END);
//            GraphElements.addEdge(START, midA);
//            GraphElements.addEdge(midA, END);
//            GraphElements.addEdge(START, midB);
//            GraphElements.addEdge(midB, END);
//            GraphElements.addEdge(midA, midB);

        LinkedList<MarkerNode> visited = new LinkedList();
        visited.add(START);
        ArrayList routes = new ArrayList();
        routes.addAll(new Search().depthFirst(graph, visited, routes));
        List<ArrayList> finalRoutes = (List<ArrayList>) routes.stream().distinct().collect(Collectors.toList());

        List<ArrayList> clone = finalRoutes;

        return finalRoutes;
    }

    private ArrayList depthFirst(Graph graph, LinkedList<MarkerNode> visited, ArrayList routes) {
        LinkedList<MarkerNode> nodes = graph.adjacentNodes(visited.getLast());
        // examine adjacent nodes
        for (MarkerNode node : nodes) {
            if (visited.contains(node)) {
                continue;
            }
            if (node.equals(END)) {
                visited.add(node);
                routes.add(printPath(visited));
                visited.removeLast();
                break;
            }
        }
        for (MarkerNode node : nodes) {
            if (visited.contains(node) || node.equals(END)) {
                continue;
            }
            visited.addLast(node);
            depthFirst(graph, visited, routes);
            visited.removeLast();
        }
        return routes;
    }

    private Route printPath(LinkedList<MarkerNode> visited) {
        Route route = new Route();
        for (MarkerNode node : visited) {
            route.addMarker(node);
        }
        return route;
    }

    public static List getRoutes() {
        return routes;
    }

    public static void setSTART(MarkerNode START) {
        Search.START = START;
    }

    public static void setEND(MarkerNode END) {
        Search.END = END;
    }
}
