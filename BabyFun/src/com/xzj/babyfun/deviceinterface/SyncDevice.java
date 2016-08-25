package com.xzj.babyfun.deviceinterface;



public interface SyncDevice {

    
    /**
     * @Description: 设置设备时间
     * @return
     */
    DeviceResponse<?> setDeviceTime();
    
    /**
     * @Description: 获取设备时间
     * @return
     */
    DeviceResponse<?> getDeviceTime();

    DeviceResponse<?> startSendBreathData();
    
    DeviceResponse<?> stopSendBreathData();

    DeviceResponse<?> getBodyTemperature();

    DeviceResponse<?> getAllNoSyncInfo();


}
