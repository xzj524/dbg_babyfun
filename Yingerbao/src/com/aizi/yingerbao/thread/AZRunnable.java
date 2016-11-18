package com.aizi.yingerbao.thread;

import android.text.TextUtils;

import com.aizi.yingerbao.logging.SLog;

public abstract class AZRunnable implements Runnable {

    private String name;

    private short priority = RUNNABLE_LOGIC_LOCAL;

    public static final short RUNNABLE_HTTP_LOGIC = 100;
    public static final short RUNNABLE_LOGIC_LOCAL = 99;
    public static final short RUNNABLE_HTTP_EXTRA = 98;

    public static final short RUNNABLE_EXTRA_OPERATE = 95;
    public static final short RUNNABLE_HTTP_STATISTIC = 90;
    public static final short RUNNABLE_HTTP_DOWNLOAD = 80;
    public static final short RUNNABLE_TIMER = 50;

    public AZRunnable() {

    }

    public AZRunnable(String name, short priority) {
        this.name = name;
        this.priority = priority;
    }

    public final void run() {
        if (!TextUtils.isEmpty(name)) {
            Thread.currentThread().setName(name);
        }
        SLog.d("PushRunnable", "running: " + name);
        brun();
    }

    public abstract void brun();

    public String getRunnableName() {
        return name;
    }

    public short getPriority() {
        return priority;
    }

    public void setPriority(short priority) {
        this.priority = priority;
    }

    public void setName(String name) {
        this.name = name;
    }

}
