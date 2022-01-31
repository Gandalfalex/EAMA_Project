package com.example.meetforsport.ui.EventCreator.DataHolder;

public class LocationHolder extends DataHolder{

    private String l_address = "";
    private float longitude = 0;
    private float latitude = 0;


    public LocationHolder(int id, int longitude, int latitude, String name, String l_address){
        super(id, name);
        this.latitude = latitude;
        this.longitude = longitude;
        this.l_address = l_address;
    }

    public LocationHolder(int id, String name){
        super(id, name);
    }

    public String getL_address() {
        return l_address;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setL_address(String l_address) {
        this.l_address = l_address;
    }

    public void setLongitude(String longitude) {
        this.longitude =  Float.valueOf(longitude);
    }

    public void setLatitude(String latitude) {
        this.latitude = Float.valueOf(latitude);
    }

    public float getLatitude() {
        return latitude;
    }

}
