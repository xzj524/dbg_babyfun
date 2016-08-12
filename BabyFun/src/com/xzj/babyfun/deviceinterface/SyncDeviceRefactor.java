package com.xzj.babyfun.deviceinterface;

import android.content.Context;

import com.xzj.babyfun.synctime.DeviceTime;

public class SyncDeviceRefactor implements SyncDevice{

    SyncDeviceImpl mSyncDeviceImpl;
    public SyncDeviceRefactor(Context context) {
        // TODO Auto-generated constructor stub
        mSyncDeviceImpl = new SyncDeviceImpl(context);
    }

    @Override
    public DeviceResponse<?> setDeviceTime() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DeviceResponse<?> startSendBreathData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DeviceResponse<?> stopSendBreathData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DeviceResponse<?> getDeviceTime() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DeviceResponse<?> getBodyTemperature() {
        // TODO Auto-generated method stub
        return null;
    }

}
