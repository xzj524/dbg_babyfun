/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aizi.yingerbao.service;

import java.util.UUID;

import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.deviceinterface.DeviceFactory;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.utility.RecvMessageHandler;
import com.aizi.yingerbao.utility.PrivateParams;
import com.aizi.yingerbao.utility.Utiliy;

import de.greenrobot.event.EventBus;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class BluetoothService extends Service {
    private final static String TAG = BluetoothService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    BluetoothGattCharacteristic mCharacterChar;
    BluetoothGattService mDataService;
    
    int mConnectTimes = 0;
    boolean mIsConnectRepeat = false;
    
    PendingIntent mCheckPendingIntent;

    private static final  UUID BLE_UUID_NUS_SERVICE = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb");
    private static final UUID BLE_UUID_NUS_TX_CHARACTERISTIC = UUID.fromString("00000002-0000-1000-8000-00805f9b34fb");
    private static final UUID BLE_UUID_NUS_RX_CHARACTERISTIC = UUID.fromString("00000003-0000-1000-8000-00805f9b34fb");
    private static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public final static String ACTION_GATT_CONNECTED =
            "com.aizi.yingerbao.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.aizi.yingerbao.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.aizi.yingerbao.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.aizi.yingerbao.ACTION_DATA_AVAILABLE";

    public final static String EXTRA_TYPE =
            "com.aizi.yingerbao.EXTRA_TYPE";
    public final static String DEVICE_DOES_NOT_SUPPORT_BLUETOOTH =
            "com.aizi.yingerbao.DEVICE_DOES_NOT_SUPPORT_BLUETOOTH";

    protected static final long SYNC_DATA_TIMEOUT = 60 * 60 * 12 * 1000;
    
    private OnBluetoothServiceListener mBluetoothListener;
    
    public interface OnBluetoothServiceListener{  
        public void onBluetoothWrite(Intent intent);  
    } 
    
    public void setCallback(OnBluetoothServiceListener callback) {         
        this.mBluetoothListener = callback;
    }


    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String connectionAction = null;
             
             if (status == gatt.GATT_FAILURE) {
                 SLog.e(TAG, "GATT STATUS  Failure **** = " + status);
             }
            
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                SLog.e(TAG, "Connected to GATT server. Attempting to start service discovery");
                mBluetoothGatt.discoverServices(); // Attempts to discover services after successful connection.
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectionAction = ACTION_GATT_DISCONNECTED;
                SLog.e(TAG, "Disconnected from GATT server.");   
                PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 0);
