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

package com.xzj.babyfun.service;

import java.io.UnsupportedEncodingException;
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
import android.util.Log;
import android.widget.Toast;

import com.xzj.babyfun.baseheader.BaseL2Message;
import com.xzj.babyfun.utility.BaseMessageHandler;

import de.greenrobot.event.EventBus;

/**
 * Service for managing connection and data communication with a GATT server hosted on a
 * given Bluetooth LE device.
 */
public class UartService extends Service {
    private final static String TAG = UartService.class.getSimpleName();

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    
    
    BluetoothGattCharacteristic mTempCharacterChar;
    BluetoothGattCharacteristic mCharacterChar;
    BluetoothGattCharacteristic mSleepCharacterChar;
    
    BluetoothGattService mTempService;
    BluetoothGattService mHumitService;
    BluetoothGattService mSleepService;
    BluetoothGattService mDataService;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    
    public static final int DATA_TYPE_TEMP_HUMIT = 1;
    public static final int DATA_TYPE_SLEEP = 2;
    public static final int DATA_TYPE_PM25 = 3;
    
  

    public final static String ACTION_GATT_CONNECTED =
            "com.nordicsemi.nrfUART.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.nordicsemi.nrfUART.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.nordicsemi.nrfUART.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.nordicsemi.nrfUART.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA_TEMP =
            "com.nordicsemi.nrfUART.EXTRA_DATA_TEMP";
    public final static String EXTRA_DATA_HUMIT =
            "com.nordicsemi.nrfUART.EXTRA_DATA_HUMIT";
    public final static String EXTRA_DATA_SLEEP =
            "com.nordicsemi.nrfUART.EXTRA_DATA_SLEEP";
    public final static String EXTRA_DATA_PM25 =
            "com.nordicsemi.nrfUART.EXTRA_DATA_PM25";
    public final static String EXTRA_TYPE =
            "com.nordicsemi.nrfUART.EXTRA_TYPE";
    public final static String DEVICE_DOES_NOT_SUPPORT_BLUETOOTH =
            "com.aizi.blueTooth.DEVICE_DOES_NOT_SUPPORT_BLUETOOTH";
    
    public static final UUID TX_POWER_UUID = UUID.fromString("00001804-0000-1000-8000-00805f9b34fb");
    public static final UUID TX_POWER_LEVEL_UUID = UUID.fromString("00002a07-0000-1000-8000-00805f9b34fb");
    public static final UUID CCCD = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");
    public static final UUID FIRMWARE_REVISON_UUID = UUID.fromString("00002a26-0000-1000-8000-00805f9b34fb");
    public static final UUID DIS_UUID = UUID.fromString("0000180a-0000-1000-8000-00805f9b34fb");
    public static final UUID RX_SERVICE_UUID = UUID.fromString("6e400001-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID RX_CHAR_UUID = UUID.fromString("6e400002-b5a3-f393-e0a9-e50e24dcca9e");
    public static final UUID TX_CHAR_UUID = UUID.fromString("6e400003-b5a3-f393-e0a9-e50e24dcca9e");
    
   
    // Implements callback methods for GATT events that the app cares about.  For example,
    // connection change and services discovered.
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                Log.e(TAG, "Connected to GATT server.");
                // Attempts to discover services after successful connection.
              Log.e(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                Log.e(TAG, "Disconnected from GATT server.");
                Log.e(TAG, "UartService disconnect 1");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
            	Log.e(TAG, "mBluetoothGatt = " + mBluetoothGatt );
            	
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
                Log.e(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
        	Log.e(TAG, "enableTXNotification2");
        	Log.e(TAG, "status = " + status);
        	Toast.makeText(getApplicationContext(), "status = " + status, Toast.LENGTH_SHORT).show();
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
        	gatt.getDevice();
        	BaseMessageHandler.acquireBaseData(gatt.getDevice(), characteristic);
        	handleNotificationData(characteristic);
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }
        
        private void handleNotificationData(BluetoothGattCharacteristic characteristic) {
            // TODO Auto-generated method stub
            byte[] notificationDataBytes = characteristic.getValue();
            if (notificationDataBytes[0] == 0xab) {
                
            }
            }

        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            boolean isSend = true;
            if (BLE_UUID_NUS_TX_CHARACTERISTIC.equals(characteristic.getUuid())) {
            if (BluetoothGatt.GATT_SUCCESS == status) {
                BaseMessageHandler.isWriteSuccess = true;
            } else {
                BaseMessageHandler.isWriteSuccess = false;
            }
            }
        };
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }
    
