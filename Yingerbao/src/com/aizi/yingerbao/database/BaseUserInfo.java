package com.aizi.yingerbao.database;

import cn.bmob.v3.BmobObject;


public class BaseUserInfo extends BmobObject{
    
    public String mPhoneNumeber;
    public String mPhoneImei;
    public String mDeviceMac;
    public String mDeviceType;
    
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
}
