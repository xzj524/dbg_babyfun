package com.aizi.yingerbao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import cn.qqtheme.framework.picker.NumberPicker;

import com.aizi.yingerbao.baseheader.BaseL2Message;
import com.aizi.yingerbao.baseheader.KeyPayload;
import com.aizi.yingerbao.command.CommandSendRequest;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.deviceinterface.DeviceFactory;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.utility.PrivateParams;
import com.aizi.yingerbao.utility.Utiliy;

import de.greenrobot.event.EventBus;

public class TestActivity extends Activity {
    
    Button mTestButton;
    private ListView messageListView;
    
    private static final String TAG = TestActivity.class.getSimpleName();
    private ArrayAdapter<String> listAdapter;
    
    Button mGetNoSyncData;
    Button mGetAllData;
    Button mStartBreathData;
    Button mStopBreathData;
    Button mGetBreathHistoryData;
    Button mGetRealTimeData;
    Button mGetClearLog;
    Button mGetExceptionEvent;
    Button mGetBreathStopEvent;
    Button mGetTemperatureEvent;
    Button mGetDeviceTime;
    Button mActiviteDev;
    Button mCheckDev;
    Button mFixDevtime;
    Button mUpdateRom;
    Button mShowDeviceCharge;
    
    TextView mDevicechargetTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        
        messageListView = (ListView) findViewById(R.id.listMessage);
        listAdapter = new ArrayAdapter<String>(this, R.layout.message_detail);
        messageListView.setAdapter(listAdapter);
        messageListView.setDivider(null);
        
        mDevicechargetTextView = (TextView) findViewById(R.id.text_device_charge);
        mDevicechargetTextView.setText("- -");
        
        EventBus.getDefault().register(this);
        
        mGetClearLog = (Button) findViewById(R.id.btn_clearlog);
        mGetClearLog.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
             listAdapter.clear();

