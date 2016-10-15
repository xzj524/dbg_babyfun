package com.aizi.xiaohuhu.synctime;

import android.content.Context;

import com.aizi.xiaohuhu.deviceinterface.AsyncDeviceFactory;
import com.aizi.xiaohuhu.deviceinterface.DeviceError;
import com.aizi.xiaohuhu.deviceinterface.DeviceTimeListener;

public class DeviceTimeHelper {
    
    static DeviceTimeListener mListener; 
    
    public static void getDeviceTime() {
        
    }
    
    public static void setDeviceTime(Context context, DeviceTime dvtime) {
        
        AsyncDeviceFactory.getInstance(context).setDeviceTime();
    }
    
    class devTimeListener implements DeviceTimeListener {

        @Override
        public void onSetDeviceTime(boolean result) {
            // TODO Auto-generated method stub
            
        }

        @Override
        public void onError(DeviceError error) {
            // TODO Auto-generated method stub
            
        }
        
    }
}
