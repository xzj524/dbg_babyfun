package com.aizi.xiaohuhu;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.usage.UsageEvents.Event;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.aizi.xiaohuhu.R;
import com.aizi.xiaohuhu.R.string;
import com.aizi.xiaohuhu.constant.Constant;
import com.aizi.xiaohuhu.deviceinterface.AsyncDeviceFactory;
import com.aizi.xiaohuhu.eventbus.AsycEvent;
import com.aizi.xiaohuhu.logging.SLog;
import com.aizi.xiaohuhu.utility.MessageParse;

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
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).getAllNoSyncInfo();
              
            }
        });
        
        mGetAllData = (Button) findViewById(R.id.btn_getalldata);
        mGetAllData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).getAllSyncInfo();
                
            }
        });
        
        mStartBreathData = (Button) findViewById(R.id.btn_startbreath);
        mStartBreathData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).startSendBreathData();
                
            }
        });
        
        mStopBreathData = (Button) findViewById(R.id.btn_stopbreath);
        mStopBreathData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).stopSendBreathData();
               
            }
        });
        
        mGetBreathHistoryData = (Button) findViewById(R.id.btn_gethistorybreath);
        mGetBreathHistoryData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).getBreathStopInfo();
              
            }
        });
        
        
        mGetRealTimeData = (Button) findViewById(R.id.btn_getrealtimedata);
        mGetRealTimeData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).getBodyTemperature();
            }
        });
        
        
       
    }
    
    public void onEventMainThread(Intent event) {  
        
        Calendar calendar = Calendar.getInstance();
        String currentDateTimeString = "[" + calendar.get(Calendar.HOUR) + ":"
                + calendar.get(Calendar.MINUTE) + ":"
                + calendar.get(Calendar.SECOND) + ":"
                + calendar.get(Calendar.MILLISECOND)
                + "]: ";
        
        String transferdata = event.getStringExtra("transferdata");
        
        String action = event.getAction();
        if (Constant.DATA_TRANSFER_RECEIVE.equals(action)) {
            SLog.e(TAG, "HEX Receive string l2load2 = " + transferdata);
            listAdapter.add(currentDateTimeString + " RECV: "+ transferdata);
        } else if (Constant.DATA_TRANSFER_SEND.equals(action)) {
            SLog.e(TAG, "HEX Send string l2load2 = " + transferdata);
            // = DateFormat.getTimeInstance().format(new Date());
            listAdapter.add(currentDateTimeString + " SEND: "+ transferdata);
        }
        
        messageListView.smoothScrollToPosition(listAdapter.getCount() - 1);
    } 
    
}
