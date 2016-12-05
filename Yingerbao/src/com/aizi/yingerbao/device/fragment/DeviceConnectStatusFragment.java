/*
 * Copyright (C) 2016 Aizi Inc. All rights reserved.
 */
package com.aizi.yingerbao.device.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
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
    private BluetoothDevice mDevice = null;
    OnDeviceConnectListener mListener;
    Animation mProgressAnimation; 
    Context mContext;

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
    
    /** 正在验证设备的状态view gourp */
    public ViewGroup mCheckDeviceViewGroup;
   
    /** 连接设备的状态 */
    public static ConnectDeviceState mCurrentState;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{  
            mContext = activity.getApplicationContext();
            mListener =(OnDeviceConnectListener)activity;  
        }catch(ClassCastException e){  
            throw new ClassCastException(activity.toString() + " must implement OnDeviceConnectListener");  
        }  
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentState = ConnectDeviceState.IDEL;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View deviceStatusView = inflater.inflate(R.layout.yingerbao_status_fragment, container, false);
        
        mProgressImageView = (ImageView) deviceStatusView.findViewById(R.id.progressImageView);
        mProgressImageView.setOnClickListener(new UpdateStatusOnclickListener());

        mSyncDataViewGroup = (ViewGroup) deviceStatusView.findViewById(R.id.syncDataLayout);
        mClickConnectViewGroup = (ViewGroup) deviceStatusView.findViewById(R.id.clicktostart);
        mConnectingInfoViewGroup = (ViewGroup) deviceStatusView.findViewById(R.id.connectInfoLayout);
        mConnectedSucceedViewGroup = (ViewGroup) deviceStatusView.findViewById(R.id.connectedSucceedLayout);
        mConnectedFailedViewGroup = (ViewGroup) deviceStatusView.findViewById(R.id.connectedFailedLayout);
        mCheckDeviceViewGroup = (ViewGroup) deviceStatusView.findViewById(R.id.checkdeviceLayout);
        
        if (!Utiliy.isBluetoothConnected(mContext)) {
            doUpdateStatusClick();
            
            new Handler().postDelayed(new Runnable(){   
            // 判断检测超时
                public void run() {   
                    if (mCurrentState == ConnectDeviceState.IDEL) {
                        mCurrentState = ConnectDeviceState.FAIL;
                        doUpdateStatusClick();
                    }
                }   
             }, WAIT_PERIOD);   
        } else {
            mCurrentState = ConnectDeviceState.CONNECTED;
            doUpdateStatusClick();
        }
        
        EventBus.getDefault().register(this);
        return deviceStatusView;
    }
    
    /**
     * 开始检查连接动画
     */
    public void startConnectingAnimation() {

        if (!mIsConnectingAnimation) {
            mIsConnectingAnimation = true;
            mProgressImageView.setImageResource(R.drawable.lightline);
            mProgressAnimation = AnimationUtils.loadAnimation(mContext,
                    R.anim.connecting_router_rotate_animation);
            mProgressImageView.startAnimation(mProgressAnimation);
            
/*            mConnectedSucceedViewGroup.setVisibility(View.GONE);
            mConnectedFailedViewGroup.setVisibility(View.GONE);
            mClickConnectViewGroup.setVisibility(View.GONE);
            mConnectingInfoViewGroup.setVisibility(View.VISIBLE);*/
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
            
            mConnectingInfoViewGroup.setVisibility(View.VISIBLE);
            mConnectedSucceedViewGroup.setVisibility(View.GONE);
            mConnectedFailedViewGroup.setVisibility(View.GONE);
            mClickConnectViewGroup.setVisibility(View.GONE);
            mSyncDataViewGroup.setVisibility(View.GONE);
            mCheckDeviceViewGroup.setVisibility(View.GONE);
        } else if (mCurrentState == ConnectDeviceState.SEARCHING_DEVICE) {
            
        } else if (mCurrentState == ConnectDeviceState.FATAL_DEVICE_NOT_CONNECT) {
            mCurrentState = ConnectDeviceState.SEARCHING_DEVICE;
            startConnectingAnimation();
        } else if (mCurrentState == ConnectDeviceState.CONNECTED) {
            mProgressImageView.clearAnimation();
            mProgressImageView.startAnimation(mProgressAnimation);
            
            mCheckDeviceViewGroup.setVisibility(View.VISIBLE);
            mSyncDataViewGroup.setVisibility(View.GONE);
            mClickConnectViewGroup.setVisibility(View.GONE);
            mConnectedSucceedViewGroup.setVisibility(View.GONE);
            mConnectingInfoViewGroup.setVisibility(View.GONE);
            mConnectedFailedViewGroup.setVisibility(View.GONE);
            
            Intent checkintent = new Intent("com.aizi.yingerbao.checkdevice");
            mListener.onDeviceConnected(checkintent);
            mCurrentState = ConnectDeviceState.CHECKING_DEVICE;

        } else if (mCurrentState == ConnectDeviceState.CHECKING_DEVICE) {
            mProgressImageView.clearAnimation();
            mProgressImageView.startAnimation(mProgressAnimation);
            mSyncDataViewGroup.setVisibility(View.VISIBLE);
            mCheckDeviceViewGroup.setVisibility(View.GONE);
            mClickConnectViewGroup.setVisibility(View.GONE);
            mConnectedSucceedViewGroup.setVisibility(View.GONE);
            mConnectingInfoViewGroup.setVisibility(View.GONE);
            mConnectedFailedViewGroup.setVisibility(View.GONE);
            mCheckDeviceViewGroup.setVisibility(View.GONE);
            mCurrentState = ConnectDeviceState.SYNC_DATA_SUCCEED;
            Intent syncintent = new Intent("com.aizi.yingerbao.sync_data");
            mListener.onDeviceConnected(syncintent);
        } else if (mCurrentState == ConnectDeviceState.SYNC_DATA_SUCCEED) {
            SLog.e(TAG, "SYNC_DATA_SUCCEED ");
            mIsConnectingAnimation = false;
            mProgressImageView.clearAnimation();
            mSyncDataViewGroup.setVisibility(View.GONE);
            mClickConnectViewGroup.setVisibility(View.GONE);
            mConnectedSucceedViewGroup.setVisibility(View.VISIBLE);
            mConnectingInfoViewGroup.setVisibility(View.GONE);
            mConnectedFailedViewGroup.setVisibility(View.GONE);
            mCheckDeviceViewGroup.setVisibility(View.GONE);
            
            PrivateParams.setSPLong(mContext,
                    Constant.SYNC_DATA_SUCCEED_TIMESTAMP, System.currentTimeMillis());
            
        } else if (mCurrentState == ConnectDeviceState.FAIL) {
            SLog.e(TAG, "Scan Bluetooth Service failed or disconnect");
            mIsConnectingAnimation = false;
            mCurrentState = ConnectDeviceState.IDEL;
            mProgressImageView.clearAnimation();
            mConnectedSucceedViewGroup.setVisibility(View.INVISIBLE);
            mConnectingInfoViewGroup.setVisibility(View.INVISIBLE);
            mConnectedFailedViewGroup.setVisibility(View.VISIBLE);
            mSyncDataViewGroup.setVisibility(View.GONE);
            mCheckDeviceViewGroup.setVisibility(View.GONE);
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
        CHECKING_DEVICE, //校验设备
        CONNECTED, // 连接成功
        SYNCING_DATA, // 正在同步设备
        SYNC_DATA_SUCCEED, // 同步数据成功
        FATAL_DEVICE_NOT_CONNECT, // 无法连接
        FAIL // 失败
    }

    public void setCurrentStateFailed(){
        mCurrentState = ConnectDeviceState.FAIL;    
    }
    
    public void setCurrentStateConnected(){
        mCurrentState = ConnectDeviceState.CONNECTED;  
    }
    
    public void setCurSyncDataSucceed(){
        mCurrentState = ConnectDeviceState.SYNC_DATA_SUCCEED;  
    }
    
    public void setCurSyncData(){
        mCurrentState = ConnectDeviceState.SYNCING_DATA;  
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
            setCurSyncDataSucceed();
            doUpdateStatusClick();
            Intent intent = new Intent(mContext, YingerBaoActivity.class);
            startActivity(intent);
        } else if (action.equals(Constant.BLUETOOTH_SCAN_FOUND)) {
            String devaddress = PrivateParams.getSPString(mContext, 
                    Constant.AIZI_DEVICE_ADDRESS);
            if (!TextUtils.isEmpty(devaddress)) {
                // 根据蓝牙地址连接蓝牙设备
                BluetoothApi.getInstance(mContext).mBluetoothService.connect(devaddress);
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
            }else {
                mCurrentState = ConnectDeviceState.SYNC_DATA_SUCCEED;
                SLog.e(TAG, "sync data don not consumed six hour");
            } 
            
            doUpdateStatusClick();
        }
    } 
}
