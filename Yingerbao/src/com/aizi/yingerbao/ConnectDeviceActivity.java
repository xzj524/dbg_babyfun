package com.aizi.yingerbao;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.Window;

import com.aizi.yingerbao.device.fragment.DeviceConnectStatusFragment.OnDeviceConnectListener;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.service.ScanDevicesService;
import com.aizi.yingerbao.view.HorizontalProgressBarWithNumber;
import com.aizi.yingerbao.view.TopBarView;
import com.aizi.yingerbao.view.TopBarView.onTitleBarClickListener;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

public class ConnectDeviceActivity extends Activity implements OnDeviceConnectListener,
onTitleBarClickListener {
    
    private static final String TAG = ConnectDeviceActivity.class.getSimpleName();
    
    private ScanDevicesService mScanService = null;
    
    int mTotalSyncDataLen = 0;
    int mCurSyncDataLen = 0;

    TopBarView mConnectTopBarView;
    
    private HorizontalProgressBarWithNumber mProgressBar;
    private static final int MSG_PROGRESS_UPDATE = 0x110;

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_fun);
        
        EventBus.getDefault().register(this);

        mProgressBar = (HorizontalProgressBarWithNumber) findViewById(R.id.data_transfer_progress);
        //mHandler.sendEmptyMessage(MSG_PROGRESS_UPDATE);
        
        initScanService();

        mConnectTopBarView = (TopBarView) findViewById(R.id.hometopbar);
        mConnectTopBarView.setClickListener(this);
             
    }
    
    private void initScanService(){
        try {
            for (int i = 0; i < 3; i++) {
                Intent bindscanIntent = new Intent(this, ScanDevicesService.class);
                bindService(bindscanIntent, mScanServiceConnection, Context.BIND_AUTO_CREATE);
                if (mScanService != null) {
                    SLog.e(TAG, "mScanService = " + mScanService);
                    break;
                }
            }      
        } catch (Exception e) {
            SLog.e(TAG, e);
        } 
    }

   
    private ServiceConnection mScanServiceConnection = new ServiceConnection() {
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mScanService = null;
        }
        
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mScanService = ((ScanDevicesService.ScanBinder) service).getService();
           /* if (mScanService != null) {
                mScanService.startScanDevice();
            }*/
        }
    };
    
 
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == MSG_PROGRESS_UPDATE) {
                
            }
            int progress = mProgressBar.getProgress();
            
            if (progress <= 100) {
                mProgressBar.setProgress(++progress);
            }
            mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 100);
        };
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mScanServiceConnection);
        EventBus.getDefault().unregister(this);//反注册EventBus  
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onRightClick() {
    }

    @Override
    public void onDeviceConnected(Intent intent) {
        String action = intent.getAction();
        if (action.equals("com.aizi.yingerbao.scandevices")) {
            SLog.e(TAG, "start scan bluetooth1");
            if (mScanService != null) {
                SLog.e(TAG, "start scan bluetooth2");  
                mScanService.startScanDevice();
            } else {
                SLog.e(TAG, "start scan bluetooth3");
                Intent bindIntent = new Intent(this, ScanDevicesService.class);
                if (bindService(bindIntent, mScanServiceConnection, Context.BIND_AUTO_CREATE)) {
                    if (mScanService != null) {
                        mScanService.startScanDevice();
                        SLog.e(TAG, "start scan bluetooth4");
                    }
                }
            }
        } else if (action.equals("com.aizi.finish")) {
            finish();
        } else if (action.equals("com.aizi.transfer")) {
            
        }
    }

    @Override
    public void onCalendarClick() {
        // TODO Auto-generated method stub
        
    }
    
    
    public void onEventMainThread(Intent event) { 
        
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
}
