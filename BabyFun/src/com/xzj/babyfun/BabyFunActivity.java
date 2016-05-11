package com.xzj.babyfun;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.xzj.babyfun.chart.BarChartFragment;
import com.xzj.babyfun.chart.SleepyChart;
import com.xzj.babyfun.crc.CRC16;
import com.xzj.babyfun.eventbus.AsycEvent;
import com.xzj.babyfun.logging.SLog;
import com.xzj.babyfun.receiver.BabyStatusReceiver;
import com.xzj.babyfun.receiver.BabyStatusReceiver.DataInteraction;
import com.xzj.babyfun.service.ScanBlueToothService;
import com.xzj.babyfun.service.ScanBlueToothService.OnScanDeviceListener;
import com.xzj.babyfun.service.UartService;
import com.xzj.babyfun.ui.component.main.BabyStatusIndicateFragment;
import com.xzj.babyfun.ui.component.main.HomePageTopTitleFragment.OnButtonClickedListener;
import com.xzj.babyfun.ui.component.main.RealTimeStatusFragment;
import com.xzj.babyfun.ui.component.main.RealTimeStatusFragment.OnStatusSelectedListener;
import com.xzj.babyfun.ui.component.main.RouterStatusFragment;
import com.xzj.babyfun.ui.component.main.RouterStatusFragment.OnItemSelectedListener;

import de.greenrobot.event.EventBus;

public class BabyFunActivity extends Activity implements OnItemSelectedListener, OnStatusSelectedListener, OnButtonClickedListener, DataInteraction {
    
    private static final String TAG = BabyFunActivity.class.getSimpleName();
    
    BluetoothAdapter mAdapter;
    Button mSearchBtnButton;
    Fragment mContent;
    private FragmentManager mFragmentMan;
    RouterStatusFragment routerfragment;
    SleepyChart chartFragment;
    BarChartFragment barChartFragment;
    RealTimeStatusFragment realTimeStatusFragment;
    BabyStatusIndicateFragment babyStatusIndicateFragment;
    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_SELECT_DEVICE = 1;
    private BluetoothDevice mDevice = null;
    private UartService mService = null;
    private ScanBlueToothService mScanService = null;
    static int tempValue = 0;
    static int humitValue = 0;
    static int pm25Value = 0;
    static int sleepValue = 0;
    static int freshtimes = 0;
    boolean mTemHide = false;
    boolean mSleepHide = false;

    LineChart[] mCharts = new LineChart[4]; // 4条数据  
    static Typeface mTf; // 自定义显示字体  
    static int[] mColors = new int[] { Color.rgb(137, 230, 81), Color.rgb(240, 240, 30),//  
            Color.rgb(89, 199, 250), Color.rgb(250, 104, 104), Color.rgb(4, 158, 255) }; // 自定义颜色 
    
    static ArrayList<Entry> yVals = new ArrayList<Entry>(); 
    SlidingMenu menu;
    
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
        
        byte p[] = {0,1,2,3,4,5,6,7,8,9,10,11,12,13};
        int crc16 = CRC16.calcCrc16(p);
        SLog.e(TAG, "crc16 = " + crc16);
        
        //注册EventBus  
       EventBus.getDefault().register(this);
        
        initUartService();
        initScanService();
        //registerReceiver(UARTStatusChangeReceiver, makeGattUpdateIntentFilter());

