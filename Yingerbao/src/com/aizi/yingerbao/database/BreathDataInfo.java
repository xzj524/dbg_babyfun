package com.aizi.yingerbao.database;

import com.aizi.yingerbao.utility.Utiliy;

import android.content.Context;
import cn.bmob.v3.BmobObject;


public class BreathDataInfo extends BmobObject {
  
    protected long mBreathTimestamp = 0;
    protected int mBreathIsAlarm = 0;
    protected int mBreathDuration = 0;
    protected int mBreathYear = 0;
    protected int mBreathMonth = 0;
    protected int mBreathDay = 0;
    protected int mBreathHour = 0;
    protected int mBreathMinute = 0;
    protected int mBreathSecond = 0;
    
    protected String mPhoneNumeber;
    protected String mPhoneImei;
    protected String mDeviceMac;
    protected String mDeviceType;
    

    
    public BreathDataInfo(Context context) {
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

    public long getBreathTimestamp() {
        return mBreathTimestamp;
    }

    public void setBreathTimestamp(long breathtimestamp) {
        this.mBreathTimestamp = breathtimestamp;
    }
    
    public int getBreathIsAlarm() {
        return mBreathIsAlarm;
    }

    public void setBreathIsAlarm(int breathisalarm) {
        this.mBreathIsAlarm = breathisalarm;
    }

    public int getBreathDuration () {
        return mBreathDuration;
    }
    
    public void setBreathDuration (int breathduration) {
        this.mBreathDuration = breathduration;
    }
    
    public int getBreathYear () {
        return mBreathYear;
    }
    
    public void setBreathYear (int breathyear) {
        this.mBreathYear = breathyear;
    }
    
    
    public int getBreathMonth () {
        return mBreathMonth;
    }
    
    public void setBreathMonth (int breathmonth) {
        this.mBreathMonth = breathmonth;
    }
    
    public int getBreathDay () {
        return mBreathDay;
    }
    
    public void setBreathDay (int breathday) {
        this.mBreathDay = breathday;
    }
    
    public int getBreathHour () {
        return mBreathHour;
    }
    
    public void setBreathHour (int breathhour) {
        this.mBreathHour = breathhour;
    }
    
    public int getBreathMinute () {
        return mBreathMinute;
    }
    
    public void setBreathMinute (int breathminute) {
        this.mBreathMinute = breathminute;
    }
    
    public int getBreathSecond () {
        return mBreathSecond;
    }
    
    public void setBreathSecond (int breathsecond) {
        this.mBreathSecond = breathsecond;
    }

}
