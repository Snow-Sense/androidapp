package com.example.snowiot.snowiotsimple;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Felipe on 3/31/2017.
 */

public class MarkerInfo {

    private String name;

    public MarkerInfo() {
        super();
        this.name = "";
    }

    public String getName()
    {
        return name;
    }

    public void setName(String drivewayName)
    {
        name = drivewayName;
    }

    public Map<String, Object> toMap()
    {
        HashMap<String, Object> userMapInfo = new HashMap<>();
        userMapInfo.put("name", name);
        return userMapInfo;
    }

}

