package main.Helpers;

import main.GraphElements.MarkerNode;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class DijkstraTest {

    @Test
    public void calculateShortestPathFromSource() {

        ArrayList<MarkerNode> grid = new ArrayList<>();

        for (int i = 0; i < 3; i++){
            for (int j = 0; j < 3; j++){
                grid.add(new MarkerNode(i, j, 100));
            }
        }

        grid = gridSetup(grid);

        Dijkstra dij = new Dijkstra(3);

        grid = (ArrayList<MarkerNode>) dij.calculateShortestPathFromSource(grid, grid.get(0));

        assertEquals(grid.get(2).getShortestPath().size(), 2);
        assertEquals(grid.get(6).getShortestPath().size(), 2);
        assertEquals(grid.get(8).getShortestPath().size(), 2);
        assertEquals(grid.get(0).getShortestPath().size(), 0);

        assertTrue(grid.get(8).getShortestPath().contains(grid.get(0)) && grid.get(8).getShortestPath().contains(grid.get(4)));



    }

    private ArrayList<MarkerNode> gridSetup(ArrayList<MarkerNode> grid) {
        ArrayList<MarkerNode> children;

        // XXX
        // XXX
        // OXX
        children = new ArrayList<>();
        children.add(grid.get(1));
        children.add(grid.get(3));
        children.add(grid.get(4));
        grid.get(0).setChildren(children);

        // XXX
        // XXX
        // XOX
        children = new ArrayList<>();
        children.add(grid.get(0));
        children.add(grid.get(2));
        children.add(grid.get(3));
        children.add(grid.get(4));
        children.add(grid.get(5));
        grid.get(1).setChildren(children);

        // XXX
        // XXX
        // XXO
        children = new ArrayList<>();
        children.add(grid.get(1));
        children.add(grid.get(4));
        children.add(grid.get(5));

        grid.get(2).setChildren(children);

        // XXX
        // OXX
        // XXX
        children = new ArrayList<>();
        children.add(grid.get(0));
        children.add(grid.get(1));
        children.add(grid.get(4));
        children.add(grid.get(6));
        children.add(grid.get(7));
        grid.get(3).setChildren(children);

        // XXX
        // XOX
        // XXX
        children = new ArrayList<>();
        children.add(grid.get(0));
        children.add(grid.get(1));
        children.add(grid.get(2));
        children.add(grid.get(3));
        children.add(grid.get(5));
        children.add(grid.get(6));
        children.add(grid.get(7));
        children.add(grid.get(8));
        grid.get(4).setChildren(children);

        // XXX
        // XXO
        // XXX
        children = new ArrayList<>();
        children.add(grid.get(2));
        children.add(grid.get(4));
        children.add(grid.get(7));
        children.add(grid.get(8));
        grid.get(5).setChildren(children);

        // OXX
        // XXX
        // XXX
        children = new ArrayList<>();
        children.add(grid.get(3));
        children.add(grid.get(4));
        children.add(grid.get(7));

        grid.get(6).setChildren(children);

        // XOX
        // XXX
        // XXX
        children = new ArrayList<>();
        children.add(grid.get(3));
        children.add(grid.get(4));
        children.add(grid.get(5));
        children.add(grid.get(6));
        children.add(grid.get(8));
        grid.get(7).setChildren(children);

        // XXO
        // XXX
        // XXX
        children = new ArrayList<>();
        children.add(grid.get(4));
        children.add(grid.get(5));
        children.add(grid.get(7));
        grid.get(8).setChildren(children);

        return grid;
    }
}