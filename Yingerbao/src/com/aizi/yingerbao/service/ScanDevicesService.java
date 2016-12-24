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

import com.aizi.yingerbao.bluttooth.BluetoothApi;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.utility.PrivateParams;

public class ScanDevicesService extends Service{
    
    private static final String TAG = ScanDevicesService.class.getSimpleName();
    private Handler mHandler;
    private boolean mScanning = false;
    BluetoothAdapter mBluetoothAdapter;
    List<BluetoothDevice> mDeviceList;
    Map<String, Integer> mDevRssiValues;
    
    private static final long SCAN_PERIOD = 18 * 1000; //扫描设备超时时间
   
    
    @Override
    public IBinder onBind(Intent intent) {
        mHandler = new Handler();
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "手机不支持蓝牙", Toast.LENGTH_SHORT).show();
            stopSelf();
        }  
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        // 检查手机设备是否支持蓝牙功能
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "手机不支持蓝牙", Toast.LENGTH_SHORT).show();
            stopSelf();
        } else {
            // 启动手机蓝牙功能
            mBluetoothAdapter.enable();
        }
        
        // 初始化设备容器 
        mDeviceList = new ArrayList<BluetoothDevice>();
        mDevRssiValues = new HashMap<String, Integer>();
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
        mDeviceList.clear();
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
            // 扫描超时之后停止扫描
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                   if (mScanning) { //在扫描进行中才会停止扫描
                       mBluetoothAdapter.stopLeScan(mBLEScanCallback);
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
            boolean isDiscovery = isDiscoveryDevice(device,rssi);
                      
            if (isDiscovery) {  
                SLog.e(TAG, "Discovery is " + isDiscovery 
                        + " connect_interrupt = " +  PrivateParams.getSPInt(getApplicationContext(), "connect_interrupt", 0));
                // 中断连接定时器 
                if (PrivateParams.getSPInt(getApplicationContext(), "connect_interrupt", 0) != 1) {
                    if (BluetoothApi.getInstance(getApplicationContext()).mBluetoothService != null) {
                        BluetoothApi.getInstance(getApplicationContext()).mBluetoothService.connect(device.getAddress(), false);
                    }
                }
            }
        }
    };

    
    private boolean isDiscoveryDevice(BluetoothDevice device, int rssi) {
        boolean isDeviceFound = false;
        try {
            if (!isDeviceFound) {
                if (!TextUtils.isEmpty(device.getName())) {
                    if (device.getName().equals(Constant.AIZI_DEVICE_TAG)
                            || device.getName().equals(Constant.AIZI_DEVICE_TEST_TAG)) {
                        SLog.e(TAG, "searching....  Address = " + device.getAddress() 
                                + " Name = " + device.getName()
                                + " rssi = " + rssi
                                + " SCAN_RANG = " + Constant.SCAN_RANG);
                        if (rssi > Constant.SCAN_RANG) {
                            mDeviceList.add(device);
                            mDevRssiValues.put(device.getAddress(), rssi);
                            
                            isDeviceFound = true;
                            PrivateParams.setSPString(getApplicationContext(), Constant.AIZI_DEVICE_ADDRESS, 
                                    device.getAddress());
                            
                            PrivateParams.setSPString(getApplicationContext(), Constant.AIZI_PHONE_ADDRESS, 
                                    mBluetoothAdapter.getAddress());
                            
                            mScanning = false;
                            mBluetoothAdapter.stopLeScan(mBLEScanCallback);
                        }
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