//                if (mBluetoothGatt != null) {
//                    mBluetoothGatt.close();
                close();
                if (!TextUtils.isEmpty(mBluetoothDeviceAddress)
                        && Constant.AIZI_USERACTIVTY_QUIT != 1) {
                    connect(mBluetoothDeviceAddress, true);
                }
                //}
                //broadcastUpdate(connectionAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            boolean notifyres = false;
            if (status == BluetoothGatt.GATT_SUCCESS) {
                try { // 延时100ms                 
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    SLog.e(TAG, e);
                }
                
                for (int i = 0; i < 3; i++) {
                    notifyres = enableDataNotification();
                    if (notifyres) {
                        SLog.e(TAG, "enableDataNotification success");
                        break;
                    } else {
                        SLog.e(TAG, "enableDataNotification failed");
                    }
                }
                
                if (notifyres) { // 使能数据成功
                    // 搜索设备状态，成功
                    PrivateParams.setSPInt(getApplicationContext(), "search_device_status", 3);
                    SLog.e(TAG, "Bluetooth  is Ready, mBluetoothGatt = " + mBluetoothGatt );
                    if (PrivateParams.getSPInt(getApplicationContext(), "connect_interrupt", 0) == 1) {
                        // 检测到连接过程中断
                        return;
                    }
                    new Thread(new Runnable() {
                        
                        @Override
                        public void run() {
                            try {
                                Thread.sleep(500);
                                DeviceFactory.getInstance(getApplicationContext()).checkDeviceValid();
                                // 设置检查设备状态，开始
                                PrivateParams.setSPInt(getApplicationContext(), "check_device_status", 1);
                                // 设置校验设备超时定时器
                                if (mCheckPendingIntent != null) {
                                    Utiliy.cancelAlarmPdIntent(getApplicationContext(), mCheckPendingIntent);
                                }
                                mCheckPendingIntent = Utiliy.getDelayPendingIntent(getApplicationContext(), Constant.ALARM_WAIT_CHECK_DEVICE);
                                Utiliy.setDelayAlarm(getApplicationContext(), Constant.WAIT_CHECK_PERIOD, mCheckPendingIntent);
                            
                            } catch (InterruptedException e) {
                                SLog.e(TAG, e);
                            }
                        }
                    }).start();
                    PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 1);
                    
                    broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                    mConnectTimes = 0;
                    
                    // 设置定时器，用于定时同步数据
                    //Utiliy.cancelAlarmPdIntent(getApplicationContext(), Utiliy.getRepeatAlarmPendingIntent(getApplicationContext()));
                    //Utiliy.setRepeatAlarm(getApplicationContext(), SYNC_DATA_TIMEOUT, Utiliy.getRepeatAlarmPendingIntent(getApplicationContext()));
                    return;
                } else {
                    disconnect(true);
                    broadcastUpdate(ACTION_GATT_DISCONNECTED);
                }
            } else {
                SLog.e(TAG, "Bluetooth  is not Discovered ");
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            RecvMessageHandler.getInstance(getApplicationContext())
                .acquireBaseData(getApplicationContext(),gatt.getDevice(), characteristic);
        }
        
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            String str = null;
            if (BLE_UUID_NUS_TX_CHARACTERISTIC.equals(characteristic.getUuid())) {
                String write_content = Utiliy.printHexString(characteristic.getValue());
                if (BluetoothGatt.GATT_SUCCESS == status) {
                    RecvMessageHandler.isWriteSuccess = true;
                    RecvMessageHandler.repeattime = 0;
                } else {
                    RecvMessageHandler.isWriteSuccess = false;
                }
                Intent intent = new Intent();
                intent.putExtra("write_result", RecvMessageHandler.isWriteSuccess);
                intent.putExtra("write_content", Utiliy.printHexString(characteristic.getValue()));
                mBluetoothListener.onBluetoothWrite(intent);
                
                str = "onCharacteristicWrite isWriteSuccess = " + RecvMessageHandler.isWriteSuccess
                        + " write_content "+ write_content + " status = " + status ; 
                SLog.e(TAG, str);
                Utiliy.logToFile(str);// 写入本地日志文件
               
            }
        };
        
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            
        };
    };

    private void broadcastUpdate(String action) {
        if (!TextUtils.isEmpty(action)) {
            Intent intent = new Intent(action);
            EventBus.getDefault().post(intent); 
        }
    }

    public class LocalBinder extends Binder {
           public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        //disconnect(true);
        SLog.e(TAG, "BluetoothService onUnbind");
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initBluetooth() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                SLog.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            SLog.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        mBluetoothAdapter.enable();
        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     *
     * @return Return true if the connection is initiated successfully. The connection result
     *         is reported asynchronously through the
     *         {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     *         callback.
     */
    public boolean connect(final String address, boolean isrepeat) {
        
        close();
        boolean isInit = initBluetooth();
        if (isInit) {
            if (mBluetoothAdapter == null || address == null) {
                SLog.e(TAG, "BluetoothAdapter not initialized or unspecified address.");
                return false;
            }

            final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            if (device == null) {
                SLog.e(TAG, "Device not found.  Unable to connect.");
                PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 0);
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
                return false;
            }
            // We want to directly connect to the device, so we are setting the autoConnect
            // parameter to false.
            mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
            SLog.e(TAG, "Trying to create a new connection. isrepeat = "
                    + isrepeat + " deviceaddress = " + device.getAddress());
            mBluetoothDeviceAddress = address;
            mIsConnectRepeat = isrepeat;
            PrivateParams.setSPString(getApplicationContext(),
                    Constant.AIZI_IS_CONNECT_REPEAT, ""+mIsConnectRepeat);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     * @param enablebluetooth 
     */
    public void disconnect(boolean disbluetooth) {
        try {
            if (mBluetoothAdapter == null || mBluetoothGatt == null) {
                SLog.e(TAG, "BluetoothAdapter not initialized");
                return;
            }

            PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 0);
            if (mBluetoothGatt != null) {
              mBluetoothGatt.disconnect();
            } 
            
            // 清空接收数据缓存区
            RecvMessageHandler.clearL2RecvByte();
            if (disbluetooth) {
                mBluetoothAdapter.disable();
            }
            
            SLog.e(TAG, "BluetoothAdapter DISCONNECT");
            Utiliy.dataToFile("BluetoothAdapter DISCONNECT");
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
        
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        SLog.e(TAG, "mBluetoothGatt closed");
        
        //mBluetoothDeviceAddress = null;
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 0);
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        Log.e(TAG, "BluetoothAdapter initialized");
        mBluetoothGatt.readCharacteristic(characteristic);
    }
  
    /**
     * Enable TXNotification
     *
     * @return 
     */
    public boolean  enableDataNotification() {
        boolean notifiresult = false;
        try {
            if (mBluetoothGatt != null) {
                mDataService = mBluetoothGatt.getService(BLE_UUID_NUS_SERVICE);
                if (mDataService == null) {
                    SLog.e(TAG, "enableDataNotification BLE_UUID_NUS_SERVICE not found!");
                    broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_BLUETOOTH);
                    PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 0);
                    return notifiresult;
                }
                mCharacterChar = mDataService.getCharacteristic(BLE_UUID_NUS_RX_CHARACTERISTIC);
                if (mCharacterChar == null) {
                    SLog.e(TAG, "enableDataNotification BLE_UUID_NUS_RX_CHARACTERISTIC not found!");
                    broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_BLUETOOTH);
                    PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 0);
                    return notifiresult;
                }
                mBluetoothGatt.setCharacteristicNotification(mCharacterChar, true);
                BluetoothGattDescriptor descriptor = mCharacterChar.getDescriptor(CCCD);
                if (descriptor != null) {
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    notifiresult = mBluetoothGatt.writeDescriptor(descriptor);
                }
            } else {
                notifiresult = false;
                broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_BLUETOOTH);
                PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 0);
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
        if (!notifiresult) {
            PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 0);
        }
        return notifiresult;
    }
    
    public boolean writeBaseRXCharacteristic(byte[] value) {
        boolean wrstatus = false;
        try {
            if (mBluetoothGatt != null) {
                BluetoothGattService TxService = mBluetoothGatt.getService(BLE_UUID_NUS_SERVICE);
                if (TxService == null) {
                    SLog.e(TAG, "writeBaseRXCharacteristic BLE_UUID_NUS_SERVICE not found!");
                    broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_BLUETOOTH);
                    return wrstatus;
                }
                BluetoothGattCharacteristic TxChar = TxService.getCharacteristic(BLE_UUID_NUS_TX_CHARACTERISTIC);
                if (TxChar == null) {
                    SLog.e(TAG, "writeBaseRXCharacteristic BLE_UUID_NUS_TX_CHARACTERISTIC not found!");
                    broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_BLUETOOTH);
                    return wrstatus;
                }
                
                TxChar.setValue(value);
                wrstatus = mBluetoothGatt.writeCharacteristic(TxChar);
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
        String writeresult = "write TXchar status = " + wrstatus  + " value = " + Utiliy.printHexString(value);
        SLog.e(TAG, writeresult);
        Utiliy.logToFile(writeresult);
        return wrstatus;  
    }
}
