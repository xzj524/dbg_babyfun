package com.xzj.babyfun.task;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import android.os.Process;

/**
 * 线程优先级factory
 * 
 * @author kongxiuli
 * 
 */
public class PriorityThreadFactory implements ThreadFactory {
    /** 线程优先级 */
    private final int mPriority;
    /** 线程计数器 */
    private final AtomicInteger mNumber = new AtomicInteger();
    /** 线程名 */
    private final String mName;

    /**
     * 构造函数
     * 
     * @param name
     *            线程名
     * @param priority
     *            线程优先级
     */
    public PriorityThreadFactory(String name, int priority) {
        mName = name;
        mPriority = priority;
    }

    /**
     * 产生新的新线程，并设置线程优先级
     * 
     * @param r
     *            可因为对象
     * @return 新建的线程
     */
    public Thread newThread(Runnable r) {
        return new Thread(r, mName + '-' + mNumber.getAndIncrement()) {
            @Override
            public void run() {
                Process.setThreadPriority(mPriority);
                super.run();
            }
        };
    }
}
