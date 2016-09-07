package com.aizi.xiaohuhu;

import com.aizi.xiaohuhu.R;
import com.aizi.xiaohuhu.deviceinterface.AsyncDeviceFactory;
import com.aizi.xiaohuhu.utility.Utiliy;


import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class CriticalActivity extends Activity {
    
    Button mFever;
    Button mBreathAbnormal;
    
    Button mGetNoSyncData;
    Button mGetAllData;
    Button mStartBreathData;
    Button mStopBreathData;
    Button mGetBreathHistoryData;
    Button mGetRealTimeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_critical);
        
        mFever = (Button) findViewById(R.id.fever);
        mBreathAbnormal = (Button) findViewById(R.id.breathabnormal);
        
        mFever.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Utiliy.showFeverNotification(getApplicationContext(), 
                        "孩子发烧了！！", "孩子发烧了，请及时就医。", null);
            }
        });
        
        mBreathAbnormal.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Utiliy.showBreathNotification(getApplicationContext(), 
                        "孩子呼吸停滞！！", "孩子呼吸停滞，请及时处理。", null);
            }
        });
        
        mGetNoSyncData = (Button) findViewById(R.id.getnosyncdata);
        mGetNoSyncData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).getAllNoSyncInfo();
            }
        });
        
        mGetAllData = (Button) findViewById(R.id.getallsyncdata);
        mGetAllData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).getAllSyncInfo();
            }
        });
        
        mStartBreathData = (Button) findViewById(R.id.startbreathdata);
        mStartBreathData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).startSendBreathData();
            }
        });
        
        mStopBreathData = (Button) findViewById(R.id.stopbreathdata);
        mStopBreathData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).stopSendBreathData();
            }
        });
        
        mGetBreathHistoryData = (Button) findViewById(R.id.getbreathhistorydata);
        mGetBreathHistoryData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).getBreathStopInfo();
            }
        });
        
        
        mGetRealTimeData = (Button) findViewById(R.id.gettempdata);
        mGetRealTimeData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).getBodyTemperature();
            }
        });
    }
}
