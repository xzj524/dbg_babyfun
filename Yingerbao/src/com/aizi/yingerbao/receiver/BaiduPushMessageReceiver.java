package com.aizi.yingerbao.receiver;

import java.util.List;

import android.content.Context;

import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.utility.PrivateParams;
import com.baidu.android.pushservice.PushMessageReceiver;

public class BaiduPushMessageReceiver extends PushMessageReceiver{
    
    public static final String TAG = "BaiduPushMessageReceiver";

    @Override
    public void onBind(Context context, int errorCode, String appid, String userId, String channelId, String requestId) {
        String responseString = "onBind errorCode=" + errorCode + " appid=" + appid
                + " userId=" + userId + " channelId=" + channelId + " requestId=" + requestId;
        SLog.e(TAG, responseString);
        
        PrivateParams.setSPString(context, Constant.BD_PUSH_CHANNELID, channelId);
    }

    @Override
    public void onDelTags(Context arg0, int arg1, List<String> arg2, List<String> arg3, String arg4) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onListTags(Context arg0, int arg1, List<String> arg2, String arg3) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onMessage(Context arg0, String arg1, String arg2) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onNotificationArrived(Context arg0, String arg1, String arg2, String arg3) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onNotificationClicked(Context arg0, String arg1, String arg2, String arg3) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onSetTags(Context arg0, int arg1, List<String> arg2, List<String> arg3, String arg4) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onUnbind(Context arg0, int arg1, String arg2) {
        // TODO Auto-generated method stub
        
    }

}
