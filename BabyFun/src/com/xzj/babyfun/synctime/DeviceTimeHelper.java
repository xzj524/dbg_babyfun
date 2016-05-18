package com.xzj.babyfun.synctime;

import android.content.Context;

import com.xzj.babyfun.deviceinterface.AsyncDeviceFactory;
import com.xzj.babyfun.deviceinterface.DeviceError;
import com.xzj.babyfun.deviceinterface.DeviceTimeListener;

public class DeviceTimeHelper {
    
    static DeviceTimeListener mListener; 
    
    public static void getDeviceTime() {
        
    }
    
    public static void setDeviceTime(Context context, DeviceTime dvtime) {
        
        AsyncDeviceFactory.getInstance(context).setDeviceTime(dvtime, mListener);
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
