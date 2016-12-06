package com.aizi.yingerbao.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.aizi.yingerbao.ConnectDeviceActivity;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.deviceinterface.DeviceFactory;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.utility.BaseMessageHandler;
import com.aizi.yingerbao.utility.PrivateParams;
import com.aizi.yingerbao.utility.Utiliy;

public class AlarmManagerReceiver extends BroadcastReceiver{
    
    public static final String TAG = AlarmManagerReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        try {
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(Constant.ACTION_ALARM_MESSAGE)) {
                    if (intent.hasExtra(Constant.ALARM_WAIT_TYPE)) {
                        int waittype = intent.getIntExtra(Constant.ALARM_WAIT_TYPE, 0);
                        switch (waittype) {
                        case 1: // 接收L1数据超时
                            SLog.e(TAG, "Receive Base L1 Alarm");
                            BaseMessageHandler.mIsReceOver = true;
                            BaseMessageHandler.mL1squenceid = -1;
                            if (BaseMessageHandler.mL2OutputStream != null) {
                                BaseMessageHandler.mL2OutputStream.reset();
                                BaseMessageHandler.mL2OutputStream.close();  
                            }
                            break;
                         case 2: // 设备验证超时 
                             if (PrivateParams.getSPInt(context, "check_device_status", 0) == 1) {
                                 handleTimeOut(context, 1);
                             } 
                            break;
                         case 3: // 同步数据超时 
                             if (PrivateParams.getSPInt(context, "sync_data_status", 0) == 1) {
                                 handleTimeOut(context, 2);
                             } 
                            break;
                         case 4: // 搜索设备超时 
                             if (PrivateParams.getSPInt(context, "search_device_status", 0) == 1) {
                                 handleTimeOut(context, 3);
                             } 
                            break;
                        default:
                            break;
                        }
                    }
                }
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }

    private void handleTimeOut(Context context, int totype) {
        Intent intent = new Intent(Constant.ACTION_DEVICE_CONNECT_RECEIVER);
        intent.putExtra(Constant.DEVICE_CONNECT_DELAY_TYPE, totype);
        context.sendBroadcast(intent);
        
        SLog.e(TAG, "setAlarm  time out");
    }

}
