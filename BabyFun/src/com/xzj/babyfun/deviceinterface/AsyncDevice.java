package com.xzj.babyfun.deviceinterface;

import java.util.concurrent.Future;

import com.xzj.babyfun.synctime.DeviceTime;

public interface AsyncDevice {
    
    /**
     * @Description: 设置设备时间
     * @param dvtime
     * @param listener
     * @return
     */
    Future<?> setDeviceTime(final DeviceTime dvtime,  final DeviceTimeListener listener);

    
    /**
     * @Description: 启动设备发送呼吸数据
     * @param dvtime
     * @param listener
     * @return
     */
    Future<?> startSendBreathData();

}
