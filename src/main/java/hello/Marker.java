package hello;

public class Marker {

    private long order;
    private double lat, lng;


    public Marker(long id, double lat, double lng) {
        this.order = id;
        this.lat = lat;
        this.lng = lng;
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

}
