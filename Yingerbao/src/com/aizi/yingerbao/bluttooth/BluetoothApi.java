package com.aizi.yingerbao.bluttooth;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.eventbus.AsycEvent;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.service.BluetoothService;
import com.aizi.yingerbao.thread.AZRunnable;
import com.aizi.yingerbao.utility.Utiliy;

import de.greenrobot.event.EventBus;

public class BluetoothApi {
    
    private static final String TAG = BluetoothApi.class.getSimpleName();
    protected static final long SLEEP_TIME = 1 * 1000;
 
    private static BluetoothApi mInstance;
    public BluetoothService mBluetoothService = null;
    // 建立一个装数据的队列
    public SendDataQueue mSendDataQueue = new SendDataQueue();
    ExecutorService mExecutorService = Executors.newCachedThreadPool();
    Consumer consumer = new Consumer(Constant.AIZI_SEND_DATA, mSendDataQueue);
    Context mContext;
    private WriteTimerTask mTimerTask;
    Timer mTimer;
    static AsycEvent mWriteAsycEvent;
    private static final Object synchronizedLock = new Object();
    private int mRetryTimes = 0;

    public BluetoothApi(Context context) {
        EventBus.getDefault().register(this);
        bindBluetoothService(context);
        mContext = context;
        mExecutorService.submit(consumer);
        mTimer = new Timer(true);
    }
    
