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

package com.aizi.xiaohuhu.service;

import java.util.List;
import java.util.UUID;

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

import com.aizi.xiaohuhu.constant.Constant;
import com.aizi.xiaohuhu.logging.SLog;
import com.aizi.xiaohuhu.utility.BaseMessageHandler;
import com.aizi.xiaohuhu.utility.PrivateParams;

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
    private int mConnectionState = STATE_DISCONNECTED;
    
    BluetoothGattCharacteristic mCharacterChar;
    
    BluetoothGattService mDataService;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    
    
    private static final  UUID BLE_UUID_NUS_SERVICE = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb");
    private static final UUID BLE_UUID_NUS_TX_CHARACTERISTIC = UUID.fromString("00000002-0000-1000-8000-00805f9b34fb");
    private static final UUID BLE_UUID_NUS_RX_CHARACTERISTIC = UUID.fromString("00000003-0000-1000-8000-00805f9b34fb");
    private static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    public final static String ACTION_GATT_CONNECTED =
            "com.aizi.xiaohuhu.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.aizi.xiaohuhu.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.aizi.xiaohuhu.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.aizi.xiaohuhu.ACTION_DATA_AVAILABLE";

    public final static String EXTRA_TYPE =
            "com.aizi.xiaohuhu.EXTRA_TYPE";
    public final static String DEVICE_DOES_NOT_SUPPORT_BLUETOOTH =
            "com.aizi.xiaohuhu.DEVICE_DOES_NOT_SUPPORT_BLUETOOTH";
    
    
   
    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String connectionAction = null;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                connectionAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                SLog.e(TAG, "Connected to GATT server.");
                mBluetoothGatt.discoverServices(); // Attempts to discover services after successful connection.
                SLog.e(TAG, "Attempting to start service discovery:");
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                connectionAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                SLog.e(TAG, "Disconnected from GATT server.");   
                PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 0);
            }
            broadcastUpdate(connectionAction);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                boolean isEnable = enableDataNotification();
                if (isEnable) { // 蓝牙已经连接并且准备好数据传输
                    SLog.e(TAG, "Bluetooth  is Ready, mBluetoothGatt = " + mBluetoothGatt );
                    PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 1);
                    broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                    return;
                }
            } else {  
                SLog.e(TAG, "Bluetooth  is not Discovered ");
            }
            
            broadcastUpdate(ACTION_GATT_DISCONNECTED);
            PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 0);
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
        	BaseMessageHandler.acquireBaseData(gatt.getDevice(), characteristic);
        }
        

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if (BLE_UUID_NUS_TX_CHARACTERISTIC.equals(characteristic.getUuid())) {
                if (BluetoothGatt.GATT_SUCCESS == status) {
                    BaseMessageHandler.isWriteSuccess = true;
                    SLog.e(TAG, "onCharacteristicWrite isWriteSuccess = true");
                } else {
                    BaseMessageHandler.isWriteSuccess = false;
                    SLog.e(TAG, "onCharacteristicWrite isWriteSuccess = false");
                }
            }
        };
    };

    private void broadcastUpdate(String action) {
        if (!TextUtils.isEmpty(action)) {
            Intent intent = new Intent(action);
            EventBus.getDefault().post(intent); 
        }
    }
    
    private boolean isHeartRateInUINT16(final byte value) {
		return ((value & 0x01) != 0);
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
        close();
        //EventBus.getDefault().unregister(this);//反注册EventBus  
        return super.onUnbind(intent);
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
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
        
        //EventBus.getDefault().register(this);

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
    public boolean connect(final String address) {
        if (mBluetoothAdapter == null || address == null) {
            SLog.e(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            SLog.e(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 0);
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
                return false;
            }
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
    
        SLog.e(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 0);
        broadcastUpdate(ACTION_GATT_DISCONNECTED);
        mBluetoothGatt.disconnect();
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
        
        mBluetoothDeviceAddress = null;
        mBluetoothGatt.close();
        mBluetoothGatt = null;
        PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 0);
        broadcastUpdate(ACTION_GATT_DISCONNECTED);
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
        try {
            mDataService = mBluetoothGatt.getService(BLE_UUID_NUS_SERVICE);
            if (mDataService == null) {
                SLog.e(TAG, "enableDataNotification BLE_UUID_NUS_SERVICE not found!");
                broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_BLUETOOTH);
                PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 0);
                return false;
            }
            mCharacterChar = mDataService.getCharacteristic(BLE_UUID_NUS_RX_CHARACTERISTIC);
            if (mCharacterChar == null) {
                SLog.e(TAG, "enableDataNotification BLE_UUID_NUS_RX_CHARACTERISTIC not found!");
                broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_BLUETOOTH);
                PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 0);
                return false;
            }
            mBluetoothGatt.setCharacteristicNotification(mCharacterChar,true);
            BluetoothGattDescriptor descriptor = mCharacterChar.getDescriptor(CCCD);
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);

            return true;
        } catch (Exception e) {
            // TODO: handle exception
            SLog.e(TAG, e);
        }
        PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 0);
        return false;
    }
    
    public void writeBaseRXCharacteristic(byte[] value)
    {
        BluetoothGattService RxService = mBluetoothGatt.getService(BLE_UUID_NUS_SERVICE);
        if (RxService == null) {
            SLog.e(TAG, "writeBaseRXCharacteristic BLE_UUID_NUS_SERVICE not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_BLUETOOTH);
            return;
        }
        BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(BLE_UUID_NUS_TX_CHARACTERISTIC);
        if (RxChar == null) {
            SLog.e(TAG, "writeBaseRXCharacteristic BLE_UUID_NUS_TX_CHARACTERISTIC not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_BLUETOOTH);
            return;
        }
        RxChar.setValue(value);
        boolean status = mBluetoothGatt.writeCharacteristic(RxChar);
        
        SLog.e(TAG, "write TXchar status = " + status);  
    }
    
    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;

        return mBluetoothGatt.getServices();
    }
    
    
    
}
