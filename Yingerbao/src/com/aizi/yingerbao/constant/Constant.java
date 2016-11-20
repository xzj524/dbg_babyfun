package com.aizi.yingerbao.constant;

import java.util.UUID;

public class Constant {
    /** 日志前缀配置 **/
    public static final String LOG_PREFIX = "AIZI-";
    
    public static final UUID BLE_UUID_NUS_SERVICE = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb");
    public static final UUID BLE_UUID_NUS_TX_CHARACTERISTIC = UUID.fromString("00000002-0000-1000-8000-00805f9b34fb");
    public static final UUID BLE_UUID_NUS_RX_CHARACTERISTIC = UUID.fromString("00000003-0000-1000-8000-00805f9b34fb");
  
    public static final String BASE_L2_MESSAGE = "bsl2Msg";
    
    public static final String EXTERNAL_FILE_ROOT = "com.aizi.yingerbao";
    public static final String EXTERNAL_FILE_LOC = EXTERNAL_FILE_ROOT + "/files";
    public static final String EXTERNAL_FILE_DATA = EXTERNAL_FILE_ROOT + "/data";
    
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
    
    /** 爱子科技设备标识 */
    public static final String AIZI_DEVICE_TAG = "my_hrm";
    
    /** 存储app私有的shared preference */
    public static final String SHARED_NAME_PRIVATE_SETTINGS = "aizi_pst";
    
    
    /** 存储私设备信息的shared preference */
    public static final String AIZI_DEVICE_PRIVATE_SETTINGS = "aizi_device_pst";
    
    
    /** 存储DeviceAddress  */
    public static final String AIZI_DEVICE_ADDRESS = "aizi_dev_address";
    
    /** 存储手机蓝牙Address  */
    public static final String AIZI_PHONE_ADDRESS = "aizi_phone_address";
    
    /** 设备时间与手机端偏差限制  */
    public static final long DEVICE_TIME_OFFSET = 60 * 60 * 1000;
    
    
    /** 用户登录是否成功  */
    public static final String LOGIN_VALUE = "login_value";
    
    /** 蓝牙连接是否准备好  */
    public static final String BLUETOOTH_IS_READY = "bluetooth_is_ready";
    
    /** 数据传输完成  */
    public static final String DATA_TRANSFER_COMPLETED = "com.data.transfer.completed";
    
    /** 数据传输接收  */
    public static final String DATA_TRANSFER_RECEIVE = "com.data.transfer.receive";
    
    /** 数据传输发送  */
    public static final String DATA_TRANSFER_SEND = "com.data.transfer.send";
    
    /** 数据传输单实例  */
    public static final String ACITON_DATA_TRANSFER = "com.aizi.yingerbao.datatransfer";
    
    /** 数据日期 年  */
    public static final String DATA_DATE_YEAR = "calendar_year";
    /** 数据日期 月  */
    public static final String DATA_DATE_MONTH = "calendar_month";
    /** 数据日期 日  */
    public static final String DATA_DATE_DAY = "calendar_day";
    
    /** 温度实时数据  */
    public static final String DATA_REALTIME_TEMPERATURE = "data_realtime_temperature";
    
    /** 蓝牙扫描成功  */
    public static final String BLUETOOTH_SCAN_FOUND = "bluetooth_scan_found";
    
    /** 蓝牙扫描失败  */
    public static final String BLUETOOTH_SCAN_NOT_FOUND = "bluetooth_scan_not_found";
    
    /** 保存用户账号  */
    public static final String AIZI_USER_ACCOUNT = "aizi_user_account";
    
    /** 数据传输标识  */
    public static final String AIZI_SEND_DATA = "aizi_send_data";

    public static final String REQUEST_KEY_SEND_CODE = "request_key_send_code";

    public static final String REQUEST_KEY_RESULT_CODE = "request_key_result_code";
    
    
    /** 数据传输类型  */
    
    public static final String DATA_TRANSFER_TYPE = "com.aizi.yingerbao.datatransfer.TYPE";
    
    public static final int TRANSFER_TYPE_SUCCEED = 1;

    public static final int TRANSFER_TYPE_NOT_COMPLETED = 2;
    
    public static final int TRANSFER_TYPE_ERROR = 3;
    
}
