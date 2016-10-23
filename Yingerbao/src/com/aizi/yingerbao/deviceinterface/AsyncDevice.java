package com.aizi.yingerbao.deviceinterface;

import java.util.concurrent.Future;

import com.aizi.yingerbao.synctime.DeviceTime;

public interface AsyncDevice {
    
    /***********设置接口*************/
    /**
     * @Description: 设置设备时间
     * @param dvtime
     * @param listener
     * @return
     */
    Future<?> setDeviceTime();

    /***********获取内容接口*************/
    /**
     * @Description: 获取设备时间
     * @param dvtime
     * @param listener
     * @return
     */
    Future<?> getDeviceTime();
    
    
    /**
     * @Description: 获取实时数据
     * @param dvtime
     * @param listener
     * @return
     */
    Future<?> getRealTimeData();
    
    /**
     * @Description: 获取实时温度数据（温度计模式）
     * @param dvtime
     * @param listener
     * @return
     */
    Future<?> getRealTimeTempData();
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
    
    /**
     * @Description: 获取异常事件内容
     * @param dvtime
     * @param listener
     * @return
     */
    Future<?> getExceptionEvent();
    
    
    
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
