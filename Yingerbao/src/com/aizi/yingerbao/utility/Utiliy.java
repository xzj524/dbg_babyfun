package com.aizi.yingerbao.utility;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListListener;

import com.aizi.yingerbao.BabyBreathEmergencyActivity;
import com.aizi.yingerbao.BabyFeverEmergencyActivity;
import com.aizi.yingerbao.ConnectDeviceActivity;
import com.aizi.yingerbao.R;
import com.aizi.yingerbao.baseheader.BaseL2Message;
import com.aizi.yingerbao.baseheader.KeyPayload;
import com.aizi.yingerbao.command.CommandCenter;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.database.BreathDataInfo;
import com.aizi.yingerbao.database.TemperatureDataInfo;
import com.aizi.yingerbao.database.YingerbaoDatabase;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.receiver.AlarmManagerReceiver;
import com.aizi.yingerbao.thread.AZRunnable;
import com.aizi.yingerbao.thread.ThreadPool;

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
        Notification notif = NotificationBuilderManager.createFeverNotification(context, 0, title, content, customcontent, false);
        nmNotificationManager.notify(System.currentTimeMillis() + "", 0, notif);
    }
    
    public static void showBreathNotification(Context context,
            String title, String content,
            String customcontent) {
        NotificationManager nmNotificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notif = NotificationBuilderManager.createBreathNotification(context, 0, title, content, null, false);
        int req = Long.valueOf(System.currentTimeMillis()).intValue();
        nmNotificationManager.notify(req, notif);
    }
    
    
    /** 
     * @Description: 显示连接对话框
     * @Context
     */
    
    public static void showConnectDialog(final Context context){
        
        final AlertDialog.Builder normalDialog = 
            new AlertDialog.Builder(context);
        normalDialog.setIcon(R.drawable.yingerbao_96);
        normalDialog.setTitle("连接设备");
        normalDialog.setMessage("设备未连接，是否连接设备?\n请先摇动设备保证能够正确连接。");
        //normalDialog.setTitle(title);
        //normalDialog.setMessage(content);
        normalDialog.setPositiveButton("确定", 
            new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(context, ConnectDeviceActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
    
    public static void showQuitDialog(final Context context, String title, String content){
        
        final AlertDialog.Builder normalDialog = 
            new AlertDialog.Builder(context);
        normalDialog.setIcon(R.drawable.yingerbao_96);
        normalDialog.setTitle("退出应用");
        normalDialog.setMessage("确定退出应用？");
        normalDialog.setPositiveButton("确定", 
            new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
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
           
            File logFile = new File(sdPath, Constant.EXTERNAL_FILE_DATA + "/" + "aizi_data_" + time + ".log");
            FileWriter fw = new FileWriter(logFile, true);
            fw.write(writeStr);
            fw.close();
        } catch (Exception e) {
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
                String devname = device.getName();
                if (!TextUtils.isEmpty(devname)) {
                    if (devname.equals(Constant.AIZI_DEVICE_TAG)
                            || devname.equals(Constant.AIZI_DEVICE_TEST_TAG)) {
                        int state = mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT);
                        if (state == BluetoothProfile.STATE_CONNECTED) {
                            isConnected = true;
                            break;
                        }
                    }
                }
              }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
        return isConnected;  
  }
    
    
    /**
     * 把16进制字符串转换成字节数组
     * @param hexString
     * @return byte[]
     */
    public static byte[] hexStringToByte(String hex) {
            byte[] result = null;
            try {
                if (!TextUtils.isEmpty(hex)) {
                    hex = hex.toUpperCase(); 
                    int len = (hex.length() / 2);
                    result = new byte[len];
                    char[] achar = hex.toCharArray();
                    if (achar != null) {
                        for (int i = 0; i < len; i++) {
                            int pos = i * 2;
                            if (pos >= 0 && pos + 1 < achar.length) {
                                if (i >= 0 && i < len) {
                                    result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
                                }
                           } 
                       }
                    }
                }
            } catch (Exception e) {
                SLog.e(TAG, e);
            }
            return result;
       }
      
      private static int toByte(char c) {
         byte b = (byte) "0123456789ABCDEF".indexOf(c);
         return b;
      }

      /**
       * 根据commandid，version等信息组合成baseL2msg
       * 
       * @return BaseL2Message
       */
      public static BaseL2Message generateBaseL2Msg(short commandid, short version, 
              KeyPayload keyPayload){
          BaseL2Message bsl2Msg = new BaseL2Message();
          try {
              bsl2Msg.commanID = commandid;
              bsl2Msg.versionCode = version;
              bsl2Msg.payload = new byte[keyPayload.keyLen + 3];
              System.arraycopy(keyPayload.toByte(), 0, bsl2Msg.payload, 0, keyPayload.keyLen + 3);
          } catch (Exception e) {
              SLog.e(TAG, e);
          }
          return bsl2Msg;
      }
      
      /**
       * 将指定byte数组以16进制的形式打印到控制台
       * 
       * @param hint
       *            String
       * @param b
       *            byte[]
       * @return void
       */
      public static String printHexString(byte[] b) {
          String hexString = "";
          for (int i = 0; i < b.length; i++)
          {
              String hex = Integer.toHexString(b[i] & 0xFF);
              if (hex.length() == 1)
              {
                  hex = '0' + hex;
              }
              
              hexString += " " + hex;
          }
          return hexString;
      }
      
      
      private String getPhoneBlueAddress(Context context) {
          String phoneaddress = null;
          try {
              BluetoothManager mBluetoothManager 
                  = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
              if (mBluetoothManager == null) {
                  SLog.e(TAG, "Unable to initialize BluetoothManager.");
                  return phoneaddress;
              }
              
              phoneaddress = mBluetoothManager.getAdapter().getAddress();
          } catch (Exception e) {
              SLog.e(TAG, e);
          }
          return phoneaddress;  
      }

    public static void reflectTranDataType(Context context, int res) {
        try {
            SLog.e(TAG, "reflectTranDataType " + res);
            CommandCenter.getInstance(context).handleIntent(res);
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }
    
    /**
     * 获取Receiver接收的intent
     *
     * @param context
     * @param intent
     * @param action
     *
     * @return 返回intent
     *
     * @author xuzejun01
     */
    public static Intent getReceiverIntent(Context context, Intent intent,
                                              String action) {

        intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
        intent.setAction(action);
        intent.setClass(context, AlarmManagerReceiver.class);
        return intent;
    }
    
    /**
     * 获取定时闹钟pendingIntent
     * 
     * @return pendingIntent
     */
    public static PendingIntent getRepeatAlarmPendingIntent(Context context) {
        Intent intent = new Intent();
        intent = getReceiverIntent(context, intent, Constant.ACTION_ALARM_MESSAGE);
        intent.putExtra(Constant.ALARM_WAIT_TYPE, 4); // 设置定时读取设备数据类型

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        return alarmPendingIntent;
    }
    
    public static PendingIntent getDelayPendingIntent(final Context context, int waittype) {
        Intent intent = new Intent();
        intent = getReceiverIntent(context, intent, Constant.ACTION_ALARM_MESSAGE);
        intent.putExtra(Constant.ALARM_WAIT_TYPE, waittype);

        PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(
                context, waittype, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        
        return alarmPendingIntent;
    }
    
    public static void setDelayAlarm(final Context context, long waittime, PendingIntent pdIntent) {
        try {
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            long expiredtime = System.currentTimeMillis() + waittime;
            // 设置定时器
            if (Build.VERSION.SDK_INT < 19) {
                alarm.set(AlarmManager.RTC_WAKEUP, expiredtime, pdIntent);
            } else if (Build.VERSION.SDK_INT >= 19) {
                alarm.setExact(AlarmManager.RTC_WAKEUP, expiredtime, pdIntent);
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }
    
    public static void setRepeatAlarm(final Context context, long repeattime, PendingIntent pdIntent) {
        try {
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            long firsttime = System.currentTimeMillis() + repeattime;
            // 设置定时器
            alarm.setRepeating(AlarmManager.RTC_WAKEUP, firsttime, repeattime, pdIntent);
            SLog.e(TAG, "setAlarm  repeat = " + repeattime);
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }

    public static void cancelAlarmPdIntent(Context context, PendingIntent pdintent) {
        try {
            AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (pdintent != null) {
                alarm.cancel(pdintent);
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }

    public static String getAppVersionName(Context context) {
        try {  
            PackageInfo pi=context.getPackageManager().getPackageInfo(context.getPackageName(), 0);  
            return pi.versionName;  
        } catch (Exception e) {  
            SLog.e(TAG, e);
        }  
        return null;
    }

    public static void saveLog(String descrip, String payload) {
        Utiliy.logToFile(descrip + payload);
        SLog.e(TAG, descrip + payload);      
    }

    public static void initCurrentDataDate(final Context context) {
        try {
            new Thread(new Runnable() {
                
                @Override
                public void run() {
                    Calendar calendar = Calendar.getInstance();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH) + 1;
                    int day = calendar.get(Calendar.DAY_OF_MONTH);
                    
                    PrivateParams.setSPInt(context, Constant.DATA_DATE_YEAR, year);
                    PrivateParams.setSPInt(context, Constant.DATA_DATE_MONTH, month);
                    PrivateParams.setSPInt(context, Constant.DATA_DATE_DAY, day);
                }
            }).start();
            
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }

    public static void cancelAlarmNotify(Context context) {
        VibratorUtil.StopVibrate(context);
        MediaUtil.getInstance(context).stopAlarm();
    }
    
    
    public static void showEmergencyBreath(Context context,
            String title, String content, String customcontent) {
            Intent intent = new Intent(context, BabyBreathEmergencyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            intent.putExtra("custom_content", customcontent);
            
            context.startActivity(intent);
    }
    
    public static void showEmergencyFever(Context context,
            String title, String content, String customcontent) {
            Intent intent = new Intent(context, BabyFeverEmergencyActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("title", title);
            intent.putExtra("content", content);
            intent.putExtra("custom_content", customcontent);
            
            context.startActivity(intent);
    }

    public static String getPhoneImei(Context context) {
        String imei = null;
        try {
            if (context != null) {
                TelephonyManager tm = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE); 
                imei = tm.getDeviceId();
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
        return imei;
    }

    public static String getPhoneNumber(Context mContext) {
        String phonenum = null;
        try {
            BmobUser bmobUser = BmobUser.getCurrentUser();
            if (bmobUser != null) {
                phonenum =  bmobUser.getMobilePhoneNumber();
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
        return phonenum;
    }

    public static String getDeviceId(Context context) {
        String deviceid = null;
        try {
            deviceid = PrivateParams.getSPString(context, Constant.AIZI_DEVICE_ADDRESS);
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
        return deviceid;
    }
    
    public static void sendSavedBreathData(final Context context) {
        ThreadPool.getInstance().submitRunnable(
                new AZRunnable("sendBreathDataStatisticData", AZRunnable.RUNNABLE_HTTP_STATISTIC) {

                    @Override
                    public void brun() {
                        sendBreathDataStatistics(context);
                    }

                });
    }
    

    private static void sendBreathDataStatistics(final Context context) {
        try {
            long currentTime = System.currentTimeMillis();
            long lastSendTime = getLastSendStatisticTime(context);
            
            List<BreathDataInfo> breathinfos 
                       = YingerbaoDatabase.getBreathInfoEnumClassList(context, currentTime, lastSendTime);
            SLog.e(TAG, "breathinfos size = " + breathinfos.size()
                    + " currenttime = " + currentTime
                    + " lastsendtime = " + lastSendTime);
            List<BmobObject> breathbmobinfos = new ArrayList<BmobObject>();
            for (BreathDataInfo breathinfo : breathinfos) {
                breathbmobinfos.add(breathinfo);
                if (breathbmobinfos.size() == 50) {
                    bmobBatchData(breathbmobinfos);
                    breathbmobinfos.clear();
                    setLastSendStatisticTime(context);
                    Thread.sleep(500);
                }
            }
            SLog.e(TAG, "breathbmobinfos size = " + breathbmobinfos.size());
            if (breathbmobinfos.size() > 0 && breathbmobinfos.size() < 50) {
                bmobBatchData(breathbmobinfos);
                breathbmobinfos.clear();
                setLastSendStatisticTime(context);
            }
            
            
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }
    
    public static void sendSavedTempData(final Context context) {
        ThreadPool.getInstance().submitRunnable(
                new AZRunnable("sendTempDataStatisticData", AZRunnable.RUNNABLE_HTTP_STATISTIC) {

                    @Override
                    public void brun() {
                        sendTempDataStatistics(context);
                    }
                });
    }
    
    private static void sendTempDataStatistics(Context context) {
        try {
            long currentTime = System.currentTimeMillis();
            long lastSendTime = getLastSendStatisticTime(context);
            
            List<TemperatureDataInfo> tempinfos 
                       = YingerbaoDatabase.getTemperatureInfoEnumClassList(context, currentTime, lastSendTime);
            List<BmobObject> tempbmobinfos = new ArrayList<BmobObject>();
            for (TemperatureDataInfo tempinfo : tempinfos) {
                tempbmobinfos.add(tempinfo);
                if (tempbmobinfos.size() == 50) {
                    bmobBatchData(tempbmobinfos);
                    tempbmobinfos.clear();
                    setLastSendStatisticTime(context);
                    Thread.sleep(500);
                }
            }
            
            if (tempbmobinfos.size() > 0 && tempbmobinfos.size() < 50) {
                bmobBatchData(tempbmobinfos);
                tempbmobinfos.clear();
                setLastSendStatisticTime(context);
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
       
    }

    
    
    
    
    private static void setLastSendStatisticTime(Context context) {
        if (context == null) {
            SLog.w(TAG, "setLastSendStatisticTime mContext == null");
            return;
        }
        PrivateParams.setSPLong(context, Constant.CUR_STATISTIC_TIME, System.currentTimeMillis());
    }
    
    private static long getLastSendStatisticTime(Context context) {
        if (context == null) {
            SLog.e(TAG, "getLastSendStatisticTime mContext == null");
            return 0;
        }
        long time = PrivateParams.getSPLong(context, Constant.CUR_STATISTIC_TIME);
        return time;
    }
    
    private static void bmobBatchData(final List<BmobObject> datainfos) {
        try {
            new BmobBatch().insertBatch(datainfos).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> o, BmobException e) {
                    if(e==null){
                        for(int i=0;i<o.size();i++){
                            BatchResult result = o.get(i);
                            BmobException ex =result.getError();
                            if(ex==null){
                                //SLog.e(TAG, i+" succeed : "+result.getCreatedAt()+", "+result.getObjectId()+", "+result.getUpdatedAt());
                            }else{
                                SLog.e(TAG, i+" failed : "+ex.getMessage()+","+ex.getErrorCode());
                            }
                        }
                    } else {
                        SLog.e(TAG, "failed: "+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }
}
