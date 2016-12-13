package com.aizi.yingerbao.deviceinterface;

import java.util.Calendar;

import android.content.Context;
import android.text.TextUtils;

import com.aizi.yingerbao.baseheader.BaseL2Message;
import com.aizi.yingerbao.baseheader.KeyPayload;
import com.aizi.yingerbao.command.CommandSendRequest;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.synctime.DeviceTime;
import com.aizi.yingerbao.utility.PrivateParams;
import com.aizi.yingerbao.utility.Utiliy;

public class SyncDeviceImpl implements SyncDevice{
    
    private final String TAG = SyncDeviceImpl.class.getSimpleName();
    Context mContext;

    public SyncDeviceImpl(Context context) {
        mContext = context;
    }
    
    @Override
    public DeviceResponse<?> updateDeviceRom() {
        SLog.e(TAG, " updateDeviceRom ");
        if (Utiliy.isBluetoothConnected(mContext)) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 3;
            keyPayload.keyLen = 0; 

            BaseL2Message bsl2Msg 
            = Utiliy.generateBaseL2Msg(Constant.COMMAND_ID_UPDATE_ROM, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            new CommandSendRequest(mContext, bsl2Msg).addSendTask();
            String str = "updateDeviceRom";
            Utiliy.dataToFile(str);
        }
        
        return null;
    }
    

    @Override
    public DeviceResponse<?> setDeviceTime() {
        SLog.e(TAG, " setDeviceTime ");
        if (Utiliy.isBluetoothConnected(mContext)) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 1;
            keyPayload.keyLen = 4; 
            
            DeviceTime dvtm = acqDeviceTime();
            keyPayload.keyValue = dvtm.toByte();  
            
            BaseL2Message bsl2Msg 
            = Utiliy.generateBaseL2Msg(Constant.COMMAND_ID_SETTING, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            new CommandSendRequest(mContext, bsl2Msg).addSendTask();
            String str = "setDeviceTime";
            Utiliy.dataToFile(str);
        }
        
        return null;
    }



    private DeviceTime acqDeviceTime() {
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
        SLog.e(TAG, " startSendBreathData ");
        if (Utiliy.isBluetoothConnected(mContext)) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 1;
            keyPayload.keyLen = 0;
            
            BaseL2Message bsl2Msg 
            = Utiliy.generateBaseL2Msg(Constant.COMMAND_ID_MANUFACTURE_TEST, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            //boolean isSendL2Over = BaseMessageHandler.sendL2Message(bsl2Msg);
            new CommandSendRequest(mContext, bsl2Msg).addSendTask();
            String str = "startSendBreathData";
            Utiliy.dataToFile(str);
        }
        return null;
    }



    @Override
    public DeviceResponse<?> stopSendBreathData() {
        SLog.e(TAG, " stopSendBreathData ");
        if (Utiliy.isBluetoothConnected(mContext)) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 4;
            keyPayload.keyLen = 0;
            
            BaseL2Message bsl2Msg 
            = Utiliy.generateBaseL2Msg(Constant.COMMAND_ID_MANUFACTURE_TEST, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            //boolean isSendL2Over = BaseMessageHandler.sendL2Message(bsl2Msg);
            new CommandSendRequest(mContext, bsl2Msg).addSendTask();
            String str = "stopSendBreathData";
            Utiliy.dataToFile(str);
        }
        return null;
    }
    
    @Override
    public DeviceResponse<?> manufactureTestCommand() {
        SLog.e(TAG, " manufactureTestCommand ");
        if (Utiliy.isBluetoothConnected(mContext)) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 8;
            keyPayload.keyLen = 0;
            
            BaseL2Message bsl2Msg 
            = Utiliy.generateBaseL2Msg(Constant.COMMAND_ID_MANUFACTURE_TEST, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            new CommandSendRequest(mContext, bsl2Msg).addSendTask();
            String str = "manufactureTestCommand";
            Utiliy.dataToFile(str);
        }
        return null;
    }




