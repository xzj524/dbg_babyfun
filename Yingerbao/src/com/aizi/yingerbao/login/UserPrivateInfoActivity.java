package com.aizi.yingerbao.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.aizi.yingerbao.R;
import com.aizi.yingerbao.view.TopBarView.onTitleBarClickListener;

public class UserPrivateInfoActivity extends Activity implements onTitleBarClickListener{

    TextView mPhoneTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_private_info);
        
        mPhoneTextView = (TextView) findViewById(R.id.user_priviate);
        
        Intent intent = getIntent();
        if (intent.hasExtra("user_private_account")) {
            String useraccount = intent.getStringExtra("user_private_account");
            mPhoneTextView.setText("手机号码：      " + useraccount);
        }
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onRightClick() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onCalendarClick() {
        // TODO Auto-generated method stub
        
    }
}
