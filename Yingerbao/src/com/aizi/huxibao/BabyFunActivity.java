package com.aizi.huxibao;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.DropBoxManager.Entry;
import android.os.IBinder;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.aizi.xiaohuhu.chart.BarChartFragment;
import com.aizi.xiaohuhu.chart.SleepyChart;
import com.aizi.xiaohuhu.deviceinterface.AsyncDeviceFactory;
import com.aizi.xiaohuhu.logging.SLog;
import com.aizi.xiaohuhu.service.BluetoothService;
import com.aizi.xiaohuhu.service.ScanDevicesService;
import com.aizi.xiaohuhu.ui.component.main.BabyStatusIndicateFragment;
import com.aizi.xiaohuhu.ui.component.main.DeviceConnectStatusFragment;
import com.aizi.xiaohuhu.ui.component.main.DeviceConnectStatusFragment.OnDeviceConnectListener;
import com.aizi.xiaohuhu.ui.component.main.RealTimeStatusFragment;
import com.aizi.xiaohuhu.utility.MessageParse;
import com.aizi.xiaohuhu.view.TopBarView;
import com.aizi.xiaohuhu.view.TopBarView.onTitleBarClickListener;
import com.aizi.yingerbao.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;
import com.umeng.analytics.MobclickAgent.UMAnalyticsConfig;

import de.greenrobot.event.EventBus;

