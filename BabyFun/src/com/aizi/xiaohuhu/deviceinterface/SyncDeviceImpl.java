package com.aizi.xiaohuhu.deviceinterface;

import java.util.Calendar;

import android.content.Context;
import android.content.Intent;

import com.aizi.xiaohuhu.baseheader.BaseL2Message;
import com.aizi.xiaohuhu.baseheader.KeyPayload;
import com.aizi.xiaohuhu.constant.Constant;
import com.aizi.xiaohuhu.logging.SLog;
import com.aizi.xiaohuhu.service.BluetoothService;
import com.aizi.xiaohuhu.synctime.DeviceTime;
import com.aizi.xiaohuhu.utility.BaseMessageHandler;
import com.aizi.xiaohuhu.utility.PrivateParams;

import de.greenrobot.event.EventBus;

public class SyncDeviceImpl implements SyncDevice{
    
    private static final String TAG = SyncDeviceImpl.class.getSimpleName();
    private BluetoothService mBluetoothService = null;
    private static boolean mIsBluetoothReady;
    private String mAddress;
    Context mContext;

    public SyncDeviceImpl(Context context) {
        // TODO Auto-generated constructor stub
        mContext = context;
        mIsBluetoothReady = false;      
    }
    

    @Override
    public DeviceResponse<?> setDeviceTime() {
        // TODO Auto-generated method stub
        SLog.e(TAG, " setDeviceTime ");
        if (PrivateParams.getSPInt(mContext, Constant.BLUETOOTH_IS_READY, 0) == 1) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 1;
            keyPayload.keyLen = 4; 
            
            DeviceTime dvtm = acqDeviceTime();
            keyPayload.keyValue = dvtm.toByte();  
            
            BaseL2Message bsl2Msg 
            = BaseMessageHandler.generateBaseL2Msg(Constant.COMMAND_ID_SETTING, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            boolean isSendL2Over = BaseMessageHandler.sendL2Message(bsl2Msg);
            mIsBluetoothReady = true;
        }
        
        return null;
    }



    private DeviceTime acqDeviceTime() {
        // TODO Auto-generated method stub
       DeviceTime dvtm = new DeviceTime();
       Calendar calendar = Calendar.getInstance(); 
       
       dvtm.year = calendar.get(Calendar.YEAR) - 2000;
       dvtm.month = calendar.get(Calendar.MONTH)+1;
       dvtm.day = calendar.get(Calendar.DAY_OF_MONTH);
       dvtm.hour = calendar.get(Calendar.HOUR_OF_DAY);
       dvtm.min = calendar.get(Calendar.MINUTE);
       dvtm.second = calendar.get(Calendar.SECOND);
       return dvtm;
    }



    @Override
    public DeviceResponse<?> startSendBreathData() {
        // TODO Auto-generated method stub
        SLog.e(TAG, " startSendBreathData ");
        if (PrivateParams.getSPInt(mContext, Constant.BLUETOOTH_IS_READY, 0) == 1) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 1;
            keyPayload.keyLen = 0;
            
            BaseL2Message bsl2Msg 
            = BaseMessageHandler.generateBaseL2Msg(Constant.COMMAND_ID_MANUFACTURE_TEST, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            boolean isSendL2Over = BaseMessageHandler.sendL2Message(bsl2Msg);
        }
        return null;
    }



    @Override
    public DeviceResponse<?> stopSendBreathData() {
        // TODO Auto-generated method stub
        SLog.e(TAG, " stopSendBreathData ");
        if (PrivateParams.getSPInt(mContext, Constant.BLUETOOTH_IS_READY, 0) == 1) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 4;
            keyPayload.keyLen = 0;
            
            BaseL2Message bsl2Msg 
            = BaseMessageHandler.generateBaseL2Msg(Constant.COMMAND_ID_MANUFACTURE_TEST, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            boolean isSendL2Over = BaseMessageHandler.sendL2Message(bsl2Msg);
        }
        return null;
    }



    @Override
    public DeviceResponse<?> getDeviceTime() {
        // TODO Auto-generated method stub
        SLog.e(TAG, " getDeviceTime ");
        if (PrivateParams.getSPInt(mContext, Constant.BLUETOOTH_IS_READY, 0) == 1) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 3;
            keyPayload.keyLen = 0;
            
            BaseL2Message bsl2Msg 
            = BaseMessageHandler.generateBaseL2Msg(Constant.COMMAND_ID_SETTING, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            boolean isSendL2Over = BaseMessageHandler.sendL2Message(bsl2Msg);
        }
        return null;
    }



    @Override
    public DeviceResponse<?> getBodyTemperature() {
        // TODO Auto-generated method stub
        SLog.e(TAG, " getBodyTemperature ");
        if (PrivateParams.getSPInt(mContext, Constant.BLUETOOTH_IS_READY, 0) == 1) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 1;
            keyPayload.keyLen = 0;
            
            BaseL2Message bsl2Msg 
            = BaseMessageHandler.generateBaseL2Msg(Constant.COMMAND_ID_DATA, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            boolean isSendL2Over = BaseMessageHandler.sendL2Message(bsl2Msg);
        }

        return null;
    }



    @Override
    public DeviceResponse<?> getAllNoSyncInfo() {
        // TODO Auto-generated method stub
        SLog.e(TAG, " getAllNoSyncInfo ");
        if (PrivateParams.getSPInt(mContext, Constant.BLUETOOTH_IS_READY, 0) == 1) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 3;
            keyPayload.keyLen = 0;
            
            BaseL2Message bsl2Msg 
            = BaseMessageHandler.generateBaseL2Msg(Constant.COMMAND_ID_DATA, 
                    Constant.BASE_VERSION_CODE, keyPayload);
        
            boolean isSendL2Over = BaseMessageHandler.sendL2Message(bsl2Msg);
        }

        return null;
    }



    @Override
    public DeviceResponse<?> getAllSyncInfo() {
        // TODO Auto-generated method stub
        SLog.e(TAG, " getAllSyncInfo ");
        if (PrivateParams.getSPInt(mContext, Constant.BLUETOOTH_IS_READY, 0) == 1) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 4;
            keyPayload.keyLen = 0;
            
            BaseL2Message bsl2Msg 
            = BaseMessageHandler.generateBaseL2Msg(Constant.COMMAND_ID_DATA, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            boolean isSendL2Over = BaseMessageHandler.sendL2Message(bsl2Msg);
        }

        return null;
    }

    @Override
    public DeviceResponse<?> getBreahStopInfo() {
        // TODO Auto-generated method stub
        SLog.e(TAG, " getBreahStopInfo ");
        if (PrivateParams.getSPInt(mContext, Constant.BLUETOOTH_IS_READY, 0) == 1) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 11;
            keyPayload.keyLen = 0;
            
            BaseL2Message bsl2Msg 
            = BaseMessageHandler.generateBaseL2Msg(Constant.COMMAND_ID_DATA, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            boolean isSendL2Over = BaseMessageHandler.sendL2Message(bsl2Msg);
        }
        return null;
    }

}
