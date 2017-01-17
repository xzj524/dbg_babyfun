package com.aizi.yingerbao;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.deviceinterface.DeviceFactory;
import com.aizi.yingerbao.utility.Utiliy;
import com.aizi.yingerbao.view.TopBarView;
import com.aizi.yingerbao.view.TopBarView.onTitleBarClickListener;
import com.qihoo.appstore.common.updatesdk.lib.UpdateHelper;

public class AboutActivity extends Activity implements onTitleBarClickListener{
    
    private static final String TAG = AboutActivity.class.getSimpleName();
    
    TopBarView mAboutTopView;
    TextView mAppVersionTextView;
    Button mCheckUpdate;
    Button mResetManuSettings;    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        
        mAboutTopView = (TopBarView) findViewById(R.id.abouttopbar);
        mAboutTopView.setClickListener(this);
        
        mResetManuSettings = (Button) findViewById(R.id.reset_manu_device);
        mResetManuSettings.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                showResetManuDialog(getApplicationContext());
            }
        });
        
        mCheckUpdate = (Button) findViewById(R.id.checkupdate);
        mCheckUpdate.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                UpdateHelper.getInstance().init(getApplicationContext(), Color.parseColor("#0A93DB"));
                UpdateHelper.getInstance().setDebugMode(true);
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
    
    /** 
     * @Description: 显示 恢复出厂设置对话框
     * @Context
     */
    
    public void showResetManuDialog(final Context context){
        
        final AlertDialog.Builder normalDialog = 
            new AlertDialog.Builder(this);
        normalDialog.setIcon(R.drawable.yingerbao_96);
        normalDialog.setTitle("恢复出厂设置");
        normalDialog.setMessage("是否恢复出厂设置?\n恢复出厂设置后，设备会清空数据，请谨慎操作！");
        //normalDialog.setTitle(title);
        //normalDialog.setMessage(content);
        normalDialog.setPositiveButton("确定", 
            new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (!Utiliy.isBluetoothConnected(context)) {
                    showNormalDialog();
                } else {
                    DeviceFactory.getInstance(context).resetManuSettings(0);
                }
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
    
    
public void showNormalDialog(){
        
        final AlertDialog.Builder normalDialog = 
            new AlertDialog.Builder(this);
        normalDialog.setIcon(R.drawable.yingerbao_96);
        normalDialog.setTitle("连接设备");
        normalDialog.setMessage("设备未连接，是否连接设备,\n请先摇动设备保证能够正确连接。");
        normalDialog.setPositiveButton("确定", 
            new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), ConnectDeviceActivity.class);
                startActivity(intent);
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