             WindowManager mWm = (WindowManager) getApplicationContext()
                     .getSystemService(getApplicationContext().WINDOW_SERVICE);
             Display display = mWm.getDefaultDisplay();
             int width = display.getWidth();
             int height = display.getHeight();
             String resolution = height + "_" + width;
             SLog.e(TAG, "resolution = " + resolution);
            }
        });
        
        mGetNoSyncData = (Button) findViewById(R.id.btn_getnosyncdata);
        mGetNoSyncData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DeviceFactory.getInstance(getApplicationContext()).getAllNoSyncInfo(2);
              
            }
        });
        
        mGetAllData = (Button) findViewById(R.id.btn_getalldata);
        mGetAllData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DeviceFactory.getInstance(getApplicationContext()).getAllSyncInfo();
                
            }
        });
        
        mStartBreathData = (Button) findViewById(R.id.btn_startbreath);
        mStartBreathData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DeviceFactory.getInstance(getApplicationContext()).startSendBreathData();
                
            }
        });
        
        mStopBreathData = (Button) findViewById(R.id.btn_stopbreath);
        mStopBreathData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DeviceFactory.getInstance(getApplicationContext()).stopSendBreathData();
               
            }
        });
        
        mGetBreathHistoryData = (Button) findViewById(R.id.btn_gethistorybreath);
        mGetBreathHistoryData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DeviceFactory.getInstance(getApplicationContext()).getBreathStopInfo();
              
            }
        });
        
        
        mGetRealTimeData = (Button) findViewById(R.id.btn_getrealtimedata);
        mGetRealTimeData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DeviceFactory.getInstance(getApplicationContext()).getRealTimeData();
            }
        });
        
        mGetExceptionEvent = (Button) findViewById(R.id.btn_getexceptionevent);
        mGetExceptionEvent.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DeviceFactory.getInstance(getApplicationContext()).getExceptionEvent();
            }
        });
        
        
        mGetBreathStopEvent = (Button) findViewById(R.id.btn_getbreathstopevent);
        mGetBreathStopEvent.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DeviceFactory.getInstance(getApplicationContext()).getBreathStopInfo();
            }
        });
        
        
        mGetTemperatureEvent = (Button) findViewById(R.id.btn_getdevicetemp);
        mGetTemperatureEvent.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DeviceFactory.getInstance(getApplicationContext()).getRealTimeTempData();
 
            }
        });
        
        
        mGetDeviceTime = (Button) findViewById(R.id.btn_getdevicetime);
        mGetDeviceTime.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DeviceFactory.getInstance(getApplicationContext()).getDeviceTime();
                Intent intent = new Intent();
                
                if (Utiliy.isBluetoothConnected(getApplicationContext())) {
                    SLog.e(TAG, "BlueTooth is ready");
                   
                } else {
                    SLog.e(TAG, "BlueTooth is not ready");
                }
            }
        });
        
        mActiviteDev = (Button) findViewById(R.id.btn_activitedev);
        mActiviteDev.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DeviceFactory.getInstance(getApplicationContext()).activateDevice();
              
            }
        });
        
        mCheckDev = (Button) findViewById(R.id.btn_checkdev);
        mCheckDev.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DeviceFactory.getInstance(getApplicationContext()).checkDeviceValid();
              
            }
        });
        
        mFixDevtime = (Button) findViewById(R.id.btn_setdevicetime);
        mFixDevtime.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (Utiliy.isBluetoothConnected(getApplicationContext())) {
                    DeviceFactory.getInstance(getApplicationContext()).setDeviceTime();
                }
            }
        });
        
        
        
        mUpdateRom = (Button) findViewById(R.id.btn_updaterom);
        mUpdateRom.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (Utiliy.isBluetoothConnected(getApplicationContext())) {
                    DeviceFactory.getInstance(getApplicationContext()).updateDeviceRom();
                }
            }
        });
        
        mShowDeviceCharge = (Button) findViewById(R.id.btn_showdevicecharge);
        mShowDeviceCharge.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                int charg=PrivateParams.getSPInt(getApplicationContext(), Constant.CUR_STATISTIC_CHARGE, 0);
                mDevicechargetTextView.setText("" + charg);
                
                NumberPicker picker = new NumberPicker(TestActivity.this);
                picker.setAnimationStyle(R.style.Animation_CustomPopup);
                picker.setOffset(1);//偏移量
                picker.setRange(30, 43, 0.5);//数字范围
                picker.setSelectedItem(37.5);
                picker.setLabel("℃");
                picker.setTitleText("报警温度值");
                picker.setOnNumberPickListener(new NumberPicker.OnNumberPickListener() {
                    @Override
                    public void onNumberPicked(int index, Number item) {
                        //showToast("index=" + index + ", item=" + item.doubleValue());
                        SLog.e(TAG, "index=" + index + ", item=" + item.doubleValue());
                    }
                });
                picker.show();
            }
        });
        
        
        
        
        
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    
    public void onEventMainThread(Intent event) {  
        
        String time = new SimpleDateFormat("yyyy-MM-dd ").format(new Date());
        Calendar calendar = Calendar.getInstance();
        String currentDateTimeString = "[" + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                + calendar.get(Calendar.MINUTE) + ":"
                + calendar.get(Calendar.SECOND) + ":"
                + calendar.get(Calendar.MILLISECOND)
                + "]: ";
        
        
        String transferdata = null;
        if (event.hasExtra("transferdata")) {
            transferdata = event.getStringExtra("transferdata");
        }
        
        String devtimeString = null;
        if (event.hasExtra("device_time")) {
            devtimeString = event.getStringExtra("device_time");
        }
        
        String action = event.getAction();
        String datalog = null;
        if (Constant.DATA_TRANSFER_RECEIVE.equals(action)) {
            SLog.e(TAG, "HEX Receive string l2load2 = " + transferdata);
            datalog = currentDateTimeString + " RECV: "+ transferdata;
        } else if (Constant.DATA_TRANSFER_SEND.equals(action)) {
            SLog.e(TAG, "HEX Send string l2load2 = " + transferdata);
            datalog = currentDateTimeString + " SEND: "+ transferdata;
        } else if (Constant.DATA_TRANSFER_TIME.equals(action)) {
            datalog = devtimeString;
        }
        
        if (!TextUtils.isEmpty(datalog)) {
            listAdapter.add(datalog);
            messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
        }  
    } 
    
    

}
