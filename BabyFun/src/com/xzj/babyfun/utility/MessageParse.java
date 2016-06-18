package com.xzj.babyfun.utility;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.content.Intent;

import com.xzj.babyfun.baseheader.BaseL2Message;
import com.xzj.babyfun.baseheader.KeyPayload;
import com.xzj.babyfun.breath.BabyBreath;
import com.xzj.babyfun.constant.Constant;
import com.xzj.babyfun.deviceinterface.AsyncDeviceFactory;
import com.xzj.babyfun.logging.SLog;
import com.xzj.babyfun.synctime.DeviceTime;

import de.greenrobot.event.EventBus;

public class MessageParse {
    
    private DataInputStream mDis;
    int mBreathStartResult;
    int mBreathCount;
    
    /** single instance. */
    private static volatile MessageParse mInstance;   
    protected MessageParse(Context context) {
        EventBus.getDefault().register(this); 
        mBreathStartResult = 1;
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
        SLog.e("breathtest", "handleL2Msg L2 DATA");
       /* Object parcel = intent.getParcelableExtra(Constant.BASE_L2_MESSAGE);
        BaseL2Message bMsg = null;
        if (parcel != null && (parcel instanceof BaseL2Message)) {
            bMsg = (BaseL2Message) parcel;
        }
        
        if (bMsg == null) {
            return;
        }*/
        
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
                
                break;
            case Constant.COMMAND_ID_MANUFACTURE_TEST:
                handleManufature(params);
                break;


            default:
                break;
            }
        }
                
      
    }


    private void handleManufature(List<KeyPayload> params) {
        // TODO Auto-generated method stub
        for (KeyPayload kpload:params) {
            if (kpload.key == 2) { // 呼吸测试启动返回
                SLog.e("breathtest", "START BREATH RETURN");
                if (kpload.keyLen == 1) {
                    mBreathStartResult = kpload.keyValue[0] & 0x0f;
                }
            } else if (kpload.key == 5) { // 呼吸测试数据
                SLog.e("breathtest", "START REAL BREATH DATA");
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
                    
                   // AsyncDeviceFactory.getInstance()
                                       
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
