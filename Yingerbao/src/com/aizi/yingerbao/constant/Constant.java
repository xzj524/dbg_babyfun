package com.aizi.yingerbao.constant;

import java.util.UUID;

import android.content.Intent;

public class Constant {
    /** 日志前缀配置 **/
    public static final String LOG_PREFIX = "AIZI-";
    
    /** 关于版本号前缀 **/
    public static final String ABOUT_PREFIX = "Android 版本-";
    
    /** 报警时振动频率 **/
    public static final long[] EMERGENCY_PATTERN = {500, 1000}; 
    
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
    
    //扫描范围
    public static int SCAN_RANG = -90; 
    
    //退出标识
    public static int AIZI_USERACTIVTY_QUIT = 0;
    
    /** 爱子科技设备标识测试版 */
    public static final String AIZI_DEVICE_TEST_TAG = "my_hrm";
    /** 爱子科技设备标识 */
    public static final String AIZI_DEVICE_TAG = "YingerBao";
    
    /** 定时器延时类型 */
    public static final String ALARM_WAIT_TYPE = "wait_type";
    
    /** 定时器延时类型 */
    public static final String DEVICE_CONNECT_DELAY_TYPE = "device_connect_delay_type";
    
    /** 定时器接收L1数据超时 */
    public static final int ALARM_WAIT_L1 = 1;
    
    /** 设备校验超时 */
    public static final int ALARM_WAIT_CHECK_DEVICE = 2;
    
    /** 同步数据超时 */
    public static final int ALARM_WAIT_SYNC_DATA = 3;
    
    /** 搜索设备超时 */
    public static final int ALARM_WAIT_SEARCH_DEVICE = 4;
    
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
    
    /** 设备同步数据超时时间  */
    public static final long WAIT_SYNC_PERIOD = 15 * 1000;
    
    /** 搜索设备超时时间  */
    public static final long WAIT_SEARCH_DEVICE_PERIOD = 20 * 1000;
    
    /** 验证设备超时时间  */
    public static final long WAIT_CHECK_PERIOD = 15 * 1000;
    
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
    
    /** 工厂测试命令结果反馈  */
    public static final String MANU_TEST_RESULT = "com.aizi.yingerbao.manu.test";
    
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
    public static final String AIZI_COMMAND_DATA = "aizi_command_data";
    
    /** 数据传输标识  */
    public static final String AIZI_SEND_DATA = "aizi_send_data";

    public static final String REQUEST_KEY_SEND_CODE = "request_key_send_code";

    public static final String REQUEST_KEY_RESULT_CODE = "request_key_result_code";
    
    
    /** 数据传输类型  */
    
    public static final String DATA_TRANSFER_TYPE = "com.aizi.yingerbao.datatransfer.TYPE";
    
    public static final int TRANSFER_TYPE_SUCCEED = 0;

    public static final int TRANSFER_TYPE_NOT_COMPLETED = 1;
    
    public static final int TRANSFER_TYPE_ERROR = 2;
    
    public static final int TRANSFER_TYPE_ERROR_CLEAR = 3;
    
    

    public static final String ACTIVATE_RESULT = "activate_device_result";

    public static final String NOT_SYNC_DATA_LEN = "not_sync_data_len";
    public static final String ACTION_TOTAL_DATA_LEN = "com.aizi.yingerbao.total_data_len";
    public static final String ACTION_RECE_DATA = "com.aizi.yingerbao.rece_data";
    public static final String RECE_SYNC_DATA_LEN = "rece_sync_data_len";
    public static final String RECE_SYNC_DATA_RESULT = "rece_sync_data_result";
    public static final String RECE_BREATH_DATA_RESULT = "rece_breath_data_result";

    public static final String SYNC_DATA_SUCCEED_TIMESTAMP = "sync_data_succeed_timestamp";

    public static final String ACTION_CHECKDEVICE_SUCCEED = "com.aizi.yingerbao.checkdev_succeed";
    
    public static final String ACTION_CHECKDEVICE_FAILED = "com.aizi.yingerbao.checkdev_failed";

    public static final String IS_SYNC_DATA = "is_sync_data";
    
    /** 延时触发action */
    public static final String ACTION_ALARM_MESSAGE =
            "com.aizi.yingerbao.action.alarm.message";

    public static final String ACTION_DEVICE_CONNECT_RECEIVER 
            = "com.aizi.yingerbao.action.deviceconnect.receiver";
    
    public static final String CUR_STATISTIC_TIME = "com.aizi.yingerbao.cst";
    public static final String CUR_STATISTIC_CHARGE = "com.aizi.yingerbao.charge";
    
    public static final String AIZI_IS_CONNECT_REPEAT = "com.aizi.yingerbao.repeat.connect";
    
    public static final String AIZI_CONFIG_TEMPERATURE = "com.aizi.yingerbao.config.temperature";
    public static final String AIZI_CONFIG_BREATHSTOP = "com.aizi.yingerbao.config.breathstop";
    public static final String AIZI_CONFIG_BREATHLIGHT = "com.aizi.yingerbao.config.breathlight";
    public static final String AIZI_CONFIG_LIESLEEP = "com.aizi.yingerbao.config.liesleep";

    /** 设备时间数据  */
    public static final String DATA_TRANSFER_TIME = "com.data.transfer.devicetime";
    
    public static final String DATA_TEMP_ALARM_VALUE_NEW = "com.data.temp.alarmvalue_new";
    
    public static final String DATA_TEMP_ALARM_VALUE_OLD = "com.data.temp.alarmvalue_old";
    
  

    public static void setSearchRange(int searchparam) {
        SCAN_RANG = searchparam;
    }  
}
