package com.aizi.yingerbao.task;

/**
 * 请求任务
 * 
 * @author xuzejun
 * 
 */
public abstract class RequestTask implements Runnable {
    /** 任务id */
    private long id;
    /** 任务是否取消 */
    private boolean mCanceled = false;

    /**
     * 获取任务id
     * 
     * @return 任务ID
     */
    public long getId() {
        return id;
    }

    /**
     * 设置任务id
     * 
     * @param taskID
     *            任务ID
     */
    public void setId(long taskID) {
        this.id = taskID;
    }

    /**
     * 任务是否取消
     * 
     * @return 任务是否被cancel
     */
    public boolean isCanceled() {
        return mCanceled;
    }

    /**
     * 取消任务
     */
    public void cancel() {
        mCanceled = true;
    }

    /**
     * 同步执行任务，并将结果回调给用户
     */
    public void run() {
    }

}
