package com.example.snowiot.snowiotsimple;

/**
 * Created by Felipe on 4/21/2017.
 */

public class SnowBuildupNotific {

    private int enableWarning;
    private int snowThresholdFlag;
    private int snowThreshold;

    public SnowBuildupNotific() {
        enableWarning = 0;
        snowThresholdFlag = 0;
        snowThreshold = 0;
    }

    public void setEnableWarning(int enableWarning) {
        this.enableWarning = enableWarning;
    }

    public void setSnowThresholdFlag(int snowThresholdFlag) {
        this.snowThresholdFlag = snowThresholdFlag;
    }

    public void setSnowThreshold(int snowThreshold) {
        this.snowThreshold = snowThreshold;
    }

    public int getEnableWarning() {
        return enableWarning;
    }

    public int getSnowThresholdFlag() {
        return snowThresholdFlag;
    }

    public int getSnowThreshold() {
        return snowThreshold;
    }
}
