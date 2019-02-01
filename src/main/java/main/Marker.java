package main;

public class Marker {

    private long order;
    private double lat, lng, elevation;


    public Marker(long id, double lat, double lng, double ele) {
        this.order = id;
        this.lat = lat;
        this.lng = lng;
        this.elevation = ele;
    }

    public long getId() {
        return order;
    }

    public double getlat() {
        return lat;
    }

    public double getlng() {
        return lng;
    }

    public double getElevation() {
        return elevation;
    }


}
