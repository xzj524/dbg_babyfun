package com.aizi.xiaohuhu;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.aizi.xiaohuhu.R;

public class BabyExplainActivity extends Activity {
    
    TextView mTitleTextView;
    TextView mContentTextView;
    Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_explain);
        
        mTitleTextView = (TextView) findViewById(R.id.babyexplaintitle);
        mContentTextView = (TextView) findViewById(R.id.babyexplaincontent);
        mButton = (Button) findViewById(R.id.userkonw);
        
        Intent intent = getIntent();
        if (intent != null) {
            String titleString = intent.getStringExtra("title");
            String contentString = intent.getStringExtra("content");
            
            mTitleTextView.setText(titleString);
            mContentTextView.setText(contentString);
        }
        
        mButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
    }
}
