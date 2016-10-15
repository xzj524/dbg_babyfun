package com.aizi.yingerbao.task;

import java.util.concurrent.Future;

/**
 * 任务分发接口
 * 
 * @author kongxiuli
 * 
 */
public interface TaskDispatcher {
    /**
     * 任务分发方法
     * 
     * @param task
     *            待处理的任务
     * @return 可运行任务
     */
    Future<?> dispatch(RequestTask task);

    /**
     * 关闭任务分发器
     */
    void shutdown();

    /**
     * 取消某个待执行的任务
     * 
     * @param taskId
     *            任务ID
     */
    void cancel(long taskId);
}
