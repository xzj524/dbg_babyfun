package com.xzj.babyfun.utility;

import android.R;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.xzj.babyfun.BabyEmergencyActivity;
import com.xzj.babyfun.BabyExplainActivity;

public class NotificationBuilderManager {
    public static final int MEDIA_BUILDER_ID = 8888;
    public static final String MEDIA_NOFIFY_DESCRIPTION = "富媒体消息：点击后下载与查看";
    private static String TAG = "NotificationBuilderManager";
    private static String SHARE_PRE_FILE = "notification_builder_storage";
    private static Object notification_builder_lock = new Object();
    private static int DEFAULT_ID = 0;

    public static Notification createNotification(Context context, int id,
            String title, String content, boolean noDisturb) {
        synchronized (notification_builder_lock) {
          //  PushNotificationBuilder builder = getBuilder(context, id);
          //  Builder builder = new Builder(context);
            
            Intent intent = new Intent(context, BabyEmergencyActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
           // startActivity(intent);
            int req = Long.valueOf(System.currentTimeMillis()).intValue();
            PendingIntent pendingIntent = PendingIntent.getActivity(context, req,  
                    intent, 0);  
            
            Notification notify = new Notification.Builder(context)  
            .setSmallIcon(R.drawable.ic_dialog_alert)
            .setTicker(content)  
            .setContentTitle(title)  
            .setContentText(content)  
            .setContentIntent(pendingIntent).build();
            notify.defaults = Notification.DEFAULT_ALL;
            notify.flags |= Notification.FLAG_AUTO_CANCEL;
                 
            return notify;
        }
    }
   }
