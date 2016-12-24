package com.aizi.yingerbao.database;


public class TemperatureInfoEnumClass extends BaseUserInfo{
    
    public long mTmTimestamp = 0;
    public String mTmValue;
    public int mTmYear = 0;
    public int mTmMonth = 0;
    public int mTmDay = 0;
    public int mTmMinute = 0;
    
    public long getTemperatureTimestamp() {
        return mTmTimestamp;
    }

    public void setTemperatureTimestamp(long temperaturetimestamp) {
        this.mTmTimestamp = temperaturetimestamp;
    }
    
    public String getTemperatureValue() {
        return mTmValue;
    }

    public void setTemperatureValue(String temperaturevalue) {
        this.mTmValue = temperaturevalue;
    }
    
    public int getTemperatureYear() {
        return mTmYear;
    }

    public void setTemperatureYear(int temperatureyear) {
        this.mTmYear = temperatureyear;
    }
    
    public int getTemperatureMonth() {
        return mTmMonth;
    }

    public void setTemperatureMonth(int temperaturemonth) {
        this.mTmMonth = temperaturemonth;
    }
    
    public int getTemperatureDay() {
        return mTmDay;
    }

    public void setTemperatureDay(int temperatureday) {
        this.mTmDay = temperatureday;
    }
    
    
    public int getTemperatureMinute() {
        return mTmMinute;
    }

    public void setTemperatureMinute(int temperatureminute) {
        this.mTmMinute = temperatureminute;
    }
}
