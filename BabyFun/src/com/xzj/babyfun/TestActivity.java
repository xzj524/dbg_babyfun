package com.xzj.babyfun;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.xzj.babyfun.utility.MessageParse;

public class TestActivity extends Activity {
    
    Button mTestButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        mTestButton = (Button) findViewById(R.id.setbtn);
        
        mTestButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                MessageParse.handleSettings(null);
            }
        });
    }
}
