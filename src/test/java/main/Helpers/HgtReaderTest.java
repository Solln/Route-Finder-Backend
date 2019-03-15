package main.Helpers;

import org.junit.Test;

import static org.junit.Assert.*;

public class HgtReaderTest {

    @Test
    public void getElevation() {

        HgtReader reader = new HgtReader();

        double elevation = reader.getElevation(56.5, -3.5);
        assertEquals(elevation, 96, 0.1);

        elevation = reader.getElevation(57.5, -4.5);
        assertEquals(elevation, 165, 0.1);

    }
}