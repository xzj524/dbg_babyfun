package com.aizi.yingerbao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

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
        
        Intent intent = getIntent();
        if (intent != null) {
            String breathcustom = intent.getStringExtra("content");
            //mBabyBreathTextView.setText(breathcustom);
        }
        
        mButtonUserKown.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        mButtonUserCall.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EmergencyPhoneNumberActivity.class);
                startActivity(intent);
            }
        });
    }
}
