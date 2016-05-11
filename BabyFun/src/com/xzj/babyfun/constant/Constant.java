package com.xzj.babyfun.constant;

import java.util.UUID;

public class Constant {
    /** 日志前缀配置 **/
    public static final String LOG_PREFIX = "AIZI-";
    
    public static final UUID BLE_UUID_NUS_SERVICE = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb");
    public static final UUID BLE_UUID_NUS_TX_CHARACTERISTIC = UUID.fromString("00000002-0000-1000-8000-00805f9b34fb");
    public static final UUID BLE_UUID_NUS_RX_CHARACTERISTIC = UUID.fromString("00000003-0000-1000-8000-00805f9b34fb");
  
}
