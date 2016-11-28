package com.aizi.yingerbao;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;

import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.device.fragment.DeviceConnectStatusFragment;
import com.aizi.yingerbao.device.fragment.DeviceConnectStatusFragment.CheckingState;
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
    int mIncrementDataLen = 0;
    int mCurSyncDataLen = 0;

    TopBarView mConnectTopBarView;
    
    private HorizontalProgressBarWithNumber mProgressBar;
    private static final int MSG_PROGRESS_UPDATE = 0x100;
    private static final int MSG_PROGRESS_COMPLETED = 0x200;
    private static final int MSG_PROGRESS_AUTO_COMPLETED = 0x300;
    
    DeviceConnectStatusFragment mDevConnectFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_fun);
        
        EventBus.getDefault().register(this);

        mProgressBar = (HorizontalProgressBarWithNumber) findViewById(R.id.data_transfer_progress);
        
        initScanService();

        mConnectTopBarView = (TopBarView) findViewById(R.id.hometopbar);
        mConnectTopBarView.setClickListener(this);
        
        mDevConnectFragment 
            = (DeviceConnectStatusFragment)getFragmentManager().findFragmentById(R.id.deviceConnectFragment);
             
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {   
            /*Intent intent=new Intent();   
            intent.setClass(ChildActivity.this, MainActivity.class);   
            startActivity(intent);   
            ChildActivity.this.finish();   */
            SLog.e(TAG, "backbutton is called ");
            if (mDevConnectFragment != null) {
                if (mDevConnectFragment.getCurrentState() != CheckingState.FAIL
                        || mDevConnectFragment.getCurrentState() != CheckingState.IDEL
                        || mDevConnectFragment.getCurrentState() != CheckingState.CONNECTED
                        || mDevConnectFragment.getCurrentState() != CheckingState.FATAL_DEVICE_NOT_CONNECT
                        || mDevConnectFragment.getCurrentState() != CheckingState.IDEL) {
                    
                } 
            }
            
        }      
        return super.onKeyDown(keyCode, event);
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
            if (mScanService != null) {
                mScanService.startScanDevice();
            }
        }
    };
    
 
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int progress = 0;
            if (msg.what == MSG_PROGRESS_UPDATE) {
                progress = mProgressBar.getProgress();
                if (progress <= mCurSyncDataLen) {
                    mProgressBar.setProgress(++progress);
                    mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 100);
                } else {
                    mHandler.removeMessages(MSG_PROGRESS_UPDATE);
                }
            } else if (msg.what == MSG_PROGRESS_COMPLETED) {
                mCurSyncDataLen = 0;
                int res = msg.arg1;
                //if (res == 4) { //根据返回结果做对应处理
                    //mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_AUTO_COMPLETED, 100);
                    mHandler.sendEmptyMessage(MSG_PROGRESS_AUTO_COMPLETED);
                //}
            } else if (msg.what == MSG_PROGRESS_AUTO_COMPLETED) {
                progress = mProgressBar.getProgress();
                if (progress < 100) {
                    mProgressBar.setProgress(++progress);
                    mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_AUTO_COMPLETED, 100);
                    SLog.e(TAG, "MSG_PROGRESS_AUTO_COMPLETED 3");
                } else {
                    mHandler.removeMessages(MSG_PROGRESS_AUTO_COMPLETED);
                    if (mDevConnectFragment != null) {
                        mDevConnectFragment.doUpdateStatusClick();
                    }
                    
                    SLog.e(TAG, "MSG_PROGRESS_AUTO_COMPLETED 1");
                }
            }
        };
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mScanServiceConnection);
        EventBus.getDefault().unregister(this);//反注册EventBus  
        mHandler.removeMessages(MSG_PROGRESS_UPDATE);
        mHandler.removeMessages(MSG_PROGRESS_COMPLETED);
        mHandler.removeMessages(MSG_PROGRESS_AUTO_COMPLETED);
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
        } else if (action.equals("com.aizi.yingerbao.checkdevice")) {
            mTotalSyncDataLen = 0;
        } else if (action.equals("com.aizi.yingerbao.sync_data")) {
            mProgressBar.setVisibility(View.VISIBLE); 
        }
    }

    @Override
    public void onCalendarClick() {
        // TODO Auto-generated method stub
        
    }
    
    
    public void onEventMainThread(Intent intent) { 
        Message message = new Message();  
        String action = intent.getAction();
        if (TextUtils.isEmpty(action)) {
            return;
        }
        if (action.equals(Constant.ACTION_RECE_DATA)) {
            if (intent.hasExtra(Constant.RECE_SYNC_DATA_RESULT)) {
                int result = intent.getIntExtra(Constant.RECE_SYNC_DATA_RESULT, 0);
            } else if (intent.hasExtra(Constant.RECE_BREATH_DATA_RESULT)) {
                int result = intent.getIntExtra(Constant.RECE_BREATH_DATA_RESULT, 0);
                message.what = MSG_PROGRESS_COMPLETED;
                message.arg1 = result;
                mHandler.sendMessage(message);  
                SLog.e(TAG, "MSG_PROGRESS_COMPLETED  result = " + result);
                return;
            }
            
            SLog.e(TAG, "RECV  SYNC DATA  mTotalSyncDataLen = " + mTotalSyncDataLen);
            if (intent.hasExtra(Constant.RECE_SYNC_DATA_LEN)) {
                mIncrementDataLen = intent.getIntExtra(Constant.RECE_SYNC_DATA_LEN, 0);
                SLog.e(TAG, "RECV  SYNC DATA  mIncrementDataLen = " + mIncrementDataLen);
                if (mTotalSyncDataLen != 0 && mIncrementDataLen != 0) {
                    float recvrate = (((float)mIncrementDataLen) / mTotalSyncDataLen);
                    mCurSyncDataLen += (int)(recvrate * 100);
                    
                    message.what = MSG_PROGRESS_UPDATE; 
                    message.arg1 = mCurSyncDataLen;
                    mHandler.sendMessage(message);  
                    SLog.e(TAG, "RECV  SYNC DATA  mCurSyncDataLen " + mCurSyncDataLen);
                }
            }
        } else if (action.equals(Constant.ACTION_TOTAL_DATA_LEN)) {
            if (intent.hasExtra(Constant.NOT_SYNC_DATA_LEN)) {
                mTotalSyncDataLen = intent.getIntExtra(Constant.NOT_SYNC_DATA_LEN, 0);
                SLog.e(TAG, "RECV SYNC DATA  mTotalSyncDataLen real = " + mTotalSyncDataLen);
                if (mTotalSyncDataLen < 1000) {
                    mTotalSyncDataLen += 200; // 因为该数值不准确，加上一个值比实际值大
                } else if (mTotalSyncDataLen > 1000) {
                    mTotalSyncDataLen += 500;
                }
            }
        }
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
