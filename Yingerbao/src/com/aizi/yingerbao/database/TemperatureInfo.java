package com.aizi.yingerbao.database;


public class TemperatureInfo extends BaseUserInfo {
    public long mTmTimestamp = 0;
    public String mTmValue;
    public int mTmYear = 0;
    public int mTmMonth = 0;
    public int mTmDay = 0;
    public int mTmMinute = 0;
    
    
    public long getTmTimestamp() {
        return mTmTimestamp;
    }

    public void setTmTimestamp(long temperaturetimestamp) {
        this.mTmTimestamp = temperaturetimestamp;
    }
    
    public String getTmValue() {
        return mTmValue;
    }

    public void setTmValue(String temperaturevalue) {
        this.mTmValue = temperaturevalue;
    }
    
    public int getTmYear() {
        return mTmYear;
    }

   /* public void setTemperatureYear(int temperatureyear) {
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
    }*/
}
