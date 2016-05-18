package com.xzj.babyfun.utility;

import java.io.ByteArrayInputStream;

import android.content.Context;
import android.content.Intent;
import android.hardware.ConsumerIrManager;
import android.os.Handler;

import com.xzj.babyfun.baseheader.BaseL2Message;
import com.xzj.babyfun.constant.Constant;

import de.greenrobot.event.EventBus;

public class MessageParse {
    
    /** single instance. */
    private static volatile MessageParse mInstance;   
    protected MessageParse(Context context) {
        EventBus.getDefault().register(this); 
    }

    
    public static MessageParse getInstance(Context context) {
        if (mInstance != null) {
            return mInstance;
        } else {
            mInstance = new MessageParse(context);
            return mInstance;
        }
    }
    
    public void onEvent(Intent intent) {
        Object parcel = intent.getParcelableExtra(Constant.BASE_L2_MESSAGE);
        BaseL2Message bMsg = null;
        if (parcel != null && (parcel instanceof BaseL2Message)) {
            bMsg = (BaseL2Message) parcel;
        }
        
        if (bMsg == null) {
            return;
        }
        
        handleL2Msg(bMsg);
        
        
        
        //Toast.makeText(mContext, "123456", Toast.LENGTH_SHORT).show();
    }


    private void handleL2Msg(BaseL2Message bMsg) {
        // TODO Auto-generated method stub
        switch (bMsg.commanID) {
        case Constant.COMMAND_ID_UPDATE_ROM:
            
            break;
        case Constant.COMMAND_ID_SETTING:
            ByteArrayInputStream settingInputStream = new ByteArrayInputStream(bMsg.payload);
           
            break;
        case Constant.COMMAND_ID_BIND:
            
            break;
        case Constant.COMMAND_ID_DATA:
            
            break;

        default:
            break;
        }
    }
}
