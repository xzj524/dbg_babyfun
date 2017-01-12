package com.aizi.yingerbao.utility;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.List;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;

import com.aizi.yingerbao.baseheader.BaseL2Message;
import com.aizi.yingerbao.baseheader.KeyPayload;
import com.aizi.yingerbao.breath.BabyBreath;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.database.BreathDataInfo;
import com.aizi.yingerbao.database.DevCheckInfo;
import com.aizi.yingerbao.database.ExceptionEvent;
import com.aizi.yingerbao.database.SleepInfo;
import com.aizi.yingerbao.database.TemperatureDataInfo;
import com.aizi.yingerbao.database.YingerbaoDatabase;
import com.aizi.yingerbao.deviceinterface.DeviceFactory;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.synctime.DeviceTime;

import de.greenrobot.event.EventBus;

public class MessageParse {
    
    private static final String TAG = MessageParse.class.getSimpleName();
    
    private DataInputStream mDis;
    int mBreathStartResult;
    int mBreathCount;
    PendingIntent mSyncDataPendingIntent;
    Context mContext;
    boolean mIsDeviceActivited = false;
    boolean mIsDeviceTimed = false;
    boolean mIsSyncData = true;
    
    private static final int RECV_DATA_COUNT = 1440;
    private static final int WAIT_LOAD_DATA_TIME = 1000 * 60 * 60 * 6;
    
    /** single instance. */
    private static volatile MessageParse mInstance;   
    protected MessageParse(Context context) {
        EventBus.getDefault().register(this); 
        mBreathStartResult = 1;
        mContext = context;
    }

    
    public static MessageParse getInstance(Context context) {
        if (mInstance != null) {
            return mInstance;
        } else {
            mInstance = new MessageParse(context);
            return mInstance;
        }
    }
    
    public void onEvent(BaseL2Message bsl2Msg) {
        handleL2Msg(bsl2Msg);
    }
    
    public void RecvBaseL2Msg(BaseL2Message bsl2Msg) {
        handleL2Msg(bsl2Msg);
    }

