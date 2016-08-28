package com.aizi.xiaohuhu.deviceinterface;


public interface DeviceTimeListener extends DeviceListener{

    void onSetDeviceTime(final boolean result);
    void onError(DeviceError error);
}
