package com.aizi.yingerbao;

import java.util.ArrayList;
import java.util.List;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.aizi.yingerbao.DeviceListActivity.DeviceAdapter;
import com.aizi.yingerbao.chart.BarChartFragment;
import com.aizi.yingerbao.chart.SleepyChart;
import com.aizi.yingerbao.deviceinterface.AsyncDeviceFactory;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.service.BluetoothService;
import com.aizi.yingerbao.service.ScanDevicesService;
import com.aizi.yingerbao.ui.component.main.BabyStatusIndicateFragment;
import com.aizi.yingerbao.ui.component.main.DeviceConnectStatusFragment;
import com.aizi.yingerbao.ui.component.main.DeviceConnectStatusFragment.OnDeviceConnectListener;
import com.aizi.yingerbao.ui.component.main.RealTimeStatusFragment;
import com.aizi.yingerbao.utility.MessageParse;
import com.aizi.yingerbao.view.TopBarView;
import com.aizi.yingerbao.view.TopBarView.onTitleBarClickListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;
import com.umeng.analytics.MobclickAgent.UMAnalyticsConfig;

import de.greenrobot.event.EventBus;

public class ConnectDeviceActivity extends Activity implements OnDeviceConnectListener,
onTitleBarClickListener {
    
    private static final String TAG = ConnectDeviceActivity.class.getSimpleName();
    
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
    
    List<BluetoothDevice> mDeviceList;
    private DeviceAdapter mDeviceAdapter;

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

        initScanService();

        mFragmentMan = getFragmentManager();
        mDeviceConnectFragment = (DeviceConnectStatusFragment) mFragmentMan.findFragmentById(R.id.deviceConnectFragment);

        topBarView = (TopBarView) findViewById(R.id.hometopbar);
        topBarView.setClickListener(this);
        
        if (mScanService != null) {
            mScanService.startScanList();
            SLog.e(TAG, "mScanService  startScanList");
        }
        
        
/*        mDeviceList = new ArrayList<BluetoothDevice>();
        mDeviceAdapter = new DeviceAdapter(this, mDeviceList);
        
        ListView newDevicesListView = (ListView) findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mDeviceAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);*/
        
    }
    
 private OnItemClickListener mDeviceClickListener = new OnItemClickListener() {
        
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            BluetoothDevice device = mDeviceList.get(position);
            // mBluetoothAdapter.stopLeScan(mLeScanCallback);
            // 停止扫描设备
  
/*            Bundle b = new Bundle();
            b.putString(BluetoothDevice.EXTRA_DEVICE, mDeviceList.get(position).getAddress());

            Intent result = new Intent();
            result.putExtras(b);
            setResult(Activity.RESULT_OK, result);
            finish();*/
            
        }
    };
    
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
    
    //Bluetooth service connected/disconnected
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
        }
    };
    
 
    private void initScanService(){
        Intent bindscanIntent = new Intent(this, ScanDevicesService.class);
        bindService(bindscanIntent, mScanServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void initBluetoothService() {
        Intent bindIntent = new Intent(this, BluetoothService.class);
        boolean isbind = bindService(bindIntent, mBluetoothServiceConnection, Context.BIND_AUTO_CREATE);
        SLog.e(TAG, "service_init  " + isbind);
     }
    
    public static int getTempValue() {
        return tempValue;
    }
    
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        EventBus.getDefault().unregister(this);//反注册EventBus  
    }

    @Override
    public void onBackClick() {
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
    
    
    
    class DeviceAdapter extends BaseAdapter {
        Context context;
        List<BluetoothDevice> devices;
        LayoutInflater inflater;

        public DeviceAdapter(Context context, List<BluetoothDevice> devices) {
            this.context = context;
            inflater = LayoutInflater.from(context);
            this.devices = devices;
        }

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return devices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewGroup vg;

            if (convertView != null) {
                vg = (ViewGroup) convertView;
            } else {
                vg = (ViewGroup) inflater.inflate(R.layout.device_element, null);
            }

            BluetoothDevice device = devices.get(position);
            final TextView tvname = ((TextView) vg.findViewById(R.id.name));

            tvname.setText(device.getName());
            if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                SLog.e(TAG, "device::"+device.getName());
                tvname.setTextColor(Color.WHITE);
            } else {
                tvname.setTextColor(Color.GRAY);
            }
            return vg;
        }
    }

}
