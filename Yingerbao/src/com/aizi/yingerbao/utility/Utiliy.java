package com.aizi.yingerbao.utility;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import android.R.transition;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.provider.SyncStateContract.Constants;

import com.aizi.yingerbao.ConnectDeviceActivity;
import com.aizi.yingerbao.R;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.logging.SLog;

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

    private static final String TAG = "Utiliy";
  
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
    
    
    /** 
     * @Description: 显示连接对话框
     * @Context
     */
    
    public static void showNormalDialog(final Context context){
        
        final AlertDialog.Builder normalDialog = 
            new AlertDialog.Builder(context);
        normalDialog.setIcon(R.drawable.yingerbao_96);
        normalDialog.setTitle("连接设备");
        normalDialog.setMessage("设备未连接，是否连接设备,\n请先摇动设备保证能够正确连接。");
        normalDialog.setPositiveButton("确定", 
            new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, ConnectDeviceActivity.class);
                context.startActivity(intent);
            }
        });
        normalDialog.setNegativeButton("取消", 
            new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //...To-do
            }
        });
        // 显示
        normalDialog.show();
    }
    
    
    
    
    /** 
     * @Description: 显示连接对话框
     * @Context
     */
    
    public static boolean isBluetoothReady(Context context){
        boolean res = false;
        int bluetoothstatus = PrivateParams.getSPInt(context, Constant.BLUETOOTH_IS_READY, 0);
        if (bluetoothstatus == 1) {
            res = true;
        } else if (bluetoothstatus == 0) {
            res = false;
        }
        return res;
    }

    
    public static synchronized void logToFile(String logStr) {

        try {
            String time = new SimpleDateFormat("yyyy-MM-dd ").format(new Date());
            Calendar calendar = Calendar.getInstance();
            String currentDateTimeString = "[" + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                    + calendar.get(Calendar.MINUTE) + ":"
                    + calendar.get(Calendar.SECOND) + ":"
                    + calendar.get(Calendar.MILLISECOND)
                    + "]: ";
            String writeStr = time + " " + currentDateTimeString + " " + logStr + "\n\r";
            String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            File dir = new File(sdPath + "/" +Constant.EXTERNAL_FILE_LOC);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
            Date today = new Date();
            String date = dateformat.format(today);
            File logFile = new File(sdPath, Constant.EXTERNAL_FILE_LOC + "/" + "aizi_" + date + ".log");
            FileWriter fw = new FileWriter(logFile, true);
            fw.write(writeStr);
            fw.close();
        } catch (Throwable e) {
            SLog.e(TAG, e);
        }
    }

    
    public static synchronized void dataToFile(String logStr) {

        try {
            String time = new SimpleDateFormat("yyyy-MM-dd ").format(new Date());
            Calendar calendar = Calendar.getInstance();
            String currentDateTimeString = "[" + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                    + calendar.get(Calendar.MINUTE) + ":"
                    + calendar.get(Calendar.SECOND) + ":"
                    + calendar.get(Calendar.MILLISECOND)
                    + "]: ";
            String writeStr = time + " " + currentDateTimeString + " " + logStr + "\n\r";
            String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            File dir = new File(sdPath + "/" +Constant.EXTERNAL_FILE_DATA);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
            Date today = new Date();
            String date = dateformat.format(today);
            File logFile = new File(sdPath, Constant.EXTERNAL_FILE_DATA + "/" + "aizi_data_" + date + ".log");
            FileWriter fw = new FileWriter(logFile, true);
            fw.write(writeStr);
            fw.close();
        } catch (Throwable e) {
            SLog.e(TAG, e);
        }
    }

    
    public static synchronized void temperatureToFile(String logStr) {

        try {

            String writeStr = logStr + "\n";
            String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            File dir = new File(sdPath + "/" +Constant.EXTERNAL_FILE_DATA);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");
            Date today = new Date();
            String date = dateformat.format(today);
            File logFile = new File(sdPath, Constant.EXTERNAL_FILE_DATA + "/" + "aizi_temperature_data_" + date + ".log");
            FileWriter fw = new FileWriter(logFile, true);
            fw.write(writeStr);
            fw.close();
        } catch (Throwable e) {
            SLog.e(TAG, e);
        }
    }
    
    private static int logEanbled = -1;

    private static boolean ableLogToFile(Context context) {
        if (logEanbled == -1) {
            logEanbled = checkPermission(context, "android.permission.WRITE_EXTERNAL_STORAGE") ? 0 : 1;
        }
        return (logEanbled == 0);
    }
    
    public static boolean checkPermission(Context context, String permission) {
        PackageManager pm = context.getPackageManager();
        boolean granted = (PackageManager.PERMISSION_GRANTED == pm
                .checkPermission(permission, context.getPackageName()));
        return granted;
    }
    
    
    public static boolean isBluetoothConnected(Context context) {
        boolean isConnected = false;
        try {
            BluetoothManager mBluetoothManager 
                = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                SLog.e(TAG, "Unable to initialize BluetoothManager.");
                return isConnected;
            }
            
            List<BluetoothDevice> devices = mBluetoothManager.getConnectedDevices(BluetoothProfile.GATT);
            for (BluetoothDevice device : devices) {
                SLog.e(TAG, "pairedDevices name = " + device.getName());
                if (device.getName().equals(Constant.AIZI_DEVICE_TAG)) {
                    int state = mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT);
                    if (state == BluetoothProfile.STATE_CONNECTED) {
                        isConnected = true;
                    }
                }
              }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
        return isConnected;  
  }

}
