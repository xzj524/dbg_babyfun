package com.aizi.yingerbao.database;

import cn.bmob.v3.BmobObject;

public class TemperatureInfoEnumClass extends BmobObject{
    public long mTemperatureTimestamp = 0;
    public String mTemperatureValue;
    public int mTemperatureYear = 0;
    public int mTemperatureMonth = 0;
    public int mTemperatureDay = 0;
    public int mTemperatureMinute = 0;
    
    public long getTemperatureTimestamp() {
        return mTemperatureTimestamp;
    }

    public void setTemperatureTimestamp(long temperaturetimestamp) {
        this.mTemperatureTimestamp = temperaturetimestamp;
    }
    
    public String getTemperatureValue() {
        return mTemperatureValue;
    }

    public void setTemperatureValue(String temperaturevalue) {
        this.mTemperatureValue = temperaturevalue;
    }
    
    public int getTemperatureYear() {
        return mTemperatureYear;
    }

    public void setTemperatureYear(int temperatureyear) {
        this.mTemperatureYear = temperatureyear;
    }
    
    public int getTemperatureMonth() {
        return mTemperatureMonth;
    }

    public void setTemperatureMonth(int temperaturemonth) {
        this.mTemperatureMonth = temperaturemonth;
    }
    
    public int getTemperatureDay() {
        return mTemperatureDay;
    }

    public void setTemperatureDay(int temperatureday) {
        this.mTemperatureDay = temperatureday;
    }
    
    
    public int getTemperatureMinute() {
        return mTemperatureMinute;
    }

    public void setTemperatureMinute(int temperatureminute) {
        this.mTemperatureMinute = temperatureminute;
    }
}
