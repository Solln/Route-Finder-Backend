package main.Algorithms;

import main.GraphElements.MarkerNode;

import java.util.ArrayList;
import java.util.List;

public class MultiRunTree implements Algorithm{

    //Default Values
    private int slopeLimit = 30;
    private int fitness = 3;


    public MultiRunTree(int slopeLimit, int fitness){
        this.slopeLimit = slopeLimit;
        this.fitness = fitness;
    }


    public List<MarkerNode> runAlgo(ArrayList<MarkerNode> markers) {

        TreeAlgoPlus algo1 = new TreeAlgoPlus(slopeLimit, fitness);

        List<MarkerNode> newMarkers = algo1.runAlgo(markers);

        List<MarkerNode> finalMarkers = new ArrayList<>();

        List<List<MarkerNode>> markerSets = new ArrayList<>();

        for (int i = 0; i < newMarkers.size() - 1; i++) {

            List<MarkerNode> set = new ArrayList();
            set.add(newMarkers.get(i));
            set.add(newMarkers.get(i + 1));

            markerSets.add(set);
        }

        for (List<MarkerNode> set : markerSets) {
            set.get(0).setChildren(null);
            set.get(1).setChildren(null);
            set.get(0).setParent(null);
            set.get(1).setParent(null);
        }



        List<List<MarkerNode>> testList = new ArrayList<>();

        for (List<MarkerNode> set : markerSets) {
            List<MarkerNode> splitMarkers = new TreeAlgoPlus(slopeLimit, fitness).runAlgo((ArrayList<MarkerNode>) set);

            testList.add(splitMarkers);

            splitMarkers.remove(splitMarkers.size()-1);

            finalMarkers.addAll(splitMarkers);
        }

        finalMarkers.add(markers.get(1));

        return finalMarkers;

    }

    public double getTotalDistance() {
        return 0;
    }

}
