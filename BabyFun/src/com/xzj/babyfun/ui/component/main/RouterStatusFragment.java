/*
 * Copyright (C) 2014 Baidu Inc. All rights reserved.
 */
package com.xzj.babyfun.ui.component.main;

import android.app.Activity;
import android.app.Fragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.xzj.babyfun.DeviceListActivity;
import com.xzj.babyfun.R;
import com.xzj.babyfun.service.ScanBlueToothService;
import com.xzj.babyfun.service.ScanBlueToothService.OnScanDeviceListener;
import com.xzj.babyfun.service.UartService;

/**
 * 
 * @author xuzejun
 * @since 2016-4-2
 */
public class RouterStatusFragment extends Fragment{
    
    private static final String TAG = RouterStatusFragment.class.getSimpleName();

    public static final int ABNORMAL_REQUEST_CODE = 1;
    private static final int REQUEST_SELECT_DEVICE = 1;
    private BluetoothDevice mDevice = null;
    private UartService mService = null;
    OnItemSelectedListener mListener;
    OnScanDeviceListener mScanDeviceListener;
    private ScanBlueToothService mScanService = null;

    /** 路由名称 */
    private TextView mRouterNameTextView;

    /** 速度 */
    private TextView mRouterSpeedTextView;

    /** 远程还是本地 */
    private TextView mRemoteOrLocalTextView;

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

    /** 正在检查的image view */
    private ImageView mCheckingItemImageView;

    /** 连接完成的状态view gourp */
    public ViewGroup mConnectedSucceedViewGroup;
    
    /** 连接失败的状态view gourp */
    public ViewGroup mConnectedFailedViewGroup;

    /** 连接状态标识image view */
    private ImageView mConnectedStatusIndicatorImageView;

    /** 设置的image view */
    private ImageView mSettingImageView;
    
    /** 设置的image view */
    private LinearLayout mStatusView;

    /** 正在检测的状态 */
    public static CheckingState mCurrentState;

    /** 检测状态结果 */
    private int[] mCheckStates;

    /** UI handler */
    private final Handler mUIHandler = new Handler(Looper.getMainLooper());

    /** 是否显示了百度登陆 */
    private boolean mIsBaiduLoginShow;

    /** 是否destory */
    private boolean mIsDestroyed;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try{  
            mListener =(OnItemSelectedListener)activity;  
           // mScanDeviceListener = (OnScanDeviceListener)activity;
        }catch(ClassCastException e){  
            throw new ClassCastException(activity.toString()+"must implement OnArticleSelectedListener");  
        }  
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        mCurrentState = CheckingState.IDEL;
        
   
       // service_init();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View babyfunStatusView = inflater.inflate(R.layout.babyfun_status_fragment, container, false);
        
       // service_init();
        mCheckNetworkConnectingTextView = (TextView) babyfunStatusView.findViewById(R.id.checkingNetworkTextView);

        mProgressImageView = (ImageView) babyfunStatusView.findViewById(R.id.progressImageView);
        mProgressImageView.setOnClickListener(new UpdateStatusOnclickListener());

        mConnectingInfoViewGroup = (ViewGroup) babyfunStatusView.findViewById(R.id.connectInfoLayout);
        mCheckingItemImageView = (ImageView) babyfunStatusView.findViewById(R.id.checkingItemImageView);

        mConnectedSucceedViewGroup = (ViewGroup) babyfunStatusView.findViewById(R.id.connectedSucceedLayout);
        mConnectedStatusIndicatorImageView = (ImageView) babyfunStatusView
                .findViewById(R.id.connectedStatusIndicatorImageView);

        mConnectedStatusTextView = (TextView) babyfunStatusView.findViewById(R.id.connectedStatusTextView);

        mConnectedFailedViewGroup = (ViewGroup) babyfunStatusView.findViewById(R.id.connectedFailedLayout);
//        mStatusView = (LinearLayout) babyfunStatusView.findViewById(R.id.environment1);
//
//        mStatusView.setOnClickListener(new View.OnClickListener() {
//            
//            @Override
//            public void onClick(View v) {
//                // TODO Auto-generated method stub
//                Toast.makeText(v.getContext(), "点击", Toast.LENGTH_SHORT).show();
//            }
//        });
        return babyfunStatusView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

