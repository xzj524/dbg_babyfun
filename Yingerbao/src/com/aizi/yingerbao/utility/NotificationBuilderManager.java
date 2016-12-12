package com.aizi.yingerbao.utility;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.aizi.yingerbao.BabyBreathEmergencyActivity;
import com.aizi.yingerbao.BabyFeverEmergencyActivity;
import com.aizi.yingerbao.R;


public class NotificationBuilderManager {
    public static final int MEDIA_BUILDER_ID = 8888;
    private static String TAG = "NotificationBuilderManager";
    private static String SHARE_PRE_FILE = "notification_builder_storage";
    private static Object notification_builder_lock = new Object();
    private static int DEFAULT_ID = 0;

    public static Notification createFeverNotification(Context context, int id,
            String title, String content, String customcontent, boolean noDisturb) {
        synchronized (notification_builder_lock) {
            Intent intent = new Intent(context, BabyFeverEmergencyActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            intent.putExtra("custom_content", customcontent);
            int req = Long.valueOf(System.currentTimeMillis()).intValue();
            PendingIntent pendingIntent = PendingIntent.getActivity(context, req,  
                    intent, 0);  
            
            Notification notify = new Notification.Builder(context)  
            .setSmallIcon(R.drawable.yingerbao_512)
            .setTicker(content)  
            .setContentTitle(title)  
            .setContentText(content)  
            .setContentIntent(pendingIntent).build();
            notify.defaults = Notification.DEFAULT_ALL;
            notify.flags |= Notification.FLAG_AUTO_CANCEL;
                 
            return notify;
        }
    }
    
    public static Notification createBreathNotification(Context context, int id,
            String title, String content, String customcontent, boolean noDisturb) {
        synchronized (notification_builder_lock) {
            Intent intent = new Intent(context, BabyBreathEmergencyActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            intent.putExtra("custom_content", customcontent);
            int req = Long.valueOf(System.currentTimeMillis()).intValue();
            PendingIntent pendingIntent = PendingIntent.getActivity(context, req,  
                    intent, 0);  
            
            Notification notify = new Notification.Builder(context)  
            .setSmallIcon(R.drawable.yingerbao_512)
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
