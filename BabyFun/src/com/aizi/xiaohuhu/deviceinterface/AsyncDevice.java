package com.aizi.xiaohuhu.deviceinterface;

import java.util.concurrent.Future;

import com.aizi.xiaohuhu.synctime.DeviceTime;

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
    
    /**
     * @Description: 获取全部未同步数据
     * @param dvtime
     * @param listener
     * @return
     */
    Future<?> getAllNoSyncInfo();
    
    /**
     * @Description: 获取全部数据
     * @param dvtime
     * @param listener
     * @return
     */
    Future<?> getAllSyncInfo();
    
    /**
     * @Description: 获取呼吸停滞数据
     * @param dvtime
     * @param listener
     * @return
     */
    Future<?> getBreathStopInfo();
    
    
    
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
