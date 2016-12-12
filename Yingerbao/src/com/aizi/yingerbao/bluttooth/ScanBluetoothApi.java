package com.aizi.yingerbao.bluttooth;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.aizi.yingerbao.device.fragment.DeviceConnectStatusFragment.ConnectDeviceState;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.service.BluetoothService;
import com.aizi.yingerbao.service.ScanDevicesService;

public class ScanBluetoothApi {
    
    private static final String TAG = ScanBluetoothApi.class.getSimpleName();
    
    Context mContext;
    private static ScanBluetoothApi mInstance;
    public ScanDevicesService mScanService = null;
    
    public ScanBluetoothApi(Context context) {
        mContext = context;
        bindBluetoothService(context);
        
    }
    
    private void bindBluetoothService(Context context) {
        Intent bindblueIntent = new Intent(context, ScanDevicesService.class);
        context.bindService(bindblueIntent, mScanServiceConnection, Context.BIND_AUTO_CREATE);
     }
    
    private ServiceConnection mScanServiceConnection = new ServiceConnection() {
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            //mScanService = null;
        }
        
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mScanService = ((ScanDevicesService.ScanBinder) service).getService();
            if (mScanService != null) {
                //if (mDevConnectFragment.getCurrentState() == ConnectDeviceState.IDEL
                //        || mDevConnectFragment.getCurrentState() == ConnectDeviceState.FAIL) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                    SLog.e(TAG, "onServiceConnected mScanService = " + mScanService );
                    mScanService.startScanDevice(); 
               // }
            } 
            
        }
    };
    

}
