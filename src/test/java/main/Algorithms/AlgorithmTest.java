package main.Algorithms;

import main.GraphElements.MarkerNode;
import main.Helpers.ConverterWorkshop;
import main.Helpers.HgtReader;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class AlgorithmTest {

    // NOTE THAT A CHECK HAS ALREADY BEEN COMPLETED WITHIN THE ALGORITHM CLASS TO CHECK IT IS THE "BEST ROUTE"

    HgtReader reader = new HgtReader();
    ConverterWorkshop converter = new ConverterWorkshop();

    // Markers for a valley located in the Cairngorms National Park, Scotland
    private MarkerNode START = new MarkerNode(56.8349,-3.7718, reader.getElevation(56.8349,-3.7718));
    private MarkerNode END = new MarkerNode(56.8860,-3.6746, reader.getElevation(56.8860,-3.6746));
    private double dist = converter.getDistance(START.getLat(), START.getLng(), END.getLat(), END.getLng());



    private ArrayList<MarkerNode> markers = new ArrayList<>();

    @Test
    public void meshRunAlgo() {
        MeshAlgo algo = new MeshAlgo();
        ArrayList<MarkerNode> returnedList = runAlgo(algo);

        // Check for reasonable resolution
        double markers = dist / 88;
        assertTrue(returnedList.size() > markers);
    }

    @Test
    public void midPointRunAlgo() {
        MidPointAlgo algo = new MidPointAlgo();
        ArrayList<MarkerNode> returnedList = runAlgo(algo);

        // Check for reasonable resolution
        double markers = 9;
        assertTrue(returnedList.size() == markers);
    }

    @Test
    public void multiRunAlgo() {
        MultiRunTree algo = new MultiRunTree();
        ArrayList<MarkerNode> returnedList = runAlgo(algo);

        // Check for reasonable resolution
        double markers = 122;
        assertTrue(returnedList.size() == markers);
    }

    @Test
    public void treeRunAlgo() {
        TreeAlgo algo = new TreeAlgo();
        ArrayList<MarkerNode> returnedList = runAlgo(algo);

        // Check for reasonable resolution
        double markers = 12;
        assertTrue(returnedList.size() == markers);
    }

    @Test
    public void treePlusRunAlgo() {
        TreeAlgoPlus algo = new TreeAlgoPlus();
        ArrayList<MarkerNode> returnedList = runAlgo(algo);

        // Check for reasonable resolution
        double markers = 12;
        assertTrue(returnedList.size() == markers);
    }


    @Test
    public void getTotalDistance() {

    }

    private ArrayList<MarkerNode> runAlgo(Algorithm algo){
        markers.add(START);
        markers.add(END);

        ArrayList<MarkerNode> returnedMarkerList = (ArrayList<MarkerNode>) algo.runAlgo(markers);

        // Check for Start and End nodes
        assertEquals(returnedMarkerList.get(0), START);
        assertEquals(returnedMarkerList.get(returnedMarkerList.size()-1), END);

        assertTrue(returnedMarkerList.size() > 2);

        return returnedMarkerList;
    }

}