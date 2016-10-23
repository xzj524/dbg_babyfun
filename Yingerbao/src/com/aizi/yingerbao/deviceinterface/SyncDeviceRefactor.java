package com.aizi.yingerbao.deviceinterface;

import android.content.Context;

import com.aizi.yingerbao.synctime.DeviceTime;

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
    public DeviceResponse<?> getRealTimeData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DeviceResponse<?> getAllNoSyncInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DeviceResponse<?> getAllSyncInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DeviceResponse<?> getBreahStopInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DeviceResponse<?> getRealTimeTempData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DeviceResponse<?> getExceptionEvent() {
        // TODO Auto-generated method stub
        return null;
    }

}
