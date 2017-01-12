package com.aizi.yingerbao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
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

import com.aizi.yingerbao.bluttooth.BluetoothApi;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.device.fragment.DeviceConnectStatusFragment;
import com.aizi.yingerbao.device.fragment.DeviceConnectStatusFragment.ConnectDeviceState;
import com.aizi.yingerbao.device.fragment.DeviceConnectStatusFragment.OnDeviceConnectListener;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.service.ScanDevicesService;
import com.aizi.yingerbao.utility.PrivateParams;
import com.aizi.yingerbao.utility.Utiliy;
import com.aizi.yingerbao.view.HorizontalProgressBarWithNumber;
import com.aizi.yingerbao.view.TopBarView;
import com.aizi.yingerbao.view.TopBarView.onTitleBarClickListener;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

public class ConnectDeviceActivity extends Activity implements OnDeviceConnectListener,
onTitleBarClickListener {
    
    private static final String TAG = ConnectDeviceActivity.class.getSimpleName();
    
    private ScanDevicesService mScanService;
    
    int mTotalSyncDataLen = 0;
    int mIncrementDataLen = 0;
    int mCurSyncDataLen = 0; // 百分比

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
        mConnectTopBarView = (TopBarView) findViewById(R.id.hometopbar);
        mConnectTopBarView.setClickListener(this);
        mDevConnectFragment 
            = (DeviceConnectStatusFragment)getFragmentManager().findFragmentById(R.id.deviceConnectFragment);
        
        //initScanService();
        PrivateParams.setSPInt(getApplicationContext(), "connect_interrupt", 0);
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {   
            if (mDevConnectFragment != null) {
                if (mDevConnectFragment.getCurrentState() == ConnectDeviceState.SEARCHING_DEVICE
                        || mDevConnectFragment.getCurrentState() == ConnectDeviceState.CHECKING_DEVICE
                        || mDevConnectFragment.getCurrentState() == ConnectDeviceState.SYNCING_DATA) {
                    
                    if (mDevConnectFragment.getCurrentState() == ConnectDeviceState.SEARCHING_DEVICE) {
                        showConnectQuitDialog(this, "连接退出", "设备正在连接，请勿退出！", ConnectDeviceState.SEARCHING_DEVICE);
                    } else if (mDevConnectFragment.getCurrentState() == ConnectDeviceState.CHECKING_DEVICE) {
                        showConnectQuitDialog(this, "设备校验退出", "设备正在校验，请勿退出！", ConnectDeviceState.SEARCHING_DEVICE);
                    } else if (mDevConnectFragment.getCurrentState() == ConnectDeviceState.SYNCING_DATA) {
                        showConnectQuitDialog(this, "数据同步退出", "数据正在同步，请勿退出！", ConnectDeviceState.SEARCHING_DEVICE);
                    }
                } 
            }
        }      
        return super.onKeyDown(keyCode, event);
    }
    
    private void initScanService(){
        try {
            Intent bindscanIntent = new Intent(getApplicationContext(), ScanDevicesService.class);
            boolean isBind = bindService(bindscanIntent, mScanServiceConnection, Context.BIND_AUTO_CREATE);
            if (isBind) {
                SLog.e(TAG, "mScanService = true " + mScanService );
            } else {
                SLog.e(TAG, "mScanService is false");
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
                if (mDevConnectFragment.getCurrentState() == ConnectDeviceState.IDEL
                        || mDevConnectFragment.getCurrentState() == ConnectDeviceState.FAIL
                        || mDevConnectFragment.getCurrentState() == ConnectDeviceState.SEARCHING_DEVICE) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mScanService.startScanDevice(); 
                }
            } 
            SLog.e(TAG, "onServiceConnected mScanService = " + mScanService 
                    + " currentState = " + mDevConnectFragment.getCurrentState());
        }
    };
    
 
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            int progress = 0;
            if (msg.what == MSG_PROGRESS_UPDATE) {
                progress = mProgressBar.getProgress();
                if (progress <= mCurSyncDataLen) {
                    mProgressBar.setVisibility(View.VISIBLE); 
                    mProgressBar.setProgress(++progress);
                    mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_UPDATE, 50);
                } else {
                    mHandler.removeMessages(MSG_PROGRESS_UPDATE);
                }
            } else if (msg.what == MSG_PROGRESS_COMPLETED) {
                mCurSyncDataLen = 0;
                int res = msg.arg1;
                mHandler.sendEmptyMessage(MSG_PROGRESS_AUTO_COMPLETED);
            } else if (msg.what == MSG_PROGRESS_AUTO_COMPLETED) {
                progress = mProgressBar.getProgress();
                if (progress < 100) {
                    mProgressBar.setProgress(++progress);
                    mHandler.sendEmptyMessageDelayed(MSG_PROGRESS_AUTO_COMPLETED, 50);
                } else {
                    mHandler.removeMessages(MSG_PROGRESS_AUTO_COMPLETED);
                    if (mDevConnectFragment != null) {
                        mDevConnectFragment.setSyncDataSucceed();
                        mDevConnectFragment.doUpdateStatusClick();
                        SLog.e(TAG, "doUpdateStatusClick setSyncDataSucceed "
                                    + mDevConnectFragment.getCurrentState());
                        PrivateParams.setSPLong(getApplicationContext(),
                                Constant.SYNC_DATA_SUCCEED_TIMESTAMP, System.currentTimeMillis());
                    } 
                    Utiliy.sendSavedBreathData(getApplicationContext());
                    Utiliy.sendSavedTempData(getApplicationContext());
                    SLog.e(TAG, "MSG_PROGRESS_AUTO_COMPLETED");
                    finish();
                }
            }
        };
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTotalSyncDataLen = 0;
        //unbindService(mScanServiceConnection);
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
            if (mScanService != null) {
                SLog.e(TAG, "start scan bluetooth");  
                mScanService.startScanDevice();
            } else {
                SLog.e(TAG, "init scan service");
                initScanService();
            }
        } else if (action.equals("com.aizi.yingerbao.sync_finish")) {
            finish();
        } else if (action.equals("com.aizi.yingerbao.checkdevice")) {
            mTotalSyncDataLen = 0;
        } else if (action.equals("com.aizi.yingerbao.sync_data")) {
            boolean isShowProgress = false;
            if (intent.hasExtra("show_progress")) {
                isShowProgress = intent.getBooleanExtra("show_progress", false);
                if (mProgressBar != null) {
                    if (isShowProgress) {
                        mProgressBar.setProgress(0);
                    } else {
                        mProgressBar.setVisibility(View.GONE); 
                    }
                     
                }
            }
        } else if (action.equals("com.aizi.yingerbao.sync_data_fail")) {
            Message message = new Message(); 
            message.what = MSG_PROGRESS_COMPLETED;
            mHandler.sendMessage(message); 
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
                    SLog.e(TAG, "RECV  SYNC DATA  mCurSyncDataRate " + mCurSyncDataLen);
                }
            }
        } else if (action.equals(Constant.ACTION_TOTAL_DATA_LEN)) {
            if (intent.hasExtra(Constant.NOT_SYNC_DATA_LEN)) {
                mTotalSyncDataLen = intent.getIntExtra(Constant.NOT_SYNC_DATA_LEN, 0);
                //SLog.e(TAG, "RECV SYNC DATA  mTotalSyncDataLen real = " + mTotalSyncDataLen);
                if (mTotalSyncDataLen <= 100) { // 因为该数值不准确，加上一个值比实际值大
                    mTotalSyncDataLen += 10;
                } else if (mTotalSyncDataLen > 100 && mTotalSyncDataLen <= 200) {
                    mTotalSyncDataLen += 30;
                } else if (mTotalSyncDataLen > 200 && mTotalSyncDataLen <= 500) {
                    mTotalSyncDataLen += 50;
                } else if (mTotalSyncDataLen > 500 && mTotalSyncDataLen <= 1000) {
                    mTotalSyncDataLen += 100; 
                } else if (mTotalSyncDataLen >= 1000) {
                    mTotalSyncDataLen += 300;
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
    
    /** 
     * @param state 
     * @Description: 显示退出对话框
     * @Context
     */
    
    public void showConnectQuitDialog(final Context context, String title, String content, final ConnectDeviceState state){
        
        final AlertDialog.Builder normalDialog = 
            new AlertDialog.Builder(context);
        normalDialog.setIcon(R.drawable.yingerbao_96);
        normalDialog.setTitle(title);
        normalDialog.setMessage(content);
        normalDialog.setPositiveButton("退出", 
            new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Constant.AIZI_USERACTIVTY_QUIT = 1;
                mDevConnectFragment.setCurrentStateIdel();
                PrivateParams.setSPInt(getApplicationContext(), "connect_interrupt", 1);
                BluetoothApi.getInstance(getApplicationContext()).mBluetoothService.disconnect(false);
                finish();
            }
        });
        normalDialog.setNegativeButton("继续", 
            new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //...To-do
            }
        });
        // 显示
        normalDialog.show();
    }
}
