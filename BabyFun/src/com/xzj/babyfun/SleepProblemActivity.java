package com.xzj.babyfun;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SleepProblemActivity extends Activity {
    
    Button mButton;
    
    TextView mTitleView;
    TextView mFactorView;
    TextView mExplainView;
    TextView mCommentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_problem);
        
        mTitleView = (TextView) findViewById(R.id.sleepproblemtitle);
        mFactorView = (TextView) findViewById(R.id.sleepfactorcontent);
        mExplainView = (TextView) findViewById(R.id.sleepexplaincontent);
        mCommentView = (TextView) findViewById(R.id.sleepsuggestcontent);
        
        Intent intent = getIntent();
        if (intent.hasExtra("problem_type")) {
            
            String titleString = intent.getStringExtra("title");
            String fatorString = intent.getStringExtra("fator");
            String explainString = intent.getStringExtra("explain");
            String commentString = intent.getStringExtra("comment");
            
            setTitleView(titleString);
            setFactorView(fatorString);
            setExplainView(explainString);
            setCommentView(commentString);
            
            short type = intent.getShortExtra("problem_type", (short) 0);
      
        }
         
        
        mButton = (Button) findViewById(R.id.userkonw);
        mButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
            }
        });
    }
    
    public void setTitleView(String title) {
        mTitleView.setText(title);   
    }
    
    public void setFactorView(String factor) {
        mFactorView.setText(factor);   
    }
    
    public void setExplainView(String explain) {
        mExplainView.setText(explain);   
    }
    
    public void setCommentView(String comment) {
        mCommentView.setText(comment);   
    }
}
