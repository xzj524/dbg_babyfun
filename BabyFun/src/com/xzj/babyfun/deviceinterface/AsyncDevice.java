package com.xzj.babyfun.deviceinterface;

import java.util.concurrent.Future;

import com.xzj.babyfun.synctime.DeviceTime;

public interface AsyncDevice {
    
    /***********设置接口*************/
    /**
     * @Description: 设置设备时间
     * @param dvtime
     * @param listener
     * @return
     */
    Future<?> setDeviceTime();

    /**
     * @Description: 获取设备时间
     * @param dvtime
     * @param listener
     * @return
     */
    Future<?> getDeviceTime();
    
    
    /**
     * @Description: 获取温度
     * @param dvtime
     * @param listener
     * @return
     */
    Future<?> getBodyTemperature();
    
    /**
     * @Description: 获取睡眠数据
     * @param dvtime
     * @param listener
     * @return
     */
    Future<?> getSleepInfo();
    
    
    
    /***********控制接口*************/
    /**
     * @Description: 启动设备发送呼吸数据
     * @param dvtime
     * @param listener
     * @return
     */
    Future<?> startSendBreathData();

    /**
     * @Description: 停止设备发送呼吸数据
     * @param dvtime
     * @param listener
     * @return
     */
    Future<?> stopSendBreathData();

}
