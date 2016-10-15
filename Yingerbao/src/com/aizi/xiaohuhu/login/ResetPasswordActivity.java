package com.aizi.xiaohuhu.login;

import android.app.Activity;
import android.os.Bundle;

import com.aizi.yingerbao.R;
import com.aizi.xiaohuhu.view.TopBarView;
import com.aizi.xiaohuhu.view.TopBarView.onTitleBarClickListener;

public class ResetPasswordActivity extends Activity  implements onTitleBarClickListener{
    
    TopBarView topBarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        topBarView = (TopBarView) findViewById(R.id.topbar);
        topBarView.setClickListener(this);
    }

    @Override
    public void onBackClick() {
        // TODO Auto-generated method stub
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
