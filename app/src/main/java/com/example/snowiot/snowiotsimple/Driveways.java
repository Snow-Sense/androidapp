package com.example.snowiot.snowiotsimple;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Felipe on 3/9/2017.
 */

public class Driveways {
    private String name;
    private double latitude;
    private double longitude;
    private boolean serviceRequest;
    private String type;
    public Address address;
    public int status;                   //0 = unavailable, 1 = available, 2 = already being serviced


    public Driveways() {
        name = "null";
        latitude = 1.0;
        longitude = 1.0;
        serviceRequest = false;
        type = "null";
        status = 0;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String drivewayName)
    {
        name = drivewayName;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public boolean getServiceRequest()
    {
        return serviceRequest;
    }

    public String getType() {return type; }

    public int getStatus() {return status; }

public Map<String, Object> toMap()
{
    HashMap<String, Object> userMapInfo = new HashMap<>();
    userMapInfo.put("name", name);
    userMapInfo.put("latitude", latitude);
    userMapInfo.put("longitude", longitude);
    userMapInfo.put("serviceRequest", serviceRequest);
    userMapInfo.put("type", type);
    userMapInfo.put("address", address);
    userMapInfo.put("status", status);

    return userMapInfo;
}

}

