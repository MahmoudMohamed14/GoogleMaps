package com.example.googlemaps.EventBus;

public class DriverRequestRecieve {
    private String key;
    private String pickuplocation,pickuplocationstring;
    private String destinationlocation,destinationlocationstring;
    private String cartype;

    public DriverRequestRecieve() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getPickuplocation() {
        return pickuplocation;
    }

    public void setPickuplocation(String pickuplocation) {
        this.pickuplocation = pickuplocation;
    }

    public String getPickuplocationstring() {
        return pickuplocationstring;
    }

    public void setPickuplocationstring(String pickuplocationstring) {
        this.pickuplocationstring = pickuplocationstring;
    }

    public String getDestinationlocation() {
        return destinationlocation;
    }

    public void setDestinationlocation(String destinationlocation) {
        this.destinationlocation = destinationlocation;
    }

    public String getDestinationlocationstring() {
        return destinationlocationstring;
    }

    public void setDestinationlocationstring(String destinationlocationstring) {
        this.destinationlocationstring = destinationlocationstring;
    }

    public String getCartype() {
        return cartype;
    }

    public void setCartype(String cartype) {
        this.cartype = cartype;
    }
}
