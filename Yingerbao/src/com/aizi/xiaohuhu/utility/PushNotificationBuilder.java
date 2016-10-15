package com.aizi.xiaohuhu.utility;

import java.io.Serializable;

import android.app.Notification;
import android.content.Context;

/**
 * PushNotificationBuilder构建自定义通知样式基类
 */
public abstract class PushNotificationBuilder implements Serializable {
    protected int mStatusbarIcon;
    protected int mNotificationFlags;
    protected int mNotificationDefaults;
    protected String mNotificationsound;
    protected long[] mVibratePattern;
    protected String mNotificationTitle;
    protected String mNotificationText;

    /**
     * 设置自定义通知状态栏图标
     * 
     * @param icon
     *            int 图标资源id
     * 
     * @return void.
     */
    public void setStatusbarIcon(int icon) {
        mStatusbarIcon = icon;
    }

    public void setNotificationTitle(String title) {
        mNotificationTitle = title;
    }

    public void setNotificationText(String text) {
        mNotificationText = text;
    }

    /**
     * 设置自定义通知flags值
     * 
     * @param flags
     *            int 通知flags值
     * 
     * @return void.
     */
    public void setNotificationFlags(int flags) {
        mNotificationFlags = flags;
    }

    /**
     * 设置自定义通知defaults值
     * 
     * @param defaults
     *            int 通知defaults值
     * 
     * @return void.
     */
    public void setNotificationDefaults(int defaults) {
        mNotificationDefaults = defaults;
    }

    /**
     * 设置自定义通知声音
     * 
     * @param sound
     *            Uri 声音资源标识
     * 
     * @return void.
     */
    public void setNotificationSound(String sound) {
        mNotificationsound = sound;
    }

    /**
     * 设置自定义通知振动样式
     * 
     * @param pattern
     *            long[] 振动样式
     * 
     * @return void.
     */
    public void setNotificationVibrate(long[] pattern) {
        mVibratePattern = pattern;
    }

    public abstract Notification construct(Context context);
}