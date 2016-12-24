package com.aizi.yingerbao.database;

import android.content.Context;
import cn.bmob.v3.BmobObject;

import com.aizi.yingerbao.utility.Utiliy;


public class BaseUserInfo extends BmobObject{
    
    protected Context mContext;
    protected String mPhoneNumeber;
    protected String mPhoneImei;
    protected String mDeviceMac;
    protected String mDeviceType;
    
    public BaseUserInfo(Context context){
        mContext = context;
        mPhoneImei = Utiliy.getPhoneImei(mContext);
        mPhoneNumeber = Utiliy.getPhoneNumber(mContext);
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
}
