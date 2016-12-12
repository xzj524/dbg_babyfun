package com.aizi.yingerbao;

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
            String titleString = intent.getStringExtra("title");
            String babytempvalue = intent.getStringExtra("custom_content");
            mBabyTempValue.setText(babytempvalue);
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
