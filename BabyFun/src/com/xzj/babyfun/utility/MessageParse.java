package com.xzj.babyfun.utility;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.List;

import android.content.Context;

import com.xzj.babyfun.baseheader.BaseL2Message;
import com.xzj.babyfun.baseheader.KeyPayload;
import com.xzj.babyfun.breath.BabyBreath;
import com.xzj.babyfun.constant.Constant;
import com.xzj.babyfun.logging.SLog;
import com.xzj.babyfun.synctime.DeviceTime;

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
        SLog.e(TAG, "handleL2Msg L2 DATA");
        handleL2Msg(bsl2Msg);
    }


    private void handleL2Msg(BaseL2Message bMsg) {
        // TODO Auto-generated method stub
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


    private void handleData(List<KeyPayload> params) {
        // TODO Auto-generated method stub
        for (KeyPayload kpload:params) {
            if (kpload.key == 2) { // 接收到实时数据
                SLog.e(TAG, "receive  realtime data");
                if (kpload.keyLen == 4) {
                    acquireTemp(kpload.keyValue); //分析实时数据
                    //mBreathStartResult = kpload.keyValue[0] & 0x0f;
                }
            } 
        }
    }


    private void acquireTemp(byte[] keyValue) {
        // TODO Auto-generated method stub
        int PNValue = (keyValue[0] & 0x80) >> 7;
        int tempHigh = keyValue[0] & 0x7f;
        int tempLow = keyValue[1] & 0xff;
        
        int humbit = keyValue[2] & 0xff;
        int energy = keyValue[3] & 0xff;
        
        if (PNValue == 1) {
            SLog.e(TAG, "temp = " + "-" + tempHigh + "." + tempLow);
        } else {
            SLog.e(TAG, "temp = " + tempHigh + "." + tempLow);
        }
       
        SLog.e(TAG, "humbit = " + humbit);
        SLog.e(TAG, "energy = " + energy);
    }


    private void handleNotify(List<KeyPayload> params) {
        // TODO Auto-generated method stub
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
            } else if (kpload.key == 5) { // 呼吸测试数据
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
