package com.xzj.babyfun.deviceinterface;

import com.xzj.babyfun.synctime.DeviceTime;


public interface SyncDevice {

    
    /**
     * @Description: 设置设备时间
     * @param dvtime
     * @return
     */
    DeviceResponse<?> setDeviceTime(DeviceTime dvtime);

}
