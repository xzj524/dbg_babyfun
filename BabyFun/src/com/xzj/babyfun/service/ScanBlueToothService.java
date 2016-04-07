package com.xzj.babyfun.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.xzj.babyfun.BabyFunActivity;

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
import android.util.Log;
import android.widget.Toast;

public class ScanBlueToothService extends Service{
    
    BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private boolean mScanning;
    List<BluetoothDevice> deviceList;
    Map<String, Integer> devRssiValues;
    private static final long SCAN_PERIOD = 5 * 1000; //10 seconds
    private static final String TAG = ScanBlueToothService.class.getSimpleName();
   // private DeviceAdapter deviceAdapter;
    
    /** 
     * 更新进度的回调接口 
     */  
    private OnScanDeviceListener onScanDeviceListener;  
      
      
    /** 
     * 注册回调接口的方法，供外部调用 
     * @param onProgressListener 
     */  
    public void setOnProgressListener(OnScanDeviceListener onScanDeviceListener) {  
        this.onScanDeviceListener = onScanDeviceListener;  
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
        }
        
        return new ScanBinder();
    }
    
    public class ScanBinder extends Binder{  
        /** 
         * 获取当前Service的实例 
         * @return 
         */  
        public ScanBlueToothService getService(){  
            return ScanBlueToothService.this;  
        }  
    }  
    
    public  List<BluetoothDevice> getDeviceList() {
        for (BluetoothDevice listDev : deviceList) {
            Log.e(TAG, "devicelist = " + listDev.getName());
        }
        return deviceList;
        
    }
    
    
    
    public void startScanList() {
        /* Initialize device list container */
        //Log.d(TAG, "populateList");
        deviceList = new ArrayList<BluetoothDevice>();
       // deviceAdapter = new DeviceAdapter(this, deviceList);
        devRssiValues = new HashMap<String, Integer>();

       /* ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(deviceAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
*/
           scanLeDevice(true);

    }

    private void scanLeDevice(final boolean enable) {
        // TODO Auto-generated method stub
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                   if (mScanning) {
                       mBluetoothAdapter.stopLeScan(mLeScanCallback);
                       onScanDeviceListener.OnScanDeviceSucceed(10);
                }

                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
            Log.e("addDevice", "address rssi");
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }
    
    
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, byte[] scanRecord) {
            
            addDevice(device,rssi);
            //Log.e("addDevice", "1address = " + device.getAddress() + "  rssi = " + rssi);
                              
          
        }
    };

    
    private void addDevice(BluetoothDevice device, int rssi) {
        boolean deviceFound = false;

        for (BluetoothDevice listDev : deviceList) {
            if (listDev.getAddress().equals(device.getAddress())) {
                deviceFound = true;
                break;
            }
        }    
        Log.e("addDevice", "2address = " + device.getAddress() + " name = " + device.getName() +  "  rssi = " + rssi);
        devRssiValues.put(device.getAddress(), rssi);
        if (!deviceFound) {
            deviceList.add(device);
            
            if (device.getName() != null) {
                if (device.getName().equals("my_hrm")) {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    onScanDeviceListener.OnScanDeviceSucceed(9);
                }
                
            }
            
            
            
          //  onScanDeviceListener.OnScanDeviceSucceed(device.getType());
        }
    }

}
