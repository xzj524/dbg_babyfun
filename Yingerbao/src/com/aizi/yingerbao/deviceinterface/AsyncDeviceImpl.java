package com.aizi.yingerbao.deviceinterface;

import java.util.concurrent.Future;

import android.content.Context;

import com.aizi.yingerbao.task.DispatcherFactory;
import com.aizi.yingerbao.task.RequestTask;
import com.aizi.yingerbao.task.TaskDispatcher;

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
    public Future<?> getRealTimeData() {
        // TODO Auto-generated method stub
        RequestTask task = new RequestTask() {

            @Override
            public void run() {
                DeviceResponse<?> response = mSyncDevice.getRealTimeData();
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
               // DeviceResponse<?> response = mSyncDevice.getAllNoSyncInfo();
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
                DeviceResponse<?> response = mSyncDevice.getBreathStopInfo();
            }
        };
        return getDispatcher().dispatch(task);
    }

    @Override
    public Future<?> getRealTimeTempData() {
        RequestTask task = new RequestTask() {

            @Override
            public void run() {
                DeviceResponse<?> response = mSyncDevice.getRealTimeTempData();
            }
        };
        return getDispatcher().dispatch(task);
    }

    @Override
    public Future<?> getExceptionEvent() {
        // TODO Auto-generated method stub
        RequestTask task = new RequestTask() {

            @Override
            public void run() {
                DeviceResponse<?> response = mSyncDevice.getExceptionEvent();
            }
        };
        return getDispatcher().dispatch(task);
    }

    @Override
    public Future<?> checkDeviceValid() {
        RequestTask task = new RequestTask() {

            @Override
            public void run() {
                DeviceResponse<?> response = mSyncDevice.checkDeviceValid();
            }
        };
        return getDispatcher().dispatch(task);
    }

    @Override
    public Future<?> activateDevice() {
        RequestTask task = new RequestTask() {

            @Override
            public void run() {
                DeviceResponse<?> response = mSyncDevice.activateDevice();
            }
        };
        return getDispatcher().dispatch(task);
    }
}
