package main;

import main.GraphElements.MarkerNode;
import main.GraphElements.Route;

import java.util.ArrayList;

public class NaismithConverter {

    ConverterWorkshop converter = new ConverterWorkshop();

    public double convertToTime(double distance, double height) {

        double time = 0;
        double horizonal = distance / 83.3333;
        double vertical = 0;

        if (height >= 0) {
            vertical = height / 10;
            time = horizonal + vertical;
        } else {
            height = height * -1;

            double slope = Math.toDegrees(Math.atan(height / distance));

            if (slope < 5) {
                time = horizonal;
            } else if (slope >= 5 && slope <= 12) {
                double minsToTakeOff = (height / 30);
                time = horizonal - minsToTakeOff;
            } else if (slope > 12) {
                double minsToAdd = (height / 30);
                time = horizonal + minsToAdd;
            }
        }

        return time;
    }

    public ArrayList<Route> calcRouteTimes(ArrayList<Route> finalRoutes) {

        for (Route route : finalRoutes) {
            double time = 0;
            ArrayList<MarkerNode> markers = route.getMarkers();
            for (int i = 0; i < markers.size(); i++) {
                if (markers.get(i).getChildren() != null) {

                    double eleA;
                    double eleB;
                    double eleChange = 0;

                    if (markers.size() == 9) {
                        System.out.println();
                    }
                    try {
                        eleA = markers.get(i).getElevation();
                        eleB = markers.get(i + 1).getElevation();
                        eleChange = eleB - eleA;
                    } catch (Exception e) {
                        System.out.println();
                    }

                    double dist = converter.getDistance(markers.get(i).getLat(), markers.get(i).getLng(),
                            markers.get(i + 1).getLat(), markers.get(i + 1).getLng());

                    time = time + convertToTime(dist, eleChange);

                } else {
                    route.setRouteTime(time);
                }
            }
        }

        return finalRoutes;

    }

}
