package com.aizi.yingerbao.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aizi.yingerbao.service.BluetoothService;

public class BabyStatusReceiver extends BroadcastReceiver{

    private static final String TAG = "BabyStatusReceiver";
    private static DataInteraction dataInteraction;
    private static DataStatusInteraction dataStatusInteraction;

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        Log.e(TAG, "UART_CONNECT_MSG   getAction"  + action);

      //*********************//
        if (action.equals(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED)) {
            Log.e(TAG, "ACTION_GATT_SERVICES_DISCOVERED 1");
            
            dataInteraction.startNotification(intent);
          
            
        } else if (action.equals(BluetoothService.ACTION_DATA_AVAILABLE)) {
            dataInteraction.setData(intent);
            if (dataStatusInteraction != null) {
                dataStatusInteraction.setData(intent);
            }
            
        } else if (action.equals(BluetoothService.ACTION_GATT_DISCONNECTED)) {
            dataInteraction.setData(intent);
            Log.e(TAG, "UartService disconnect 2");
        }
     }
    
    
    public interface DataInteraction {
        public void startNotification(Intent intent);
        public void setData(Intent intent);
    }

    public void setBRInteractionListener(DataInteraction brInteraction) {
        this.dataInteraction = brInteraction;
    }
    
    
    public interface DataStatusInteraction {
       // public void startNotification(Intent intent);
        public void setData(Intent intent);
    }

    public void setDataStatusInteractionListener(DataStatusInteraction brInteraction) {
        this.dataStatusInteraction = brInteraction;
    }
}
