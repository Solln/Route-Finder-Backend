package main.Helpers;

import main.Algorithms.MeshAlgo;
import main.Controllers.MappingHelper;
import main.GraphElements.MarkerNode;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.HashSet;
import java.util.List;


public class GraphStream {

    public static void main(String args[]) {
        Graph graph = new SingleGraph("Tutorial 1");
//        Small
        String coords = "56.9691,-3.5528/56.9537,-3.5529/";

        // Large
//        String coords = "57.0178,-3.7038/57.0344,-3.7086/";

        //Lake
//        String coords = "57.0779,-3.8012/57.0779,-3.7789/";

        MappingHelper helper = new MappingHelper(coords);
        helper.extractMarkers(coords);

        MeshAlgo algo = new MeshAlgo();

        algo.setSlopeLimit(30);

        algo.runAlgo(helper.markers);

        List<MarkerNode> nodes = algo.markerBank;

        HashSet<MarkerNode> set = new HashSet(nodes);

        for (MarkerNode node : set) {
            graph.addNode(Integer.toString(nodes.indexOf(node)));
        }

        for (MarkerNode node : nodes) {
            for (MarkerNode child : node.getChildren()) {
                String edgeID = Integer.toString(nodes.indexOf(node)) + "," + Integer.toString(nodes.indexOf(child));
                try {
                    graph.addEdge(edgeID, Integer.toString(nodes.indexOf(node)), Integer.toString(nodes.indexOf(child)));
                } catch (Exception e) {

                }
            }
        }

        graph.display();
    }


}
