package main.Helpers;

import org.junit.Test;

import static org.junit.Assert.*;

public class ConverterWorkshopTest {

    ConverterWorkshop converter = new ConverterWorkshop();

    @Test
    public void getNewBearings() {

        double newBearings[] = converter.getNewBearings(180);

        assertEquals(90, newBearings[0], 0.1);
        assertEquals(270, newBearings[1], 0.1);

        newBearings = converter.getNewBearings(0);

        assertEquals(270, newBearings[0], 0.1);
        assertEquals(90, newBearings[1], 0.1);

    }

    @Test
    public void convertToDMS() {

        assertEquals(converter.convertToDMS(10, 20), "10 0 0 N20 0 0 E");

        assertEquals(converter.convertToDMS(-10, 20), "10 0 0 S20 0 0 E");

        assertEquals(converter.convertToDMS(10, -20), "10 0 0 N20 0 0 W");

        assertEquals(converter.convertToDMS(-10, -20), "10 0 0 S20 0 0 W");

    }

    @Test
    public void convertSingleDMS() {

        assertEquals(converter.convertSingleDMS(10), "10 0 0");

        assertEquals(converter.convertSingleDMS(-10), "-10 0 0");


    }

    @Test
    public void getDistance() {

        double distance = converter.getDistance(10, 10, 11, 10);

        assertEquals(distance, 111194.9, 0.1);

    }

    @Test
    public void getBearing() {

        double bearing = converter.getBearing(10,10,20,20);
        assertEquals(bearing , 42.8, 0.1);

    }

    @Test
    public void getMidPoint() {

        double midpoint[]  = converter.getMidPoint(10, 10, 20, 20);
        assertEquals(midpoint[0], 15, 0.1);
        assertEquals(midpoint[1], 14.8, 0.1);


    }

    @Test
    public void newPoint() {

        double point[] = converter.newPoint(10, 10, 45, 1000000);

        assertEquals(point[0], 16.2, 0.1);
        assertEquals(point[1], 16.6, 0.1);

    }
}