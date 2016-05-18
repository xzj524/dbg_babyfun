package com.xzj.babyfun.task;

/**
 * 任务分发器
 * 
 * @author kongxiuli
 * 
 */
public final class DispatcherFactory {
    /**
     * 默认构造函数
     */
    private DispatcherFactory() {
        
    }

    /**
     * 获取TaskDispatcher实例
     * 
     * @return TaskDispatcher
     */
    public static TaskDispatcher getNewInstance() {
        return new TaskDispatcherImpl();
    }
}
