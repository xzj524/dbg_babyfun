package com.aizi.yingerbao.bluttooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.aizi.yingerbao.eventbus.AsycEvent;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.service.BluetoothService;
import com.aizi.yingerbao.utility.BaseMessageHandler;

import de.greenrobot.event.EventBus;

public class BluetoothApi {
    
    private static final String TAG = BluetoothApi.class.getSimpleName();
    
    private static BluetoothApi mInstance;
    public BluetoothService mBluetoothService = null;
    
    public BluetoothApi(Context context) {
        EventBus.getDefault().register(this);
        bindBluetoothService(context);
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
        Intent bindblueIntent = new Intent(context, BluetoothService.class);
        context.bindService(bindblueIntent, mBluetoothServiceConnection, Context.BIND_AUTO_CREATE);
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
        writeByte(event.getByte());
     } 
    
    
    public void writeByte(byte[] wrByte) {
        if (mBluetoothService != null) {
            mBluetoothService.writeBaseRXCharacteristic(wrByte);
            BaseMessageHandler.isWriteSuccess = false;
        }
    }
}
