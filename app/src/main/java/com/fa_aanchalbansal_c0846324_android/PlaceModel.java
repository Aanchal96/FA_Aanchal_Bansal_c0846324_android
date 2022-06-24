package com.fa_aanchalbansal_c0846324_android;

import java.io.Serializable;

public class PlaceModel implements Serializable {

    private int id, visited;
    private String title, address;
    private double lat, lng;

    public PlaceModel(int id, String title, String address, double lat , double lng , int visited) {
        this.id = id;
        this.title = title;
        this.address = address;
        this.lat = lat;
        this.lng = lng;
        this.visited = visited;
    }

    public int getPlaceId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAddress() {
        return address;
    }

    public double getLat() {
        return lat;
    }

    public double getLong() {
        return lng;
    }

    public double getVisited() {
        return visited;
    }

}
