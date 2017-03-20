package com.example.snowiot.snowiotsimple;

/**
 * Created by Felipe on 3/10/2017.
 */

import java.util.HashMap;
import java.util.Map;

public class Address {

    private String street;
    private String city;
    private String state;
    private String country;


    public Address() {
        street = "null";
        city = "null";
        state = "null";
        country = "null";
    }

    public String getStreet()
    {
        return street;
    }

    public String getCountry()
    {
        return country;
    }

    public String getCity()
    {
        return city;
    }

    public String getState()
    {
        return state;
    }

    public Map<String, Object> toMap()
    {
        HashMap<String, Object> userMapInfo = new HashMap<>();
        userMapInfo.put("city", city);
        userMapInfo.put("state", state);
        userMapInfo.put("country", country);
        userMapInfo.put("street", street);

        return userMapInfo;
    }

}
