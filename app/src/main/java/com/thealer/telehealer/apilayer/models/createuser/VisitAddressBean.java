package com.thealer.telehealer.apilayer.models.createuser;

import java.io.Serializable;

/**
 * Created by Aswin on 25,October,2018
 */
public class VisitAddressBean implements Serializable {

    private String street;
    private String street2;
    private String city;
    private double lon;
    private String zip;
    private double lat;
    private String state_long;
    private String state;

    public VisitAddressBean() {
    }

    public VisitAddressBean(String street, String street2, String city, double lon, String zip, double lat, String state_long, String state) {
        this.street = street;
        this.street2 = street2;
        this.city = city;
        this.lon = lon;
        this.zip = zip;
        this.lat = lat;
        this.state_long = state_long;
        this.state = state;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreet2() {
        return street2;
    }

    public void setStreet2(String street2) {
        this.street2 = street2;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getState_long() {
        return state_long;
    }

    public void setState_long(String state_long) {
        this.state_long = state_long;
    }
}
