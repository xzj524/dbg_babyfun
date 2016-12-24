package com.aizi.yingerbao;

import java.util.Timer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.smssdk.SMSSDK;

import com.aizi.yingerbao.bluttooth.BluetoothApi;
import com.aizi.yingerbao.command.CommandCenter;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.deviceinterface.AsyncDeviceFactory;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.login.LoginActivity;
import com.aizi.yingerbao.slidingmenu.SlidingMenuHelper;
import com.aizi.yingerbao.utility.MessageParse;
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
        
        AsyncDeviceFactory.getInstance(getApplicationContext());
        MessageParse.getInstance(getApplicationContext());
        
        Bmob.initialize(this, "d32cb6a0c62e9652f4f618ad60a76525", "Bomb");
        
        
        BmobConfig config =new BmobConfig.Builder(this)
        //设置appkey
        .setApplicationId("d32cb6a0c62e9652f4f618ad60a76525")
        //请求超时时间（单位为秒）：默认15s
        .setConnectTimeout(30)
        //文件分片上传时每片的大小（单位字节），默认512*1024
        .setUploadBlockSize(1024*1024)
        //文件的过期时间(单位为秒)：默认1800s
        .setFileExpiration(2500)
        .build();
        Bmob.initialize(config);
        
        
        UpdateHelper.getInstance().init(getApplicationContext(), Color.parseColor("#0A93DB"));
        UpdateHelper.getInstance().setDebugMode(true);
        long intervalMillis = 100 * 1000L; //第一次调用startUpdateSilent出现弹窗后，如果100秒内进行第二次调用不会查询更新
        UpdateHelper.getInstance().autoUpdate(getPackageName(), false, intervalMillis);
        
        CommandCenter.getInstance(getApplicationContext());
        
        MobclickAgent.startWithConfigure(
                new UMAnalyticsConfig(getApplicationContext(), 
                "582580076e27a45a8a00000c", "360", 
                EScenarioType.E_UM_NORMAL));
        
        mCircleButtonBreath = (CircleButton) findViewById(R.id.button0);
        mCircleButtonTemp = (CircleButton) findViewById(R.id.button1);
        mCircleButtonBreath.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), BreathActivity.class);
                startActivity(intent);
            }
        });
        mCircleButtonTemp.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
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
        //PrivateParams.setSPInt(getApplicationContext(), Constant.LOGIN_VALUE, 1);
        if (PrivateParams.getSPInt(getApplicationContext(), Constant.LOGIN_VALUE, 0) == 0) {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
        } else {
            if (!Utiliy.isBluetoothConnected(getApplicationContext())) {
                Utiliy.showConnectDialog(this);
            }
        }
    }

    @Override
    public void onBackClick() {
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
        super.onDestroy();
        try {
            PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 0);
            BluetoothApi.getInstance(getApplicationContext()).mBluetoothService.disconnect(true);
            BluetoothApi.getInstance(getApplicationContext()).unregisterEventBus();
            CommandCenter.getInstance(getApplicationContext()).clearInterfaceQueue();
            PrivateParams.setSPInt(getApplicationContext(), "check_device_status", 0);
            PrivateParams.setSPInt(getApplicationContext(), "sync_data_status", 0);
            PrivateParams.setSPInt(getApplicationContext(), "search_device_status", 0);
            PrivateParams.setSPInt(getApplicationContext(), "connect_interrupt", 0);
            //BluetoothApi.getInstance(getApplicationContext()).unbindBluetoothService();
            
            cancelAllAlarmPendingIntent(getApplicationContext());
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
        
    }

    
    private void cancelAllAlarmPendingIntent(Context context) {
        try {
            Utiliy.cancelAlarmPdIntent(context, Utiliy.getDelayPendingIntent(context, Constant.ALARM_WAIT_L1));
            Utiliy.cancelAlarmPdIntent(context, Utiliy.getDelayPendingIntent(context, Constant.ALARM_WAIT_CHECK_DEVICE));
            Utiliy.cancelAlarmPdIntent(context, Utiliy.getDelayPendingIntent(context, Constant.ALARM_WAIT_SYNC_DATA));
            Utiliy.cancelAlarmPdIntent(context, Utiliy.getDelayPendingIntent(context, Constant.ALARM_WAIT_SEARCH_DEVICE));
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
   }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
   
    
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }
    
    
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        try {
            if (keyCode==KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  
                if (Utiliy.isBluetoothConnected(getApplicationContext())) {
                    showQuitDialog(this, null, null); 
                }
            } 
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
             
        return super.onKeyDown(keyCode, event);
    }
    
    
    /** 
     * @Description: 显示退出对话框
     * @Context
     */
    
    public void showQuitDialog(Context context, String title, String content){
        
        final AlertDialog.Builder normalDialog = 
            new AlertDialog.Builder(context);
        normalDialog.setIcon(R.drawable.yingerbao_96);
        normalDialog.setTitle("退出应用");
        normalDialog.setMessage("退出后蓝牙断开，确定退出应用？");
        normalDialog.setPositiveButton("确定", 
            new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        normalDialog.setNegativeButton("取消", 
            new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //...To-do
            }
        });
        // 显示
        normalDialog.show();
    }
}
