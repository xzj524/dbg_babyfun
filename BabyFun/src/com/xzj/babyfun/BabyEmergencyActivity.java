package com.xzj.babyfun;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class BabyEmergencyActivity extends Activity {
    
    TextView mTitleTextView;
    TextView mContentTextView;
    Button mButtonUserKown;
    Button mButtonUserCall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_emergency);
        
        mTitleTextView = (TextView) findViewById(R.id.babyemergencytitle);
        mContentTextView = (TextView) findViewById(R.id.babyemergencecontent);
        mButtonUserKown = (Button) findViewById(R.id.userkonw);
        mButtonUserCall = (Button) findViewById(R.id.usercall);
        
        Intent intent = getIntent();
        if (intent != null) {
            String titleString = intent.getStringExtra("title");
            String contentString = intent.getStringExtra("content");
            
            mTitleTextView.setText(titleString);
            mContentTextView.setText(contentString);
        }
        
        mButtonUserKown.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
        
        mButtonUserCall.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //dialPhoneNumber("18811130187");
                
                Intent intent = new Intent(getApplicationContext(), EmergencyPhoneNumberActivity.class);
                startActivity(intent);
            }
        });
    }
    
    
}
