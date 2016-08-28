package com.aizi.xiaohuhu.deviceinterface;

import android.content.Context;

/**
 * 异步接口类工厂, 对用户public
 * 
 * @author kongxiuli
 * 
 */
public final class AsyncDeviceFactory {

    /**
     * 默认构造函数
     */
   private AsyncDeviceFactory() {
    }

    /** 单例实例 */
    private static AsyncDevice mInstance;

    /**
     * 获取异步接口实例
     * 
     * @param context
     *            上下文
     * @return 异步接口实例
     */
    public static synchronized AsyncDevice getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new AsyncDeviceImpl(context);
        }
        return mInstance;
    }

}
