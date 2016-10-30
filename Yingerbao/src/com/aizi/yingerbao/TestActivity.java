package com.aizi.yingerbao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.deviceinterface.AsyncDeviceFactory;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.utility.MessageParse;
import com.aizi.yingerbao.utility.Utiliy;

import de.greenrobot.event.EventBus;

public class TestActivity extends Activity {
    
    Button mTestButton;
    private ListView messageListView;
    
    private static final String TAG = MessageParse.class.getSimpleName();
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        
        messageListView = (ListView) findViewById(R.id.listMessage);
        listAdapter = new ArrayAdapter<String>(this, R.layout.message_detail);
        messageListView.setAdapter(listAdapter);
        messageListView.setDivider(null);
        
        EventBus.getDefault().register(this);
        
        mGetClearLog = (Button) findViewById(R.id.btn_clearlog);
        mGetClearLog.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
             listAdapter.clear();
              
            }
        });
        
        mGetNoSyncData = (Button) findViewById(R.id.btn_getnosyncdata);
        mGetNoSyncData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                AsyncDeviceFactory.getInstance(getApplicationContext()).getAllNoSyncInfo();
              
            }
        });
        
        mGetAllData = (Button) findViewById(R.id.btn_getalldata);
        mGetAllData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                AsyncDeviceFactory.getInstance(getApplicationContext()).getAllSyncInfo();
                
            }
        });
        
        mStartBreathData = (Button) findViewById(R.id.btn_startbreath);
        mStartBreathData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                AsyncDeviceFactory.getInstance(getApplicationContext()).startSendBreathData();
                
            }
        });
        
        mStopBreathData = (Button) findViewById(R.id.btn_stopbreath);
        mStopBreathData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                AsyncDeviceFactory.getInstance(getApplicationContext()).stopSendBreathData();
               
            }
        });
        
        mGetBreathHistoryData = (Button) findViewById(R.id.btn_gethistorybreath);
        mGetBreathHistoryData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                AsyncDeviceFactory.getInstance(getApplicationContext()).getBreathStopInfo();
              
            }
        });
        
        
        mGetRealTimeData = (Button) findViewById(R.id.btn_getrealtimedata);
        mGetRealTimeData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                AsyncDeviceFactory.getInstance(getApplicationContext()).getRealTimeData();
            }
        });
        
        mGetExceptionEvent = (Button) findViewById(R.id.btn_getexceptionevent);
        mGetExceptionEvent.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                AsyncDeviceFactory.getInstance(getApplicationContext()).getExceptionEvent();
            }
        });
        
        
        mGetBreathStopEvent = (Button) findViewById(R.id.btn_getbreathstopevent);
        mGetBreathStopEvent.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                AsyncDeviceFactory.getInstance(getApplicationContext()).getBreathStopInfo();
            }
        });
        
        
        mGetTemperatureEvent = (Button) findViewById(R.id.btn_getdevicetemp);
        mGetTemperatureEvent.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                AsyncDeviceFactory.getInstance(getApplicationContext()).getRealTimeTempData();
   /*             String tempinfo = "30";  
                Utiliy.temperatureToFile(tempinfo);
                
                String breathstop = "Breath Stop Info : " 
                        + "2016" + "-" 
                        + "10" + "-"
                        + "22" + "-"
                        + "21" + "-"
                        + "27" + "-"
                        + "10" 
                        + " BreathAlarm = " + true; 
                Utiliy.dataToFile(breathstop);*/
            }
        });
        
        
        mGetDeviceTime = (Button) findViewById(R.id.btn_getdevicetime);
        mGetDeviceTime.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                AsyncDeviceFactory.getInstance(getApplicationContext()).getDeviceTime();
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), UserActivity.class);
                startActivity(intent);
            }
        });
    }
    
    public void onEventMainThread(Intent event) {  
        
        String time = new SimpleDateFormat("yyyy-MM-dd ").format(new Date());
        Calendar calendar = Calendar.getInstance();
        String currentDateTimeString = "[" + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                + calendar.get(Calendar.MINUTE) + ":"
                + calendar.get(Calendar.SECOND) + ":"
                + calendar.get(Calendar.MILLISECOND)
                + "]: ";
        
        String transferdata = event.getStringExtra("transferdata");
        
        String action = event.getAction();
        String datalog = null;
        if (Constant.DATA_TRANSFER_RECEIVE.equals(action)) {
            SLog.e(TAG, "HEX Receive string l2load2 = " + transferdata);
            //listAdapter.add(currentDateTimeString + " RECV: "+ transferdata);
            datalog = currentDateTimeString + " RECV: "+ transferdata;
        } else if (Constant.DATA_TRANSFER_SEND.equals(action)) {
            SLog.e(TAG, "HEX Send string l2load2 = " + transferdata);
            // = DateFormat.getTimeInstance().format(new Date());
            //listAdapter.add(currentDateTimeString + " SEND: "+ transferdata);
            datalog = currentDateTimeString + " SEND: "+ transferdata;
        }
        
        listAdapter.add(datalog);

        messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
    } 
    
}
