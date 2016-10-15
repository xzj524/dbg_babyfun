package com.aizi.yingerbao.synctime;

import android.content.Context;

import com.aizi.yingerbao.deviceinterface.AsyncDeviceFactory;
import com.aizi.yingerbao.deviceinterface.DeviceError;
import com.aizi.yingerbao.deviceinterface.DeviceTimeListener;

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
