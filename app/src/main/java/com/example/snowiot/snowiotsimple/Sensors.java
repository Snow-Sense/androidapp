package com.example.snowiot.snowiotsimple;

/**
 * Created by Felipe on 12/12/2016.
 */

public class Sensors {
    private String name;
    private String state;
    private String uid;
    private float Height;
    private float Sensor1;
    private float Sensor2;
    private float SnowAccum;
    private float Temperature;
    private long timestamp;

    public Sensors() {
        name = "empty";
        state = "empty";
        uid = "empty";
        Height = 0;
        Sensor1 = 0;
        Sensor2 = 0;
        SnowAccum = 0;
        Temperature = 0;
        timestamp = 0;
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

    public float getHeight() {
        return Height;
    }

    public float getSensor1() {
        return Sensor1;
    }

    public float getSensor2() {
        return Sensor2;
    }

    public float getSnowAccum() {
        return SnowAccum;
    }

    public float getTemperature() {
        return Temperature;
    }

    public long getTimestamp() {
        return timestamp;
    }

}
