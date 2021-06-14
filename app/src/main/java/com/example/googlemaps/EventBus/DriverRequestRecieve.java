package com.example.googlemaps.EventBus;

public class DriverRequestRecieve {
    private String key;
    private String pickuplocation;

    public DriverRequestRecieve(String key, String pickuplocation) {
        this.key = key;
        this.pickuplocation = pickuplocation;
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
