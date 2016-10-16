/*
 * Copyright (C) 2014 Baidu Inc. All rights reserved.
 */
package com.aizi.yingerbao.ui.component.main;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import com.aizi.yingerbao.service.ScanDevicesService;
import com.aizi.yingerbao.service.ScanDevicesService.OnScanDeviceListener;
import com.aizi.yingerbao.utility.PrivateParams;

import de.greenrobot.event.EventBus;

/**
 * 
 * @author xuzejun
 * @since 2016-4-2
 */
public class DeviceConnectStatusFragment extends Fragment{
    
    private static final String TAG = DeviceConnectStatusFragment.class.getSimpleName();

    public static final int ABNORMAL_REQUEST_CODE = 1;
    private static final int REQUEST_SELECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BLUETOOTH = 101;
    private BluetoothDevice mDevice = null;
    private BluetoothService mService = null;
    OnDeviceConnectListener mListener;
    OnScanDeviceListener mScanDeviceListener;

    /** 远程还是本地 */
    private TextView mDeviceOrLocalTextView;

    /** 检查网络 */
    private TextView mCheckNetworkConnectingTextView;

    /** 连接后的状态 成功还是失败 */
    private TextView mConnectedStatusTextView;

    /** 连接时候的progress */
    private ImageView mProgressImageView;

    /** 正在连接动画 */
    private boolean mIsConnectingAnimation;

    /** 适配器 提供数据 */
//    private RouterAdapter mRouterAdapter;

    /** 正在连接的view group */
    private ViewGroup mConnectingInfoViewGroup;


    /** 连接完成的状态view gourp */
    public ViewGroup mConnectedSucceedViewGroup;
    
    /** 连接失败的状态view gourp */
    public ViewGroup mConnectedFailedViewGroup;
    
    public ViewGroup mClickConnectViewGroup;
    
    public ViewGroup mSyncDataViewGroup;


    /** 正在检测的状态 */
    public static CheckingState mCurrentState;

    /** UI handler */
    private final Handler mUIHandler = new Handler(Looper.getMainLooper());


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{  
            mListener =(OnDeviceConnectListener)activity;  
        }catch(ClassCastException e){  
            throw new ClassCastException(activity.toString()+"must implement OnArticleSelectedListener");  
        }  
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mCurrentState = CheckingState.IDEL;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View deviceStatusView = inflater.inflate(R.layout.babyfun_status_fragment, container, false);
        
       // service_init();
        mCheckNetworkConnectingTextView = (TextView) deviceStatusView.findViewById(R.id.checkingNetworkTextView);
        mConnectedStatusTextView = (TextView) deviceStatusView.findViewById(R.id.connectedStatusTextView);
        
        mProgressImageView = (ImageView) deviceStatusView.findViewById(R.id.progressImageView);
        mProgressImageView.setOnClickListener(new UpdateStatusOnclickListener());

        mSyncDataViewGroup = (ViewGroup) deviceStatusView.findViewById(R.id.syncDataLayout);
        mClickConnectViewGroup = (ViewGroup) deviceStatusView.findViewById(R.id.clicktostart);
        mConnectingInfoViewGroup = (ViewGroup) deviceStatusView.findViewById(R.id.connectInfoLayout);
        mConnectedSucceedViewGroup = (ViewGroup) deviceStatusView.findViewById(R.id.connectedSucceedLayout);
        mConnectedFailedViewGroup = (ViewGroup) deviceStatusView.findViewById(R.id.connectedFailedLayout);
        
        Timer mTimer = new Timer(true);
        TimerTask task = new TimerTask(){  
            public void run() {  
            doUpdateStatusClick();
          }  
        };  
        mTimer.schedule(task,1000); 
        
        EventBus.getDefault().register(this);
        
       
        