    @Override
    public DeviceResponse<?> getDeviceTime() {
        SLog.e(TAG, " getDeviceTime ");
        if (Utiliy.isBluetoothConnected(mContext)) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 3;
            keyPayload.keyLen = 0;
            
            BaseL2Message bsl2Msg 
            = Utiliy.generateBaseL2Msg(Constant.COMMAND_ID_SETTING, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            new CommandSendRequest(mContext, bsl2Msg).addSendTask();
            String str = "getDeviceTime";
            Utiliy.dataToFile(str);
        }
        return null;
    }



    @Override
    public DeviceResponse<?> getRealTimeData() {
        SLog.e(TAG, " getRealTimeData ");
        if (Utiliy.isBluetoothConnected(mContext)) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 1;
            keyPayload.keyLen = 0;
            
            BaseL2Message bsl2Msg 
            = Utiliy.generateBaseL2Msg(Constant.COMMAND_ID_DATA, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            //boolean isSendL2Over = BaseMessageHandler.sendL2Message(bsl2Msg);
            new CommandSendRequest(mContext, bsl2Msg).addSendTask();
            String str = "getRealTimeData";
            Utiliy.dataToFile(str);
        }

        return null;
    }



    @Override
    public DeviceResponse<?> getAllNoSyncInfo() {
        SLog.e(TAG, " getAllNoSyncInfo ");
        if (Utiliy.isBluetoothConnected(mContext)) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 3;
            keyPayload.keyLen = 0;
            
            BaseL2Message bsl2Msg 
                = Utiliy.generateBaseL2Msg(Constant.COMMAND_ID_DATA, 
                    Constant.BASE_VERSION_CODE, keyPayload);
        
            new CommandSendRequest(mContext, bsl2Msg).addSendTask();
            String str = "getAllNoSyncInfo";
            Utiliy.dataToFile(str);
        }

