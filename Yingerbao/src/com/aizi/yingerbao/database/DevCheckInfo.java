package com.aizi.yingerbao.database;

public class DevCheckInfo {
    public int mNoSyncSleepDataLength = 0; // 睡眠
    public int mNoSyncTempDataLength = 0;  // 温度
    public int mNoSyncBreathDataLength = 0; // 呼吸
    public int mNoSyncExceptionLength = 0; //  异常事件
    public int mDeviceCharge = 0; // 电量
    public byte mDeviceStatus;
    
    public int mCheckInfoYear = 0;
    public int mCheckInfoMonth = 0;
    public int mCheckInfoDay = 0;
    public int mCheckInfoHour = 0;
    public int mCheckInfoMinute = 0;
    public int mCheckInfoSecond = 0;

}
