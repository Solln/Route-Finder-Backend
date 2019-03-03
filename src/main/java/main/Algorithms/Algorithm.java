package main.Algorithms;

import main.GraphElements.MarkerNode;

import java.util.ArrayList;
import java.util.List;

public interface Algorithm {

    List<MarkerNode> runAlgo(ArrayList<MarkerNode> markers);

    double getTotalDistance();


}
