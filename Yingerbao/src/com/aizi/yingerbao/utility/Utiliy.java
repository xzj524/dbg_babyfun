package com.aizi.yingerbao.utility;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;

import com.aizi.yingerbao.ConnectDeviceActivity;
import com.aizi.yingerbao.R;
import com.aizi.yingerbao.command.CommandCenter;
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
        Notification notif = null;
        notif = NotificationBuilderManager.createNotification(context, 0, title, content, false);
        nmNotificationManager.notify(System.currentTimeMillis() + "", 0, notif);
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
                SLog.e(TAG, "pairedDevices name = " + device.getName());
                if (device.getName().equals(Constant.AIZI_DEVICE_TAG)) {
                    int state = mBluetoothManager.getConnectionState(device, BluetoothProfile.GATT);
                    if (state == BluetoothProfile.STATE_CONNECTED) {
                        isConnected = true;
                        break;
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

    public static void reflectTranDataType(int res) {
        try {
            Intent intent = new Intent(Constant.ACITON_DATA_TRANSFER);
            switch (res) {
            case 0:
                intent.putExtra(Constant.DATA_TRANSFER_TYPE, Constant.TRANSFER_TYPE_SUCCEED);
                break;
            case 1:
                intent.putExtra(Constant.DATA_TRANSFER_TYPE, Constant.TRANSFER_TYPE_NOT_COMPLETED);
                break;
            case 2:
                intent.putExtra(Constant.DATA_TRANSFER_TYPE, Constant.TRANSFER_TYPE_ERROR);
                break;

            default:
                break;
            }
            
            CommandCenter.getInstance().handleIntent(intent);
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }

}
