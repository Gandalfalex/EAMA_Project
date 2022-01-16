package com.example.meetforsport.ui.EventCreator.DataHolder;

public class LocationHolder extends DataHolder{

    private String l_address = "";
    private float longitute = 0;
    private float latitute = 0;


    public LocationHolder(int id, int longitute, int latitute, String name, String l_address){
        super(id, name);
        this.latitute = latitute;
        this.longitute = longitute;

        this.l_address = l_address;
    }

    public LocationHolder(int id, String name){
        super(id, name);
    }

    public String getL_address() {
        return l_address;
    }

    public float getLongitute() {
        return longitute;
    }

    public void setL_address(String l_address) {
        this.l_address = l_address;
    }

    public void setLongitute(String longitute) {
        this.longitute =  Float.valueOf(longitute);
    }

    public void setLatitute(String latitute) {
        this.latitute = Float.valueOf(latitute);
    }

    public float getLatitute() {
        return latitute;
    }

}
