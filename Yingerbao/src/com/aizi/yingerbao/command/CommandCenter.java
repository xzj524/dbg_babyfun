package com.aizi.yingerbao.command;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Intent;
import android.text.TextUtils;

import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.thread.AZRunnable;
import com.aizi.yingerbao.thread.ThreadPool;

public class CommandCenter {
    private static final String TAG = "CommandCenter";
    private static CommandCenter mInstance;
    private static SendDataQueue mSendDataQueue = new SendDataQueue();
    static CommandSendRequest mCommandSendRequest;
    ExecutorService mExecutorService = Executors.newCachedThreadPool();
    Consumer consumer = new Consumer(Constant.AIZI_SEND_DATA, mSendDataQueue);
    
    private static final Object synchronizedLock = new Object();
    private static int SLEEP_TIME = 10 * 1000;
    private static int mRetryTimes = 0;
    
    public CommandCenter() {
        //EventBus.getDefault().register(this);
        mExecutorService.submit(consumer);
    }

    public static CommandCenter getInstance() {
        if (mInstance != null) {
            return mInstance;
        } else {
            mInstance = new CommandCenter();
            return mInstance;
        }
    }

    // 生产者
    public synchronized void addCallbackRequest(CommandSendRequest commandsendRequest) {
        try {
            if (mSendDataQueue == null) {
                mSendDataQueue = new SendDataQueue();
            }
            
            if (mSendDataQueue != null) {
                mSendDataQueue.produce(commandsendRequest);
            }    
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }

    public void handleIntent(Intent intent) {
        SLog.d(TAG, "receiveIntent: " + intent.toUri(0));
        try {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action)) {
                if (action.equals(Constant.ACITON_DATA_TRANSFER)) {
                    int trantype = intent.getIntExtra(Constant.DATA_TRANSFER_TYPE, 0);
                    switch (trantype) {
                        case Constant.TRANSFER_TYPE_SUCCEED: //数据传输成功
                            synchronized (synchronizedLock) { //传输成功之后继续下一个
                                synchronizedLock.notifyAll();
                                SLog.e(TAG, "CommandCenter mCommandSendRequest  completed notifyALL");
                                ThreadPool.getInstance().shutDown();
                            }
                            mRetryTimes = 0;
                            break;
                        case Constant.TRANSFER_TYPE_NOT_COMPLETED: // 数据传输未完成
                            ThreadPool.getInstance().shutDown();
                            ThreadPool.getInstance().submitRunnable(timeOutRunnable);
                            break;
                        case Constant.TRANSFER_TYPE_ERROR: // 数据传输出错
                            synchronized (synchronizedLock) { //传输失败之后继续下一个
                                synchronizedLock.notifyAll();
                                if (mRetryTimes < 3) {
                                    // 重新加入任务队列
                                    mSendDataQueue.produce(mCommandSendRequest);
                                    mRetryTimes++;
                                } else {
                                    mRetryTimes = 0;
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }
    
    
    public void clearInterfaceQueue() {
        try {
            mSendDataQueue.clear();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            SLog.e(TAG, e);
        }
    }
    
    
    static AZRunnable timeOutRunnable = new AZRunnable("sendtimeOutRunnable", AZRunnable.RUNNABLE_TIMER) {
        @Override
        public void brun() {
            try {
                Thread.sleep(SLEEP_TIME);
                synchronized (synchronizedLock) {
                    synchronizedLock.notifyAll();
                    SLog.e(TAG, "CommandCenter mCommandSendRequest  time out notifyALL");
                }
            } catch (Exception e) {
                SLog.e(TAG, e);
            }
        }
    };
    
    
 // 定义消费者
    class Consumer implements Runnable {
        private String instance;
        private SendDataQueue sendqueue;

        public Consumer(String instance, SendDataQueue sendqueue) {
            this.instance = instance;
            this.sendqueue = sendqueue;
        }

        public void run() {
            try {
                while (true) {
                    mCommandSendRequest =  sendqueue.consume();
                    mCommandSendRequest.send();
                    SLog.e(TAG, "CommandCenter mCommandSendRequest send");
                    ThreadPool.getInstance().submitRunnable(timeOutRunnable);
                    SLog.e(TAG, "CommandCenter mCommandSendRequest set alarm timer");
                    synchronized (synchronizedLock) {
                        try {
                            synchronizedLock.wait();
                            SLog.e(TAG, "CommandCenter mCommandSendRequest completed");
                        } catch (Exception e) {
                            SLog.e(TAG, e);
                        }
                    }
                }
            } catch (Exception ex) {
                SLog.e(TAG, ex);
            }
        }
    }
    
    /**
     * 
     * 定义缓冲任务的队列
     * 
     */
    public static class SendDataQueue {
        // 发送数据队列，能容纳一百个数据
        //BlockingQueue<String> basket = new LinkedBlockingQueue<String>(3);
        BlockingQueue<CommandSendRequest> commandqueue = new LinkedBlockingQueue<CommandSendRequest>(100);
        //PriorityBlockingQueue<AsycEvent> priorityBlockingQueue = new PriorityBlockingQueue<AsycEvent>(100);

        // 生产数据
        public void produce(CommandSendRequest event) throws InterruptedException {
            // put方法放入数据，若asyceventqueue满了，等到asyceventqueue有位置
            commandqueue.put(event);
        }

        // 消费数据
        public CommandSendRequest consume() throws InterruptedException {
            // take方法取出数据，若asyceventqueue为空，等到asyceventqueue有数据为止(获取并移除此队列的头部)
            return commandqueue.take();
        }
        
     // 消费数据
        public CommandSendRequest element() throws InterruptedException {
            // 取数据但是不清空头部
            return commandqueue.element();
        }
        
        // wait
        public void waitfor() throws InterruptedException {
            commandqueue.wait();
        }
        

        // wait
        public void clear() throws InterruptedException {
            commandqueue.clear();
        }
    }
}
