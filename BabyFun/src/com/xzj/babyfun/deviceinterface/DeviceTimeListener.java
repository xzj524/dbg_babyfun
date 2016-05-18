package com.xzj.babyfun.deviceinterface;


public interface DeviceTimeListener {

    void onSetDeviceTime(final boolean result);
    void onError(DeviceError error);
}
