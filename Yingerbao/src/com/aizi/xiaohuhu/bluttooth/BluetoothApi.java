package com.aizi.xiaohuhu.bluttooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.text.TextUtils;

import com.aizi.xiaohuhu.constant.Constant;
import com.aizi.xiaohuhu.eventbus.AsycEvent;
import com.aizi.xiaohuhu.logging.SLog;
import com.aizi.xiaohuhu.service.BluetoothService;
import com.aizi.xiaohuhu.utility.BaseMessageHandler;
import com.aizi.xiaohuhu.utility.PrivateParams;

import de.greenrobot.event.EventBus;

public class BluetoothApi {
    
    private static final String TAG = BluetoothApi.class.getSimpleName();
    
    private static BluetoothApi mInstance;
    
    public BluetoothService mBluetoothService = null;
    private static boolean mIsBluetoothReady;
    private String mAddress;
    
    public BluetoothApi(Context context) {
        // TODO Auto-generated constructor stub
        EventBus.getDefault().register(this);
        bindBluetoothService(context);
        /*bindBluetoothService(context);
        mAddress = PrivateParams.getSPString(context, Constant.SHARED_DEVICE_NAME);
        if (!TextUtils.isEmpty(mAddress)) {
            mBluetoothService.connect(mAddress);
        }else {
            
        }*/
    }

    public static BluetoothApi getInstance(Context context) {
        if (mInstance != null) {
            return mInstance;
        } else {
            mInstance = new BluetoothApi(context);
            return mInstance;
        }
    }
    
    
    private void bindBluetoothService(Context context) {
        Intent bindIntent = new Intent(context, BluetoothService.class);
        boolean isbind = context.bindService(bindIntent, mBluetoothServiceConnection, Context.BIND_AUTO_CREATE);
     }
    
    private void unbindBluetoothService(Context context) {
        if (context != null) {
            context.unbindService(mBluetoothServiceConnection);
        }
     }
    
    //Bluetooth service connected/disconnected
    private ServiceConnection mBluetoothServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mBluetoothService = ((BluetoothService.LocalBinder) rawBinder).getService();
                SLog.e(TAG, "onServiceConnected mService= " + mBluetoothService);
                if (!mBluetoothService.initialize()) {
                    SLog.e(TAG, "Unable to initialize Bluetooth");
                }
        }
        public void onServiceDisconnected(ComponentName classname) {
            mBluetoothService.disconnect();
            mBluetoothService = null;
        }
    };
    
    
    public void onEvent(AsycEvent event) {   
        //mBluetoothService.writeBaseRXCharacteristic(event.getByte());
        writeByte(event.getByte());
     } 
    
    
    public void writeByte(byte[] wrByte) {
        if (mBluetoothService != null) {
            mBluetoothService.writeBaseRXCharacteristic(wrByte);
            BaseMessageHandler.isWriteSuccess = false;
        }
       
    }

}
