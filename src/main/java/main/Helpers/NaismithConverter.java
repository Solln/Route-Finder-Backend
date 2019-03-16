package main.Helpers;

import main.GraphElements.MarkerNode;
import main.GraphElements.Route;

import java.util.ArrayList;
import java.util.HashMap;

public class NaismithConverter {

    private ConverterWorkshop converter = new ConverterWorkshop();
    private int fitnessChoice;
    private HashMap<Integer, Double> fitnessMap = new HashMap<>();

    public NaismithConverter(int fitness){
        fitnessChoice = fitness;
        setupCorrections();
    }

    public double convertToTime(double distance, double height) {

        // dist 4000 per hour  height 600 per hour

        double time = 0;

        // Gets the time taken for the horizontal distance
        // 4k per hour on unstable terrain
        double horizonal = distance / 66.666666;
        double vertical = 0;

        // Gets the time taken for the vertical distance depending on the height
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

                    try {
                        eleA = markers.get(i).getElevation();
                        eleB = markers.get(i + 1).getElevation();
                        eleChange = eleB - eleA;


                        double dist = converter.getDistance(markers.get(i).getLat(), markers.get(i).getLng(),
                                markers.get(i + 1).getLat(), markers.get(i + 1).getLng());

                        time = time + convertToTime(dist, eleChange);

                    } catch (Exception e) {
                    }

                } else {
                    time = applyCorrections(time);
                    route.setRouteTime(time);
                }
            }
        }

        return finalRoutes;

    }

    public double calcSingleRouteTime(ArrayList<MarkerNode> markers) {

        double time = 0;

        for (int i = 1; i < markers.size(); i++) {

            double eleA = markers.get(i - 1).getElevation();

            double eleB = markers.get(i).getElevation();
            double eleChange = 0;

            eleChange = eleA - eleB;


            double dist = converter.getDistance(markers.get(i - 1).getLat(), markers.get(i - 1).getLng(),
                    markers.get(i).getLat(), markers.get(i).getLng());

            time = time + convertToTime(dist, eleChange);
        }


        time = applyCorrections(time);

        return time;

    }

    private double applyCorrections(double time) {

        double timeInHours = time / 60;

        int floor = (int) Math.floor(timeInHours);
        int ceiling = (int) Math.ceil(timeInHours);

        // Value after the point
        double point = timeInHours - floor;

        if (ceiling <= fitnessMap.size()) {

            // gets the difference between the 2 values ie 2 and 3.5, partVal is 1.5
            double partVal = fitnessMap.get(ceiling) - fitnessMap.get(floor);

            double addition = (partVal / 100) * (point * 100);

            time = fitnessMap.get(floor) + addition;
            time = time * 60 * 100;
            time = Math.round(time);
            time = time /100;

            return time;
        }
        else{
            return 99999;
        }

    }

    private void setupCorrections(){
        if (fitnessChoice == 1) {
            fitnessMap.put(1, 0.5);
            fitnessMap.put(2, 1.0);
            fitnessMap.put(3, 1.5);
            fitnessMap.put(4, 2.0);
            fitnessMap.put(5, 2.75);
            fitnessMap.put(6, 3.5);
            fitnessMap.put(7, 4.5);
            fitnessMap.put(8, 5.5);
            fitnessMap.put(9, 6.75);
            fitnessMap.put(10, 7.75);
        }
        else if (fitnessChoice == 2) {
            fitnessMap.put(1, 0.62);
            fitnessMap.put(2, 1.25);
            fitnessMap.put(3, 2.25);
            fitnessMap.put(4, 3.25);
            fitnessMap.put(5, 4.5);
            fitnessMap.put(6, 5.5);
            fitnessMap.put(7, 6.5);
            fitnessMap.put(8, 7.75);
            fitnessMap.put(9, 8.75);
            fitnessMap.put(10, 10.0);
        }
        else if (fitnessChoice == 3) {
            fitnessMap.put(1, 0.75);
            fitnessMap.put(2, 1.5);
            fitnessMap.put(3, 3.0);
            fitnessMap.put(4, 4.25);
            fitnessMap.put(5, 5.5);
            fitnessMap.put(6, 7.0);
            fitnessMap.put(7, 8.5);
            fitnessMap.put(8, 10.0);
            fitnessMap.put(9, 11.5);
            fitnessMap.put(10, 13.25);
        }
        else if (fitnessChoice == 4) {
            fitnessMap.put(1, 1.0);
            fitnessMap.put(2, 2.0);
            fitnessMap.put(3, 3.5);
            fitnessMap.put(4, 5.0);
            fitnessMap.put(5, 6.75);
            fitnessMap.put(6, 8.5);
            fitnessMap.put(7, 10.5);
            fitnessMap.put(8, 12.5);
            fitnessMap.put(9, 14.5);
        }
        else if (fitnessChoice == 5) {
            fitnessMap.put(1, 1.37);
            fitnessMap.put(2, 2.75);
            fitnessMap.put(3, 4.25);
            fitnessMap.put(4, 5.75);
            fitnessMap.put(5, 7.5);
            fitnessMap.put(6, 9.5);
            fitnessMap.put(7, 11.5);
        }

    }


}
