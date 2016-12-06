/*
 * Copyright (C) 2016 Aizi Inc. All rights reserved.
 */
package com.aizi.yingerbao.device.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.aizi.yingerbao.R;
import com.aizi.yingerbao.YingerBaoActivity;
import com.aizi.yingerbao.bluttooth.BluetoothApi;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.deviceinterface.DeviceFactory;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.service.BluetoothService;
import com.aizi.yingerbao.utility.PrivateParams;
import com.aizi.yingerbao.utility.Utiliy;

import de.greenrobot.event.EventBus;

/**
 * 
 * @author xuzejun
 * @since 2016-4-2
 */
public class DeviceConnectStatusFragment extends Fragment{
    
    private static final String TAG = DeviceConnectStatusFragment.class.getSimpleName();

    public static final int ABNORMAL_REQUEST_CODE = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH = 101;
    private static final long WAIT_PERIOD = 10 * 1000; //10 seconds

    private static final long WAIT_CHECK_PERIOD = 15 * 1000;

    private BluetoothDevice mDevice = null;
    OnDeviceConnectListener mListener;
    Animation mProgressAnimation; 
    Context mContext;
    MessageReceiver mMessageReceiver;
    PendingIntent mCheckPendingIntent;
    PendingIntent mSyncDataPendingIntent;
    PendingIntent mSearchPendingIntent;

    /** 连接时候的progress */
    private ImageView mProgressImageView;

    /** 正在连接动画 */
    private boolean mIsConnectingAnimation = false;

    /** 正在连接的view group */
    private ViewGroup mConnectingInfoViewGroup;

    /** 连接完成的状态view gourp */
    public ViewGroup mConnectedSucceedViewGroup;
    
    /** 连接失败的状态view gourp */
    public ViewGroup mConnectedFailedViewGroup;
    
    /** 点击连接设备的状态view gourp */
    public ViewGroup mClickConnectViewGroup;
    
    /** 正在同步数据的状态view gourp */
    public ViewGroup mSyncDataViewGroup;
    
    /** 同步数据失败的状态view gourp */
    public ViewGroup mSyncDataFailedViewGroup;
    
    /** 正在验证设备的状态view gourp */
    public ViewGroup mCheckDeviceViewGroup;
    
    /** 验证设备失败的状态view gourp */
    public ViewGroup mCheckDeviceFailedViewGroup;
    
   
    /** 连接设备的状态 */
    public static ConnectDeviceState mCurrentState = ConnectDeviceState.IDEL;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {  
            mContext = activity.getApplicationContext();
            mListener =(OnDeviceConnectListener)activity;  
        }catch(ClassCastException e){  
            throw new ClassCastException(activity.toString() + " must implement OnDeviceConnectListener");  
        }  
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mCurrentState = ConnectDeviceState.IDEL;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View deviceStatusView = inflater.inflate(R.layout.yingerbao_status_fragment, container, false);
        
        mProgressImageView = (ImageView) deviceStatusView.findViewById(R.id.progressImageView);
        mProgressImageView.setOnClickListener(new UpdateStatusOnclickListener());

        mSyncDataViewGroup = (ViewGroup) deviceStatusView.findViewById(R.id.syncdataLayout);
        mSyncDataFailedViewGroup = (ViewGroup) deviceStatusView.findViewById(R.id.syncdatafailedLayout);
        mClickConnectViewGroup = (ViewGroup) deviceStatusView.findViewById(R.id.clicktostart);
        mConnectingInfoViewGroup = (ViewGroup) deviceStatusView.findViewById(R.id.connectInfoLayout);
        mConnectedSucceedViewGroup = (ViewGroup) deviceStatusView.findViewById(R.id.connectedSucceedLayout);
        mConnectedFailedViewGroup = (ViewGroup) deviceStatusView.findViewById(R.id.connectedFailedLayout);
        mCheckDeviceViewGroup = (ViewGroup) deviceStatusView.findViewById(R.id.checkdeviceLayout);
        mCheckDeviceFailedViewGroup = (ViewGroup) deviceStatusView.findViewById(R.id.checkdevicefailedLayout);
        
