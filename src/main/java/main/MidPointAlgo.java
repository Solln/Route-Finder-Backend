package main;

import main.graph.MarkerNode;
import main.graph.Route;
import main.testGraph.Search;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MidPointAlgo {

    private ArrayList<MarkerNode> markers = new ArrayList<>();

    private int[] numMarkers = new int[]{3, 5, 9, 17, 33, 65, 129, 257, 513, 1025};

    private ConverterWorkshop converter = new ConverterWorkshop();

    // EVERY X.XX(X)X IS 130M


    public List<MarkerNode> runMidPointAlgo(ArrayList<MarkerNode> markers) {
        this.markers = markers;

        Route route = plotMidPoints();

        ArrayList<MarkerNode> newMarkerList;

        newMarkerList = route.getMarkers();

        return newMarkerList;
    }

    private Route plotMidPoints() {

        //CALCULATING NUMBER OF NODES START
        double dist = distance(markers.get(0).getLat(), markers.get(0).getLng(),
                markers.get(1).getLat(), markers.get(1).getLng());
        int markersThatCanBePlaced = (int) (dist * 10);
        System.out.println(dist + " Kilometers\n");
        System.out.println("Markers to be placed: " + markersThatCanBePlaced);

        int distance = Math.abs(numMarkers[0] - markersThatCanBePlaced);
        int idx = 0;
        for (int c = 1; c < numMarkers.length; c++) {
            int cdistance = Math.abs(numMarkers[c] - markersThatCanBePlaced);
            if (cdistance < distance) {
                idx = c;
                distance = cdistance;
            }
        }
        int markersToBePlaced = numMarkers[idx];

        System.out.println("Closest Value is: " + markersToBePlaced);

        // =======================================================================

        // Halving edges 3 times

        Search search = new Search();
        search.setSTART(markers.get(0));
        search.setEND(markers.get(1));
        search.main(null);
        ArrayList<Route> routes = (ArrayList<Route>) search.getRoutes();
        ArrayList<Route> trimmedRoutes = new ArrayList<>();

        for (Route ele : routes) {
            if (ele.getMarkers().size() == 9) {
                trimmedRoutes.add(ele);
            }
        }

        for (Route test : trimmedRoutes) {
            double elevationChange = 0;

            for (int i = 1; i < test.getMarkers().size(); i++) {
                double eleA = test.getMarkers().get(i-1).getElevation();
                double eleB = test.getMarkers().get(i).getElevation();

//                testString = testString + " / " + eleA + ", " + eleB;
//                output = output + eleA + eleB;

                double eleChange = eleB - eleA;

                if (eleChange > 0) {
                    elevationChange = elevationChange + eleChange;
                }
            }
            test.setElevationChange(elevationChange);
        }


        Route selected = trimmedRoutes.stream().min(Comparator.comparing(route -> route.getElevationChange())).get();

        System.out.println("Selected change is: " + selected.getElevationChange());

        for (int i = 0; i < 200; i++){
            System.out.println(trimmedRoutes.get(i).getElevationChange());
        }

        return selected;
    }


    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        dist = dist * 1.609344;
        return dist;
    }

    // This function converts decimal degrees to radians
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


}
