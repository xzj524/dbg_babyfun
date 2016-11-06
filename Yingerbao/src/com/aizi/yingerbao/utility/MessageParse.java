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

import android.R.integer;
import android.content.Context;
import android.content.Intent;

import com.aizi.yingerbao.baseheader.BaseL2Message;
import com.aizi.yingerbao.baseheader.KeyPayload;
import com.aizi.yingerbao.breath.BabyBreath;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.database.BreathStopInfo;
import com.aizi.yingerbao.database.ExceptionEvent;
import com.aizi.yingerbao.database.SleepInfo;
import com.aizi.yingerbao.database.YingerbaoDatabase;
import com.aizi.yingerbao.database.TemperatureInfo;
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
        // TODO Auto-generated method stub
        
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
           // System.out.print(hex.toUpperCase() + " ");
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
                    //mBreathStartResult = kpload.keyValue[0] & 0x0f;
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
            } 
            else if (kpload.key == 12) { // 呼吸停滞数据
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
                Utiliy.logToFile(logStr);
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
        
    }


    private void handleBreathStopData(byte[] keyValue) {
        BreathStopInfo breathStopInfo = new BreathStopInfo();
        int breathstoplength = keyValue.length;
        boolean breathalarm = false;
        if (breathstoplength % 4 == 0) {
            for (int i = 0; i < keyValue.length/4; i++) {
                int isAlarm = (keyValue[i*4] & 0x80) >> 7;
                if (isAlarm == 1) {
                    breathStopInfo.mBreathIsAlarm = isAlarm;
                    breathalarm = true;
                } else {
                    breathStopInfo.mBreathIsAlarm = 0;
                    breathalarm = false;
                }
                
                int year = (keyValue[i*4] & 0x7c) >> 2;
                int month = ((keyValue[i*4] & 0x03) << 2) | ((keyValue[1+i*4] & 0xc0) >> 6);
                int day = (keyValue[1+i*4] & 0x3e) >> 1;
                int hour = ((keyValue[1+i*4] & 0x01)  << 4) | ((keyValue[2+i*4] & 0xf0) >> 4);
                int minu = ((keyValue[2+i*4] & 0x0f) << 2) | ((keyValue[3+i*4] & 0xc0) >> 2);
                int second = (keyValue[3+i*4] & 0x3f);
                
                SLog.e(TAG, "breathstop  year = " + year 
                        + " month = " + month
                        + " day = " + day
                        + " hour = " + hour
                        + " minu = " + minu
                        + " second = " + second
                        + " isAlarm = " + isAlarm);
                
                breathStopInfo.mBreathYear = year;
                breathStopInfo.mBreathMonth = month;
                breathStopInfo.mBreathDay = day;
                breathStopInfo.mBreathHour = hour;
                breathStopInfo.mBreathMinute = minu;
                breathStopInfo.mBreathSecond = second;
                
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
            
            String tempinfo = "Time :" + temperatureinfo.mTemperatureMinute + " tempValue = " 
                              + temperatureinfo.mTemperatureValue;  
            Utiliy.dataToFile(tempinfo);
            Utiliy.temperatureToFile(temperatureinfo.mTemperatureValue + "");
            
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
    }


    private void handleNotify(List<KeyPayload> params) {
        for (KeyPayload kpload:params) {
            if (kpload.key == 1) { // 正反状态报警
                SLog.e(TAG, "正反状态 ALARM");
                if (kpload.keyLen == 1) {
                    //mBreathStartResult = kpload.keyValue[0] & 0x0f;
                }
            } else if (kpload.key == 2) { // 温度报警
                SLog.e(TAG, "TEMP ALARM");
                int babytemp = getBabyTemp(kpload.keyValue);
                
                Utiliy.showFeverNotification(mContext, 
                        "孩子发烧了！！", "孩子发烧了，"+"当前体温： " + babytemp + " 请及时就医。", null);
                
            } else if (kpload.key == 3) { // 呼吸停滞报警
                SLog.e(TAG, "BREATH ALARM");
                Utiliy.showFeverNotification(mContext, 
                        "呼吸停滞！！", "孩子呼吸停滞了， 请及时就医。", null);
            }
        }
    }


    private int getBabyTemp(byte[] keyValue) {
        // TODO Auto-generated method stub
        return 0;
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
                ArrayList<BabyBreath> babyBreaths = getBabyBreaths(kpload.keyValue);
                if (babyBreaths != null) {
                    updateBreathWave(babyBreaths);
                }
            }
        }
    }


    private void updateBreathWave(ArrayList<BabyBreath> babyBreaths) {
        // TODO Auto-generated method stub
        EventBus.getDefault().post(babyBreaths);
    }


    private ArrayList<BabyBreath> getBabyBreaths(byte[] keyValue) {
        // TODO Auto-generated method stub
        ArrayList<BabyBreath> babyBreaths = new ArrayList<BabyBreath>();
        int BreathCount = keyValue[0] & 0xff;
        if (BreathCount <= 0 || keyValue.length < 4) {
            return null;
        }
        for (int i = 0; i < BreathCount; i++) {
            BabyBreath breath = new BabyBreath();
            byte[] tmp = new byte[3];
            System.arraycopy(keyValue, i+1 + 3*i, tmp, 0, 3);
            breath.mBreathValue = tmp[2] & 0xff;
            breath.mBreathTime = (tmp[1] & 0xff) | (((tmp[0] & 0xff) << 8) & 0xff00);
            babyBreaths.add(breath);
        }
        return babyBreaths;
    }

    public static void handleSettings(List<KeyPayload> params) {
        // TODO Auto-generated method stub 
        for (KeyPayload kpload : params) {
            if (kpload.key == 4) { // 请求时间返回
                if (kpload.keyLen == 4) { //时间长度4个字节
                    DeviceTime devTime = new DeviceTime();     
                    BitSet bSet = BitSetConvert.byteArray2BitSet(kpload.keyValue);
                    devTime.year = BitSetConvert.getTimeValue(bSet, 0, 6) + 2000;
                    devTime.month = BitSetConvert.getTimeValue(bSet, 6, 4);
                    devTime.day = BitSetConvert.getTimeValue(bSet, 10, 5);
                    devTime.hour = BitSetConvert.getTimeValue(bSet, 15, 5);
                    devTime.min = BitSetConvert.getTimeValue(bSet, 20, 6);
                    devTime.second = BitSetConvert.getTimeValue(bSet, 26, 6);
                    
                    Calendar calendar = Calendar.getInstance(); 
                   
                    calendar.set(devTime.year, devTime.month, devTime.day,
                            devTime.hour, devTime.min, devTime.second);
                    calendar.getTime().getTime();
                    
                    SLog.e(TAG, "year = " + devTime.year 
                            + " month = " + devTime.month
                            + " day = " + devTime.day
                            + " hour = " + devTime.hour
                            + " min = " + devTime.min
                            + " second = " + devTime.second);                     
                }
            } else if (kpload.key == 2) { //设置时间返回结果
                if (kpload.keyLen == 1) {
                    int settimeresult = kpload.keyValue[0] & 0x0f;
                    SLog.e(TAG, "settimeresult = " + settimeresult);
                }
            }
        }
    }

    private List<KeyPayload> getKeyPayloadList(byte[] payload) {
        // TODO Auto-generated method stub
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
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return params;
    }
}
