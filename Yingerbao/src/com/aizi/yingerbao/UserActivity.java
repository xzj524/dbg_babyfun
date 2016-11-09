package com.aizi.yingerbao;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.aizi.yingerbao.bluttooth.BluetoothApi;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.slidingmenu.SlidingMenuHelper;
import com.aizi.yingerbao.utility.PrivateParams;
import com.aizi.yingerbao.view.BatteryView;
import com.aizi.yingerbao.view.CircleButton;
import com.aizi.yingerbao.view.TopBarView;
import com.aizi.yingerbao.view.TopBarView.onTitleBarClickListener;
import com.qihoo.appstore.common.updatesdk.lib.UpdateHelper;

public class UserActivity extends Activity implements onTitleBarClickListener {
    
    private  TopBarView userTopbar;
    private  BatteryView mBatteryView;
    SlidingMenuHelper mSlidingMenuHelper;

    CircleButton mCircleButtonBreath;
    CircleButton mCircleButtonTemp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        
        UpdateHelper.getInstance().init(getApplicationContext(), Color.parseColor("#0A93DB"));
        UpdateHelper.getInstance().setDebugMode(true);
        long intervalMillis = 100 * 1000L; //第一次调用startUpdateSilent出现弹窗后，如果100秒内进行第二次调用不会查询更新
        UpdateHelper.getInstance().autoUpdate(getPackageName(), false, intervalMillis);

        
        mCircleButtonBreath = (CircleButton) findViewById(R.id.button0);
        mCircleButtonTemp = (CircleButton) findViewById(R.id.button1);
        mCircleButtonBreath.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), BreathActivity.class);
                startActivity(intent);
            }
        });
        mCircleButtonTemp.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), TemperatureActivity.class);
                startActivity(intent);
                
            }
        });
        
        mBatteryView = (BatteryView) findViewById(R.id.battery_view);
        mBatteryView.setPower(95);
        
        userTopbar = (TopBarView) findViewById(R.id.userxiaohuhutopbar);
        userTopbar.setClickListener(this);
        
        mSlidingMenuHelper = new SlidingMenuHelper(this);
        mSlidingMenuHelper.initSlidingMenu();
        
        BluetoothApi.getInstance(getApplicationContext());
    }

    @Override
    public void onBackClick() {
        // TODO Auto-generated method stub
        mSlidingMenuHelper.showMenu();
    }

    @Override
    public void onRightClick() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onCalendarClick() {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 0);
        BluetoothApi.getInstance(getApplicationContext()).mBluetoothService.disconnect();
    }
}
