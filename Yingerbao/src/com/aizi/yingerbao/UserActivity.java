package com.aizi.yingerbao;

import java.util.Timer;
import java.util.TimerTask;

import u.aly.ac;

import android.R.integer;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.aizi.yingerbao.bluttooth.BluetoothApi;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.deviceinterface.AsyncDeviceFactory;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.slidingmenu.SlidingMenuHelper;
import com.aizi.yingerbao.synctime.DataTime;
import com.aizi.yingerbao.ui.component.main.DeviceConnectStatusFragment.OnDeviceConnectListener;
import com.aizi.yingerbao.utility.PrivateParams;
import com.aizi.yingerbao.utility.Utiliy;
import com.aizi.yingerbao.view.BatteryView;
import com.aizi.yingerbao.view.CircleButton;
import com.aizi.yingerbao.view.TopBarView;
import com.aizi.yingerbao.view.TopBarView.onTitleBarClickListener;
import com.qihoo.appstore.common.updatesdk.lib.UpdateHelper;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.MobclickAgent.EScenarioType;
import com.umeng.analytics.MobclickAgent.UMAnalyticsConfig;

import de.greenrobot.event.EventBus;

public class UserActivity extends Activity implements onTitleBarClickListener {
    
    private static final String TAG = UserActivity.class.getSimpleName();
    
    private  TopBarView userTopbar;
    private  BatteryView mBatteryView;
    SlidingMenuHelper mSlidingMenuHelper;

    CircleButton mCircleButtonBreath;
    CircleButton mCircleButtonTemp;
    Timer mTimer;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        
        SLog.e(TAG, "BlueTooth is connected&&&&&&&&&&&&&&111");
        
        UpdateHelper.getInstance().init(getApplicationContext(), Color.parseColor("#0A93DB"));
        UpdateHelper.getInstance().setDebugMode(true);
        long intervalMillis = 100 * 1000L; //第一次调用startUpdateSilent出现弹窗后，如果100秒内进行第二次调用不会查询更新
        UpdateHelper.getInstance().autoUpdate(getPackageName(), false, intervalMillis);
        
        MobclickAgent.startWithConfigure(
                new UMAnalyticsConfig(getApplicationContext(), 
                "582580076e27a45a8a00000c", "360", 
                EScenarioType.E_UM_NORMAL));
        
        EventBus.getDefault().register(this);

        
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
        
        if (!Utiliy.isBluetoothConnected(getApplicationContext())) {
            Utiliy.showNormalDialog(this);
        } else {
            SLog.e(TAG, "BlueTooth is connected&&&&&&&&&&&&&&");
           
        }
    }
    
    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        
       
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
    
    public void onEventMainThread(Intent intent) { 
        try {
            String action = intent.getAction();
            if (action.equals("com.aizi.transfer")) {
                if (Utiliy.isBluetoothConnected(getApplicationContext())) {
                    Thread.sleep(200);
                    AsyncDeviceFactory.getInstance(getApplicationContext()).checkDeviceValid();
                }
            }
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
        
    }
    
    TimerTask task = new TimerTask(){  
        public void run() {  
            try {
                SLog.e(TAG, "START DEVICE2");
                AsyncDeviceFactory.getInstance(getApplicationContext()).checkDeviceValid();
                Thread.sleep(1000);
                AsyncDeviceFactory.getInstance(getApplicationContext()).getAllNoSyncInfo();
                Thread.sleep(1000);
                AsyncDeviceFactory.getInstance(getApplicationContext()).getBreathStopInfo();
            } catch (Exception e) {
                SLog.e(TAG, e);
            }
      }  
   };
    
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
        
       /* try {
            if (Utiliy.isBluetoothConnected(getApplicationContext())) {
                Thread.sleep(200);
                AsyncDeviceFactory.getInstance(getApplicationContext()).checkDeviceValid();
               // Thread.sleep(500);
                AsyncDeviceFactory.getInstance(getApplicationContext()).getAllNoSyncInfo();
                Thread.sleep(500);
                AsyncDeviceFactory.getInstance(getApplicationContext()).getBreathStopInfo();
            }
            
        } catch (Exception e) {
            SLog.e(TAG, e);
        }*/
    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPause(this);

    }
}
