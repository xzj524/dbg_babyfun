package com.xzj.babyfun.deviceinterface;


public interface DeviceTimeListener extends DeviceListener{

    void onSetDeviceTime(final boolean result);
    void onError(DeviceError error);
}