        return null;
    }
    
    @Override
    public DeviceResponse<?> getAllNoSyncInfo(int datatype) {
        SLog.e(TAG, " getAllNoSyncInfo ");
        if (Utiliy.isBluetoothConnected(mContext)) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 3;
            keyPayload.keyLen = 1;
            keyPayload.keyValue = getDataType(datatype);
            
            BaseL2Message bsl2Msg 
                = Utiliy.generateBaseL2Msg(Constant.COMMAND_ID_DATA, 
                    Constant.BASE_VERSION_CODE, keyPayload);
        
            new CommandSendRequest(mContext, bsl2Msg).addSendTask();
            String str = "getAllNoSyncInfo";
            Utiliy.dataToFile(str);
        }

        return null;
    }




    private byte[] getDataType(int datatype) {
        byte[] type = new byte[1];
        try {
            type[0] = (byte) (datatype & 0xff);
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
        return type;
    }


    @Override
    public DeviceResponse<?> getAllSyncInfo() {
        SLog.e(TAG, " getAllSyncInfo ");
        if (Utiliy.isBluetoothConnected(mContext)) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 4;
            keyPayload.keyLen = 0;
            
            BaseL2Message bsl2Msg 
            = Utiliy.generateBaseL2Msg(Constant.COMMAND_ID_DATA, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            //boolean isSendL2Over = BaseMessageHandler.sendL2Message(bsl2Msg);
            new CommandSendRequest(mContext, bsl2Msg).addSendTask();
            String str = "getAllSyncInfo";
            Utiliy.dataToFile(str);
        }

        return null;
    }

    @Override
    public DeviceResponse<?> getBreathStopInfo() {
        // TODO Auto-generated method stub
        SLog.e(TAG, " getBreathStopInfo ");
        if (Utiliy.isBluetoothConnected(mContext)) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 11;
            keyPayload.keyLen = 0;
            
            BaseL2Message bsl2Msg 
            = Utiliy.generateBaseL2Msg(Constant.COMMAND_ID_DATA, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            new CommandSendRequest(mContext, bsl2Msg).addSendTask();
            String str = "getBreahStopInfo";
            Utiliy.dataToFile(str);
        }
        return null;
    }


    @Override
    public DeviceResponse<?> getRealTimeTempData() {
        SLog.e(TAG, " getRealTimeTempData ");
        if (Utiliy.isBluetoothConnected(mContext)) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 9;
            keyPayload.keyLen = 0;
            
            BaseL2Message bsl2Msg 
            = Utiliy.generateBaseL2Msg(Constant.COMMAND_ID_DATA, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            //boolean isSendL2Over = BaseMessageHandler.sendL2Message(bsl2Msg);
            new CommandSendRequest(mContext, bsl2Msg).addSendTask();
            String str = "getRealTimeTempData";
            Utiliy.dataToFile(str);
        }
        return null;
    }


    @Override
    public DeviceResponse<?> getExceptionEvent() {
        SLog.e(TAG, " getExceptionEvent ");
        if (Utiliy.isBluetoothConnected(mContext)) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 14;
            keyPayload.keyLen = 0;
            
            BaseL2Message bsl2Msg 
            = Utiliy.generateBaseL2Msg(Constant.COMMAND_ID_DATA, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            new CommandSendRequest(mContext, bsl2Msg).addSendTask();
            String str = "getExceptionEvent";
            Utiliy.dataToFile(str);
        }
        return null;
    }


    @Override
    public DeviceResponse<?> checkDeviceValid() {
        SLog.e(TAG, " checkDeviceValid ");
        if (Utiliy.isBluetoothConnected(mContext)) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 5;
            keyPayload.keyLen = 7;
            keyPayload.keyValue = getDeviceAddress(mContext);
            
            BaseL2Message bsl2Msg 
            = Utiliy.generateBaseL2Msg(Constant.COMMAND_ID_BIND, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            new CommandSendRequest(mContext, bsl2Msg).addSendTask();
            String str = "checkDeviceValid";
            Utiliy.dataToFile(str);
        }
        return null;
    }


    private byte[] getDeviceAddress(Context context) {
        byte[] checkinfo = new byte[7];
        byte[] devaddress = new byte[6];
        try {
            String connectaddress = PrivateParams.getSPString(context, Constant.AIZI_DEVICE_ADDRESS);
            if (!TextUtils.isEmpty(connectaddress)) {
                String[] blueadd = connectaddress.split(":");
                String converstr = "";
                if (blueadd != null && 6 == blueadd.length) {
                    for (int i = 0; i < blueadd.length; i++) {
                        converstr = converstr + blueadd[i];
                    }
                    devaddress = Utiliy.hexStringToByte(converstr);
                    devaddress[0] = (byte) (devaddress[0] + 0x01);
                    devaddress[1] = (byte) (devaddress[1] + 0x11);
                    devaddress[2] = (byte) (devaddress[2] + 0x21);
                    devaddress[3] = (byte) (devaddress[3] + 0x31);
                    devaddress[4] = (byte) (devaddress[4] + 0x41);
                    devaddress[5] = (byte) (devaddress[5] + 0x51);
                    
                    String devstr = Utiliy.printHexString(devaddress);
                    System.arraycopy(devaddress, 0, checkinfo, 0, blueadd.length);
                    checkinfo[6] = 0x0; // 表示Android系统
                }
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
        return checkinfo;
    }


    @Override
    public DeviceResponse<?> activateDevice() {
        SLog.e(TAG, " activateDevice ");
        if (Utiliy.isBluetoothConnected(mContext)) {
            KeyPayload keyPayload = new KeyPayload();
            keyPayload.key = 5;
            keyPayload.keyLen = 2;
            keyPayload.keyValue = getProfileInfo(mContext);
            
            BaseL2Message bsl2Msg 
            = Utiliy.generateBaseL2Msg(Constant.COMMAND_ID_SETTING, 
                    Constant.BASE_VERSION_CODE, keyPayload);
            new CommandSendRequest(mContext, bsl2Msg).addSendTask();
            String str = "activateDevice";
            Utiliy.dataToFile(str);
        }
        return null;
    }


    private byte[] getProfileInfo(Context context) {
        byte[] profileinfo = new byte[2];
        try {
            profileinfo[0] = (byte) (profileinfo[0] & 0x00);
            profileinfo[0] = (byte) (profileinfo[0] | (0xff8 << 2));
            profileinfo[0] = (byte) (profileinfo[0] & 0xfe);
            profileinfo[1] = (byte) (profileinfo[1] | 0x00);
            //profileinfo[2] = (byte) (profileinfo[2] | 0x00);
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
        return profileinfo;
    }
}
