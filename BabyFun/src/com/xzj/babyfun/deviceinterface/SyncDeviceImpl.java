package com.xzj.babyfun.deviceinterface;

import java.util.Calendar;

import android.content.Context;

import com.xzj.babyfun.baseheader.BaseL2Message;
import com.xzj.babyfun.baseheader.KeyPayload;
import com.xzj.babyfun.constant.Constant;
import com.xzj.babyfun.logging.SLog;
import com.xzj.babyfun.service.BluetoothService;
import com.xzj.babyfun.synctime.DeviceTime;
import com.xzj.babyfun.utility.BaseMessageHandler;

import de.greenrobot.event.EventBus;

public class SyncDeviceImpl implements SyncDevice{
    
    private static final String TAG = SyncDeviceImpl.class.getSimpleName();
    private BluetoothService mBluetoothService = null;
    private static boolean mIsBluetoothReady;
    private String mAddress;

    public SyncDeviceImpl(Context context) {
        // TODO Auto-generated constructor stub
        mIsBluetoothReady = false;
        //注册EventBus  
        EventBus.getDefault().register(this);
        
    }
    
   
    
    public void onEvent(BluetoothReady bleReady) {  
        mIsBluetoothReady = bleReady.isBluetoothReady;
     } 
    
    public static class BluetoothReady{
        public boolean isBluetoothReady;
        public BluetoothReady() {
            // TODO Auto-generated constructor stub
            isBluetoothReady = false;
        }
       
    }
    

    @Override
    public DeviceResponse<?> setDeviceTime() {
        // TODO Auto-generated method stub
        SLog.e(TAG, " setDeviceTime ");
        if (mIsBluetoothReady) {
           KeyPayload keyPayload = new KeyPayload();
           keyPayload.key = 1;
           keyPayload.keyLen = 4; 
           
           DeviceTime dvtm = getDeviceTime();
           keyPayload.keyValue = dvtm.toByte();  
           
           BaseL2Message bsl2Msg 
           = BaseMessageHandler.generateBaseL2Msg(Constant.COMMAND_ID_SETTING, 
                   Constant.BASE_VERSION_CODE, keyPayload);
           boolean isSendL2Over = BaseMessageHandler.sendL2Message(bsl2Msg);
        }
        
        return null;
    }



    private DeviceTime getDeviceTime() {
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

}
