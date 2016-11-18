package com.aizi.yingerbao.thread;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * BDThreadPool 1 keep core thread to coresize 2 add to the queue to queuesize
 * and list by priority 3 select the first element(highest priority) and open a
 * thread to run it 4 if thread number reaches the maximum and queue full, clear
 * the queue
 * 
 * @author lyon.ma
 * @datetime 2016-06-20
 */
public class AZThreadPoolExecutor extends ThreadPoolExecutor {

    public AZThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
            long keepAliveTime, TimeUnit unit, AZPriorityQueue<Runnable> workQueue,
            ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
    }

    public AZThreadPoolExecutor(int corePoolSize, int maximumPoolSize,
            long keepAliveTime, TimeUnit unit, AZPriorityQueue<Runnable> workQueue) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Runnable runnable, T value) {
        return new ComparableFutureTask<T>(runnable, value);
    }

    @Override
    protected <T> RunnableFuture<T> newTaskFor(Callable<T> callable) {
        return new ComparableFutureTask<T>(callable);
    }

    protected class ComparableFutureTask<V>
            extends FutureTask<V> implements Comparable<ComparableFutureTask<V>> {
        private Object object;

        public ComparableFutureTask(Callable<V> callable) {
            super(callable);
            object = callable;
        }

        public ComparableFutureTask(Runnable runnable, V result) {
            super(runnable, result);
            object = runnable;
        }

        @Override
        public int compareTo(ComparableFutureTask<V> o) {
            if (this == o) {
                return 0;
            }
            if (o == null) {
                return -1; // high priority
            }
            if (object != null && o.object != null) {
                if (object instanceof AZRunnable && o.object instanceof AZRunnable) {
                    return ((AZRunnable) o.object).getPriority() - ((AZRunnable) object).getPriority();
                }
            }
            return 0;
        }
    }

    @Override
    public synchronized void execute(Runnable command) {
        if (getQueue().size() >= (AZPriorityQueue.QUEQUE_MAX_SIZE - 1)) {
            if (getPoolSize() >= getMaximumPoolSize()) {
                getQueue().clear();
            } else {
                Runnable r = getQueue().poll();
                getQueue().offer(command);
                command = r;
            }
        }
        super.execute(command);
    }

}