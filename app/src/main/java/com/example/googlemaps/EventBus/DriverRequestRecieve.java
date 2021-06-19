package com.example.googlemaps.EventBus;

public class DriverRequestRecieve {
    private String key;
    private String pickuplocation;
    private String cartype;

    public DriverRequestRecieve(String key, String pickuplocation , String cartype) {
        this.key = key;
        this.pickuplocation = pickuplocation;
        this.cartype = cartype;
    }

    public String getCartype() {
        return cartype;
    }

    public void setCartype(String cartype) {
        this.cartype = cartype;
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
}
