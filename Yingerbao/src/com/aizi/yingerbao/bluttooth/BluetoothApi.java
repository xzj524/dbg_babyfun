package com.aizi.yingerbao.bluttooth;

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
import com.aizi.yingerbao.thread.ThreadPool;
import com.aizi.yingerbao.utility.Utiliy;

import de.greenrobot.event.EventBus;

public class BluetoothApi {
    
    private static final String TAG = BluetoothApi.class.getSimpleName();
    protected static final long SLEEP_TIME = 1;
    private static Object mWriteLock = new Object();
    
    private static BluetoothApi mInstance;
    public BluetoothService mBluetoothService = null;
    // 建立一个装数据的队列
    public SendDataQueue mSendDataQueue = new SendDataQueue();
    ExecutorService mExecutorService = Executors.newCachedThreadPool();
    Consumer consumer = new Consumer(Constant.AIZI_SEND_DATA, mSendDataQueue);
    Context mContext;
    String mWriteContent;
    private static boolean mIsWriteSucceed = true;
    private static int mWriteWaittimes = 0;
    
    AsycEvent mWriteAsycEvent;
    
    
    public BluetoothApi(Context context) {
        EventBus.getDefault().register(this);
        bindBluetoothService(context);
        mContext = context;
        mExecutorService.submit(consumer);
    }
    
    public void unregisterEventBus() {
        EventBus.getDefault().unregister(this);
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
                            if (write_result) {
                                String write_content = intent.getStringExtra("write_content");
                                if (write_content.equals(mWriteContent)) {
                                    mIsWriteSucceed = true;
                                    SLog.e(TAG, "onBluetoothWrite write byte succeed");
                                } else {
                                    SLog.e(TAG, "onBluetoothWrite write byte failed");
                                }
                                
                                if (mWriteAsycEvent != null 
                                        && !mWriteAsycEvent.mIsWait) {
                                    Utiliy.reflectTranDataType(mContext, 0);
                                }
                            } else {
                                if (mWriteAsycEvent != null 
                                        && !mWriteAsycEvent.mIsWait) {
                                    Utiliy.reflectTranDataType(mContext, 2);
                                }
                            }
                            
                            synchronized (mWriteLock) {
                                mWriteLock.notifyAll();
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
            //mBluetoothService.disconnect(true);
            mBluetoothService = null;
        }
    };
    
    public synchronized void RecvEvent(AsycEvent event) { 
        try {
            /*mWriteAsycEvent = event;
            writeAsycEvent(mWriteAsycEvent);*/
            
            Producer producer = new Producer(Constant.AIZI_SEND_DATA, mSendDataQueue, event);
            mExecutorService.submit(producer);
            
         /*   if (writeByte(event.getByte())) {
                if (!mIsWaitResult) { // 不需要设备返回结果
                    Utiliy.reflectTranDataType(mContext, 0);
                }
            }*/
            //writeByte(event.getByte());

            /*new Thread(new Runnable() {
                
                @Override
                public void run() {
                    try {
                        while (true) {
                            synchronized (mWriteLock) {
                                
                                if (mIsWriteSucceed) {
                                    mWriteWaittimes = 0;
                                    writeByte(event.getByte());
                                    break;
                                } else {
                                    mWriteWaittimes++;
                                    if (mWriteWaittimes > 2) { // 等待2秒
                                        mWriteWaittimes = 0;
                                        mIsWriteSucceed = true;
                                    } else {
                                        Thread.sleep(1000); // 休眠1000ms
                                        }
                                    }
                                    
                                }
                            }
                        } catch (Exception e) {
                            SLog.e(TAG, e);
                        }
                    }
                }).start();
*/  
            } catch (Exception e) {
                SLog.e(TAG, e);
            }
     } 
    

    AZRunnable bluetoothwriteTimeOutRunnable = new AZRunnable("bluetoothwriteTimeOutRunnable", AZRunnable.RUNNABLE_TIMER) {
        @Override
        public void brun() {
            try {
                Thread.sleep(SLEEP_TIME);
                synchronized (mWriteLock) {
                    mWriteAsycEvent = null;
                    mWriteLock.notifyAll();
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
            mWriteContent = Utiliy.printHexString(wrByte);
           /* RecvMessageHandler.isWriteSuccess = false;
            if (wrres) {
                mIsWriteSucceed = false;
            }*/
        }
        return wrres;
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
                 SLog.e(TAG, "SendQueue Produce AsycEvent");
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
               // int waittimes = 0;
                while (true) {
                    //if (RecvMessageHandler.isWriteSuccess) {
                        //waittimes = 0;
                        mWriteAsycEvent =  sendqueue.consume();
                        writeByte(mWriteAsycEvent.getByte());
                        ThreadPool.getInstance().submitRunnable(bluetoothwriteTimeOutRunnable);
                        synchronized (mWriteLock) {
                            try {
                                mWriteLock.wait();
                            } catch (Exception e) {
                                SLog.e(TAG, e);
                            }
                        }
                       /* if (event.isAck) {
                            RecvMessageHandler.isWriteSuccess = true;
                        }*/
                   /* } else {
                        waittimes++;
                        if (waittimes > 10) {
                            waittimes = 0;
                            RecvMessageHandler.repeattime++;
                            if (RecvMessageHandler.repeattime < 3) {
                                sendqueue.produce(event);
                            } 
                            RecvMessageHandler.isWriteSuccess = true;
                        }
                    }*/
                    //Thread.sleep(1000); // 休眠1000ms
                }
            } catch (InterruptedException ex) {
                SLog.e(TAG, ex);
            }
        }
    }
}