        return deviceStatusView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mCurrentState == CheckingState.IDEL) {
            startConnectingAnimation();
        }
    }
    
    /**
     * 开始检查连接
     */
    public void startConnectingAnimation() {

        if (!mIsConnectingAnimation) {
            mIsConnectingAnimation = true;
            mProgressImageView.setImageResource(R.drawable.lightline);
            Animation progressAnimation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.connecting_router_rotate_animation);
            mProgressImageView.startAnimation(progressAnimation);
            
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
            mCurrentState = CheckingState.CHECKING;     
         /*   Intent intent = new Intent("com.babyfun.scandevices");
            mListener.onDeviceConnected(intent);*/
            Intent bluetoothIntent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(bluetoothIntent,REQUEST_ENABLE_BLUETOOTH);
        } else if (mCurrentState == CheckingState.CHECKING) {
            mIsConnectingAnimation = false;
            mCurrentState = CheckingState.IDEL;
            mProgressImageView.clearAnimation();
            mConnectedSucceedViewGroup.setVisibility(View.VISIBLE);
            mConnectingInfoViewGroup.setVisibility(View.GONE);
            mConnectedFailedViewGroup.setVisibility(View.GONE);
        } else if (mCurrentState == CheckingState.FATAL_DEVICE_NOT_CONNECT) {
            mCurrentState = CheckingState.CHECKING;
            startConnectingAnimation();
        } else if (mCurrentState == CheckingState.CONNECTED) {
            mIsConnectingAnimation = false;
            mProgressImageView.clearAnimation();
            mSyncDataViewGroup.setVisibility(View.GONE);
            mClickConnectViewGroup.setVisibility(View.GONE);
            mConnectedSucceedViewGroup.setVisibility(View.VISIBLE);
            mConnectingInfoViewGroup.setVisibility(View.GONE);
            mConnectedFailedViewGroup.setVisibility(View.GONE);
        } else if (mCurrentState == CheckingState.SYNC_DATA_SUCCEED) {
            mIsConnectingAnimation = false;
            mProgressImageView.clearAnimation();
            mSyncDataViewGroup.setVisibility(View.GONE);
            mClickConnectViewGroup.setVisibility(View.GONE);
            mConnectedSucceedViewGroup.setVisibility(View.VISIBLE);
            mConnectingInfoViewGroup.setVisibility(View.GONE);
            mConnectedFailedViewGroup.setVisibility(View.GONE);
        } else if (mCurrentState == CheckingState.FAIL) {
            
            SLog.e(TAG, "Bluetooth Service disconnect");
            mIsConnectingAnimation = false;
            mCurrentState = CheckingState.IDEL;
            mProgressImageView.clearAnimation();
            mConnectedSucceedViewGroup.setVisibility(View.INVISIBLE);
            mConnectingInfoViewGroup.setVisibility(View.INVISIBLE);
            mConnectedFailedViewGroup.setVisibility(View.VISIBLE);
        }
    }
  
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
       if (requestCode == REQUEST_SELECT_DEVICE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
               
                SLog.e(TAG, "onActivityResultdevice.address = " + mDevice 
                        + "deviceaddress = "+ deviceAddress 
                        +" myserviceValue = " + mService);
             
                mListener.onDeviceConnected(data);
                doUpdateStatusClick();

            }
        } else if (requestCode == REQUEST_ENABLE_BLUETOOTH) {
            Intent intent = new Intent("com.babyfun.scandevices");
            mListener.onDeviceConnected(intent);
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
        CHECKING, // 正在检查
        FATAL_DEVICE_NOT_CONNECT, // 无法连接
        FAIL, // 失败
        CONNECTED, // 连接成功
        SYNC_DATA_SUCCEED // 同步数据成功
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
    
    public CheckingState getCurrentState() {
        return mCurrentState;
    }

   /* @Override
    public void OnScanDeviceSucceed(int touchid) {
        if (touchid == 1) {    
            String deviceAddress = PrivateParams.getSPString(getActivity().getApplicationContext(),
                    Constant.SHARED_DEVICE_ADDRESS);
            if (TextUtils.isEmpty(deviceAddress)) {
                return;
            }
            mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
            SLog.e(TAG, "... onActivityResultdevice.address==" + mDevice 
                    + "deviceaddress "+ deviceAddress 
                    +" myserviceValue = " + mService);
            BluetoothApi.getInstance(getActivity().getApplicationContext())
                        .mBluetoothService.connect(deviceAddress);
            //mService.connect(deviceAddress);
                        
        } else if (touchid == 2) {
            setCurrentStateFailed();
            doUpdateStatusClick();
        }
        
    }*/
    
    
  public void onEventMainThread(Intent event) {  
        
        String action = event.getAction();
        if (action.equals(BluetoothService.ACTION_GATT_SERVICES_DISCOVERED)) {
            setCurrentStateConnected();
            doUpdateStatusClick();
            AsyncDeviceFactory.getInstance(getActivity().getApplicationContext()).getAllNoSyncInfo();
        } else if (action.equals(BluetoothService.ACTION_GATT_DISCONNECTED)) {
            setCurrentStateFailed();
            doUpdateStatusClick();
        } else if (action.equals(Constant.DATA_TRANSFER_COMPLETED)) {
            setCurSyncDataSucceed();
            doUpdateStatusClick();
            Intent intent = new Intent(getActivity().getApplicationContext(), YingerBaoActivity.class);
            startActivity(intent);
        } else if (action.equals(Constant.BLUETOOTH_SCAN_FOUND)) {
            String deviceAddress = PrivateParams.getSPString(getActivity().getApplicationContext(),
                    Constant.SHARED_DEVICE_ADDRESS);
            if (TextUtils.isEmpty(deviceAddress)) {
                return;
            }
            mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
            SLog.e(TAG, "... onActivityResultdevice.address==" + mDevice 
                    + "deviceaddress "+ deviceAddress 
                    +" myserviceValue = " + mService);
            BluetoothApi.getInstance(getActivity().getApplicationContext())
                        .mBluetoothService.connect(deviceAddress);
        } else if (action.equals(Constant.BLUETOOTH_SCAN_NOT_FOUND)) {
            setCurrentStateFailed();
            doUpdateStatusClick();
        }
    } 


}