        mProgressImageView.setImageResource(R.drawable.lightline);
        mProgressAnimation = AnimationUtils.loadAnimation(mContext,
                R.anim.connecting_router_rotate_animation);
        
        if (!Utiliy.isBluetoothConnected(mContext)) {
            mCurrentState = ConnectDeviceState.IDEL;
        } 
        SLog.e(TAG, "current state = " + mCurrentState);
        doUpdateStatusClick();
        
        EventBus.getDefault().register(this);
        registerMessageReceiver();
        return deviceStatusView;
    }
    
    private void setSearchDeviceTimeout() {
        if (mSearchPendingIntent != null) {
            Utiliy.cancelAlarmPdIntent(mContext, mSearchPendingIntent);
        }
        mSearchPendingIntent = Utiliy.getDelayPendingIntent(mContext, Constant.ALARM_WAIT_SEARCH_DEVICE);
        Utiliy.setDelayAlarm(mContext, Constant.WAIT_SEARCH_DEVICE_PERIOD, mSearchPendingIntent);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterMessageReceiver();
        EventBus.getDefault().unregister(this);
    }
    
    /**
     * 开始检查连接动画
     */
    public void startConnectingAnimation() {

        if (!mIsConnectingAnimation) {
            mIsConnectingAnimation = true;
            mProgressImageView.startAnimation(mProgressAnimation);
        }
    }


    /**
     * 更新状态的OnClick
     * 
     */
    private class UpdateStatusOnclickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            doUpdateStatusClick();
        }
    }

    /**
     * 更新状态的click
     */
    public void doUpdateStatusClick() {
        if (mCurrentState == ConnectDeviceState.IDEL) {
            startConnectingAnimation();
            mCurrentState = ConnectDeviceState.SEARCHING_DEVICE;     
            Intent bluetoothIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bluetoothIntent,REQUEST_ENABLE_BLUETOOTH);
            
            // 搜索设备状态，开始
            PrivateParams.setSPInt(mContext, "search_device_status", 1);
            setSearchDeviceTimeout();
            
            mConnectingInfoViewGroup.setVisibility(View.VISIBLE);
            mConnectedSucceedViewGroup.setVisibility(View.GONE);
            mConnectedFailedViewGroup.setVisibility(View.GONE);
            mClickConnectViewGroup.setVisibility(View.GONE);
            mSyncDataViewGroup.setVisibility(View.GONE);
            mSyncDataFailedViewGroup.setVisibility(View.GONE);
            mCheckDeviceViewGroup.setVisibility(View.GONE);
            mCheckDeviceFailedViewGroup.setVisibility(View.GONE);
        } else if (mCurrentState == ConnectDeviceState.FATAL_DEVICE_NOT_CONNECT) {
            mCurrentState = ConnectDeviceState.SEARCHING_DEVICE;
            startConnectingAnimation();
        } else if (mCurrentState == ConnectDeviceState.CONNECTED) {
            mProgressImageView.clearAnimation();
            mProgressImageView.startAnimation(mProgressAnimation);
            
            mCheckDeviceViewGroup.setVisibility(View.VISIBLE);
            mCheckDeviceFailedViewGroup.setVisibility(View.GONE);
            mSyncDataViewGroup.setVisibility(View.GONE);
            mSyncDataFailedViewGroup.setVisibility(View.GONE);
            mClickConnectViewGroup.setVisibility(View.GONE);
            mConnectedSucceedViewGroup.setVisibility(View.GONE);
            mConnectingInfoViewGroup.setVisibility(View.GONE);
            mConnectedFailedViewGroup.setVisibility(View.GONE);
            
            Intent checkintent = new Intent("com.aizi.yingerbao.checkdevice");
            mListener.onDeviceConnected(checkintent);
            mCurrentState = ConnectDeviceState.CHECKING_DEVICE;
        } else if (mCurrentState == ConnectDeviceState.SYNCING_DATA) {
            mProgressImageView.clearAnimation();
            mProgressImageView.startAnimation(mProgressAnimation);
            
            mSyncDataViewGroup.setVisibility(View.VISIBLE);
            mSyncDataFailedViewGroup.setVisibility(View.GONE);
            mCheckDeviceViewGroup.setVisibility(View.GONE);
            mClickConnectViewGroup.setVisibility(View.GONE);
            mConnectedSucceedViewGroup.setVisibility(View.GONE);
            mConnectingInfoViewGroup.setVisibility(View.GONE);
            mConnectedFailedViewGroup.setVisibility(View.GONE);
            mCheckDeviceViewGroup.setVisibility(View.GONE);
            mCheckDeviceFailedViewGroup.setVisibility(View.GONE);
            
            Intent syncintent = new Intent("com.aizi.yingerbao.sync_data");
            mListener.onDeviceConnected(syncintent); // 展示进度条
        } else if (mCurrentState == ConnectDeviceState.SYNC_DATA_SUCCEED) {
            SLog.e(TAG, "SYNC_DATA_SUCCEED ");
            mIsConnectingAnimation = false;
            mProgressImageView.clearAnimation();
            
            mConnectedSucceedViewGroup.setVisibility(View.VISIBLE);
            mSyncDataViewGroup.setVisibility(View.GONE);
            mSyncDataFailedViewGroup.setVisibility(View.GONE);
            mClickConnectViewGroup.setVisibility(View.GONE);
            mConnectingInfoViewGroup.setVisibility(View.GONE);
            mConnectedFailedViewGroup.setVisibility(View.GONE);
            mCheckDeviceViewGroup.setVisibility(View.GONE);
            mCheckDeviceFailedViewGroup.setVisibility(View.GONE);
        } else if (mCurrentState == ConnectDeviceState.FAIL) {
            SLog.e(TAG, "Scan Bluetooth Service failed or disconnect");
            mIsConnectingAnimation = false;
            mCurrentState = ConnectDeviceState.IDEL;
            mProgressImageView.clearAnimation();
            
            mConnectedFailedViewGroup.setVisibility(View.VISIBLE);
            mConnectedSucceedViewGroup.setVisibility(View.GONE);
            mConnectingInfoViewGroup.setVisibility(View.GONE);
            mSyncDataViewGroup.setVisibility(View.GONE);
            mSyncDataFailedViewGroup.setVisibility(View.GONE);
            mCheckDeviceViewGroup.setVisibility(View.GONE);
            mCheckDeviceFailedViewGroup.setVisibility(View.GONE);
        } else if (mCurrentState == ConnectDeviceState.CHECKING_DEVICE_FAILED) {
            SLog.e(TAG, "Checking Device failed");
            mIsConnectingAnimation = false;
            mCurrentState = ConnectDeviceState.REPEAT_CHECKING_DEVICE;
            mProgressImageView.clearAnimation();
 
            mCheckDeviceFailedViewGroup.setVisibility(View.VISIBLE);
            mConnectedFailedViewGroup.setVisibility(View.GONE);
            mConnectedSucceedViewGroup.setVisibility(View.GONE);
            mConnectingInfoViewGroup.setVisibility(View.GONE);
            mSyncDataViewGroup.setVisibility(View.GONE);
            mSyncDataFailedViewGroup.setVisibility(View.GONE);
            mCheckDeviceViewGroup.setVisibility(View.GONE);
            
        } else if (mCurrentState == ConnectDeviceState.REPEAT_CHECKING_DEVICE) {
            mCurrentState = ConnectDeviceState.CHECKING_DEVICE;
            mProgressImageView.startAnimation(mProgressAnimation);
            DeviceFactory.getInstance(mContext).checkDeviceValid();
            // 设置检查设备状态，开始
            PrivateParams.setSPInt(mContext, "check_device_status", 1);
            // 设置校验设备超时定时器
            if (mCheckPendingIntent != null) {
                Utiliy.cancelAlarmPdIntent(mContext, mCheckPendingIntent);
            }
            mCheckPendingIntent = Utiliy.getDelayPendingIntent(mContext, Constant.ALARM_WAIT_CHECK_DEVICE);
            Utiliy.setDelayAlarm(mContext, WAIT_CHECK_PERIOD, mCheckPendingIntent);
            SLog.e(TAG, "setAlarm  checkDevice ");
            
        } else if (mCurrentState == ConnectDeviceState.SYNC_DATA_FAILED) {
            SLog.e(TAG, "Sync Data  failed");
            mIsConnectingAnimation = false;
            mCurrentState = ConnectDeviceState.REPEAT_SYNCING_DATA;
            mProgressImageView.clearAnimation();
            
            mSyncDataFailedViewGroup.setVisibility(View.VISIBLE);
            mCheckDeviceFailedViewGroup.setVisibility(View.GONE);
            mConnectedFailedViewGroup.setVisibility(View.GONE);
            mConnectedSucceedViewGroup.setVisibility(View.GONE);
            mConnectingInfoViewGroup.setVisibility(View.GONE);
            mSyncDataViewGroup.setVisibility(View.GONE);
            mCheckDeviceViewGroup.setVisibility(View.GONE);
        } else if (mCurrentState == ConnectDeviceState.REPEAT_SYNCING_DATA) {
            mCurrentState = ConnectDeviceState.SYNCING_DATA;
            mProgressImageView.startAnimation(mProgressAnimation);
            
            DeviceFactory.getInstance(mContext).getAllNoSyncInfo();
            DeviceFactory.getInstance(mContext).getBreahStopInfo();
            // 读取数据状态，开始
            PrivateParams.setSPInt(mContext, "sync_data_status", 1);
            if (mSyncDataPendingIntent != null) {
                Utiliy.cancelAlarmPdIntent(mContext, mSyncDataPendingIntent);
            }
            mSyncDataPendingIntent = Utiliy.getDelayPendingIntent(mContext, Constant.ALARM_WAIT_CHECK_DEVICE);
            Utiliy.setDelayAlarm(mContext, Constant.WAIT_SYNC_PERIOD, mSyncDataPendingIntent);
        }
    }
  
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
       if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            Intent enableintent = new Intent("com.aizi.yingerbao.scandevices");
            mListener.onDeviceConnected(enableintent);
        }
    }
    
    //Container Activity must implement this interface  
    public interface OnDeviceConnectListener{  
        public void onDeviceConnected(Intent intent);  
    } 


    /**
     * 检查的状态
     * 
     * @author panxu
     * @since 2014-8-8
     */
    public static enum ConnectDeviceState {
        IDEL, // idel
        SEARCHING_DEVICE, // 正在搜索设备
        CONNECTED, // 连接成功
        CHECKING_DEVICE, //校验设备
        REPEAT_CHECKING_DEVICE, //重新校验设备
        CHECKING_DEVICE_FAILED, // 校验设备失败
        SYNCING_DATA, // 正在同步数据
        REPEAT_SYNCING_DATA, // 重新同步数据
        SYNC_DATA_FAILED, // 同步数据失败
        SYNC_DATA_SUCCEED, // 同步数据成功
        FATAL_DEVICE_NOT_CONNECT, // 无法连接
        FAIL // 失败
    }

    public void setCurrentStateIdel(){
        mCurrentState = ConnectDeviceState.IDEL;    
    }
    
    public void setCurrentStateFailed(){
        mCurrentState = ConnectDeviceState.FAIL;    
    }
    
    public void setCurrentStateConnected(){
        mCurrentState = ConnectDeviceState.CONNECTED;  
    }
    
    public void setSyncDataSucceed(){
        mCurrentState = ConnectDeviceState.SYNC_DATA_SUCCEED;  
    }
    
    public void setSyncingData(){
        mCurrentState = ConnectDeviceState.SYNCING_DATA;  
    }
    
    public void setSyncDataFailed(){
        mCurrentState = ConnectDeviceState.SYNC_DATA_FAILED;  
    }
    
    public void setCheckDeviceFailed(){
        mCurrentState = ConnectDeviceState.CHECKING_DEVICE_FAILED;  
    }
    
    public ConnectDeviceState getCurrentState() {
        return mCurrentState;
    }
    
  public void onEventMainThread(Intent event) {  
        
        String action = event.getAction();
        if (action.equals(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED)) {
            SLog.e(TAG, "Device is connected!!  Ready for check device"); 
            setCurrentStateConnected();
            doUpdateStatusClick();
        } else if (action.equals(BluetoothService.ACTION_GATT_DISCONNECTED)) {
            setCurrentStateFailed();
            doUpdateStatusClick();
        } else if (action.equals(Constant.DATA_TRANSFER_COMPLETED)) {
            setSyncDataSucceed();
            doUpdateStatusClick();
            Intent intent = new Intent(mContext, YingerBaoActivity.class);
            startActivity(intent);
        } else if (action.equals(Constant.BLUETOOTH_SCAN_FOUND)) {
            String devaddress = PrivateParams.getSPString(mContext, 
                    Constant.AIZI_DEVICE_ADDRESS);
            if (!TextUtils.isEmpty(devaddress)) {
                // 根据蓝牙地址连接蓝牙设备
                BluetoothApi.getInstance(mContext).mBluetoothService.connect(devaddress, false);
                mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(devaddress);
                SLog.e(TAG, "Connecting Device = " + mDevice 
                        + "DeviceAddress "+ devaddress);
            }
        } else if (action.equals(Constant.BLUETOOTH_SCAN_NOT_FOUND)) {
            setCurrentStateFailed();
            doUpdateStatusClick();
        } else if (action.equals(Constant.ACTION_CHECKDEVICE_SUCCEED)) {
            boolean isSyncData = true;
            if (event.hasExtra(Constant.IS_SYNC_DATA)) {
                isSyncData = event.getBooleanExtra(Constant.IS_SYNC_DATA, true);
            }
            if (isSyncData) {
                SLog.e(TAG, "sync data has consumed six hour ");
                mCurrentState = ConnectDeviceState.SYNCING_DATA;
            }else {
                SLog.e(TAG, "sync data don not consumed six hour");
                mCurrentState = ConnectDeviceState.SYNC_DATA_SUCCEED;
            } 
            doUpdateStatusClick();
        }
    } 
  
  /**
   * 动态注册广播
   */
  public void registerMessageReceiver() {
      mMessageReceiver = new MessageReceiver();
      IntentFilter filter = new IntentFilter();

      filter.addAction(Constant.ACTION_DEVICE_CONNECT_RECEIVER);
      mContext.registerReceiver(mMessageReceiver, filter);
  }
  
  /**
   * 动态注册广播
   */
  public void unregisterMessageReceiver() {
      mContext.unregisterReceiver(mMessageReceiver);
  }
  
  
  public class MessageReceiver extends BroadcastReceiver {

      @Override
      public void onReceive(Context context, Intent intent) {
          String action = intent.getAction();
          if (!TextUtils.isEmpty(action)) {
              if (action.equals(Constant.ACTION_DEVICE_CONNECT_RECEIVER)) {
                  int delaytype = intent.getIntExtra(Constant.DEVICE_CONNECT_DELAY_TYPE, 0);
                  switch (delaytype) {
                    case 1:
                        if (mCurrentState == ConnectDeviceState.CHECKING_DEVICE) {
                            mCurrentState = ConnectDeviceState.CHECKING_DEVICE_FAILED;
                            // 设置检查设备状态，失败
                            PrivateParams.setSPInt(mContext, "check_device_status", 2);
                            SLog.e(TAG, "CHECKING_DEVICE_FAILED ");  
                        }
                        break;
                    case 2:
                        if (mCurrentState == ConnectDeviceState.SYNCING_DATA) {
                            mCurrentState = ConnectDeviceState.SYNC_DATA_FAILED;
                            // 读取数据状态，失败
                            PrivateParams.setSPInt(mContext, "sync_data_status", 2);
                            SLog.e(TAG, "SYNC_DATA_FAILED ");  
                        }
                        break;
                    case 3:                          
                        if (mCurrentState == ConnectDeviceState.SEARCHING_DEVICE) {
                            mCurrentState = ConnectDeviceState.FAIL;
                            // 搜索设备状态，失败
                            PrivateParams.setSPInt(mContext, "search_device_status", 2);
                            SLog.e(TAG, "SEARCH_DEVICE_FAILED ");  
                        }
                        break;
                    default:
                        break;
                  }
                  doUpdateStatusClick();
              }
          }
      }
  }
}
