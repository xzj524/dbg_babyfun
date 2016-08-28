package com.aizi.xiaohuhu.deviceinterface;

import java.util.concurrent.Future;

import android.content.Context;

import com.aizi.xiaohuhu.task.DispatcherFactory;
import com.aizi.xiaohuhu.task.RequestTask;
import com.aizi.xiaohuhu.task.TaskDispatcher;

public class AsyncDeviceImpl implements AsyncDevice {

    /** 任务分发器 */
    private TaskDispatcher mDispatcher;
    /** 同步接口类 */
    private SyncDevice mSyncDevice;
    
    AsyncDeviceImpl(Context context){
        mDispatcher = DispatcherFactory.getNewInstance();
        mSyncDevice = DeviceFactory.getInstance(context);
    }
    
    private TaskDispatcher getDispatcher() {
        // TODO Auto-generated method stub
        if (mDispatcher == null) {
            mDispatcher = DispatcherFactory.getNewInstance();
        }
        return mDispatcher;
    }

    @Override
    public Future<?> setDeviceTime() {
        // TODO Auto-generated method stub
        
        RequestTask task = new RequestTask() {

            @Override
            public void run() {
                DeviceResponse<?> response = mSyncDevice.setDeviceTime();
               /* if (response.error == null) {
                    listener.onSetDeviceTime((Boolean) response.result);
                } else {
                    listener.onError(response.error);
                }*/
            }
        };
        return getDispatcher().dispatch(task);
    }
    
    @Override
    public Future<?> getDeviceTime() {
        // TODO Auto-generated method stub
      
        RequestTask task = new RequestTask() {

            @Override
            public void run() {
                DeviceResponse<?> response = mSyncDevice.getDeviceTime();
               /* if (response.error == null) {
                    listener.onSetDeviceTime((Boolean) response.result);
                } else {
                    listener.onError(response.error);
                }*/
            }
        };
        return getDispatcher().dispatch(task);
    }

    @Override
    public Future<?> startSendBreathData() {
        
        RequestTask task = new RequestTask() {

            @Override
            public void run() {
                DeviceResponse<?> response = mSyncDevice.startSendBreathData();
               /* if (response.error == null) {
                    listener.onSetDeviceTime((Boolean) response.result);
                } else {
                    listener.onError(response.error);
                }*/
            }
        };
        return getDispatcher().dispatch(task);
    }

    @Override
    public Future<?> stopSendBreathData() {

        RequestTask task = new RequestTask() {

            @Override
            public void run() {
                DeviceResponse<?> response = mSyncDevice.stopSendBreathData();
     
            }
        };
        return getDispatcher().dispatch(task);
    }

    @Override
    public Future<?> getBodyTemperature() {
        // TODO Auto-generated method stub
        RequestTask task = new RequestTask() {

            @Override
            public void run() {
                DeviceResponse<?> response = mSyncDevice.getBodyTemperature();
               /* if (response.error == null) {
                    listener.onSetDeviceTime((Boolean) response.result);
                } else {
                    listener.onError(response.error);
                }*/
            }
        };
        return getDispatcher().dispatch(task);
    }

    @Override
    public Future<?> getSleepInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Future<?> getAllNoSyncInfo() {
        // TODO Auto-generated method stub
        RequestTask task = new RequestTask() {

            @Override
            public void run() {
                DeviceResponse<?> response = mSyncDevice.getAllNoSyncInfo();
            }
        };
        return getDispatcher().dispatch(task);
    }

    @Override
    public Future<?> getAllSyncInfo() {
        // TODO Auto-generated method stub
        RequestTask task = new RequestTask() {

            @Override
            public void run() {
                DeviceResponse<?> response = mSyncDevice.getAllSyncInfo();
            }
        };
        return getDispatcher().dispatch(task);
    }

    @Override
    public Future<?> getBreathStopInfo() {
        // TODO Auto-generated method stub
        RequestTask task = new RequestTask() {

            @Override
            public void run() {
                DeviceResponse<?> response = mSyncDevice.getBreahStopInfo();
            }
        };
        return getDispatcher().dispatch(task);
    }




}
