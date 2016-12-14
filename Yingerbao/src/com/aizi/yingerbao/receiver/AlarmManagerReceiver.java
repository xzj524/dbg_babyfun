package com.aizi.yingerbao.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.utility.BaseMessageHandler;
import com.aizi.yingerbao.utility.PrivateParams;

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
                        case Constant.ALARM_WAIT_L1: // 接收L1数据超时
                            BaseMessageHandler.mL1squenceid = -1;
                            // 清空L2数据缓存区
                            BaseMessageHandler.clearL2RecvByte();
                            break;
                         case Constant.ALARM_WAIT_CHECK_DEVICE: // 设备验证超时 
                             if (PrivateParams.getSPInt(context, "check_device_status", 0) == 1) {
                                 handleTimeOut(context, 1);
                             } 
                            break;
                         case Constant.ALARM_WAIT_SYNC_DATA: // 同步数据超时 
                             if (PrivateParams.getSPInt(context, "sync_data_status", 0) == 1) {
                                 handleTimeOut(context, 2);
                             } 
                            break;
                         case Constant.ALARM_WAIT_SEARCH_DEVICE: // 搜索设备超时 
                             if (PrivateParams.getSPInt(context, "search_device_status", 0) == 1) {
                                 PrivateParams.setSPInt(context, "connect_interrupt", 1);
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
        
        SLog.e(TAG, "setAlarm  time out " + totype);
    }

}
