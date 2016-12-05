package com.aizi.yingerbao.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.utility.BaseMessageHandler;
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
                        case 1:
                            SLog.e(TAG, "Receive Base L1 Alarm");
                            BaseMessageHandler.mIsReceOver = true;
                            BaseMessageHandler.mL1squenceid = -1;
                            if (BaseMessageHandler.mL2OutputStream != null) {
                                BaseMessageHandler.mL2OutputStream.reset();
                                BaseMessageHandler.mL2OutputStream.close();  
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

}
