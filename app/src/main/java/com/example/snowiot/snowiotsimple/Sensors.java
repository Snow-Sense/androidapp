package com.example.snowiot.snowiotsimple;

/**
 * Created by Felipe on 12/12/2016.
 */

public class Sensors {
    private String name;
    private String state;
    private String uid;

    public Sensors() {
    }

    public Sensors(String name, String uid, String state) {
        this.name = name;
        this.state = state;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public String getUid() {
        return uid;
    }

    public String getState() {
        return state;
    }
}
