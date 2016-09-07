package com.aizi.xiaohuhu.constant;

import java.util.UUID;

public class Constant {
    /** 日志前缀配置 **/
    public static final String LOG_PREFIX = "AIZI-";
    
    public static final UUID BLE_UUID_NUS_SERVICE = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb");
    public static final UUID BLE_UUID_NUS_TX_CHARACTERISTIC = UUID.fromString("00000002-0000-1000-8000-00805f9b34fb");
    public static final UUID BLE_UUID_NUS_RX_CHARACTERISTIC = UUID.fromString("00000003-0000-1000-8000-00805f9b34fb");
  
    public static final String BASE_L2_MESSAGE = "bsl2Msg";
    
    public static final short COMMAND_ID_UPDATE_ROM = 1;
    public static final short COMMAND_ID_SETTING = 2;
    public static final short COMMAND_ID_BIND = 3;
    public static final short COMMAND_ID_NOTIFY = 4;
    public static final short COMMAND_ID_DATA = 5;
    public static final short COMMAND_ID_MANUFACTURE_TEST = 6;
    public static final short COMMAND_ID_CONTROL = 7;
    public static final short COMMAND_ID_DUMP_STACK = 8;
    public static final short COMMAND_ID_FLASH_TEST = 9;
    
    public static final short BASE_VERSION_CODE = 1;
    
    /** 存储app私有的shared preference */
    public static final String SHARED_NAME_PRIVATE_SETTINGS = "aizi_pst";
    
    /** 存储DeviceAddress  */
    public static final String SHARED_DEVICE_ADDRESS = "dev_address";
    
    /** 设备时间与手机端偏差限制  */
    public static final long DEVICE_TIME_OFFSET = 60 * 60 * 1000;
    
    
    /** 用户登录是否成功  */
    public static final String LOGIN_VALUE = "login_value";
    
    /** 蓝牙连接是否准备好  */
    public static final String BLUETOOTH_IS_READY = "bluetooth_is_ready";
}
