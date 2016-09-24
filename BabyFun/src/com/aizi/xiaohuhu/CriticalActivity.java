package com.aizi.xiaohuhu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.aizi.xiaohuhu.deviceinterface.AsyncDeviceFactory;
import com.aizi.xiaohuhu.utility.Utiliy;

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
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(intent);
            }
        });
        
        mGetAllData = (Button) findViewById(R.id.getallsyncdata);
        mGetAllData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).getAllSyncInfo();
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(intent);
            }
        });
        
        mStartBreathData = (Button) findViewById(R.id.startbreathdata);
        mStartBreathData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).startSendBreathData();
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(intent);
            }
        });
        
        mStopBreathData = (Button) findViewById(R.id.stopbreathdata);
        mStopBreathData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).stopSendBreathData();
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(intent);
            }
        });
        
        mGetBreathHistoryData = (Button) findViewById(R.id.getbreathhistorydata);
        mGetBreathHistoryData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).getBreathStopInfo();
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(intent);
            }
        });
        
        
        mGetRealTimeData = (Button) findViewById(R.id.gettempdata);
        mGetRealTimeData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).getBodyTemperature();
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(intent);
                
                //new SimpleCalendarDialogFragment().show(getFragmentManager(), "test-simple-calendar");
            }
        });
    }
}