    public void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
    }

    public static BluetoothApi getInstance(Context context) {
        if (mInstance != null) {
            return mInstance;
        } else {
            mInstance = new BluetoothApi(context);
            return mInstance;
        }
    }
    
    private void bindBluetoothService(Context context) {
        Intent bindblueIntent = new Intent(context, BluetoothService.class);
        context.bindService(bindblueIntent, mBluetoothServiceConnection, Context.BIND_AUTO_CREATE);
     }
    
    public void unbindBluetoothService() {
        if (mContext != null) {
            mContext.unbindService(mBluetoothServiceConnection);
        }
     }
    
    //Bluetooth service connected/disconnected
    private ServiceConnection mBluetoothServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mBluetoothService = ((BluetoothService.LocalBinder) rawBinder).getService();
            mBluetoothService.setCallback(new BluetoothService.OnBluetoothServiceListener() {
                
                @Override
                public void onBluetoothWrite(Intent intent) {
                    try {
                        if (intent.hasExtra("write_result")) {
                            boolean write_result = intent.getBooleanExtra("write_result", false);
                            String write_content = intent.getStringExtra("write_content");
                            SLog.e(TAG, "onBluetoothWrite Result = " + write_result
                                    + " Content = " + write_content);
                            if (mWriteAsycEvent != null) {
                                if (!mWriteAsycEvent.isAck) { // 如果不是ACK
                                    if (write_result) { // 写数据成功
                                        if (!mWriteAsycEvent.mIsWait) { // 不需要等待返回结果的命令
                                            Utiliy.reflectTranDataType(mContext, 0); //返回写成功
                                        }
                                    } else { // 写数据失败
                                        if (mRetryTimes < 3) {
                                            writeByte(mWriteAsycEvent.getByte());
                                            resetTimerTask();
                                            mRetryTimes++;
                                            return;
                                        } else {
                                            Utiliy.reflectTranDataType(mContext, 2); //返回写失败
                                            mRetryTimes = 0;
                                        }  
                                    }
                                } 
                            }
 
                            synchronized (synchronizedLock) {
                                if (mTimerTask != null) {
                                    mTimerTask.cancel();
                                }
                                synchronizedLock.notifyAll();
                            }
                        }
                    } catch (Exception e) {
                        SLog.e(TAG, e);
                    }
                   
                }


            });
            SLog.e(TAG, "onServiceConnected mService= " + mBluetoothService);
            if (!mBluetoothService.initBluetooth()) {
                SLog.e(TAG, "Unable to initialize Bluetooth");
            }
        }
        public void onServiceDisconnected(ComponentName classname) {
            mBluetoothService = null;
        }
    };
    
    public synchronized void RecvEvent(AsycEvent event) { 
        try {
            Producer producer = new Producer(Constant.AIZI_SEND_DATA, mSendDataQueue, event);
            mExecutorService.submit(producer);
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
     } 
    

    static AZRunnable bluetoothwriteTimeOutRunnable = new AZRunnable("bluetoothwriteTimeOutRunnable", AZRunnable.RUNNABLE_TIMER) {
        @Override
        public void brun() {
            try {
                Thread.sleep(SLEEP_TIME);
                synchronized (synchronizedLock) {
                    mWriteAsycEvent = null;
                    synchronizedLock.notifyAll();
                }
            } catch (Exception e) {
                SLog.e(TAG, e);
            }
        }
    };
    
    
    public void onEvent(AsycEvent event) { 
        Producer producer = new Producer(Constant.AIZI_SEND_DATA, mSendDataQueue, event);
        mExecutorService.submit(producer);
        SLog.e(TAG, "onEvent WritByte");
     } 
    
    
    public boolean writeByte(byte[] wrByte) {
        boolean wrres = false;
        if (mBluetoothService != null) {
            wrres = mBluetoothService.writeBaseRXCharacteristic(wrByte);
        }
        return wrres;
    }
    
    class WriteTimerTask extends TimerTask{

        @Override
        public void run() {
            synchronized (synchronizedLock) {
                mWriteAsycEvent = null;
                synchronizedLock.notifyAll();
            } 
        }
    }
    

    
    // 定义生产者
    class Producer implements Runnable {
        private String instance;
        private SendDataQueue sendqueue;
        private AsycEvent mAsycEvent;

        public Producer(String instance, SendDataQueue sendqueue, AsycEvent asycevent) {
            this.instance = instance;
            this.sendqueue = sendqueue;
            this.mAsycEvent = asycevent;
        }

        public void run() {
            try {
                 sendqueue.produce(mAsycEvent);
            } catch (InterruptedException ex) {
                SLog.e(TAG, ex);
            }
        }
    }

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
                    //SLog.e(TAG, "Bluetooth Consumer wait before 1");
                    mWriteAsycEvent =  sendqueue.consume();
                    resetTimerTask();
                    writeByte(mWriteAsycEvent.getByte());
                    //SLog.e(TAG, "Bluetooth Consumer wait before 2");
                    synchronized (synchronizedLock) {
                        try {  
                            //SLog.e(TAG, "Bluetooth Consumer wait before 3");
                            synchronizedLock.wait();
                        } catch (Exception e) {
                            SLog.e(TAG, e);
                        }
                        //SLog.e(TAG, "Bluetooth Consumer wait before 4");
                    }
                }
            } catch (InterruptedException ex) {
                SLog.e(TAG, ex);
            }
        }
    }
    
    
    private void resetTimerTask() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
        }
        mTimerTask = new WriteTimerTask();
        mTimer.schedule(mTimerTask, SLEEP_TIME);
    }
    
    /**
     * 
     * 定义缓冲任务的队列
     * 
     */
    public class SendDataQueue {
        // 发送数据队列，能容纳一百个数据
        BlockingQueue<AsycEvent> asyceventqueue = new LinkedBlockingQueue<AsycEvent>(100);

        // 生产数据
        public void produce(AsycEvent event) throws InterruptedException {
            // put方法放入数据，若asyceventqueue满了，等到asyceventqueue有位置
            asyceventqueue.put(event);
        }

        // 消费数据
        public AsycEvent consume() throws InterruptedException {
            // take方法取出数据，若asyceventqueue为空，等到asyceventqueue有数据为止(获取并移除此队列的头部)
            return asyceventqueue.take();
        }
        
        // 消费数据
        public AsycEvent element() throws InterruptedException {
            // 取数据但是不清空头部
            return asyceventqueue.element();
        }
        
        // wait
        public void waitfor() throws InterruptedException {
            asyceventqueue.wait();
        }
        
        // wait
        public void clearqueue() throws InterruptedException {
            asyceventqueue.clear();
        }
    }
    
}
