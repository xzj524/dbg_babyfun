package com.xzj.babyfun.deviceinterface;

import android.content.Context;

/**
 * 同步接口factory
 * 
 * @author xuzejun
 * 
 */
public final class DeviceFactory {
    /** 同步接口实例 */
    private static SyncDevice mInstance;

    /** 默认构造函数 */
    private DeviceFactory() {

    }

    /**
     * 获取同步调用接口
     * 
     * @param context
     *            应用上下文
     * @return 返回同步调用接口
     */
    public static synchronized SyncDevice getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SyncDeviceImpl(context.getApplicationContext());
        }
        return mInstance;
    }
}