public class BabyFunActivity extends Activity implements OnDeviceConnectListener,
onTitleBarClickListener {
    
    private static final String TAG = BabyFunActivity.class.getSimpleName();
    
    BluetoothAdapter mAdapter;
    Button mSearchBtnButton;
    Button mMessageBtn;
    Button mSettingBtn;
    Button mStopBluetoothService;
    Button mLogin;
    
    Button mGetDevicetimeButton;
    Button mSetDevicetimeButton;
    
    Button mBreathBtn;
    Fragment mContent;
    private FragmentManager mFragmentMan;
    DeviceConnectStatusFragment mDeviceConnectFragment;
    SleepyChart chartFragment;
    BarChartFragment barChartFragment;
    RealTimeStatusFragment realTimeStatusFragment;
    BabyStatusIndicateFragment babyStatusIndicateFragment;
    private static final int REQUEST_SELECT_DEVICE = 1;
    private BluetoothDevice mDevice = null;
    private BluetoothService mService = null;
    private ScanDevicesService mScanService = null;
    static int tempValue = 0;
    static int humitValue = 0;
    static int pm25Value = 0;
    static int sleepValue = 0;
    static int freshtimes = 0;
    boolean mTemHide = false;
    boolean mSleepHide = false;
    
    
    ViewGroup mMessageCenterViewGroup;
    ViewGroup mBabyBreathViewGroup;
    ViewGroup mBabySleepViewGroup;
    ViewGroup mSettingsViewGroup;
    ViewGroup mSyncDataViewGroup;

    TopBarView topBarView;

    static int[] mColors = new int[] { Color.rgb(137, 230, 81), Color.rgb(240, 240, 30),//  
            Color.rgb(89, 199, 250), Color.rgb(250, 104, 104), Color.rgb(4, 158, 255) }; // 自定义颜色 
    
    static ArrayList<Entry> yVals = new ArrayList<Entry>(); 
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_fun);
     
   /*     if (PrivateParams.getSPInt(getApplicationContext(), Constant.LOGIN_VALUE, 0) == 0) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }*/
        
        MobclickAgent.startWithConfigure(
                new UMAnalyticsConfig(getApplicationContext(), 
                "57ca68af67e58ebc68003313", "Umeng", 
                EScenarioType.E_UM_NORMAL));
     
        AsyncDeviceFactory.getInstance(getApplicationContext());
        MessageParse.getInstance(getApplicationContext());
        
       
        
        //注册EventBus  
      //  EventBus.getDefault().register(this);
        
        //initBluetoothService();
        initScanService();

        mFragmentMan = getFragmentManager();
        mDeviceConnectFragment = (DeviceConnectStatusFragment) mFragmentMan.findFragmentById(R.id.deviceConnectFragment);

        topBarView = (TopBarView) findViewById(R.id.hometopbar);
        topBarView.setClickListener(this);
        
        if (mScanService != null) {
            mScanService.startScanList();
            SLog.e(TAG, "mScanService  startScanList");
        }
        
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onResume(this);
    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPause(this);
    }
    
    public void switchContent(Fragment from, Fragment to) {
       
        if (mContent != to) {
            mContent = to;
            FragmentTransaction transaction = mFragmentMan.beginTransaction().setCustomAnimations(
                    android.R.anim.fade_in, R.anim.slide_out_to_bottom);
            transaction.hide(from).show(to).commit(); // 隐藏当前的fragment，显示下一个
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            Toast.makeText(getApplicationContext(), "resultcode = " + resultCode, Toast.LENGTH_SHORT).show();
        }
        
        switch (requestCode) {

            case REQUEST_SELECT_DEVICE:
                //When the DeviceListActivity return, with the selected device address
                if (resultCode == Activity.RESULT_OK && data != null) {
                    String deviceAddress = data.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
                    SLog.e(TAG, "... onActivityResultdevice.address==" 
                    + mDevice + "deviceaddress "+ deviceAddress +" myserviceValue = " + mService);       
                    mService.connect(deviceAddress);
                }
                break;
        default:
            SLog.e(TAG, "wrong request code"+ requestCode);
            break;
        }
    }
    
    public void startdevlistactivity() {
        Intent newIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
    }
    
    //UART service connected/disconnected
    private ServiceConnection mBluetoothServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
                mService = ((BluetoothService.LocalBinder) rawBinder).getService();
                SLog.e(TAG, "onServiceConnected mService= " + mService);
                if (!mService.initialize()) {
                    SLog.e(TAG, "Unable to initialize Bluetooth");
                    finish();
                }
        }
        public void onServiceDisconnected(ComponentName classname) {
                mService.disconnect();
                mService = null;
        }
    };
    
    private ServiceConnection mScanServiceConnection = new ServiceConnection() {
        
        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            mScanService = null;
        }
        
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            mScanService = ((ScanDevicesService.ScanBinder) service).getService();
            SLog.e(TAG, "onServiceConnected mScanService = " + mScanService);
            /*mScanService.setOnProgressListener(new OnScanDeviceListener() {
                
                @Override
                public void OnScanDeviceSucceed(int touchid) {
                    if (touchid == 1) {
                        List<BluetoothDevice> devicelist = new ArrayList<BluetoothDevice>();
                        devicelist = mScanService.getDeviceList();
                        for (BluetoothDevice listDev : devicelist) {
                            SLog.e(TAG, "LISTNAME = " + listDev.getName());
                            if (listDev.getName() != null) {
                                if (listDev.getName().equals("my_hrm")) {     
                                    String deviceAddress = listDev.getAddress();
                                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
                                   
                                    SLog.e(TAG, "... onActivityResultdevice.address==" + mDevice 
                                            + "deviceaddress "+ deviceAddress 
                                            +" myserviceValue = " + mService);
                                    mService.connect(deviceAddress);
                                    
                                    break;
                                }
                            }
                        }
                    } else if (touchid == 2) {
                        mDeviceConnectFragment.setCurrentStateFailed();
                        mDeviceConnectFragment.doUpdateStatusClick();
                    }
                }
            });*/
        }
    };
    
 
    private void initScanService(){
        Intent bindIntent = new Intent(this, ScanDevicesService.class);
        boolean isbind = bindService(bindIntent, mScanServiceConnection, Context.BIND_AUTO_CREATE);
        SLog.e(TAG, "initScanService  " + isbind + " mscanservice = " + mScanService);
    }
    
    
    
    private void initBluetoothService() {
        Intent bindIntent = new Intent(this, BluetoothService.class);
        boolean isbind = bindService(bindIntent, mBluetoothServiceConnection, Context.BIND_AUTO_CREATE);
        SLog.e(TAG, "service_init  " + isbind);
     }
    
    public static int getTempValue() {
        return tempValue;
    }
    
  /*  public void onEventMainThread(AsycEvent event) {  
        mService.writeBaseRXCharacteristic(event.getByte());
    } */
    
  
    
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        /*unbindService(mBluetoothServiceConnection);
        unbindService(mScanServiceConnection);
        mService.disconnect();*/
        EventBus.getDefault().unregister(this);//反注册EventBus  
    }

    @Override
    public void onBackClick() {
        /*mSlidingMenuHelper.showMenu();
        SLog.e(TAG, "SlidingMenuHelper  showing");*/
        finish();
    }

    @Override
    public void onRightClick() {
        // TODO Auto-generated method stub
    }

    @Override
    public void onDeviceConnected(Intent intent) {
        String action = intent.getAction();
        if (action.equals("com.babyfun.scandevices")) {
            if (mScanService != null) {
                mScanService.startScanList();
            } else {
                Intent bindIntent = new Intent(this, ScanDevicesService.class);
                if (bindService(bindIntent, mScanServiceConnection, Context.BIND_AUTO_CREATE)) {
                    if (mScanService != null) {
                        mScanService.startScanList();
                    }
                }
            }
        }
    }

    @Override
    public void onCalendarClick() {
        // TODO Auto-generated method stub
        
    }

}
