package com.aizi.yingerbao.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aizi.yingerbao.R;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.view.TopBarView;
import com.aizi.yingerbao.view.TopBarView.onTitleBarClickListener;

public class UserPrivateInfoActivity extends Activity implements onTitleBarClickListener{

    TextView mPhoneTextView;
    Button mResetPassCodeButton;
    private  TopBarView mUserPrivateInfoTopbar;
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_private_info);
        
        mPhoneTextView = (TextView) findViewById(R.id.user_private_phone);
        
        mUserPrivateInfoTopbar = (TopBarView) findViewById(R.id.userprivatetopbar);
        mUserPrivateInfoTopbar.setClickListener(this);
        
        Intent intent = getIntent();
        if (intent.hasExtra("user_private_account")) {
            String useraccount = intent.getStringExtra("user_private_account");
            mPhoneTextView.setText(useraccount);
        }
        
        mResetPassCodeButton = (Button) findViewById(R.id.resetpasscode);
        mResetPassCodeButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ResetPasswordActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackClick() {
        SLog.e("AIZI", "USERPRIVATEBACK");
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
