package com.example.meetforsport.ui.EventCreator.DataHolder;

public class LocationHolder extends DataHolder{

    private String l_address = "";
    private int longitute = 0;
    private int latitute = 0;


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

    public int getLongitute() {
        return longitute;
    }

    public void setL_address(String l_address) {
        this.l_address = l_address;
    }

    public void setLongitute(int longitute) {
        this.longitute = longitute;
    }

    public void setLatitute(int latitute) {
        this.latitute = latitute;
    }

    public int getLatitute() {
        return latitute;
    }

}
