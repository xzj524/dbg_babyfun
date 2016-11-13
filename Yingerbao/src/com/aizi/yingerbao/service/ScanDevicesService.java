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
import com.aizi.yingerbao.utility.ListDataSave;
import com.aizi.yingerbao.utility.PrivateParams;

import de.greenrobot.event.EventBus;

public class ScanDevicesService extends Service{
    
    private static final String TAG = ScanDevicesService.class.getSimpleName();
    private Handler mHandler;
    private boolean mScanning;
    BluetoothAdapter mBluetoothAdapter;
    List<BluetoothDevice> mDeviceList;
    Map<String, Integer> devRssiValues;
    
    private static final long SCAN_PERIOD = 10 * 1000; //10 seconds
    
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
            // 打开蓝牙
            mBluetoothAdapter.enable();
        }
        
        /* Initialize device list container */
        mDeviceList = new ArrayList<BluetoothDevice>();
        devRssiValues = new HashMap<String, Integer>();
        return new ScanBinder();
    }
    
    /** 
     * 获取当前Service的实例 
     * @return 
     */  
    public class ScanBinder extends Binder{  
        public ScanDevicesService getService(){  
            return ScanDevicesService.this;  
        }  
    }  
    
    public  List<BluetoothDevice> getDeviceList() {
        return mDeviceList;
    }

    public void startScanDevice() {
        scanBLEDevice(true);
    }
    
    public void stopScanDevice() {
        mScanning = false;
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.stopLeScan(mBLEScanCallback);
        }
    }

    private void scanBLEDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                   if (mScanning) {
                       mBluetoothAdapter.stopLeScan(mBLEScanCallback);
                       // 没有扫描到蓝牙设备
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
            boolean isdiscovery = isDiscoveryDevice(device,rssi);
            if (isdiscovery) {
                Intent intent = new Intent(Constant.BLUETOOTH_SCAN_FOUND);
                EventBus.getDefault().post(intent);
            }
        }
    };

    
    private boolean isDiscoveryDevice(BluetoothDevice device, int rssi) {
        boolean isDeviceFound = false;
        try {
            for (BluetoothDevice listDev : mDeviceList) {
                if (listDev.getAddress().equals(device.getAddress())) {
                    // 设备已经发现
                    isDeviceFound = true;
                    stopScanDevice();
                    SLog.e(TAG, "device is already in the device list ");
                    break;
                }
            }    
            SLog.e(TAG, "searching....  address = " + device.getAddress() 
                      + " name = " + device.getName() 
                      + " rssi = " + rssi);
           
            if (!isDeviceFound) {
                if (!TextUtils.isEmpty(device.getName())) {
                    if (device.getName().equals(Constant.AIZI_DEVICE_TAG)) {
                        mDeviceList.add(device);
                        devRssiValues.put(device.getAddress(), rssi);
                        isDeviceFound = true;
                        /*ListDataSave listDataSave = new ListDataSave(getApplicationContext(),
                                Constant.AIZI_DEVICE_PRIVATE_SETTINGS);
                        listDataSave.setDataList(Constant.AIZI_DEVICE_ADDRESS, mDeviceList);*/
                        
                        PrivateParams.setSPString(getApplicationContext(), Constant.AIZI_DEVICE_ADDRESS, 
                                device.getAddress());
                        
                        mScanning = false;
                        mBluetoothAdapter.stopLeScan(mBLEScanCallback);
                    }
                }
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
        return isDeviceFound;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        scanBLEDevice(false);
    }
    
    @Override
    public boolean onUnbind(Intent intent) {
        scanBLEDevice(false);
        return super.onUnbind(intent);
    }

}