    private synchronized void handleL2Msg(BaseL2Message bMsg) {
        
        String l2payload = Utiliy.printHexString(bMsg.toByte());
        SLog.e(TAG, "HEX string l2load1 = " + l2payload);
        Utiliy.logToFile(" L2 RECV DATA : " + l2payload);// 写入本地日志文件
        
        Intent intent = new Intent(Constant.DATA_TRANSFER_RECEIVE);
        intent.putExtra("transferdata", "L2 " + l2payload);
        EventBus.getDefault().post(intent); // 显示在界面上
        
        List<KeyPayload> params = getKeyPayloadList(bMsg.payload);
        if (params != null) {
            switch (bMsg.commanID) {
            case Constant.COMMAND_ID_UPDATE_ROM:
                break;
            case Constant.COMMAND_ID_SETTING:
                handleSettings(params);
                break;
            case Constant.COMMAND_ID_BIND:
                handleBind(params);
                break;
            case Constant.COMMAND_ID_DATA:
                handleData(params);
                break;
            case Constant.COMMAND_ID_MANUFACTURE_TEST:
                handleManufature(params);
                break;
            case Constant.COMMAND_ID_NOTIFY:
                handleNotify(params);
                break;
            default:
                break;
            }
        }
    }
    
    
    private void handleBind(List<KeyPayload> params) {
        try {
            for (KeyPayload kpload:params) {
                if (kpload.key == 6) { // 收到连接合法性返回
                    SLog.e(TAG, "receive  check device data");
                    if (kpload.keyLen >= 14) {
                        handlecheckinfo(kpload.keyValue); // 分析校验返回
                    } else {
                        Intent intent = new Intent(Constant.ACTION_CHECKDEVICE_FAILED);
                        EventBus.getDefault().post(intent);
                    }
                } 
            } 
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }


    private void handlecheckinfo(byte[] keyValue) {
        try {
            DevCheckInfo devCheckInfo = new DevCheckInfo();
            devCheckInfo.mCheckInfoYear = (keyValue[0] & 0xfc) >> 2;
            devCheckInfo.mCheckInfoMonth = ((keyValue[0] & 0x03) << 2) | ((keyValue[1] & 0xc0) >> 6);
            devCheckInfo.mCheckInfoDay = (keyValue[1] & 0x3e) >> 1;
            devCheckInfo.mCheckInfoHour = ((keyValue[1] & 0x01)  << 4) | ((keyValue[2] & 0xf0) >> 4);
            devCheckInfo.mCheckInfoMinute = ((keyValue[2] & 0x0f) << 2) | ((keyValue[3] & 0xc0) >> 6);
            devCheckInfo.mCheckInfoSecond = (keyValue[3] & 0x3f);
            
            devCheckInfo.mNoSyncSleepDataLength = ((keyValue[4] << 8) & 0xff00) | (keyValue[5] & 0xff);
            devCheckInfo.mNoSyncTempDataLength = ((keyValue[6] << 8) & 0xff00) | (keyValue[7] & 0xff);
            devCheckInfo.mNoSyncBreathDataLength = ((keyValue[8] << 8) & 0xff00) | (keyValue[9] & 0xff);
            devCheckInfo.mNoSyncExceptionLength = ((keyValue[10] << 8) & 0xff00) | (keyValue[11] & 0xff);
            devCheckInfo.mDeviceCharge = keyValue[12] & 0xff;
            devCheckInfo.mDeviceStatus = keyValue[13];
            
            
            boolean ischecked = IsDeviceChecked(devCheckInfo);
            if (ischecked) {
                // 校验设备接口调用成功
                Utiliy.reflectTranDataType(mContext, 0);
                int isDeviceActivited = (keyValue[13] & 0x10) >> 4;
                int totaldatalen =  
                         devCheckInfo.mNoSyncTempDataLength // 温度数据长度
                        + devCheckInfo.mNoSyncBreathDataLength // 呼吸停滞数据长度
                        + devCheckInfo.mNoSyncExceptionLength; // 异常数据长度
                Intent intent = new Intent(Constant.ACTION_TOTAL_DATA_LEN);
                intent.putExtra(Constant.NOT_SYNC_DATA_LEN, totaldatalen);
                EventBus.getDefault().post(intent);
                
                PrivateParams.setSPInt(mContext, "NoSyncDataLength", totaldatalen);
                PrivateParams.setSPInt(mContext, "GetCheckinfo", 1);
                
                String result = "CheckDevice return SleepDataLength = " + devCheckInfo.mNoSyncSleepDataLength
                        + " TempDataLength = " + devCheckInfo.mNoSyncTempDataLength
                        + " BreathDataLength = " + devCheckInfo.mNoSyncBreathDataLength
                        + " mDeviceCharge = " + devCheckInfo.mDeviceCharge
                        + " mDeviceStatus = " + (int)devCheckInfo.mDeviceStatus
                        + " isDeviceActivited = " + isDeviceActivited;           
                SLog.e(TAG, result);
                Utiliy.dataToFile(result);
                
                if (PrivateParams.getSPInt(mContext, "check_device_status", 0) == 2) {
                    return;// 表示设备身份验证超时
                }
               
                
                if (PrivateParams.getSPInt(mContext, "connect_interrupt", 0) == 1) {
                    return; // 检测到连接过程中断
                }
                
                
                
                Thread.sleep(500); // 身份验证完成之后延时500ms

                if (isDeviceActivited == 0) { // 如果没有激活过设备则进行激活
                    DeviceFactory.getInstance(mContext).activateDevice();
                    mIsDeviceActivited = false;
                } else {
                    mIsDeviceActivited = true;
                }
                
                if (!checkDeviceConfig(devCheckInfo.mDeviceStatus)) {
                    DeviceFactory.getInstance(mContext).updateDeviceConfig();
                }

                setDeviceTime(devCheckInfo); // 校时操作
                
                if (PrivateParams.getSPString(mContext, Constant.AIZI_IS_CONNECT_REPEAT).equals("true")) {
                    return; // 连接断开之后重试，不用继续读取数据。
                }
                
                long curtime = System.currentTimeMillis();
                long syncdatatime = PrivateParams.getSPLong(mContext, Constant.SYNC_DATA_SUCCEED_TIMESTAMP);
                if ((curtime - syncdatatime > WAIT_LOAD_DATA_TIME || totaldatalen > 60) 
                        || devCheckInfo.mNoSyncBreathDataLength > 0) {           
                    // 距上次同步数据超过六小时，并且未同步数据大于60,或者呼吸停滞数据存在时.
                    SLog.e(TAG, "sync data has consumed six hour ");
                    DeviceFactory.getInstance(mContext).getExceptionEvent();
                    DeviceFactory.getInstance(mContext).getAllNoSyncInfo(2);
                    DeviceFactory.getInstance(mContext).getBreathStopInfo();
                    // 读取数据状态，开始
                    PrivateParams.setSPInt(mContext, "sync_data_status", 1);
                    setSyncDataAlarm();
                    mIsSyncData = true;
                } else {
                    mIsSyncData = false;
                    SLog.e(TAG, "sync data don not consumed six hour");
                }  
                
                if (mIsDeviceActivited && mIsDeviceTimed) {
                    intent = new Intent(Constant.ACTION_CHECKDEVICE_SUCCEED);
                    intent.putExtra(Constant.IS_SYNC_DATA, mIsSyncData);
                    EventBus.getDefault().post(intent);
                }
            } else {
                devCheckInfo.mDeviceCharge = 0;
                Intent intent = new Intent(Constant.ACTION_CHECKDEVICE_FAILED);
                EventBus.getDefault().post(intent);
            }
            
            PrivateParams.setSPInt(mContext, Constant.CUR_STATISTIC_CHARGE, devCheckInfo.mDeviceCharge);
            
           
            
            /***工厂测试***/
           /* intent = new Intent(Constant.ACTION_CHECKDEVICE_SUCCEED);
            intent.putExtra(Constant.IS_SYNC_DATA, false);
            EventBus.getDefault().post(intent);*/
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }


    private boolean checkDeviceConfig(byte devconfig) {
        int TemperatureAlarmOnOff = (int)(devconfig & 0x01);
        int BreathStopAlarmOnOff = (int)(devconfig & 0x02) >> 1;
        int BreathLightOnOff = (int)(devconfig & 0x04) >> 2;
        int LieSleepAlarmOnOff = (int)(devconfig & 0x08) >> 3;
        
        int phoneTemperatureAlarmOnOff = PrivateParams.getSPInt(mContext, Constant.AIZI_CONFIG_TEMPERATURE, 1);
        int phoneBreathStopAlarmOnOff = PrivateParams.getSPInt(mContext, Constant.AIZI_CONFIG_BREATHSTOP, 1);
        int phoneBreathLightOnOff = PrivateParams.getSPInt(mContext, Constant.AIZI_CONFIG_BREATHLIGHT, 1);
        int phoneLieSleepAlarmOnOff = PrivateParams.getSPInt(mContext, Constant.AIZI_CONFIG_LIESLEEP, 0);
        
        if (TemperatureAlarmOnOff == phoneTemperatureAlarmOnOff
                && BreathStopAlarmOnOff == phoneBreathStopAlarmOnOff
                && BreathLightOnOff == phoneBreathLightOnOff
                && LieSleepAlarmOnOff == phoneLieSleepAlarmOnOff) {
            return true;
        }
        return false;
    }


    private void updateDeviceConfig(byte devconfig) {
        int TemperatureAlarmOnOff = (int)(devconfig & 0x01);
        int BreathStopAlarmOnOff = (int)(devconfig & 0x02);
        int BreathLightOnOff = (int)(devconfig & 0x04);
        int LieSleepAlarmOnOff = (int)(devconfig & 0x80);
        
        PrivateParams.setSPInt(mContext, Constant.AIZI_CONFIG_TEMPERATURE, TemperatureAlarmOnOff);
        PrivateParams.setSPInt(mContext, Constant.AIZI_CONFIG_BREATHSTOP, BreathStopAlarmOnOff);
        PrivateParams.setSPInt(mContext, Constant.AIZI_CONFIG_BREATHLIGHT, BreathLightOnOff);
        PrivateParams.setSPInt(mContext, Constant.AIZI_CONFIG_LIESLEEP, LieSleepAlarmOnOff);
    }


    private boolean IsDeviceChecked(DevCheckInfo devCheckInfo) {
        if (devCheckInfo.mCheckInfoYear < 0 || devCheckInfo.mCheckInfoYear > 64) {
            return false;
        }
        
        if (devCheckInfo.mCheckInfoMonth < 1 || devCheckInfo.mCheckInfoMonth > 12) {
            return false;
        }
        
        if (devCheckInfo.mCheckInfoDay < 1 || devCheckInfo.mCheckInfoDay > 31) {
            return false;
        }
        
        if (devCheckInfo.mCheckInfoHour < 0 || devCheckInfo.mCheckInfoHour > 23) {
            return false;
        }
        
        if (devCheckInfo.mCheckInfoMinute < 0 || devCheckInfo.mCheckInfoMinute > 59) {
            return false;
        }
        
        if (devCheckInfo.mCheckInfoSecond < 0 || devCheckInfo.mCheckInfoSecond > 59) {
            return false;
        }
        
        if (devCheckInfo.mDeviceCharge < 0 || devCheckInfo.mDeviceCharge > 100) {
            return false;
        }
        
        return true;
    }


    private void setSyncDataAlarm() {
        if (mSyncDataPendingIntent != null) {
            Utiliy.cancelAlarmPdIntent(mContext, mSyncDataPendingIntent);
        }
        mSyncDataPendingIntent = Utiliy.getDelayPendingIntent(mContext, Constant.ALARM_WAIT_SYNC_DATA);
        Utiliy.setDelayAlarm(mContext, Constant.WAIT_SYNC_PERIOD, mSyncDataPendingIntent);
    }


    private void setDeviceTime(DevCheckInfo devCheckInfo) {
        
        try {
            Calendar calendar = Calendar.getInstance(); 
            int year = calendar.get(Calendar.YEAR) - 2000;
            int month = calendar.get(Calendar.MONTH)+1;
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);
            int second = calendar.get(Calendar.SECOND);
            
            SLog.e(TAG, "phonetime  year = " + year 
                    + " month = " + month
                    + " day = " + day
                    + " hour = " + hour
                    + " minu = " + min
                    + " second = " + second);
            SLog.e(TAG, "devicetime  year = " + devCheckInfo.mCheckInfoYear 
                    + " month = " + devCheckInfo.mCheckInfoMonth
                    + " day = " + devCheckInfo.mCheckInfoDay
                    + " hour = " + devCheckInfo.mCheckInfoHour
                    + " minu = " + devCheckInfo.mCheckInfoMinute
                    + " second = " + devCheckInfo.mCheckInfoSecond);
            
            if (year != devCheckInfo.mCheckInfoYear
                    || month != devCheckInfo.mCheckInfoMonth
                    || day != devCheckInfo.mCheckInfoDay
                    || hour != devCheckInfo.mCheckInfoHour
                    || Math.abs(min - devCheckInfo.mCheckInfoMinute) > 30) {
                DeviceFactory.getInstance(mContext).setDeviceTime();
                mIsDeviceTimed = false;
            } else {
                mIsDeviceTimed = true;
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }



  //写文件  
    public void writeSDFile(String fileName, byte[] write_str) throws IOException{    
      
            File file = new File(fileName);    
      
            FileOutputStream fos = new FileOutputStream(file);    
      
            byte [] bytes = write_str;   
      
            fos.write(bytes);   
      
            fos.close();   
    }   

    private void handleData(List<KeyPayload> params) {
        for (KeyPayload kpload:params) {
            if (kpload.key == 2) { // 接收到实时数据
                SLog.e(TAG, "receive  realtime data");
                if (kpload.keyLen == 4) {
                    acquireTemp(kpload.keyValue); // 分析实时温度数据
                }
            } else if (kpload.key == 5) {
                SLog.e(TAG, "receive data complete " + kpload.keyLen);
                handledatacompleted(kpload.keyValue);
            } else if (kpload.key == 6) {
                SLog.e(TAG, "receiver sleep data " + kpload.keyLen);
                handleSleepData(kpload.keyValue);
            } else if (kpload.key == 7) { //温度数据
                SLog.e(TAG, "receiver temp data " + kpload.keyLen);
                handleTempData(kpload.keyValue);
            } else if (kpload.key == 8) { //湿度数据
                SLog.e(TAG, "receiver humit data " + kpload.keyLen);
                handleHumbitData(kpload.keyValue);
            } else if (kpload.key == 10) { // 实时温度数据
                handleRealTimeTemperatureData(kpload.keyValue);
            } else if (kpload.key == 12) { // 呼吸停滞数据
                SLog.e(TAG, "receiver breath stop data " + kpload.keyLen);
                handleBreathStopData(kpload.keyValue);
            } else if (kpload.key == 13) { // 呼吸停滞数据结束
                SLog.e(TAG, "receiver breath stop data completed " + kpload.keyLen);
                handleBreathStoprefect(kpload.keyValue);
            } else if (kpload.key == 15) {
                handleExceptionData(kpload.keyValue);
            } else if (kpload.key == 16) {
                handleExceptionComplete(kpload.keyValue);
            }
        }
    }


    private void handleExceptionComplete(byte[] keyValue) {
        try {
            int exceptionresult = keyValue[0] & 0xff;
            if (exceptionresult == 0 || exceptionresult == 1) {
                Utiliy.reflectTranDataType(mContext, 0);
            } else if (exceptionresult == 2) {
                Utiliy.reflectTranDataType(mContext, 2);
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }


    private void handleBreathStoprefect(byte[] keyValue) {
        try {
            int datastopresult = keyValue[0] & 0xff;
            if (datastopresult == 0 || datastopresult == 1) {
                Utiliy.reflectTranDataType(mContext, 0);
            } else if (datastopresult == 2) {
                Utiliy.reflectTranDataType(mContext, 2);
            }
            
            Intent intent = new Intent(Constant.ACTION_RECE_DATA);
            intent.putExtra(Constant.RECE_BREATH_DATA_RESULT, datastopresult);
            EventBus.getDefault().post(intent);
            
            // 读取数据状态，成功
            PrivateParams.setSPInt(mContext, "sync_data_status", 3);
            
            // 取消接收数据定时器
            if (mSyncDataPendingIntent != null) {
                Utiliy.cancelAlarmPdIntent(mContext, mSyncDataPendingIntent);
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }


    private void handledatacompleted(byte[] keyValue) {
        try {
            int datatransresult = keyValue[0] & 0xff;
            if (datatransresult == 0 
                    || datatransresult == 2
                    || datatransresult == 4) {    
                Utiliy.reflectTranDataType(mContext, 0);
            } else if (datatransresult == 1 || datatransresult == 3) {
                Utiliy.reflectTranDataType(mContext, 2);
            }
            
            Intent intent = new Intent(Constant.ACTION_RECE_DATA);
            intent.putExtra(Constant.RECE_SYNC_DATA_RESULT, datatransresult);
            EventBus.getDefault().post(intent);
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }


    private void handleExceptionData(byte[] keyValue) {
        
        Intent intent = new Intent(Constant.ACTION_RECE_DATA);
        intent.putExtra(Constant.RECE_SYNC_DATA_LEN, keyValue.length);
        EventBus.getDefault().post(intent);
        
        ExceptionEvent exEvent = new ExceptionEvent();
        String exceptionlog = Utiliy.printHexString(keyValue);
        SLog.e(TAG, "Exception Log = " + exceptionlog);
        List<BmobObject> exceptiondatainfos = new ArrayList<BmobObject>();
        int exceptionlen = 0;
        int exceptionlength = keyValue.length;
        if (exceptionlength % 8 == 0) {
            for (int i = 0; i < exceptionlength/8; i++) {
                exEvent.mExceptionYear = (keyValue[i*8] & 0xfc) >> 2;
                exEvent.mExceptionMonth = ((keyValue[i*8] & 0x03) << 2) | ((keyValue[1+i*8] & 0xc0) >> 6);
                exEvent.mExceptionDay = (keyValue[1+i*8] & 0x3e) >> 1;
                exEvent.mExceptionHour = ((keyValue[1+i*8] & 0x01)  << 4) | ((keyValue[2+i*8] & 0xf0) >> 4);
                exEvent.mExceptionMinute = ((keyValue[2+i*8] & 0x0f) << 2) | ((keyValue[3+i*8] & 0xc0) >> 6);
                exEvent.mExceptionSecond = (keyValue[3+i*8] & 0x3f);
                
                exEvent.mExceptionType = keyValue[4+i*8] & 0xff;
                exEvent.mExceptionData1 = keyValue[5+i*8] & 0xff;
                exEvent.mExceptionData2 = keyValue[6+i*8] & 0xff;
                exEvent.mExceptionData3 = keyValue[7+i*8] & 0xff;
                
                exceptiondatainfos.add(exEvent);
                exceptionlen = exceptiondatainfos.size();
                SLog.e(TAG, "exception len = " + exceptionlen);
                if (exceptionlen == 50 ) {
                    bmobBatchData(exceptiondatainfos);
                    exceptiondatainfos.clear();
                }
            
                String logStr = "Exception occured at " + exEvent.mExceptionYear 
                            + "-" + exEvent.mExceptionMonth
                            + "-" + exEvent.mExceptionDay
                            + "-" + exEvent.mExceptionHour
                            + "-" + exEvent.mExceptionMinute
                            + "-" + exEvent.mExceptionSecond
                            + " : " + exEvent.mExceptionType
                            + "-" + exEvent.mExceptionData1
                            + "-" + exEvent.mExceptionData2
                            + "-" + exEvent.mExceptionData3;
                Utiliy.dataToFile(logStr);
                SLog.e(TAG, "Exception Event = " + logStr);
                Intent transfer_intent = new Intent(Constant.DATA_TRANSFER_RECEIVE);
                transfer_intent.putExtra("transferdata", logStr);
                EventBus.getDefault().post(transfer_intent); // 显示在界面上  
            }
        }
        
        if (exceptionlen < 50 && exceptionlen > 0) {
            bmobBatchData(exceptiondatainfos);
            exceptiondatainfos.clear();
        }
        
        Utiliy.reflectTranDataType(mContext, 1);
        setSyncDataAlarm();
    }


    private void handleRealTimeTemperatureData(byte[] keyValue) {
        int PNValue = (keyValue[0] & 0x80) >> 7;
        int tempHigh = keyValue[0] & 0x7f;
        int tempLow = keyValue[1] & 0xff;
        int errType = 0;
        
        String tempString;
        if (PNValue == 1) {
            tempString = "-" + tempHigh + "." + tempLow;
        } else {
            tempString = tempHigh + "." + tempLow;
        }
        
        if (tempHigh == 0 && tempLow == 255) { //温度传感器损坏
            errType = 1;
        } else if (tempHigh == 1 && tempLow == 255) { // 温度超出合理范围
            errType = 2;
        }
        
        Intent intent = new Intent(Constant.DATA_REALTIME_TEMPERATURE);
        intent.putExtra("realtime_temperature", tempString);
        intent.putExtra("error_type", errType);
        EventBus.getDefault().post(intent);
        
        
        Utiliy.reflectTranDataType(mContext, 0);
        
        String result = "Receiver RealTime Temperature = " + tempString;
        SLog.e(TAG, result);
        Utiliy.dataToFile(result);
        
    }

    private void handleBreathStopData(byte[] keyValue) {
        
        Intent intent = new Intent(Constant.ACTION_RECE_DATA);
        intent.putExtra(Constant.RECE_SYNC_DATA_LEN, keyValue.length);
        EventBus.getDefault().post(intent);

        BreathDataInfo breathinfo = new BreathDataInfo(mContext);
        int breathstoplength = keyValue.length;
        if (breathstoplength % 8 == 0) {
            for (int i = 0; i < keyValue.length/8; i++) {
                int year = (keyValue[i*8] & 0xfc) >> 2;
                int month = ((keyValue[i*8] & 0x03) << 2) | ((keyValue[1+i*8] & 0xc0) >> 6);
                int day = (keyValue[1+i*8] & 0x3e) >> 1;
                int hour = ((keyValue[1+i*8] & 0x01)  << 4) | ((keyValue[2+i*8] & 0xf0) >> 4);
                int minu = ((keyValue[2+i*8] & 0x0f) << 2) | (((keyValue[3+i*8] & 0xc0) >> 6) & 0x03);
                int second = (keyValue[3+i*8] & 0x3f);
                
                breathinfo.setBreathYear(year + 2000);
                breathinfo.setBreathMonth(month);
                breathinfo.setBreathDay(day);
                breathinfo.setBreathHour(hour);
                breathinfo.setBreathMinute(minu);
                breathinfo.setBreathSecond(second);
 
                int isAlarm = (keyValue[4 + i*8] & 0xff);
                if (isAlarm == 1) {
                    breathinfo.setBreathIsAlarm(1);
                } else {
                    breathinfo.setBreathIsAlarm(0);
                }
                
                breathinfo.setBreathDuration(keyValue[5 + i*8] & 0xff);
                breathinfo.setBreathTimestamp(System.currentTimeMillis());
                YingerbaoDatabase.insertBreathInfo(mContext, breathinfo);
              
                String breathlog = "Breath Stop Info = " + year + " month = " + month
                        + " day = " + day + " hour = " + hour
                        + " minu = " + minu + " second = " + second
                        + " isAlarm = " + isAlarm + " duration = " + breathinfo.getBreathDuration();
                SLog.e(TAG, breathlog);
                Utiliy.dataToFile(breathlog);
            }
        }

        Utiliy.reflectTranDataType(mContext, 1);
        setSyncDataAlarm();
    }


    private void bmobBatchData(final List<BmobObject> datainfos) {
        try {
            new BmobBatch().insertBatch(datainfos).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> o, BmobException e) {
                    if(e==null){
                        for(int i=0;i<o.size();i++){
                            BatchResult result = o.get(i);
                            BmobException ex =result.getError();
                            if(ex==null){
                                //SLog.e(TAG, i+" succeed : "+result.getCreatedAt()+", "+result.getObjectId()+", "+result.getUpdatedAt());
                            }else{
                                SLog.e(TAG, i+" failed : "+ex.getMessage()+","+ex.getErrorCode());
                            }
                        }
                    } else {
                        SLog.e(TAG, "failed: "+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }


    private void handleHumbitData(byte[] keyValue) {
        int year = ((keyValue[0] & 0x7e) >> 1) &  0x3f;
        int month = ((keyValue[0] & 0x01) << 3)  |  ((keyValue[1] & 0xe0) >> 5);
        int day = keyValue[1] & 0x1f;
        int minu = keyValue[2] << 8 | keyValue[3];
        int sleepcount = keyValue[4] << 8 | keyValue[5];
        
        SLog.e(TAG, "Humbit year = " + year 
                + " month = " + month 
                + " day = " + day 
                + " minu = " + minu 
                + " sleepcount = " + sleepcount);
        
        for (int i = 0; i < sleepcount; i++) {
            SLog.e(TAG, "Humbit value = " + keyValue[6+i]);
        }
    }
    
    private void handleTempData(byte[] keyValue) {
        
        Intent intent = new Intent(Constant.ACTION_RECE_DATA);
        intent.putExtra(Constant.RECE_SYNC_DATA_LEN, keyValue.length);
        EventBus.getDefault().post(intent);
        
        int tempdatalength = 0;
       // List<BmobObject> tempdatainfos = new ArrayList<BmobObject>();
        TemperatureDataInfo temperatureinfo = new TemperatureDataInfo(mContext);
        int year = ((keyValue[0] & 0x7e) >> 1) &  0x3f;
        int month = ((keyValue[0] & 0x01) << 3)  |  ((keyValue[1] & 0xe0) >> 5);
        int day = keyValue[1] & 0x1f;
        int minu = ((((keyValue[2] & 0xff) << 8) & 0xff00) + (keyValue[3] & 0x0ff)) & 0x0fff;
        int tempcount = keyValue[4] << 8 | keyValue[5];
        
        SLog.e(TAG, "Temp year = " + year 
                + " month = " + month 
                + " day = " + day 
                + " minu = " + minu 
                + " tempcount = " + tempcount );
        
        temperatureinfo.setTemperatureYear(year + 2000);
        temperatureinfo.setTemperatureMonth(month);
        temperatureinfo.setTemperatureDay(day);
        
        
        if (tempcount <= RECV_DATA_COUNT) {
            for (int i = 0; i < tempcount/2; i++) {
                
                int PNValue = (keyValue[6+i*2] & 0x80) >> 7;
                int tempHigh = keyValue[6+i*2] & 0x7f;
                int tempLow = keyValue[7+i*2] & 0xff;
                
                String tempValue;
                if (PNValue == 1) {
                    SLog.e(TAG, "temp = " + "-" + tempHigh + "." + tempLow);
                    tempValue = "-" + tempHigh + "." + tempLow;
                } else {
                    SLog.e(TAG, "temp = " + tempHigh + "." + tempLow);
                    tempValue = tempHigh + "." + tempLow;
                }
                
                temperatureinfo.setTemperatureMinute(minu + i * 10);
                temperatureinfo.setTemperatureValue(tempValue);
                temperatureinfo.setTemperatureTimestamp(System.currentTimeMillis());
                // 温度数据插入数据库
                YingerbaoDatabase.insertTemperatureInfo(mContext, temperatureinfo);
               /* tempdatainfos.add(temperatureinfo);
                tempdatalength = tempdatainfos.size();
                if (tempdatalength == 50) {
                    bmobBatchData(tempdatainfos);
                    tempdatainfos.clear();
                }*/
                
                String tempinfo = "Device Time : " + temperatureinfo.getTemperatureYear()
                        + "-" + temperatureinfo.getTemperatureMonth()
                        + "-" + temperatureinfo.getTemperatureDay()
                        + "-" + temperatureinfo.getTemperatureMinute()
                        + " tempValue = " + temperatureinfo.getTemperatureValue();  
                Utiliy.dataToFile(tempinfo);
            }
        }
        
      /*  if (tempdatalength > 0 && tempdatalength < 50) {
            bmobBatchData(tempdatainfos);
            tempdatainfos.clear();
        }*/
        
        Utiliy.reflectTranDataType(mContext, 1);
        setSyncDataAlarm();
    }


    private void handleSleepData(byte[] keyValue) {
        // TODO Auto-generated method stub
        if (keyValue.length > 6) {
            List<SleepInfo> sleepInfos = getSleepInfoList(keyValue);
        } else {
            SLog.e(TAG, "There is no Sleep Data");
        }
    }


    private List<SleepInfo> getSleepInfoList(byte[] keyValue) {
        List<SleepInfo> sleepInfos = new ArrayList<SleepInfo>();
        
        Intent intent = new Intent(Constant.ACTION_RECE_DATA);
        intent.putExtra(Constant.RECE_SYNC_DATA_LEN, keyValue.length);
        EventBus.getDefault().post(intent);
        
        int year = ((keyValue[0] & 0x7e) >> 1) &  0x3f;
        int month = ((keyValue[0] & 0x01) << 3)  |  ((keyValue[1] & 0xe0) >> 5);
        int day = keyValue[1] & 0x1f;
        int minu = ((((keyValue[2] & 0xff) << 8) & 0xff00) + (keyValue[3] & 0x0ff)) & 0x0fff;
        int sleepcount = keyValue[4] << 8 | keyValue[5];
        
        SleepInfo sleepInfo = new SleepInfo();
        if (keyValue.length == 6 + sleepcount) {
            if (sleepcount <= 1440) {
                SLog.e(TAG, "sleep date  year = " + year 
                        + " month = " + month 
                        + " day = " + day 
                        + " min = " + minu
                        + " sleepcount = " + sleepcount);
                for (int i = 0; i < sleepcount; i++) {
                   
                   sleepInfo.mSleepTimestamp = System.currentTimeMillis();
                   sleepInfo.mSleepYear = year;
                   sleepInfo.mSleepMonth = month;
                   sleepInfo.mSleepDay = day;
                   sleepInfo.mSleepMinute = minu+i;
                   sleepInfo.mSleepValue = keyValue[6+i] & 0x0ff;
                   
                   YingerbaoDatabase.insertSleepInfo(mContext, sleepInfo);
                   sleepInfos.add(sleepInfo);
               }
           } 
        }
        Utiliy.reflectTranDataType(mContext, 1);
        setSyncDataAlarm();
        return sleepInfos;
    }

    private void acquireTemp(byte[] keyValue) {
        int PNValue = (keyValue[0] & 0x80) >> 7;
        int tempHigh = keyValue[0] & 0x7f;
        int tempLow = keyValue[1] & 0xff;
        
        int humbit = keyValue[2] & 0xff;
        int energy = keyValue[3] & 0xff;
        
        String tempString;
        if (PNValue == 1) {
            SLog.e(TAG, "temp = " + "-" + tempHigh + "." + tempLow);
            tempString = "-" + tempHigh + "." + tempLow;
        } else {
            SLog.e(TAG, "temp = " + tempHigh + "." + tempLow);
            tempString = tempHigh + "." + tempLow;
        }
        
        EventBus.getDefault().post(tempString);
       
        SLog.e(TAG, "humbit = " + humbit);
        SLog.e(TAG, "energy = " + energy);
        
        Utiliy.reflectTranDataType(mContext, 0);
        
        String result = "Receiver RealTime Temperature 1 = " + tempString;
        SLog.e(TAG, result);
        Utiliy.dataToFile(result);
    }


    private void handleNotify(List<KeyPayload> params) {
        for (KeyPayload kpload:params) {
            if (kpload.key == 2) { // 发烧报警
                SLog.e(TAG, "FEVER ALARM");
                String babytemp = getBabyEmergencyTemp(kpload.keyValue);
                Utiliy.showEmergencyFever(mContext, null, null, babytemp);
            } else if (kpload.key == 3) { // 呼吸停滞报警
                SLog.e(TAG, "BREATH ALARM");
                Utiliy.showEmergencyBreath(mContext, null, null, null);
            }
        }
    }


    private String getBabyEmergencyTemp(byte[] keyValue) {
        int PNValue = (keyValue[4] & 0x80) >> 7;
        int tempHigh = keyValue[4] & 0x7f;
        int tempLow = keyValue[5] & 0xff;
        
        String tempString = "";
        if (PNValue == 1) {
            SLog.e(TAG, "temp = " + "-" + tempHigh + "." + tempLow);
            tempString = "-" + tempHigh + "." + tempLow;
        } else {
            SLog.e(TAG, "temp = " + tempHigh + "." + tempLow);
            tempString = tempHigh + "." + tempLow;
        }
        return tempString;
    }


    private void handleManufature(List<KeyPayload> params) {
        for (KeyPayload kpload:params) {
            if (kpload.key == 2) { // 呼吸测试启动返回
                SLog.e(TAG, "START BREATH ALREADY");
                if (kpload.keyLen == 1) {
                    mBreathStartResult = kpload.keyValue[0] & 0x0f;
                    if (mBreathStartResult == 0) {
                        Utiliy.reflectTranDataType(mContext, 0);
                    } else if (mBreathStartResult == 1) {
                        Utiliy.reflectTranDataType(mContext, 2);
                    }
                }
            } else if (kpload.key == 3) { // 呼吸测试数据
                SLog.e(TAG, "RECEIVE REAL BREATH DATA");
                BabyBreath babyBre = getBabyBreath(kpload.keyValue);
                if (babyBre != null) {
                    updateBreathWave(babyBre);
                }
            } else if (kpload.key == 5) { // 呼吸测试关闭返回
                int breathstopresult = kpload.keyValue[0] & 0x0f;
                if (breathstopresult == 0) {
                    Utiliy.reflectTranDataType(mContext, 0);
                } else if (breathstopresult == 1) {
                    Utiliy.reflectTranDataType(mContext, 2);
                }
            } else if (kpload.key == 9) { // 自动化测试命令返回
                if (kpload.keyLen >= 4) {
                    int checkres = kpload.keyValue[0] & 0x01; // 自动化检测结果
                    int acceleration = (kpload.keyValue[1] & 0xff) << 8 | (kpload.keyValue[2] & 0xff); // 加速度值
                    
                    int PNValue = (kpload.keyValue[3] & 0x80) >> 7;
                    int tempHigh = kpload.keyValue[3] & 0x7f;
                    int tempLow = kpload.keyValue[4] & 0xff;
                    
                    String tempString = "";
                    if (PNValue == 1) {
                        tempString = "-" + tempHigh + "." + tempLow;
                    } else {
                        tempString = tempHigh + "." + tempLow;
                    }
                    
                    SLog.e(TAG, "AutoCheckResult = " + checkres 
                            + " Acceleration = " + acceleration
                            + " CurrentTemperature = " + tempString);
                    
                    Intent intent = new Intent(Constant.MANU_TEST_RESULT);
                    intent.putExtra("manu_check_result", checkres);
                    intent.putExtra("manu_acceleration", acceleration);
                    intent.putExtra("manu_temperature", tempString);
                    EventBus.getDefault().post(intent);
                    
                    Utiliy.reflectTranDataType(mContext, 0);
                }
            }
        }
    }

    private void updateBreathWave(BabyBreath babyBreath) {
        EventBus.getDefault().post(babyBreath);
    }


    private BabyBreath getBabyBreath(byte[] keyValue) {
        
        BabyBreath breath = null;
        if (keyValue.length  == 4) {
            breath = new BabyBreath();
            breath.mBreathTime = (keyValue[1] & 0xff) | (((keyValue[0] & 0xff) << 8) & 0xff00);
            breath.mBreathValue = keyValue[2] & 0xff;
            breath.mBreathFreq = keyValue[3] & 0xff;
            SLog.e(TAG, "breathtime = " + breath.mBreathTime 
                    + "  value = " + breath.mBreathValue
                    + " freq = " + breath.mBreathFreq);
            
        }    
        return breath;
    }

    public void handleSettings(List<KeyPayload> params) {
        try {
            for (KeyPayload kpload : params) {
                if (kpload.key == 4) { // 请求时间返回
                    if (kpload.keyLen == 4) { //时间长度4个字节
                        DeviceTime devTime = new DeviceTime();     
                        BitSet bSet = BitSetConvert.byteArray2BitSet(kpload.keyValue);
                        devTime.year = BitSetConvert.getTimeValue(bSet, 0, 6);
                        devTime.month = BitSetConvert.getTimeValue(bSet, 6, 4);
                        devTime.day = BitSetConvert.getTimeValue(bSet, 10, 5);
                        devTime.hour = BitSetConvert.getTimeValue(bSet, 15, 5);
                        devTime.min = BitSetConvert.getTimeValue(bSet, 20, 6);
                        devTime.second = BitSetConvert.getTimeValue(bSet, 26, 6);
                       
                        
                        String  curDeviceTime = "Current Device Time : "
                                + (devTime.year  + 2000)
                                + "年" + devTime.month
                                + "月" + devTime.day
                                + "日" + devTime.hour
                                + "点" + devTime.min
                                + "分" + devTime.second
                                + "秒";
                        
                        Intent intent = new Intent(Constant.DATA_TRANSFER_TIME);
                        intent.putExtra("device_time", curDeviceTime);
                        EventBus.getDefault().post(intent); 
                        
                        
                        //请求时间成功
                        Utiliy.reflectTranDataType(mContext, 0);
                        SLog.e(TAG, curDeviceTime);   
                        Utiliy.dataToFile(curDeviceTime);  
                    }
                } else if (kpload.key == 2) { //设置时间返回结果
                    if (kpload.keyLen == 1) {
                        int settimeresult = kpload.keyValue[0] & 0x0f;
                        PrivateParams.setSPInt(mContext, "SetTimeinfo" , 1);
                        
                        String result =  "settimeresult = " + settimeresult;
                        if (settimeresult == 0 || settimeresult == 1) {
                            //设置时间成功
                            Utiliy.reflectTranDataType(mContext, 0);
                            SLog.e(TAG, " mIsDeviceTimed = " + mIsDeviceTimed 
                                     + " mIsDeviceActivited = " + mIsDeviceActivited);
                            if (!mIsDeviceTimed && mIsDeviceActivited) {
                                mIsDeviceTimed = true;
                                // 设置检查设备状态，成功
                                PrivateParams.setSPInt(mContext, "check_device_status", 3);
                                Intent intent = new Intent(Constant.ACTION_CHECKDEVICE_SUCCEED);
                                intent.putExtra(Constant.IS_SYNC_DATA, mIsSyncData);
                                EventBus.getDefault().post(intent); 
                            }
                        } else if (settimeresult == 2) {
                            //设置时间失败
                            Utiliy.reflectTranDataType(mContext, 2);
                          /*  if (!mIsDeviceTimed) { // 设置时间失败不应该阻塞后续的流程
                                Intent intent = new Intent(Constant.ACTION_CHECKDEVICE_FAILED);
                                EventBus.getDefault().post(intent); 
                            }*/
                        }
                        SLog.e(TAG, result);
                        Utiliy.dataToFile(result);
                    }
                } else if (kpload.key == 6) { // 激活设备返回
                    if (kpload.keyLen == 1) {
                        int activateresult = kpload.keyValue[0] & 0x0f;
                        String result = null;
                        if (activateresult == 0) { // 激活设备成功
                            PrivateParams.setSPInt(mContext, Constant.ACTIVATE_RESULT, 1);
                            result =  "Activate device success "; //激活设备成功
                            Utiliy.reflectTranDataType(mContext, 0);
                            mIsDeviceActivited = true;
                            if (mIsDeviceTimed) {      
                                // 设置检查设备状态，成功
                                PrivateParams.setSPInt(mContext, "check_device_status", 3);
                                Intent intent = new Intent(Constant.ACTION_CHECKDEVICE_SUCCEED);
                                intent.putExtra(Constant.IS_SYNC_DATA, mIsSyncData);
                                EventBus.getDefault().post(intent); 
                            }
                        } else { // 激活设备失败
                            PrivateParams.setSPInt(mContext, Constant.ACTIVATE_RESULT, 0);
                            result =  "Activate Device Failed";
                            //激活设备失败
                            Utiliy.reflectTranDataType(mContext, 3); // 返回错误并清理命令，激活失败同身份验证失败
                            if (mIsDeviceTimed && !mIsDeviceActivited) {
                                Intent intent = new Intent(Constant.ACTION_CHECKDEVICE_FAILED);
                                EventBus.getDefault().post(intent); 
                            }
                        }
                        SLog.e(TAG, result);
                        Utiliy.dataToFile(result);
                    }
                } else if (kpload.key == 18) { // 温度范围设置返回key
                    if (kpload.keyLen == 1) {
                        int settempalarmresult = kpload.keyValue[0] & 0x0f;
                        if (settempalarmresult == 0) { // 设置成功
                            Toast.makeText(mContext.getApplicationContext(), "设置温度报警值成功", Toast.LENGTH_SHORT).show();
                            SLog.e(TAG, "set temperature ALARM succeed");
                           
                            String newalarmvalue = PrivateParams.getSPString(mContext, Constant.DATA_TEMP_ALARM_VALUE_NEW);
                            if (TextUtils.isEmpty(newalarmvalue)) {
                                PrivateParams.setSPString(mContext, Constant.DATA_TEMP_ALARM_VALUE_NEW, "37.5");
                            } else {
                                PrivateParams.setSPString(mContext, Constant.DATA_TEMP_ALARM_VALUE_OLD, newalarmvalue);
                            }
                            return;
                        } else if (settempalarmresult == 1) {
                            Toast.makeText(mContext, "设置温度报警值不合法", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(mContext, "设置温度报警值失败", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "设置温度报警值失败", Toast.LENGTH_SHORT).show();
                    }
                    String oldalarmvalue = PrivateParams.getSPString(mContext, Constant.DATA_TEMP_ALARM_VALUE_OLD);
                    if (!TextUtils.isEmpty(oldalarmvalue)) {
                        PrivateParams.setSPString(mContext, Constant.DATA_TEMP_ALARM_VALUE_NEW, oldalarmvalue);
                    }    
                }
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }


    private List<KeyPayload> getKeyPayloadList(byte[] payload) {
        List<KeyPayload> params = new ArrayList<KeyPayload>();
        ByteArrayInputStream settingInputStream = new ByteArrayInputStream(payload);
        mDis = new DataInputStream(settingInputStream);
        KeyPayload keyPd = new KeyPayload();
        
        try {
            while (mDis.available() > 0) {
                keyPd.key = (short) (mDis.readByte() & 0xff);
                keyPd.keyLen = mDis.readShort();
                keyPd.keyValue = new byte[keyPd.keyLen];
                mDis.read(keyPd.keyValue, 0, keyPd.keyLen);
              
                params.add(keyPd);
             }
        } catch (IOException e) {
            SLog.e(TAG, e);
        }

        return params;
    }
}
