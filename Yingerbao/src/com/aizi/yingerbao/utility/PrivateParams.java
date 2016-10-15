package com.aizi.yingerbao.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.logging.SLog;

/**
 * 私有变量存储工具类 用来存储所有私有的变量 所有的sharedprefence都保存在pst命名的preference中
 * 
 * @author zhangchi09
 * 
 */
public class PrivateParams {

    private static final String TAG = "PrivateParams";

    /**
     * 保存私有int类型数据
     * 
     * @param context
     * @param key
     * @param value
     */
    public static void setSPInt(Context context, String key, int value) {
        try {
            SharedPreferences prvSetting =
                    context.getSharedPreferences(Constant.SHARED_NAME_PRIVATE_SETTINGS, Context.MODE_PRIVATE);
            Editor editor = prvSetting.edit();
            editor.putInt(key, value);
            editor.commit();
        } catch (Exception ex) {
            SLog.e(TAG, ex);
        }
    }

    /**
     * 获取保存的私有int类型数据
     * 
     * @param context
     * @param key
     * @return
     */
    public static int getSPInt(Context context, String key, int defValue) {
        SharedPreferences prvSetting =
                context.getSharedPreferences(Constant.SHARED_NAME_PRIVATE_SETTINGS, Context.MODE_PRIVATE);
        return prvSetting.getInt(key, defValue);
    }

    /**
     * 保存共享int类型数据
     * 
     * @param context
     * @param key
     * @param value
     */
    public static void setMutiproSPInt(Context context, String key, int value) {
        try {
            SharedPreferences prvSetting =
                    context.getSharedPreferences(Constant.SHARED_NAME_PRIVATE_SETTINGS, Context.MODE_MULTI_PROCESS);
            Editor editor = prvSetting.edit();
            editor.putInt(key, value);
            editor.commit();
        } catch (Exception ex) {
            SLog.e(TAG, ex);
        }
    }

    /**
     * 获取保存的共享int类型数据
     * 
     * @param context
     * @param key
     * @return
     */
    public static int getMutiproSPInt(Context context, String key, int defValue) {
        SharedPreferences prvSetting =
                context.getSharedPreferences(Constant.SHARED_NAME_PRIVATE_SETTINGS, Context.MODE_MULTI_PROCESS);
        return prvSetting.getInt(key, defValue);
    }

    /**
     * 保存string类型数据
     * 
     * @param context
     * @param key
     * @param value
     */
    public static void setSPString(Context context, String key, String value) {
        try {
            SharedPreferences prvSetting =
                    context.getSharedPreferences(Constant.SHARED_NAME_PRIVATE_SETTINGS, Context.MODE_MULTI_PROCESS);

            Editor editor = prvSetting.edit();
            editor.putString(key, value);
            editor.commit();
        } catch (Exception ex) {
            SLog.e(TAG, ex);
        }
    }

    /**
     * 获取string类型数据
     * @param context
     * @param key
     * @return
     */
    public static String getSPString(Context context, String key) {
        SharedPreferences prvSetting =
                context.getSharedPreferences(Constant.SHARED_NAME_PRIVATE_SETTINGS, Context.MODE_PRIVATE);
        return prvSetting.getString(key, "");
    }

    /**
     * 保存long类型数据
     * @param context
     * @param key
     * @param value
     */
    public static void setSPLong(Context context, String key, long value) {
        try {
            SharedPreferences prvSetting =
                    context.getSharedPreferences(Constant.SHARED_NAME_PRIVATE_SETTINGS, Context.MODE_PRIVATE);

            Editor editor = prvSetting.edit();
            editor.putLong(key, value);
            editor.commit();
        } catch (Exception ex) {
            SLog.e(TAG, ex);
        }
    }

    /**
     * 获取long类型数据
     * @param context
     * @param key
     * @return
     */
    public static long getSPLong(Context context, String key) {
        SharedPreferences prvSetting =
                context.getSharedPreferences(Constant.SHARED_NAME_PRIVATE_SETTINGS, Context.MODE_PRIVATE);
        return prvSetting.getLong(key, 0L);
    }


 
}
