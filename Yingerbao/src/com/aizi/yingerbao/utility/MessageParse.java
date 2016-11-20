package com.aizi.yingerbao.utility;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.aizi.yingerbao.baseheader.BaseL2Message;
import com.aizi.yingerbao.baseheader.KeyPayload;
import com.aizi.yingerbao.breath.BabyBreath;
import com.aizi.yingerbao.command.CommandCenter;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.database.BreathStopInfo;
import com.aizi.yingerbao.database.DevCheckInfo;
import com.aizi.yingerbao.database.ExceptionEvent;
import com.aizi.yingerbao.database.SleepInfo;
import com.aizi.yingerbao.database.TemperatureInfo;
import com.aizi.yingerbao.database.YingerbaoDatabase;
import com.aizi.yingerbao.deviceinterface.AsyncDeviceFactory;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.synctime.DeviceTime;

import de.greenrobot.event.EventBus;

public class MessageParse {
    
    private static final String TAG = MessageParse.class.getSimpleName();
    
    private DataInputStream mDis;
    int mBreathStartResult;
    int mBreathCount;
    Context mContext;
    
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
        SLog.e(TAG, "bsl2Msg = " + Arrays.toString(bsl2Msg.toByte()));
        handleL2Msg(bsl2Msg);
    }


    private void handleL2Msg(BaseL2Message bMsg) {
        
        String l2payload = printHexString(bMsg.toByte());
        SLog.e(TAG, "HEX string l2load1 = " + l2payload);
        Utiliy.logToFile(" L2 " + " RECV " + l2payload);// 写入本地日志文件
        
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
    
    private void sendCallback(Context context, Intent intent, int sendCode, int resultCode) {
        intent.putExtra(Constant.REQUEST_KEY_SEND_CODE, sendCode);
        //intent.setClass(context, CommandService.class);
        intent.putExtra(Constant.REQUEST_KEY_RESULT_CODE,
                resultCode);
        context.startService(intent);
    }
    
    private void handleBind(List<KeyPayload> params) {
        try {
            for (KeyPayload kpload:params) {
                if (kpload.key == 6) { // 收到连接合法性返回
                    SLog.e(TAG, "receive  check device data");
                    if (kpload.keyLen == 8) {
                        handlecheckinfo(kpload.keyValue); // 分析校验返回
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
            
            devCheckInfo.mNoSyncDataLength = ((keyValue[4] << 8) & 0xff00) | (keyValue[5] & 0xff);
            devCheckInfo.mDeviceCharge = keyValue[6] & 0xff;
            devCheckInfo.mDeviceStatus = keyValue[7];
            
            PrivateParams.setSPInt(mContext, "NoSyncDataLength", devCheckInfo.mNoSyncDataLength);
            PrivateParams.setSPInt(mContext, "GetCheckinfo", 1);
            
            String result = "Check Device return mNoSyncDataLength = " + devCheckInfo.mNoSyncDataLength
                    + " mDeviceCharge = " + devCheckInfo.mDeviceCharge
                    + " mDeviceStatus = " + (int)devCheckInfo.mDeviceStatus;
            
            SLog.e(TAG, result);
            
            Utiliy.dataToFile(result);
            
           // AsyncDeviceFactory.getInstance(mContext).activateDevice();
           // AsyncDeviceFactory.getInstance(mContext).getDeviceTime();
            //setDeviceTime(devCheckInfo);
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
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
                    || Math.abs(min - devCheckInfo.mCheckInfoMinute) > 5) {
                AsyncDeviceFactory.getInstance(mContext).setDeviceTime();
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }


    /**
     * 将指定byte数组以16进制的形式打印到控制台
     * 
     * @param hint
     *            String
     * @param b
     *            byte[]
     * @return void
     */
    public static String printHexString(byte[] b)
    {
        String hexString = "";
        for (int i = 0; i < b.length; i++)
        {
            String hex = Integer.toHexString(b[i] & 0xFF);
            if (hex.length() == 1)
            {
                hex = '0' + hex;
            }
            
            hexString += " " + hex;
        }
        return hexString;
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
        // TODO Auto-generated method stub
        SLog.e(TAG, "handleData List<KeyPayload> params");
        for (KeyPayload kpload:params) {
            if (kpload.key == 2) { // 接收到实时数据
                SLog.e(TAG, "receive  realtime data");
                if (kpload.keyLen == 4) {
                    acquireTemp(kpload.keyValue); // 分析实时温度数据
                }
            } else if (kpload.key == 5) {
                SLog.e(TAG, "receive data complete " + kpload.keyLen);
            } else if (kpload.key == 6) {
                SLog.e(TAG, "receiver sleep data " + kpload.keyLen);
                handleSleepData(kpload.keyValue);
            } else if (kpload.key == 7) { //温度数据
                SLog.e(TAG, "receiver temp data " + + kpload.keyLen);
                handleTempData(kpload.keyValue);
            } else if (kpload.key == 8) { //湿度数据
                SLog.e(TAG, "receiver humit data " + + kpload.keyLen);
                handleHumbitData(kpload.keyValue);
            } else if (kpload.key == 10) { // 实时温度数据
                SLog.e(TAG, "receiver realtime temp data " + + kpload.keyLen);
                handleRealTimeTemperatureData(kpload.keyValue);
            } else if (kpload.key == 12) { // 呼吸停滞数据
                SLog.e(TAG, "receiver breath stop data " + + kpload.keyLen);
                handleBreathStopData(kpload.keyValue);
            } else if (kpload.key == 13) { // 呼吸停滞数据结束
                SLog.e(TAG, "receiver breath stop data completed " + + kpload.keyLen);
            } else if (kpload.key == 15) {
                handleExceptionData(kpload.keyValue);
            }
        }
    }


    private void handleExceptionData(byte[] keyValue) {
        ExceptionEvent exEvent = new ExceptionEvent();
        String exceptionlog = printHexString(keyValue);
        SLog.e(TAG, "Exception Log = " + exceptionlog);
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
                Intent intent = new Intent(Constant.DATA_TRANSFER_RECEIVE);
                intent.putExtra("transferdata", logStr);
                EventBus.getDefault().post(intent); // 显示在界面上  
            }
        }
    }


    private void handleRealTimeTemperatureData(byte[] keyValue) {
        int PNValue = (keyValue[0] & 0x80) >> 7;
        int tempHigh = keyValue[0] & 0x7f;
        int tempLow = keyValue[1] & 0xff;
        
        String tempString;
        if (PNValue == 1) {
            SLog.e(TAG, "temp = " + "-" + tempHigh + "." + tempLow);
            tempString = "-" + tempHigh + "." + tempLow;
        } else {
            SLog.e(TAG, "temp = " + tempHigh + "." + tempLow);
            tempString = tempHigh + "." + tempLow;
        }
        Intent intent = new Intent(Constant.DATA_REALTIME_TEMPERATURE);
        intent.putExtra("realtime_temperature", tempString);
        EventBus.getDefault().post(intent);
        
        String result = "Receiver RealTime Temperature = " + tempString;
        SLog.e(TAG, result);
        Utiliy.dataToFile(result);
        
    }


    private void handleBreathStopData(byte[] keyValue) {
        BreathStopInfo breathStopInfo = new BreathStopInfo();
        int breathstoplength = keyValue.length;
        boolean breathalarm = false;
        if (breathstoplength % 8 == 0) {
            for (int i = 0; i < keyValue.length/8; i++) {
            
                int year = (keyValue[i*8] & 0xfc) >> 2;
                int month = ((keyValue[i*8] & 0x03) << 2) | ((keyValue[1+i*8] & 0xc0) >> 6);
                int day = (keyValue[1+i*8] & 0x3e) >> 1;
                int hour = ((keyValue[1+i*8] & 0x01)  << 4) | ((keyValue[2+i*8] & 0xf0) >> 4);
                int minu = ((keyValue[2+i*8] & 0x0f) << 2) | (((keyValue[3+i*8] & 0xc0) >> 6) & 0x03);
                int second = (keyValue[3+i*8] & 0x3f);

                breathStopInfo.mBreathYear = year + 2000;
                breathStopInfo.mBreathMonth = month;
                breathStopInfo.mBreathDay = day;
                breathStopInfo.mBreathHour = hour;
                breathStopInfo.mBreathMinute = minu;
                breathStopInfo.mBreathSecond = second;
                
                int isAlarm = (keyValue[4 + i*8] & 0xff);
                if (isAlarm == 1) {
                    breathStopInfo.mBreathIsAlarm = isAlarm;
                    breathalarm = true;
                } else {
                    breathStopInfo.mBreathIsAlarm = 0;
                    breathalarm = false;
                }
                
                breathStopInfo.mBreathDuration = keyValue[5 + i*8] & 0xff;
                breathStopInfo.mBreathTimestamp = System.currentTimeMillis();
                
                SLog.e(TAG, "breathstop  year = " + year 
                        + " month = " + month
                        + " day = " + day
                        + " hour = " + hour
                        + " minu = " + minu
                        + " second = " + second
                        + " isAlarm = " + isAlarm
                        + " duration = " + breathStopInfo.mBreathDuration);
                
                
                String breathstop = "Breath Stop Info : " 
                                   + breathStopInfo.mBreathYear + "-" 
                                   + breathStopInfo.mBreathMonth + "-"
                                   + breathStopInfo.mBreathDay + "-"
                                   + breathStopInfo.mBreathHour + "-"
                                   + breathStopInfo.mBreathMinute + "-"
                                   + breathStopInfo.mBreathSecond 
                                   + " BreathAlarm = " + breathalarm; 
                Utiliy.dataToFile(breathstop);
                YingerbaoDatabase.insertBreathInfo(mContext, breathStopInfo);
            }
        }
    }


    private void handleHumbitData(byte[] keyValue) {
        // TODO Auto-generated method stub
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
        TemperatureInfo temperatureinfo = new TemperatureInfo();
        int year = ((keyValue[0] & 0x7e) >> 1) &  0x3f;
        int month = ((keyValue[0] & 0x01) << 3)  |  ((keyValue[1] & 0xe0) >> 5);
        int day = keyValue[1] & 0x1f;
        int minu = ((((keyValue[2] & 0xff) << 8) & 0xff00) + (keyValue[3] & 0x0ff)) & 0x0fff;
        int tempcount = keyValue[4] << 8 | keyValue[5];
        
        SLog.e(TAG, "Temp year = " + year 
                + " month = " + month 
                + " day = " + day 
                + " minu = " + minu 
                + " sleepcount = " + tempcount );
        
        temperatureinfo.mTemperatureYear = year + 2000;
        temperatureinfo.mTemperatureMonth = month;
        temperatureinfo.mTemperatureDay = day;
        
        for (int i = 0; i < tempcount; i++) {
            
            int PNValue = (keyValue[6+i*2] & 0x80) >> 7;
            int tempHigh = keyValue[6+i*2] & 0x7f;
            int tempLow = keyValue[7+i*2] & 0xff;
            
            String tempString;
            if (PNValue == 1) {
                SLog.e(TAG, "temp = " + "-" + tempHigh + "." + tempLow);
                tempString = "-" + tempHigh + "." + tempLow;
            } else {
                SLog.e(TAG, "temp = " + tempHigh + "." + tempLow);
                tempString = tempHigh + "." + tempLow;
            }
            
            temperatureinfo.mTemperatureMinute = minu + i * 10;
            temperatureinfo.mTemperatureValue = tempString;
            temperatureinfo.mTemperatureTimestamp = System.currentTimeMillis();
            
            String tempinfo = "Device Time : " + temperatureinfo.mTemperatureYear 
                    + "-" + temperatureinfo.mTemperatureMonth 
                    + "-" + temperatureinfo.mTemperatureDay 
                    + "-" + temperatureinfo.mTemperatureMinute 
                    + " tempValue = " + temperatureinfo.mTemperatureValue;  
            Utiliy.dataToFile(tempinfo);
            
            YingerbaoDatabase.insertTemperatureInfo(mContext, temperatureinfo);
        }
    }


    private void handleSleepData(byte[] keyValue) {
        // TODO Auto-generated method stub
        if (keyValue.length > 6) {
            List<SleepInfo> sleepInfos = getSleepInfoList(keyValue);
            /*for (SleepInfo sleepinfo : sleepInfos) {
                YingerbaoDatabase.insertSleepInfo(mContext, sleepinfo);
            }*/
        } else {
            SLog.e(TAG, "There is no Sleep Data");
        }
    }


    private List<SleepInfo> getSleepInfoList(byte[] keyValue) {
        List<SleepInfo> sleepInfos = new ArrayList<SleepInfo>();
        
        int year = ((keyValue[0] & 0x7e) >> 1) &  0x3f;
        int month = ((keyValue[0] & 0x01) << 3)  |  ((keyValue[1] & 0xe0) >> 5);
        int day = keyValue[1] & 0x1f;
        //int minu = keyValue[2] << 8 | keyValue[3];
        int minu = ((((keyValue[2] & 0xff) << 8) & 0xff00) + (keyValue[3] & 0x0ff)) & 0x0fff;
        int sleepcount = keyValue[4] << 8 | keyValue[5];
        
        SleepInfo sleepInfo = new SleepInfo();
        if (keyValue.length == 6 + sleepcount) {
            if (sleepcount <= 1440) {
                SLog.e(TAG, "sleep date  year = " + year 
                        + " month = " + month 
                        + " day = " + day 
                        + " min = " + minu);
                for (int i = 0; i < sleepcount; i++) {
                   
                   sleepInfo.mSleepTimestamp = System.currentTimeMillis();
                   sleepInfo.mSleepYear = year;
                   sleepInfo.mSleepMonth = month;
                   sleepInfo.mSleepDay = day;
                   sleepInfo.mSleepMinute = minu+i;
                   sleepInfo.mSleepValue = keyValue[6+i] & 0x0ff;
                   
                   YingerbaoDatabase.insertSleepInfo(mContext, sleepInfo);
                   sleepInfos.add(sleepInfo);
                   SLog.e(TAG, "sleep date  year = " + year 
                           + " month = " + month 
                           + " day = " + day 
                           + " min = " + minu
                           + "  sleep value = " + (keyValue[6+i] & 0x0ff));
               }
           } 
        }
        return sleepInfos;
    }

    private void acquireTemp(byte[] keyValue) {
        // TODO Auto-generated method stub
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
        
        String result = "Receiver RealTime Temperature = " + tempString;
        SLog.e(TAG, result);
        Utiliy.dataToFile(result);
    }


    private void handleNotify(List<KeyPayload> params) {
        for (KeyPayload kpload:params) {
           /* if (kpload.key == 1) { // 正反状态报警
                SLog.e(TAG, "正反状态 ALARM");
                if (kpload.keyLen == 1) {
                    //mBreathStartResult = kpload.keyValue[0] & 0x0f;
                }
            }*/ 
            if (kpload.key == 2) { // 温度报警
                SLog.e(TAG, "TEMP ALARM");
                String babytemp = getBabyTemp(kpload.keyValue);
                
                Utiliy.showFeverNotification(mContext, 
                        "孩子发烧了！！", "孩子发烧了，"+"当前体温： " + babytemp + " 请及时就医。", null);
                
            } else if (kpload.key == 3) { // 呼吸停滞报警
                SLog.e(TAG, "BREATH ALARM");
                Utiliy.showFeverNotification(mContext, 
                        "呼吸停滞！！", "孩子呼吸停滞了， 请及时就医。", null);
            }
        }
    }


    private String getBabyTemp(byte[] keyValue) {
        // TODO Auto-generated method stub
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
        // TODO Auto-generated method stub
        for (KeyPayload kpload:params) {
            if (kpload.key == 2) { // 呼吸测试启动返回
                SLog.e(TAG, "START BREATH RETURN");
                if (kpload.keyLen == 1) {
                    mBreathStartResult = kpload.keyValue[0] & 0x0f;
                }
            } else if (kpload.key == 3) { // 呼吸测试数据
                SLog.e(TAG, "START REAL BREATH DATA");
                BabyBreath babyBre = getBabyBreath(kpload.keyValue);
                if (babyBre != null) {
                    updateBreathWave(babyBre);
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
        // TODO Auto-generated method stub 
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
                        
                        //setDeviceTime(devTime);
                        
                        String  curDeviceTime = "get Current Device Time : year = "
                                + (devTime.year  + 2000)
                                + " month = " + devTime.month
                                + " day = " + devTime.day
                                + " hour = " + devTime.hour
                                + " min = " + devTime.min
                                + " second = " + devTime.second;
                        
                        CommandCenter.getInstance(mContext).handleIntent(mContext, new Intent());
                        
                        SLog.e(TAG, curDeviceTime);   
                        Utiliy.dataToFile(curDeviceTime);
                        
                        //if (PrivateParams.getSPInt(mContext, "NoSyncDataLength", 0) > 200) {
                         //   AsyncDeviceFactory.getInstance(mContext).getAllNoSyncInfo();
                         //   Thread.sleep(500);
                            //AsyncDeviceFactory.getInstance(mContext).getBreathStopInfo();
                        //}
                    }
                } else if (kpload.key == 2) { //设置时间返回结果
                    if (kpload.keyLen == 1) {
                        int settimeresult = kpload.keyValue[0] & 0x0f;
                        PrivateParams.setSPInt(mContext, "SetTimeinfo" , 1);
                        
                        String result =  "settimeresult = " + settimeresult;
                        SLog.e(TAG, result);
                        Utiliy.dataToFile(result);
                    }
                } else if (kpload.key == 6) { // 激活设备返回
                    if (kpload.keyLen == 1) {
                        int activateresult = kpload.keyValue[0] & 0x0f;
                        if (activateresult == 0) { // 激活设备成功
                            //AsyncDeviceFactory.getInstance(mContext).getDeviceTime();
                            String result =  "activate device success ";
                            SLog.e(TAG, result);
                            Utiliy.dataToFile(result);
                        }
                    }
                }
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }

    private void setDeviceTime(DeviceTime devTime) {
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
            SLog.e(TAG, "devicetime  year = " + devTime.year 
                    + " month = " + devTime.month
                    + " day = " + devTime.day
                    + " hour = " + devTime.hour
                    + " minu = " + devTime.min
                    + " second = " + devTime.second);
            
            if (year != devTime.year
                    || month != devTime.month
                    || day != devTime.day
                    || hour != devTime.hour
                    || Math.abs(min - devTime.min) > 5) {
                AsyncDeviceFactory.getInstance(mContext).setDeviceTime();
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
