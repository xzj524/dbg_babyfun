package com.aizi.yingerbao.thread;

import java.util.concurrent.TimeUnit;

import com.aizi.yingerbao.logging.SLog;

public class ThreadPool {

    /** 普通线程池 */
    private static AZThreadPoolExecutor mExeServ;
    private static ThreadPool singleInstance;

    private static final String TAG = "ThreadPool";

    public static ThreadPool getInstance() {
        if (singleInstance == null || mExeServ == null || mExeServ.isShutdown() || mExeServ.isTerminated()) {
            singleInstance = new ThreadPool();
        }
        return singleInstance;
    }

    /**
     * 线程池初始化方法
     * 
     * corePoolSize 核心线程池大小 ---- maximumPoolSize 最大线程池大小---- keepAliveTime
     * 线程池中超过corePoolSize数目的空闲线程最大存活时间---- 单位 TimeUnit keepAliveTime时间单位----
     * workQueue 阻塞队列----new PriorityBlockingQueue<Runnable>(3)====容量的阻塞队列
     * ----new CustomThreadFactory()====定制的线程工厂 rejectedExecutionHandler
     * 当提交任务数超过maxmumPoolSize+workQueue之和时, 任务会交给RejectedExecutionHandler来处理
     */
    public ThreadPool() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                shutDown();
            }
        });
        mExeServ = new AZThreadPoolExecutor(3, 100, 1, TimeUnit.MINUTES,
                new AZPriorityQueue<Runnable>());
    }

    /**
     * 将API处理的Runnable提交线程池调度
     * 
     * @param processor
     *            提交的API处理Runnable
     */
    public boolean submitRunnable(AZRunnable runnable) {
        try {
            mExeServ.submit(runnable);
            return true;
        } catch (Exception ex) {
            SLog.e(TAG, "submitRunnable e: " + ex);
            if (mExeServ == null || mExeServ.getCorePoolSize() == 0 || mExeServ.getPoolSize() == 0) {
                // 重建线程池
                mExeServ = new AZThreadPoolExecutor(3, 100, 1, TimeUnit.MINUTES,
                        new AZPriorityQueue<Runnable>());
            }
        }
        return false;
    }

    public void shutDown() {
        if (mExeServ != null) {
            try {
                mExeServ.getQueue().clear();
                mExeServ.shutdown();
                mExeServ.shutdownNow();
            } catch (Exception e) {
                SLog.e(TAG, e);
            }
        }
    }

}
