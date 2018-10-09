package com.example.surinderkahlon.mapdemo;

public class Flag {
    Double latitude;
    Double longitude;
    String flagFound;
    String flagFoundStatus;

    public Flag(){

    }
    public Flag(Double latitude,Double longitude,String flagFound,String flagFoundStatus){
this.latitude=latitude;
this.longitude=longitude;
this.flagFound=flagFound;
this.flagFoundStatus=flagFoundStatus;
    }
    public Double getLat() {
        return this.latitude;
    }
    public Double getLong() {
        return this.longitude;
    }
}
