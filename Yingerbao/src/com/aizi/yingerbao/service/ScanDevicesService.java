package com.aizi.yingerbao.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.text.TextUtils;
import android.widget.Toast;

import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.utility.PrivateParams;

import de.greenrobot.event.EventBus;

public class ScanDevicesService extends Service{
    
    private static final String TAG = ScanDevicesService.class.getSimpleName();
    BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private boolean mScanning;
    List<BluetoothDevice> mDeviceList;
    Map<String, Integer> devRssiValues;
    private static final long SCAN_PERIOD = 2 * 1000; //10 seconds
    
    /** 
     * 更新进度的回调接口 
     */  
    private OnScanDeviceListener mListener;  
    /** 
     * 注册回调接口的方法，供外部调用 
     * @param onProgressListener 
     */  
    public void setOnProgressListener(OnScanDeviceListener onScanDeviceListener) {  
        this.mListener = onScanDeviceListener;  
    } 

    public interface OnScanDeviceListener{  
        public void OnScanDeviceSucceed(int touchid);  
    } 
    
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        mHandler = new Handler();

        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "手机不支持蓝牙", Toast.LENGTH_SHORT).show();
            stopSelf();
        }  
        
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "手机不支持蓝牙", Toast.LENGTH_SHORT).show();
            stopSelf();
        } else {
            mBluetoothAdapter.enable();
        }
        
        /* Initialize device list container */
        mDeviceList = new ArrayList<BluetoothDevice>();
        devRssiValues = new HashMap<String, Integer>();
        return new ScanBinder();
    }
    
    public class ScanBinder extends Binder{  
        /** 
         * 获取当前Service的实例 
         * @return 
         */  
        public ScanDevicesService getService(){  
            return ScanDevicesService.this;  
        }  
    }  
    
    public  List<BluetoothDevice> getDeviceList() {
        for (BluetoothDevice listDev : mDeviceList) {
            SLog.e(TAG, "devicelist = " + listDev.getName());
        }
        return mDeviceList;
    }

    public void startScanList() {
        scanLeDevice(true);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                   if (mScanning) {
                       mBluetoothAdapter.stopLeScan(mBLEScanCallback);
                       //mListener.OnScanDeviceSucceed(2);
                       Intent intent = new Intent(Constant.BLUETOOTH_SCAN_NOT_FOUND);
                       EventBus.getDefault().post(intent); 
                   }
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mBLEScanCallback);
            SLog.e(TAG, "start Scan bluetooth devices");
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mBLEScanCallback);
        }
    }
    
    
    private BluetoothAdapter.LeScanCallback mBLEScanCallback =
            new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, 
                final int rssi, byte[] scanRecord) {
            addDevice(device,rssi);
        }
    };

    
    private void addDevice(BluetoothDevice device, int rssi) {
        boolean isDeviceFound = false;
        for (BluetoothDevice listDev : mDeviceList) {
            if (listDev.getAddress().equals(device.getAddress())) {
                isDeviceFound = true;
                break;
            }
        }    
        SLog.e(TAG, "address = " + device.getAddress() 
                + " name = " + device.getName() 
                +  "  rssi = " + rssi);
        devRssiValues.put(device.getAddress(), rssi);
        if (!isDeviceFound) {
            if (!TextUtils.isEmpty(device.getName())) {
                if (device.getName().equals("my_hrm")) {
                    mDeviceList.add(device);
                    PrivateParams.setSPString(getApplicationContext(), 
                            Constant.SHARED_DEVICE_ADDRESS, device.getAddress());
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mBLEScanCallback);
                    
                    Intent intent = new Intent(Constant.BLUETOOTH_SCAN_FOUND);
                    EventBus.getDefault().post(intent); 
                    //mListener.OnScanDeviceSucceed(1);
                }
            }
        }
    }
    
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        scanLeDevice(false);
    }
    
    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        return super.onUnbind(intent);
    }

}
