package com.xzj.babyfun.utility;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.Intent;

import com.xzj.babyfun.baseheader.BaseL2Message;
import com.xzj.babyfun.baseheader.KeyPayload;
import com.xzj.babyfun.constant.Constant;
import com.xzj.babyfun.deviceinterface.AsyncDeviceFactory;
import com.xzj.babyfun.synctime.DeviceTime;

import de.greenrobot.event.EventBus;

public class MessageParse {
    
    private DataInputStream mDis;
    
    /** single instance. */
    private static volatile MessageParse mInstance;   
    protected MessageParse(Context context) {
        EventBus.getDefault().register(this); 
    }

    
    public static MessageParse getInstance(Context context) {
        if (mInstance != null) {
            return mInstance;
        } else {
            mInstance = new MessageParse(context);
            return mInstance;
        }
    }
    
    public void onEvent(Intent intent) {
        Object parcel = intent.getParcelableExtra(Constant.BASE_L2_MESSAGE);
        BaseL2Message bMsg = null;
        if (parcel != null && (parcel instanceof BaseL2Message)) {
            bMsg = (BaseL2Message) parcel;
        }
        
        if (bMsg == null) {
            return;
        }
        
        handleL2Msg(bMsg);
        
        
        
        //Toast.makeText(mContext, "123456", Toast.LENGTH_SHORT).show();
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

            default:
                break;
            }
        }
                
      
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
