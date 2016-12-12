package com.aizi.yingerbao;

import android.R.integer;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.utility.Utiliy;
import com.aizi.yingerbao.view.TopBarView;
import com.aizi.yingerbao.view.TopBarView.onTitleBarClickListener;
import com.qihoo.appstore.common.updatesdk.lib.UpdateHelper;

public class AboutActivity extends Activity implements onTitleBarClickListener{
    
    private static final String TAG = AboutActivity.class.getSimpleName();
    
    TopBarView mAboutTopView;
    TextView mAppVersionTextView;
    Button mCheckUpdate;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        
        mAboutTopView = (TopBarView) findViewById(R.id.abouttopbar);
        mAboutTopView.setClickListener(this);
        mCheckUpdate = (Button) findViewById(R.id.checkupdate);
        
        mCheckUpdate.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                UpdateHelper.getInstance().init(getApplicationContext(), Color.parseColor("#0A93DB"));
                UpdateHelper.getInstance().setDebugMode(true);
                long intervalMillis = 100 * 1000L; //第一次调用startUpdateSilent出现弹窗后，如果100秒内进行第二次调用不会查询更新
                //UpdateHelper.getInstance().autoUpdate(getPackageName(), false, intervalMillis);
                UpdateHelper.getInstance().manualUpdate(getPackageName());
            }
        });
        
        mAppVersionTextView = (TextView) findViewById(R.id.aboutappversion);
        
        String appVersion = Utiliy.getAppVersionName(getApplicationContext());
        
        mAppVersionTextView.setText(Constant.ABOUT_PREFIX + appVersion);
        
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
