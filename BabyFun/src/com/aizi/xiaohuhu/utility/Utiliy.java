package com.aizi.xiaohuhu.utility;

import java.util.ArrayList;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.SyncStateContract.Constants;

/*
* @author xuzejun
* @since 2016-4-2
*/
public class Utiliy {
    
    public static ArrayList<Integer> mSleepList = new ArrayList<Integer>();

    /** 用户点击private notification发送的广播 */
    public static final String ACTION_PRIVATE_NOTIFICATION_CLICK = "com.baidu.android.pushservice.action.privatenotification.CLICK";
    /** 用户删除private notification发送的广播 */
    public static final String ACTION_PRIVATE_NOTIFICATION_DELETE = "com.baidu.android.pushservice.action.privatenotification.DELETE";
  
    /**
     * 睡眠的状态
     */
    public static enum CheckingState {
        IDEL, // 清醒状态
        FALLASLEEP, //入睡
        SHALLOWSLEEP, //浅睡眠
        DEEPSLEEP, //深睡眠
        LITTLESLEEP //小睡
    }
    
    public static void showFeverNotification(Context context,
            String title, String content,
            String customcontent) {
        NotificationManager nmNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        /*Intent clickIntent = new Intent();
        clickIntent.setClassName(pkgName, serviceName);
        clickIntent.setAction(ACTION_PRIVATE_NOTIFICATION_CLICK);
        clickIntent.setData(Uri.parse("content://" + msgId));
        //clickIntent.putExtra(Constants.EXTRA_PUBLIC_MSG, pMsg);
        clickIntent.putExtra("app_id", appId);
        clickIntent.putExtra("msg_id", msgId);
        PendingIntent clickPendingIntent = PendingIntent.getService(context, 0,
                clickIntent, 0);

        Intent deleteIntent = new Intent();
        deleteIntent.setClassName(pkgName, serviceName);
        deleteIntent.setAction(ACTION_PRIVATE_NOTIFICATION_DELETE);
        deleteIntent.setData(Uri.parse("content://" + msgId));
        //deleteIntent.putExtra(Constants.EXTRA_PUBLIC_MSG, pMsg);
        deleteIntent.putExtra("app_id", appId);
        deleteIntent.putExtra("msg_id", msgId);
        PendingIntent deletePendingIntent = PendingIntent.getService(context,
                0, deleteIntent, 0);
*/
        Notification notif = null;
        //boolean noDisturb = Utility.isNoDisturb(context, pMsg.mPkgName);
   /*     if (pMsg.mNotificationBuilder == 0) {
            notif = NotificationBuilderManager.createNotification(context,
                    pMsg.mNotificationBuilder, pMsg.mNotificationBasicStyle,
                    pMsg.mTitle, pMsg.mDescription, noDisturb);
        } else {
            notif = NotificationBuilderManager.createNotification(context,
                    pMsg.mNotificationBuilder, pMsg.mTitle, pMsg.mDescription,
                    noDisturb);
        }*/
        
        notif = NotificationBuilderManager.createNotification(context, 0, title, content, false);
        //notif.contentIntent = clickPendingIntent;
        //notif.deleteIntent = deletePendingIntent;
        //nmNotificationManager.notify(0, notif);
        nmNotificationManager.notify(System.currentTimeMillis() + "", 0, notif);
        //nmNotificationManager.notify("1232", 0, notif);
        // send a broadcast when notifications arrive at client
       // sendNotificationArrivedReceiver(context, pkgName, pMsg);
    }
    
    public static void showBreathNotification(Context context,
            String title, String content,
            String customcontent) {
        NotificationManager nmNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notif = null;
        
        notif = NotificationBuilderManager.createNotification(context, 0, title, content, false);
        int req = Long.valueOf(System.currentTimeMillis()).intValue();
        nmNotificationManager.notify(req, notif);
    }


}
