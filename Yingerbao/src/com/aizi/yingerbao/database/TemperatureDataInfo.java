package com.aizi.yingerbao.database;

import com.aizi.yingerbao.utility.Utiliy;

import android.content.Context;
import cn.bmob.v3.BmobObject;


public class TemperatureDataInfo extends BmobObject{
   
    protected long mTmTimestamp = 0;
    protected int mTmYear = 0;
    protected int mTmMonth = 0;
    protected int mTmDay = 0;
    protected int mTmMinute = 0;
    
    protected String mTmValue;
    protected String mPhoneNumeber;
    protected String mPhoneImei;
    protected String mDeviceMac;
    protected String mDeviceType;


    
    public TemperatureDataInfo(Context context) {
        mPhoneNumeber = Utiliy.getPhoneNumber(context);
        mPhoneImei = Utiliy.getPhoneImei(context);
        mDeviceMac = Utiliy.getDeviceId(context);
        mDeviceType = "android";
    }
    
    
    public void setPhoneNum(String phone){
        this.mPhoneNumeber = phone;
    }
    
    public String getPhoneNum() {
        return mPhoneNumeber;
    }

    public void setPhoneImei(String imei){
        this.mPhoneImei = imei;
    }
    
    public String getPhoneImei() {
        return mPhoneImei;
    }
    
    public void setDeviceMac(String mac){
        this.mDeviceMac = mac;
    }
    
    public String getDeviceMac() {
        return mDeviceMac;
    }
    
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