    private boolean isHeartRateInUINT16(final byte value) {
		return ((value & 0x01) != 0);
	}

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        
      //String text = new String(txValue, "UTF-8");
        try {
			Log.e(TAG, "text = " + (new String(characteristic.getValue(), "UTF-8")));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
       
        int hrValue;
        int humValue;
        //hrValue = characteristic.getValue();
        //Log.e(TAG, "hrvalue" + hrValue); 
        
     //   hrValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1);
        /*if (isHeartRateInUINT16(characteristic.getValue()[0])) {
			hrValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 1);
		} else {
			hrValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
			int batteryValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
		}*/
        
        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
        if (TEM_LEVEL_CHARACTERISTIC.equals(characteristic.getUuid())) {
            characteristic.getValue();
            hrValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            humValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
            Log.e(TAG, "TEM hrvalue = " + hrValue + "  humValue = " + humValue); 
            //Log.e(TAG, "characteristic = " + TEM_LEVEL_CHARACTERISTIC);
            intent.putExtra(EXTRA_TYPE, DATA_TYPE_TEMP_HUMIT);
            intent.putExtra(EXTRA_DATA_TEMP, hrValue);
            intent.putExtra(EXTRA_DATA_HUMIT, humValue);
           // Log.d(TAG, String.format("Received TX: %d",characteristic.getValue() ));
            
        } else if (SLEEP_LEVEL_CHARACTERISTIC.equals(characteristic.getUuid())) {
            Log.e(TAG, "characteristic = " + SLEEP_LEVEL_CHARACTERISTIC); 
            int hrValue1 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            int hrValue2 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
            int hrValue3 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 2);
            int hrValue4 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 3);
            int hrValue5 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 4);
            int hrValue6 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 5);
            int hrValue7 = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 6);
            
            Log.e(TAG, "SLEEP hrvalue = " + hrValue1 + " " + hrValue2 
                    + " " + hrValue3 + " "+ hrValue4 + " "+ hrValue5 
                    + " "+ hrValue6 + " "+ hrValue7); 
            intent.putExtra(EXTRA_TYPE, DATA_TYPE_SLEEP);
            intent.putExtra(EXTRA_DATA_SLEEP, hrValue1);   
        } else if (BLE_UUID_NUS_RX_CHARACTERISTIC.equals(characteristic.getUuid())) {
            //BaseMessageHandler.acquireBaseData(characteristic);
            hrValue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            intent.putExtra(EXTRA_TYPE, DATA_TYPE_PM25);
            intent.putExtra(EXTRA_DATA_PM25, hrValue);
            Log.e(TAG, "HR hrvalue = PM");
            //Log.e(TAG, "characteristic = " + HR_CHARACTERISTIC_UUID); 
        }
        sendBroadcast(intent);
    }

    public class LocalBinder extends Binder {
           public UartService getService() {
            return UartService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        EventBus.getDefault().register(this);
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
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
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }

        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }

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
            Log.e(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }

        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.e(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }

        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.e(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
    
        Log.e(TAG, "Trying to create a new connection.");
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
        mBluetoothGatt.disconnect();
       // mBluetoothGatt.close();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        Log.w(TAG, "mBluetoothGatt closed");
        mBluetoothDeviceAddress = null;
        mBluetoothGatt.close();
        mBluetoothGatt = null;
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
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
 
    
    public final static UUID HR_SERVICE_UUID = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb");
    private static final UUID HR_SENSOR_LOCATION_CHARACTERISTIC_UUID = UUID.fromString("00000002-0000-1000-8000-00805f9b34fb");
    private static final UUID HR_CHARACTERISTIC_UUID = UUID.fromString("00000003-0000-1000-8000-00805f9b34fb");
    
    public final static UUID BLE_UUID_NUS_SERVICE = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb");
    private static final UUID BLE_UUID_NUS_TX_CHARACTERISTIC = UUID.fromString("00000002-0000-1000-8000-00805f9b34fb");
    private static final UUID BLE_UUID_NUS_RX_CHARACTERISTIC = UUID.fromString("00000003-0000-1000-8000-00805f9b34fb");
  
    
	private final static UUID BATTERY_SERVICE = UUID.fromString("0000180F-0000-1000-8000-00805f9b34fb");
	private final static UUID BATTERY_LEVEL_CHARACTERISTIC = UUID.fromString("00002A19-0000-1000-8000-00805f9b34fb");
    
	
    private final static UUID TEM_SERVICE = UUID.fromString("00001880-0000-1000-8000-00805f9b34fb");
	private final static UUID TEM_LEVEL_CHARACTERISTIC = UUID.fromString("00002A1C-0000-1000-8000-00805f9b34fb");
	  
	private final static UUID SLEEP_SERVICE = UUID.fromString("00001890-0000-1000-8000-00805f9b34fb");
    private final static UUID SLEEP_LEVEL_CHARACTERISTIC = UUID.fromString("00002990-0000-1000-8000-00805f9b34fb");

    private static final String DEVICE_DOES_NOT_SUPPORT_UART = null;
    /**
     * Enable TXNotification
     *
     * @return 
     */
    public void enableBlueToothNotification(UUID serviceUuid, UUID characterUuid)
    { 
    	/*
    	if (mBluetoothGatt == null) {
    		showMessage("mBluetoothGatt null" + mBluetoothGatt);
    		broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
    		return;
    	}
    		*/
    	Log.e(TAG, "enableTXNotification1");
    	BluetoothGattService Service = mBluetoothGatt.getService(serviceUuid);
    	//BluetoothGattService Service = mBluetoothGatt.getService(BATTERY_SERVICE);
    	if (Service == null) {
            showMessage("Rx service not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
    	Log.e(TAG, "enableTXNotification2");
    	BluetoothGattCharacteristic CharacterChar = Service.getCharacteristic(characterUuid);
 //   	BluetoothGattCharacteristic CharacterChar = Service.getCharacteristic(BATTERY_LEVEL_CHARACTERISTIC);
    	if (CharacterChar == null) {
            showMessage("Tx charateristic not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
    	 mBluetoothGatt.setCharacteristicNotification(CharacterChar,true);
        
         BluetoothGattDescriptor descriptor = CharacterChar.getDescriptor(CCCD);
         descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        
        Log.e(TAG, "enableTXNotification3");
       
        mBluetoothGatt.writeDescriptor(descriptor);
    }
    
    
    public void  enableDataNotification() {

        mDataService = mBluetoothGatt.getService(BLE_UUID_NUS_SERVICE);
        if (mDataService == null) {
            showMessage("Rx service not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_BLUETOOTH);
            return;
        }
        mCharacterChar = mDataService.getCharacteristic(BLE_UUID_NUS_RX_CHARACTERISTIC);
        if (mCharacterChar == null) {
            showMessage("Tx charateristic not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_BLUETOOTH);
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(mCharacterChar,true);
        BluetoothGattDescriptor descriptor = mCharacterChar.getDescriptor(CCCD);
        descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
        mBluetoothGatt.writeDescriptor(descriptor);
    }
    

    
    public void writeRXCharacteristic(byte[] value)
    {
    	BluetoothGattService RxService = mBluetoothGatt.getService(RX_SERVICE_UUID);
    	showMessage("mBluetoothGatt null"+ mBluetoothGatt);
    	if (RxService == null) {
            showMessage("Rx service not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
    	BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(RX_CHAR_UUID);
        if (RxChar == null) {
            showMessage("Rx charateristic not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
        RxChar.setValue(value);
    	boolean status = mBluetoothGatt.writeCharacteristic(RxChar);
    	
        Log.e(TAG, "write TXchar - status=" + status);  
    }
    
    public void writeBaseRXCharacteristic(byte[] value)
    {
    
        BluetoothGattService RxService = mBluetoothGatt.getService(BLE_UUID_NUS_SERVICE);
        showMessage("mBluetoothGatt null"+ mBluetoothGatt);
        if (RxService == null) {
            showMessage("Rx service not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
        BluetoothGattCharacteristic RxChar = RxService.getCharacteristic(BLE_UUID_NUS_TX_CHARACTERISTIC);
        if (RxChar == null) {
            showMessage("Rx charateristic not found!");
            broadcastUpdate(DEVICE_DOES_NOT_SUPPORT_UART);
            return;
        }
        RxChar.setValue(value);
        boolean status = mBluetoothGatt.writeCharacteristic(RxChar);
        
        Log.e(TAG, "write TXchar - status=" + status);  
    }
    
    private void showMessage(String msg) {
        Log.e(TAG, msg);
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
    
    public void onEvent(Intent intent) {
        /*Object parcel = intent.getParcelableExtra("bsl2Msg");
        BaseL2Message bsl2Msg = null;
        if (parcel != null && (parcel instanceof BaseL2Message)) {
            bsl2Msg = (BaseL2Message) parcel;
            Toast.makeText(getApplicationContext(), "bsl2Message", Toast.LENGTH_SHORT).show();
        }*/
        
        String str = intent.getStringExtra("bsl2Msg");
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }
    
    
}
