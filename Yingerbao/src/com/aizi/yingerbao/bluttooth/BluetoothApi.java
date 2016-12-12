package com.aizi.yingerbao.bluttooth;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.eventbus.AsycEvent;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.service.BluetoothService;
import com.aizi.yingerbao.utility.BaseMessageHandler;

import de.greenrobot.event.EventBus;

public class BluetoothApi {
    
    private static final String TAG = BluetoothApi.class.getSimpleName();
    private Object mWriteLock = new Object();
    
    private static BluetoothApi mInstance;
    public BluetoothService mBluetoothService = null;
    // 建立一个装数据的队列
    public SendDataQueue mSendDataQueue = new SendDataQueue();
    ExecutorService mExecutorService = Executors.newCachedThreadPool();
    Consumer consumer = new Consumer(Constant.AIZI_SEND_DATA, mSendDataQueue);
    Context mContext;
    
    
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
    
    private void unbindBluetoothService(Context context) {
        if (context != null) {
            context.unbindService(mBluetoothServiceConnection);
        }
     }
    
    //Bluetooth service connected/disconnected
    private ServiceConnection mBluetoothServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
            mBluetoothService = ((BluetoothService.LocalBinder) rawBinder).getService();
                SLog.e(TAG, "onServiceConnected mService= " + mBluetoothService);
                if (!mBluetoothService.initialize()) {
                    SLog.e(TAG, "Unable to initialize Bluetooth");
                }
        }
        public void onServiceDisconnected(ComponentName classname) {
            mBluetoothService.disconnect();
            mBluetoothService = null;
        }
    };
    
    public void RecvEvent(AsycEvent event) { 
        synchronized (mWriteLock) {
            int waittimes = 0;
            try {
                while (true) {
                    if (BaseMessageHandler.isWriteSuccess) {
                        waittimes = 0;
                        SLog.e(TAG, "WriteSendBuff*******************3");
                        writeByte(event.getByte());
                        break;
                    } else {
                        waittimes++;
                        if (waittimes > 2) {
                            waittimes = 0;
                            /*BaseMessageHandler.repeattime++;
                            if (BaseMessageHandler.repeattime < 3) {
                                //sendqueue.produce(event);
                            } */
                            BaseMessageHandler.isWriteSuccess = true;
                        }
                    }
                    Thread.sleep(1000); // 休眠1000ms
                }
            } catch (Exception e) {
                SLog.e(TAG, e);
            }
           
            
            //writeByte(event.getByte());
            //SLog.e(TAG, "RecvEvent WritByte");
        }
        
        //Producer producer = new Producer(Constant.AIZI_SEND_DATA, mSendDataQueue, event);
        //mExecutorService.submit(producer);
     } 
    
    public void onEvent(AsycEvent event) { 
        Producer producer = new Producer(Constant.AIZI_SEND_DATA, mSendDataQueue, event);
        mExecutorService.submit(producer);
        SLog.e(TAG, "onEvent WritByte");
     } 
    
    
    public boolean writeByte(byte[] wrByte) {
        boolean wrres = false;
        if (mBluetoothService != null) {
            wrres = mBluetoothService.writeBaseRXCharacteristic(wrByte);
            BaseMessageHandler.isWriteSuccess = false;
            SLog.e(TAG, "WriteSendBuff*******************4");
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
        //BlockingQueue<String> basket = new PriorityBlockingQueue<String>();
        BlockingQueue<AsycEvent> asyceventqueue = new LinkedBlockingQueue<AsycEvent>(100);
        //PriorityBlockingQueue<AsycEvent> priorityBlockingQueue = new PriorityBlockingQueue<AsycEvent>(100);

        // 生产数据
        public void produce(AsycEvent event) throws InterruptedException {
            // put方法放入数据，若asyceventqueue满了，等到asyceventqueue有位置
            if (event.isAck) {
                asyceventqueue.clear();
            }
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
                 SLog.e(TAG, "WriteSendBuff*******************2");
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
                AsycEvent event = null;
                int waittimes = 0;
                while (true) {
                    if (BaseMessageHandler.isWriteSuccess) {
                        waittimes = 0;
                        event =  sendqueue.consume();
                        SLog.e(TAG, "WriteSendBuff*******************3");
                        writeByte(event.getByte());
                        if (event.isAck) {
                            BaseMessageHandler.isWriteSuccess = true;
                        }
                    } else {
                        waittimes++;
                        if (waittimes > 10) {
                            waittimes = 0;
                            BaseMessageHandler.repeattime++;
                            if (BaseMessageHandler.repeattime < 3) {
                                sendqueue.produce(event);
                            } 
                            BaseMessageHandler.isWriteSuccess = true;
                        }
                    }
                    Thread.sleep(1000); // 休眠1000ms
                }
            } catch (InterruptedException ex) {
                SLog.e(TAG, ex);
            }
        }
    }
}
