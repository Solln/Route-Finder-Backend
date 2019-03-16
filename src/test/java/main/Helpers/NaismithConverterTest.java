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
    private NaismithConverter converter;

    @Before
    public void setup() {

        ArrayList<MarkerNode> children;

        // Markers which make up the route
        MarkerNode A = new MarkerNode(57.1, -3.5, 200);
        MarkerNode B = new MarkerNode(57.2, -3.6, 300);
        MarkerNode C = new MarkerNode(57.3, -3.7, 100);

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

        converter = new NaismithConverter(3);

        assertEquals(converter.convertToTime(4000, 600), 120, 0.1);
        assertEquals(converter.convertToTime(0, 600), 60, 0.1);
        assertEquals(converter.convertToTime(4000, 0), 60, 0.1);

        // Slope of 10 Degree Decline
        assertEquals(converter.convertToTime(1700, -300), 15.5, 0.1);

        // Slope of 20 Degree Decline
        assertEquals(converter.convertToTime(877, -300), 23.1, 0.1);

    }

    @Test
    public void calcRouteTimesFitness1() {

        converter = new NaismithConverter(1);

        routes = converter.calcRouteTimes(routes);

        assertEquals(routes.get(1).getRouteTime(), 239.39, 0.1);

    }

    @Test
    public void calcRouteTimesFitness3() {

        converter = new NaismithConverter(3);

        routes = converter.calcRouteTimes(routes);

        assertEquals(routes.get(1).getRouteTime(), 464.08, 0.1);

    }

    @Test
    public void calcRouteTimesFitness5() {

        converter = new NaismithConverter(5);

        routes = converter.calcRouteTimes(routes);

        assertEquals(routes.get(1).getRouteTime(), 628.77, 0.1);

    }

    @Test
    public void calcSingleRouteTimeF1() {
        converter = new NaismithConverter(1);
        assertEquals(converter.calcSingleRouteTime(route.getMarkers()), 249.39, 0.1);

    }

    @Test
    public void calcSingleRouteTimeF3() {
        converter = new NaismithConverter(3);
        assertEquals(converter.calcSingleRouteTime(route.getMarkers()), 479.08, 0.1);

    }

    @Test
    public void calcSingleRouteTimeF5() {
        converter = new NaismithConverter(5);
        assertEquals(converter.calcSingleRouteTime(route.getMarkers()), 648.77, 0.1);

    }
}