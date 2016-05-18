package com.xzj.babyfun.task;

import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务分发器
 * 
 * @author xuzejun
 * 
 */
public class TaskDispatcherImpl implements TaskDispatcher {
    /** 初始化线程池大小 */
    private final int initPoolSize = 5;
    /** 最大线程池大小 */
    private final int maxPoolSize = 10;
    /** 空闲线程alive时间 10s */
    private final int KEEP_ALIVE_TIME = 10;
    /** 系统线程池 */
    ThreadPoolExecutor mThreadPoolExecutor;

    /** TaskDispatcherImpl 构造函数 */
    public TaskDispatcherImpl() {
        mThreadPoolExecutor = new ThreadPoolExecutor(initPoolSize, maxPoolSize, KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(), new PriorityThreadFactory("thread-pool",
                        android.os.Process.THREAD_PRIORITY_BACKGROUND), new ThreadPoolExecutor.DiscardOldestPolicy());
    }

    @Override
    public Future<?> dispatch(RequestTask task) {
        return mThreadPoolExecutor.submit(task);
    }

    @Override
    public void shutdown() {
        mThreadPoolExecutor.shutdown();
    }

    @Override
    public void cancel(long taskId) {
        // mThreadPoolExecutor.remove();
    }

}
