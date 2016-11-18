package com.aizi.yingerbao.command;

import android.content.Context;
import android.content.Intent;
import android.provider.SyncStateContract.Constants;
import android.text.TextUtils;

import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.thread.AZRunnable;
import com.aizi.yingerbao.thread.ThreadPool;

public class CommandSendRequest {

    private long requestId;
    private CommandCallback callback;
    // mill seconds
    private static int SLEEP_TIME = 1000;
    private Context context;
    private Intent intent;
    private static final String TAG = "CrossAppRequest";
    private static final Object synchronizedLock = new Object();
    private Intent returnIntent;

    public CommandSendRequest(Context context, Intent intent, CommandCallback callback) {
        this.callback = callback;
        requestId = System.currentTimeMillis();
        this.context = context;
        this.intent = intent;
    }

    public CommandSendRequest(Context context, Intent intent) {
        requestId = System.currentTimeMillis();
        this.context = context;
        this.intent = intent;
    }

    long getRequestId() {
        return requestId;
    }

    public CommandReply send() {
        /*intent.putExtra(PushConstants.CROSS_REQUEST_KEY_REQUEST_SOURCE_PACKAGE, context.getPackageName());
        intent.putExtra(PushConstants.CROSS_REQUEST_KEY_REQUEST_ID, requestId);
        intent.putExtra(PushConstants.CROSS_REQUEST_KEY_NEED_CALLBACK, true);
        intent.putExtra(PushConstants.CROSS_REQUEST_KEY_SENDING_REQUEST, true);
*/
        CommandCenter.addCallbackRequest(this);
        context.startService(intent);
        CommandReply reply = new CommandReply();
        SLog.d(TAG, "send crossapprequest: " + intent.toUri(0));
        AZRunnable timeOutRunnable = new AZRunnable("timeOutRunnable-" + requestId, AZRunnable.RUNNABLE_TIMER) {
            @Override
            public void brun() {
                try {
                    Thread.sleep(SLEEP_TIME);
                    synchronized (synchronizedLock) {
                        synchronizedLock.notifyAll();
                    }
                } catch (InterruptedException e) {
                    SLog.v(TAG, "result return, interrupted by callback");
                }
            }
        };
        ThreadPool.getInstance().submitRunnable(timeOutRunnable);
        if (callback == null) {
            synchronized (synchronizedLock) {
                try {
                    synchronizedLock.wait();

                } catch (Exception e) {
                    SLog.v(TAG, "wait exception: " + e);
                }
            }
            finish();
            if (returnIntent != null) {
                /*reply.setReplyCode(returnIntent.getIntExtra(PushConstants.CROSS_REQUEST_KEY_RESULT_CODE,
                        Constants.MSG_ERROR_ARRIVE_RECEIVER));
                if (returnIntent.hasExtra(PushConstants.CROSS_REQUEST_KEY_RESULT_DATA)) {
                    String resultString = returnIntent.getStringExtra(PushConstants.CROSS_REQUEST_KEY_RESULT_DATA);
                    if (!TextUtils.isEmpty(resultString)) {
                        reply.setReplyData(resultString.getBytes());
                    }
                }*/
            } else {
                //reply.setReplyCode(Constants.MSG_ERROR_DELIVER_TIMEOUT);
            }
        }
        return reply;
    }

    synchronized void finish() {
        callback = null;
        context = null;
        CommandCenter.removeCallbackRequest(requestId);
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
