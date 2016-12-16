package com.aizi.yingerbao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.utility.MediaUtil;
import com.aizi.yingerbao.utility.Utiliy;
import com.aizi.yingerbao.utility.VibratorUtil;
import com.umeng.analytics.MobclickAgent;

public class BabyBreathEmergencyActivity extends Activity {
    
    TextView mBabyBreathTextView;
    Button mButtonUserKown;
    Button mButtonUserCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_breath_emergency);
        
        mBabyBreathTextView = (TextView) findViewById(R.id.breathstoptext);
        mButtonUserKown = (Button) findViewById(R.id.breathuserkonw);
        mButtonUserCall = (Button) findViewById(R.id.breathusercall);
        
        VibratorUtil.Vibrate(getApplicationContext(), Constant.EMERGENCY_PATTERN, true);
        MediaUtil.getInstance(getApplicationContext()).startAlarm();
        
        Intent intent = getIntent();
        if (intent != null) {
            String breathcustom = intent.getStringExtra("content");
        }
        
        mButtonUserKown.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Utiliy.cancelAlarmNotify(getApplicationContext());
                finish();
            }

           
        });
        
        mButtonUserCall.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmergencyPhoneNumberActivity.class);
                startActivity(intent);
                Utiliy.cancelAlarmNotify(getApplicationContext());
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utiliy.cancelAlarmNotify(getApplicationContext());
    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPause(this);
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onResume(this);
    }
  
}