     /*   if (mCurrentState == CheckingState.IDEL) {
            mCurrentState = CheckingState.CHECKING;
            startConnecting();
        }*/
    }
    
   private ServiceConnection mScanServiceConnection = new ServiceConnection() {
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            mScanService = ((ScanBlueToothService.ScanBinder) service).getService();
        }
    };
    
  

    /**
     * 设置路由名称
     * 
     * @param name
     *            路由名称
     */
    public void setRouterName(String name) {
        mRouterNameTextView.setText(name);
    }


    /**
     * 设置远程还是直连
     * 
     * @param isLocal
     *            是否是直连
     */
    public void setRemoteOrLocal(boolean isLocal) {
        if (isLocal) {
            mRemoteOrLocalTextView.setText(R.string.local);
        } else {
            mRemoteOrLocalTextView.setText(R.string.remote);
        }
    }

    /**
     * 开始检查连接
     */
    public void startConnecting() {

        if (!mIsConnectingAnimation) {
            mIsConnectingAnimation = true;

        //    mProgressImageView.setImageResource(R.drawable.main_checking_status_progress);
            mProgressImageView.setImageResource(R.drawable.lightline);

            Animation progressAnimation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.connecting_router_rotate_animation);

            mProgressImageView.startAnimation(progressAnimation);

            mConnectedSucceedViewGroup.setVisibility(View.GONE);
            mConnectedFailedViewGroup.setVisibility(View.GONE);

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
        mIsDestroyed = true;


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
            if (mCurrentState == CheckingState.IDEL || mCurrentState == CheckingState.FAIL) {
                doUpdateStatusClick();
            }
            
        }
    }

    /**
     * 更新状态的click
     */
    public void doUpdateStatusClick() {
        Log.e(TAG, "mCurrentState = IDEL  1  " + mCurrentState);
        if (mCurrentState == CheckingState.IDEL) {
            startConnecting();
            mCurrentState = CheckingState.CHECKING;
            Log.e(TAG, "mCurrentState = IDEL 2");
            
            Intent enabler=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enabler,10);//鍚宻tartActivity(enabler);  鍚姩钃濈墮
            //mRouterAdapter.startCheckingStatus();
           
       
        } else if (mCurrentState == CheckingState.CHECKING) {
            mIsConnectingAnimation = false;
            mCurrentState = CheckingState.IDEL;
            mProgressImageView.clearAnimation();
            mConnectedSucceedViewGroup.setVisibility(View.VISIBLE);

            mConnectingInfoViewGroup.setVisibility(View.GONE);
            // 什么也不做
        } else if (mCurrentState == CheckingState.FATAL_DEVICE_NOT_CONNECT) {
            mCurrentState = CheckingState.CHECKING;
            startConnecting();
          
        } else if (mCurrentState == CheckingState.SUCCEED) {
            mIsConnectingAnimation = false;
            mCurrentState = CheckingState.IDEL;
            mProgressImageView.clearAnimation();
        } else if (mCurrentState == CheckingState.FAIL) {
            mIsConnectingAnimation = false;
            mCurrentState = CheckingState.IDEL;
            mProgressImageView.clearAnimation();
            mConnectedSucceedViewGroup.setVisibility(View.GONE);
            mConnectingInfoViewGroup.setVisibility(View.GONE);
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
               
                Log.e(TAG, "... onActivityResultdevice.address==" + mDevice + "deviceaddress "+ deviceAddress +" myserviceValue = " + mService);
               // ((TextView) findViewById(R.id.deviceName)).setText(mDevice.getName()+ " - connecting");
              //  mService.connect(deviceAddress);
                
              //  Log.e(TAG, "... onActivityResultdevice.address==" + mDevice + "deviceaddress "+ deviceAddress +" myserviceValue = " + mService);
                mListener.onItemSelected(data);
               // mCurrentState = CheckingState.SUCCEED;
                doUpdateStatusClick();

            }
        }
       if (requestCode == 10) {
       
           Intent intent = new Intent("com.babyfun.scandevices");
           mListener.onItemSelected(intent);
          // mScanDeviceListener.OnScanDeviceSucceed(9);
           //Intent newIntent = new Intent(getActivity(), DeviceListActivity.class);
           //startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
    }
    }
    
    //Container Activity must implement this interface  
    public interface OnItemSelectedListener{  
        public void onItemSelected(Intent intent);  
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
        FATAL_DEVICE_NOT_CONNECT, // 无法连接路由
        FAIL, // 失败
        SUCCEED
    }

    public void setCurrentStateFailed(){
        mCurrentState = CheckingState.FAIL;    
    }
    
    public CheckingState getCurrentState() {
        return mCurrentState;
    }

 
    
/*    
    //UART service connected/disconnected
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
                mService = ((UartService.LocalBinder) rawBinder).getService();
                Log.e(TAG, "onServiceConnected mService= " + mService);
                if (!mService.initialize()) {
                    Log.e(TAG, "Unable to initialize Bluetooth");
                    getActivity().finish();
                }
                
                Log.e(TAG, "Unable to initialize Bluetooth   onServiceConnected");

        }

        public void onServiceDisconnected(ComponentName classname) {
       ////     mService.disconnect(mDevice);
                mService = null;
                Log.e(TAG, "Unable to initialize Bluetooth   onServiceDisconnected");
        }
    };*/
    
    
/*    private void service_init() {
        Intent bindIntent = new Intent(getActivity(), UartService.class);
        getActivity().bindService(bindIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
        Log.e(TAG, "onServiceConnected mService= " + mService);
       // registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
        
  
      //  LocalBroadcastManager.getInstance(this).registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());
    }*/

}
