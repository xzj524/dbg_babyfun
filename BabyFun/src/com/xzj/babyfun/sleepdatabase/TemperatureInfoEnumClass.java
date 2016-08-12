package com.xzj.babyfun.sleepdatabase;

public class TemperatureInfoEnumClass {
    public long mTemperatureTimestamp = 0;
    public int mTemperatureValue = 0;
    
    public long getTemperatureTimestamp() {
        return mTemperatureTimestamp;
    }

    public void setTemperatureTimestamp(long temperaturetimestamp) {
        this.mTemperatureTimestamp = temperaturetimestamp;
    }
    
    public int getTemperatureValue() {
        return mTemperatureValue;
    }

    public void setTemperatureValue(int temperaturevalue) {
        this.mTemperatureValue = temperaturevalue;
    }
}
