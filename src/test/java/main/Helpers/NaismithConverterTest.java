package main.Helpers;

import main.GraphElements.MarkerNode;
import main.GraphElements.Route;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;


public class NaismithConverterTest {

    private Route route = new Route();
    private ArrayList<Route> routes = new ArrayList<>();
    private NaismithConverter converter = new NaismithConverter();

    @Before
    public void setup() {

        ArrayList<MarkerNode> children;

        // Markers which make up the route
        MarkerNode A = new MarkerNode(10, 10, 200);
        MarkerNode B = new MarkerNode(11, 11, 300);
        MarkerNode C = new MarkerNode(12, 12, 100);

        children = new ArrayList<>();
        children.add(B);
        A.setChildren(children);

        children = new ArrayList<>();
        children.add(B);
        B.setChildren(children);

        // Create a single route
        route.addMarker(A);
        route.addMarker(B);
        route.addMarker(C);

        // Create List of routes
        routes.add(route);
        routes.add(route);
        routes.add(route);

    }


    @Test
    public void convertToTime() {

        assertEquals(converter.convertToTime(5000, 600), 120, 0.1);
        assertEquals(converter.convertToTime(0, 600), 60, 0.1);
        assertEquals(converter.convertToTime(5000, 0), 60, 0.1);

        // Slope of 10 Degree Decline
        assertEquals(converter.convertToTime(1700, -300), 10.4, 0.1);

        // Slope of 20 Degree Decline
        assertEquals(converter.convertToTime(877, -300), 20.5, 0.1);

    }

    @Test
    public void calcRouteTimes() {

        routes = converter.calcRouteTimes(routes);

        assertEquals(routes.get(1).getRouteTime(), 3749.4, 0.1);


    }

    @Test
    public void calcSingleRouteTime() {

        assertEquals(converter.calcSingleRouteTime(route.getMarkers()), 3759.4, 0.1);

    }
}