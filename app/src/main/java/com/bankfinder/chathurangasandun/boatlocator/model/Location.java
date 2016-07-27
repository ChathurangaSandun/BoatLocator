package com.bankfinder.chathurangasandun.boatlocator.model;

/**
 * Created by Chathuranga Sandun on 7/27/2016.
 */
public class Location {
    private int locationID;
    private String date;
    private String time;
    private double  lat;
    private double lng;
    private int batryStatus;


    public Location(int locationID, String date, String time, double lat, double lng, int batryStatus) {
        this.locationID = locationID;
        this.date = date;
        this.time = time;
        this.lat = lat;
        this.lng = lng;
        this.batryStatus = batryStatus;
    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public int getBatryStatus() {
        return batryStatus;
    }

    public void setBatryStatus(int batryStatus) {
        this.batryStatus = batryStatus;
    }
}
