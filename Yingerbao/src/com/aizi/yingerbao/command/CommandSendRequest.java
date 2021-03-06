package com.aizi.yingerbao.command;

import android.content.Context;
import android.content.Intent;

import com.aizi.yingerbao.baseheader.BaseL2Message;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.utility.SendMessageHandler;

public class CommandSendRequest {

    private static final String TAG = "CommandSendRequest";
    
    private long requestId;
    private CommandCallback callback;
    private static int SLEEP_TIME = 1000;
    private Context mContext;
    private boolean mIsWaitResult;

    private static final Object synchronizedLock = new Object();
    private Intent returnIntent;
    private BaseL2Message mBL2Msg;

    public CommandSendRequest(Context context, Intent intent, CommandCallback callback) {
        this.callback = callback;
        requestId = System.currentTimeMillis();
        this.mContext = context;
    }

    public CommandSendRequest(Context context, Intent intent) {
        requestId = System.currentTimeMillis();
        this.mContext = context;
    }
    
    public CommandSendRequest(Context context, BaseL2Message bsl2Msg) {
        mContext = context;
        mBL2Msg = bsl2Msg;
        mIsWaitResult = true;
    }
    
    public CommandSendRequest(Context context, Boolean iswait, BaseL2Message bsl2Msg) {
        mContext = context;
        mBL2Msg = bsl2Msg;
        mIsWaitResult = iswait;
    }

    public void addSendTask() {
        try {
            CommandCenter.getInstance(mContext).addCallbackRequest(this);   
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }

    long getRequestId() {
        return requestId;
    }
    
    boolean getIsWaitReturn(){
        return mIsWaitResult;
    }

    public void send(boolean isrepeat) {
        try {
            SendMessageHandler.getInstance(mContext).sendL2Message(mContext, mBL2Msg, isrepeat, mIsWaitResult);
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }

    synchronized void finish() {
        callback = null;
        mContext = null;
    }

    public void onCallback(Intent intent) {
        if (callback != null) {
            callback.onCallback(0, intent);
        }
        returnIntent = intent;
        synchronized (synchronizedLock) {
            synchronizedLock.notifyAll();
        }
    }

}
