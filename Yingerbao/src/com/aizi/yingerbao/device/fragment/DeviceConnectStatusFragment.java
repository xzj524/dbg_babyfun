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
import android.widget.TextView;

import com.aizi.yingerbao.R;
import com.aizi.yingerbao.YingerBaoActivity;
import com.aizi.yingerbao.bluttooth.BluetoothApi;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.deviceinterface.AsyncDeviceFactory;
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
    private static final long SCAN_PERIOD = 10 * 1000; //10 seconds
    private BluetoothDevice mDevice = null;
    OnDeviceConnectListener mListener;

    /** 检查网络 */
    private TextView mCheckNetworkConnectingTextView;

    /** 连接后的状态 成功还是失败 */
    private TextView mConnectedStatusTextView;

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
    
    public ViewGroup mClickConnectViewGroup;
    
    public ViewGroup mSyncDataViewGroup;
    public ViewGroup mCheckDeviceViewGroup;
    
    Animation mProgressAnimation; 
    
    Context mContext;


    /** 正在检测的状态 */
    public static CheckingState mCurrentState;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{  
            mContext = activity.getApplicationContext();
            mListener =(OnDeviceConnectListener)activity;  
        }catch(ClassCastException e){  
            throw new ClassCastException(activity.toString()+"must implement OnDeviceConnectListener");  
        }  
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCurrentState = CheckingState.IDEL;
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

                public void run() {   
                    if (mCurrentState == CheckingState.IDEL) {
                        mCurrentState = CheckingState.FAIL;
                        doUpdateStatusClick();
                    }
                }   
             }, SCAN_PERIOD);   
        } else {
            mCurrentState = CheckingState.CONNECTED;
            doUpdateStatusClick();
        }
        
        EventBus.getDefault().register(this);
        return deviceStatusView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * 开始检查连接
     */
    public void startConnectingAnimation() {

        if (!mIsConnectingAnimation) {
            mIsConnectingAnimation = true;
            mProgressImageView.setImageResource(R.drawable.lightline);
            mProgressAnimation = AnimationUtils.loadAnimation(mContext,
                    R.anim.connecting_router_rotate_animation);
            mProgressImageView.startAnimation(mProgressAnimation);
            
            mConnectedSucceedViewGroup.setVisibility(View.GONE);
            mConnectedFailedViewGroup.setVisibility(View.GONE);
            mClickConnectViewGroup.setVisibility(View.GONE);
            mConnectingInfoViewGroup.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();  
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    /**
     * 更新状态的OnClick
     * 
     * @author panxu
     * @since 2014-8-8
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
        if (mCurrentState == CheckingState.IDEL) {
            startConnectingAnimation();
            mCurrentState = CheckingState.SEARCHING_DEVICE;     
            Intent bluetoothIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bluetoothIntent,REQUEST_ENABLE_BLUETOOTH);

        } else if (mCurrentState == CheckingState.FATAL_DEVICE_NOT_CONNECT) {
            mCurrentState = CheckingState.SEARCHING_DEVICE;
            startConnectingAnimation();
        } else if (mCurrentState == CheckingState.CONNECTED) {
            mProgressImageView.clearAnimation();
            mProgressImageView.startAnimation(mProgressAnimation);
            
            mCheckDeviceViewGroup.setVisibility(View.VISIBLE);
            mSyncDataViewGroup.setVisibility(View.GONE);
            mClickConnectViewGroup.setVisibility(View.GONE);
            mConnectedSucceedViewGroup.setVisibility(View.GONE);
            mConnectingInfoViewGroup.setVisibility(View.GONE);
            mConnectedFailedViewGroup.setVisibility(View.GONE);
            
            new Handler().postDelayed(new Runnable() {
                
                @Override
                public void run() {
                    AsyncDeviceFactory.getInstance(mContext).checkDeviceValid();
                    Intent checkintent = new Intent("com.aizi.yingerbao.checkdevice");
                    mListener.onDeviceConnected(checkintent);
                    mCurrentState = CheckingState.CHECKING_DEVICE;
                }
            }, 500);
        } else if (mCurrentState == CheckingState.CHECKING_DEVICE) {
            mProgressImageView.clearAnimation();
            mProgressImageView.startAnimation(mProgressAnimation);
            mSyncDataViewGroup.setVisibility(View.VISIBLE);
            mCheckDeviceViewGroup.setVisibility(View.GONE);
            mClickConnectViewGroup.setVisibility(View.GONE);
            mConnectedSucceedViewGroup.setVisibility(View.GONE);
            mConnectingInfoViewGroup.setVisibility(View.GONE);
            mConnectedFailedViewGroup.setVisibility(View.GONE);
            mCheckDeviceViewGroup.setVisibility(View.GONE);
            mCurrentState = CheckingState.SYNC_DATA_SUCCEED;
            Intent syncintent = new Intent("com.aizi.yingerbao.sync_data");
            mListener.onDeviceConnected(syncintent);
        } else if (mCurrentState == CheckingState.SYNC_DATA_SUCCEED) {
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
            
        } else if (mCurrentState == CheckingState.FAIL) {
            SLog.e(TAG, "Scan Bluetooth Service failed or disconnect");
            mIsConnectingAnimation = false;
            mCurrentState = CheckingState.IDEL;
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
    public static enum CheckingState {
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
        mCurrentState = CheckingState.FAIL;    
    }
    
    public void setCurrentStateConnected(){
        mCurrentState = CheckingState.CONNECTED;  
    }
    
    public void setCurSyncDataSucceed(){
        mCurrentState = CheckingState.SYNC_DATA_SUCCEED;  
    }
    
    public void setCurSyncData(){
        mCurrentState = CheckingState.SYNCING_DATA;  
    }
    
    public CheckingState getCurrentState() {
        return mCurrentState;
    }
    
  public void onEventMainThread(Intent event) {  
        
        String action = event.getAction();
        if (action.equals(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED)) {
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
                BluetoothApi.getInstance(mContext)
                .mBluetoothService.connect(devaddress);
                mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(devaddress);
                SLog.e(TAG, "... onActivityResultdevice.address==" + mDevice 
                        + "deviceaddress "+ devaddress);
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
                mCurrentState = CheckingState.SYNC_DATA_SUCCEED;
                SLog.e(TAG, "sync data don not consumed six hour");
            } 
            
            doUpdateStatusClick();
        }
    } 
}
