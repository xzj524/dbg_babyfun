package com.aizi.yingerbao.deviceinterface;


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

    DeviceResponse<?> getRealTimeData();

    DeviceResponse<?> getAllNoSyncInfo();

    DeviceResponse<?> getAllSyncInfo();

    DeviceResponse<?> getBreahStopInfo();

    DeviceResponse<?> getRealTimeTempData();

    DeviceResponse<?> getExceptionEvent();

    DeviceResponse<?> checkDeviceValid();

    DeviceResponse<?> activateDevice();

    DeviceResponse<?> getAllNoSyncInfo(int datatype);


}
