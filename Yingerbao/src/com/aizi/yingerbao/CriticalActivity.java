package com.aizi.yingerbao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.aizi.yingerbao.R;
import com.aizi.yingerbao.deviceinterface.AsyncDeviceFactory;
import com.aizi.yingerbao.utility.Utiliy;

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
        
        mFever.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(intent);
            }
        });
        
       /* mBreathAbnormal.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Utiliy.showBreathNotification(getApplicationContext(), 
                        "孩子呼吸停滞！！", "孩子呼吸停滞，请及时处理。", null);
            }
        });*/
        
    }
}