        BabyStatusReceiver babyStatusReceiver = new BabyStatusReceiver();
        babyStatusReceiver.setBRInteractionListener(this);
        
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);  
       // 设置触摸屏幕的模式  
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);  
        menu.setShadowWidthRes(R.dimen.shadow_width);  
        menu.setShadowDrawable(R.drawable.abs__ab_bottom_solid_light_holo);  
       // 设置滑动菜单视图的宽度  
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);  
       // 设置渐入渐出效果的值  
        menu.setFadeDegree(0.35f);  
      //* 
      //   SLIDING_WINDOW will include the Title/ActionBar in the content 
       //  section of the SlidingMenu, while SLIDING_CONTENT does not. 
        //  
       menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);  
       //为侧滑菜单设置布局  
       menu.setMenu(R.layout.slidingmenu);  
        
        mFragmentMan = getFragmentManager();
        routerfragment = (RouterStatusFragment) mFragmentMan.findFragmentById(R.id.routerStatusFragment);
        realTimeStatusFragment = (RealTimeStatusFragment) mFragmentMan.findFragmentById(R.id.realtimestatuFragment);
        
        babyStatusIndicateFragment = (BabyStatusIndicateFragment) mFragmentMan.findFragmentById(R.id.babyStatusIndicateFragment);
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
    }
    
    public void showTempfragment() {
        FragmentTransaction transaction = mFragmentMan.beginTransaction();
        transaction.show(routerfragment).hide(barChartFragment).commit();
        mTemHide = false;
    }
    
    public void hideTempfragment() {
   
        FragmentTransaction transaction = mFragmentMan.beginTransaction();
        transaction.hide(routerfragment).show(barChartFragment).commit(); // 隐藏当前的fragment，显示下一个
        mTemHide = true;
       }
    
    public void showslidingmenu() {
        menu.showMenu();
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
               
                Log.e(TAG, "... onActivityResultdevice.address==" + mDevice + "deviceaddress "+ deviceAddress +" myserviceValue = " + mService);
         
                mService.connect(deviceAddress);
                            

            }
            break;
        default:
            Log.e(TAG, "wrong request code"+ requestCode);
            break;
        }
    }
    
    public void startdevlistactivity() {
        Intent newIntent = new Intent(this, DeviceListActivity.class);
        startActivityForResult(newIntent, REQUEST_SELECT_DEVICE);
    }
    
    BroadcastReceiver mReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                
                if (device.getBondState() != BluetoothDevice.BOND_BONDED){
                    Toast.makeText(getApplicationContext(), "find device:" + device.getName()
                            + device.getAddress(), Toast.LENGTH_SHORT).show();
    
                }
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setTitle("鎼滅储瀹屾垚");
           
            }
        }
        
    };
    

    
    //UART service connected/disconnected
    private ServiceConnection mUartServiceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder rawBinder) {
                mService = ((UartService.LocalBinder) rawBinder).getService();
                Log.e(TAG, "onServiceConnected mService= " + mService);
                if (!mService.initialize()) {
                    Log.e(TAG, "Unable to initialize Bluetooth");
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
            Log.e(TAG, "mScanService = 1" + mScanService);
        }
        
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // TODO Auto-generated method stub
            mScanService = ((ScanBlueToothService.ScanBinder) service).getService();
            mScanService.setOnProgressListener(new OnScanDeviceListener() {
                
                @Override
                public void OnScanDeviceSucceed(int touchid) {
                    // TODO Auto-generated method stub
                    if (touchid == 9) {
                        routerfragment.setCurrentStateSucceed();
                        routerfragment.doUpdateStatusClick();
                        Log.e(TAG, "mScanService = 444444444444" + mScanService);
                        //deviceList = new ArrayList<BluetoothDevice>();
                        List<BluetoothDevice> devicelist = new ArrayList<BluetoothDevice>();
                        devicelist = mScanService.getDeviceList();
                        for (BluetoothDevice listDev : devicelist) {
                            Log.e(TAG, "LISTNAME = " + listDev.getName());
                            if (listDev.getName() != null) {
                                if (listDev.getName().equals("my_hrm")) {
                                    
                                    String deviceAddress = listDev.getAddress();
                                    mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
                                   
                                    Log.e(TAG, "... onActivityResultdevice.address==" + mDevice + "deviceaddress "+ deviceAddress +" myserviceValue = " + mService);
                                    mService.connect(deviceAddress);
                                    
                                    break;
                                }
                            }
                           
                        }
                        //mScanService.startScanList();
                    } else if (touchid == 10) {
                        routerfragment.setCurrentStateFailed();
                        routerfragment.doUpdateStatusClick();
                    }
                }
            });
            Log.e(TAG, "mScanService = 2" + mScanService);
        }
    };
    
 
    private void initScanService(){
        Intent bindIntent = new Intent(this, ScanBlueToothService.class);
        boolean isbind = bindService(bindIntent, mScanServiceConnection, Context.BIND_AUTO_CREATE);
        Log.e(TAG, "initScanService  " + isbind);
        
    }
    
    
    
    private void initUartService() {
        Intent bindIntent = new Intent(this, UartService.class);
        boolean isbind = bindService(bindIntent, mUartServiceConnection, Context.BIND_AUTO_CREATE);
        Log.e(TAG, "service_init  " + isbind);
     }
    
    private void search() { //寮�鍚摑鐗欏拰璁剧疆璁惧鍙鏃堕棿
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            adapter.enable();
        }
        Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        enable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600); //3600涓鸿摑鐗欒澶囧彲瑙佹椂闂�
        startActivity(enable);
       /* Intent searchIntent = new Intent(this, ComminuteActivity.class);
        startActivity(searchIntent);*/
    }
    
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UartService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(UartService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(UartService.ACTION_DATA_AVAILABLE);
        //intentFilter.addAction(UartService.DEVICE_DOES_NOT_SUPPORT_UART);
        return intentFilter;
    }
    
    private final BroadcastReceiver UARTStatusChangeReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e(TAG, "UART_CONNECT_MSG   getAction"  + action);
            final Intent mIntent = intent;
           //*********************//
            if (action.equals(UartService.ACTION_GATT_CONNECTED)) {
                 runOnUiThread(new Runnable() {
                     public void run() {
                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                             Log.e(TAG, "UART_CONNECT_MSG1");
                         //    btnConnectDisconnect.setText("Disconnect");
                             Log.e(TAG, "UART_CONNECT_MSG2");
                           //  edtMessage.setEnabled(true);
                             Log.e(TAG, "UART_CONNECT_MSG3");
                            // btnSend.setEnabled(true);
                             Log.e(TAG, "UART_CONNECT_MSG4");
                             Log.e(TAG, "UART_CONNECT_MSG5");
                     }
                 });
            }
           
          //*********************//
            if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
                 runOnUiThread(new Runnable() {
                     public void run() {
                             String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                             Log.d(TAG, "UART_DISCONNECT_MSG");
                         
                     }
                 });
            }
            
          
          //*********************//
            if (action.equals(UartService.ACTION_GATT_SERVICES_DISCOVERED)) {
                Log.e(TAG, "ACTION_GATT_SERVICES_DISCOVERED 1");
                // mService.enableTXNotification(); 
            }
          //*********************//
            if (action.equals(UartService.ACTION_DATA_AVAILABLE)) {

                 // txValue = intent.getIntExtra(UartService.EXTRA_DATA, 0);
              /*   runOnUiThread(new Runnable() {
                     public void run() {
                         try {
                            //String text = new String(txValue, "UTF-8");
                            Log.e(TAG, "VALUE = " + txValue);
                            freshtimes++;
                           // yVals.add(new Entry(txValue, freshtimes));  
                            
                           
            
                             LineData data = chartFragment.getData(txValue);
                             chartFragment.setupChart(data, mColors[4]);
                            
                            BarData data = barChartFragment.getData(txValue);
                            barChartFragment.setupChart(data, mColors[4]);
                           //  hidefragment();
                            //LineData data = getData(txValue); 
                            
                            //setupChart(chart, data, mColors[0]);  
                            String currentDateTimeString = DateFormat.getTimeInstance().format(new Date());
                              //  listAdapter.add("["+currentDateTimeString+"] RX: "+txValue);
                              //  messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
                                
                         } catch (Exception e) {
                             Log.e(TAG, e.toString());
                         }
                     }
                 });*/
             }
                  
        }
    };
    

    @Override
    public void onItemSelected(Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        if (action.equals("com.babyfun.scandevices")) {
            mScanService.startScanList();
        }else {
            Log.e(TAG, "onItemSelected " + intent.toURI());
            String deviceAddress = intent.getStringExtra(BluetoothDevice.EXTRA_DEVICE);
            mDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress);
           
            Log.e(TAG, "... onActivityResultdevice.address==" + mDevice + "deviceaddress "+ deviceAddress +" myserviceValue = " + mService);
           // ((TextView) findViewById(R.id.deviceName)).setText(mDevice.getName()+ " - connecting");
            mService.connect(deviceAddress);
        } 
    }

    @Override
    public void onStatusSelected(int touchid) {
        // TODO Auto-generated method stub
        if (touchid == 1) {
            if (mTemHide) {
                showTempfragment();
            }else {
                hideTempfragment();
            }
        }else if (touchid == 2) {
            
        }
    }

    @Override
    public void OnButtonClicked(int touchid) {
        // TODO Auto-generated method stub
        if (touchid == 2) {
            Log.e(TAG, "touchid = " + touchid);
            menu.showMenu();
      
        }
    }

    @Override
    public void startNotification(Intent intent) {
        // TODO Auto-generated method stub
        Log.e(TAG, "ACTION_GATT_SERVICES_DISCOVERED 1");
       // mService.enableSleepNotification();
      //  mService.enableTemperatureNotification();
          mService.enableDataNotification();
        
     /*   new Handler().postDelayed(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                mService.enableHumidityNotification();
                Log.e(TAG, "ACTION_GATT_SERVICES_DISCOVERED 2");
            }
        }, 1000);
       
        new Handler().postDelayed(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                
                mService.enableSleepNotification();
                Log.e(TAG, "ACTION_GATT_SERVICES_DISCOVERED 3");
            }
        }, 3000);*/
    }

    @Override
    public void setData(Intent intent) {
        // TODO Auto-generated method stub
        String action = intent.getAction();
        if (action.equals(UartService.ACTION_GATT_DISCONNECTED)) {
            Log.e(TAG, "action disconnected ***********");
            Log.e(TAG, "UartService disconnect 3");
            routerfragment.setCurrentStateFailed();
            routerfragment.doUpdateStatusClick();
         }
     int dataType = intent.getIntExtra(UartService.EXTRA_TYPE, 0);
     if (dataType == UartService.DATA_TYPE_TEMP_HUMIT) {
            tempValue = intent.getIntExtra(UartService.EXTRA_DATA_TEMP, 0);
            humitValue = intent.getIntExtra(UartService.EXTRA_DATA_HUMIT, 0);
      } else if (dataType == UartService.DATA_TYPE_PM25) {
          pm25Value = intent.getIntExtra(UartService.EXTRA_DATA_PM25, 0);
      } else if (dataType == UartService.DATA_TYPE_SLEEP) {
          sleepValue = intent.getIntExtra(UartService.EXTRA_DATA_SLEEP, 0);
      }
        
        realTimeStatusFragment.setTemperature(tempValue);
        realTimeStatusFragment.setHumit(humitValue);
        //realTimeStatusFragment.setPM25(sleepValue);    
        
        Bundle bundle = new Bundle();
        bundle.putInt("temp", tempValue);
        bundle.putInt("humit", humitValue);
    }
    
    
    public static int getTempValue() {
        return tempValue;
        
    }
    
    public void onEventMainThread(AsycEvent event) {  
        
       /*// String msg = "onEventMainThread收到了消息：" + event.getMsg();  
        Log.d("harvic", msg);  
       // tv.setText(msg);  */
        Toast.makeText(this, "enventbus write bytes", Toast.LENGTH_SHORT).show(); 
        mService.writeBaseRXCharacteristic(event.getByte());
    } 
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mService.disconnect();
        EventBus.getDefault().unregister(this);//反注册EventBus  
    }
    
}
