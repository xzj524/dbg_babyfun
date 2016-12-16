package com.aizi.yingerbao;

import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.utility.MediaUtil;
import com.aizi.yingerbao.utility.Utiliy;
import com.aizi.yingerbao.utility.VibratorUtil;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BabyFeverEmergencyActivity extends Activity {
    
    TextView mBabyTempValue;
    Button mButtonUserKown;
    Button mButtonUserCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_emergency);
        
        mBabyTempValue = (TextView) findViewById(R.id.currenttempvalue);
        mButtonUserKown = (Button) findViewById(R.id.userkonw);
        mButtonUserCall = (Button) findViewById(R.id.usercall);
        
        Intent intent = getIntent();
        if (intent != null) {
            String babytempvalue = intent.getStringExtra("custom_content");
            mBabyTempValue.setText(babytempvalue);
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
        
        VibratorUtil.Vibrate(getApplicationContext(), Constant.EMERGENCY_PATTERN, true);
        MediaUtil.getInstance(getApplicationContext()).startAlarm();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utiliy.cancelAlarmNotify(getApplicationContext());
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
}
