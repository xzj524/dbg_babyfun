package com.aizi.yingerbao;

import java.util.Calendar;

import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.aizi.yingerbao.bluttooth.BluetoothApi;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.deviceinterface.DeviceFactory;
import com.aizi.yingerbao.slidingmenu.SlidingMenuHelper;
import com.aizi.yingerbao.utility.Utiliy;
import com.aizi.yingerbao.view.CircleButton;
import com.aizi.yingerbao.view.TopBarView;
import com.aizi.yingerbao.view.TopBarView.onTitleBarClickListener;

import de.greenrobot.event.EventBus;

public class ManufatureTestActivity extends Activity implements onTitleBarClickListener {
    private static final String TAG = ManufatureTestActivity.class.getSimpleName();
    
    Button mClearLog;
    Button mSetSearchRange;
    private ListView mMsgListView;
    private ArrayAdapter<String> listAdapter;
    private  TopBarView mManuTopbar;
    
    EditText mSearchParam;
    
    CircleButton mCircleButtonTest;
    CircleButton mCircleButtonDisconnect;
    SlidingMenuHelper mSlidingMenuHelper;
    
    boolean mManuTestStart = false;
    boolean mIsMeasuring = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manufature_test);
        
        mMsgListView = (ListView) findViewById(R.id.manu_listMessage);
        listAdapter = new ArrayAdapter<String>(this, R.layout.message_detail);
        mMsgListView.setAdapter(listAdapter);
        mMsgListView.setDivider(null);
        
        mSearchParam = (EditText) findViewById(R.id.search_range_edit);
        mSetSearchRange = (Button) findViewById(R.id.manu_btn_set_range);
        mSetSearchRange.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String searchrange = mSearchParam.getText().toString();
                if (!TextUtils.isEmpty(searchrange)) {
                    int searchparam = Integer.parseInt("-" + searchrange);
                   // Toast.makeText(getApplicationContext(), " " + searchparam, Toast.LENGTH_SHORT).show();  
                    Constant.setSearchRange(searchparam);
                }
                
            }
        });
        
        mSlidingMenuHelper = new SlidingMenuHelper(this);
        mSlidingMenuHelper.initSlidingMenu();
        
        mManuTopbar = (TopBarView) findViewById(R.id.manufaturetopbar);
        mManuTopbar.setClickListener(this);
        
        mClearLog = (Button) findViewById(R.id.manu_btn_clearlog);
        mClearLog.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
             listAdapter.clear();
            }
        });
        
        mCircleButtonDisconnect = (CircleButton) findViewById(R.id.manufature_disconnect);
        mCircleButtonDisconnect.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                BluetoothApi.getInstance(getApplicationContext()).mBluetoothService.disconnect(false);
            }
        });
        
        mCircleButtonTest = (CircleButton) findViewById(R.id.manufature_test);
        mCircleButtonTest.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (Utiliy.isBluetoothConnected(getApplicationContext())) {
                    Calendar calendar = Calendar.getInstance();
                    String curCheckTime = "[" + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                            + calendar.get(Calendar.MINUTE) + ":"
                            + calendar.get(Calendar.SECOND)
                            + "]: ";
                    String checklog = curCheckTime + " 正在进行自动化测试，请稍后。。。";
                   
                    if (!mIsMeasuring) {
                        DeviceFactory.getInstance(getApplicationContext()).manufactureTestCommand();  
                        listAdapter.add(checklog);
                        mMsgListView.smoothScrollToPosition(listAdapter.getCount() - 1);
                        mIsMeasuring = true;
                        
                        new Handler().postDelayed(new Runnable() {
                            
                            @Override
                            public void run() {
                                mIsMeasuring = false; 
                            }
                        }, 5000);
                        
                    } else {
                        Toast.makeText(getApplicationContext(), "5秒内请勿重复操作！", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    showNormalDialog();
                }
            }
        });
        
        EventBus.getDefault().register(this);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    
  public void onEventMainThread(Intent event) {  
        
        Calendar calendar = Calendar.getInstance();
        String curCheckTime = "[" + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                + calendar.get(Calendar.MINUTE) + ":"
                + calendar.get(Calendar.SECOND)
                + "]: ";
        
        String action = event.getAction();
        String checklog = null;
        if (Constant.MANU_TEST_RESULT.equals(action)) {
            int checkresult = event.getIntExtra("manu_check_result", 0);
            int acceleration = event.getIntExtra("manu_acceleration", 0);
            String temperature = event.getStringExtra("manu_temperature");
            String autocheck = null;
            if (checkresult == 0) {
                autocheck = "成功";
            } else {
                autocheck = "失败";
            }
            checklog = curCheckTime + "\n"
                    + "设备加速度 ：" + acceleration + " " + autocheck + "\n"
                    + "检测温度 ：" + temperature  + "\n";
        } 
        
        if (!TextUtils.isEmpty(checklog)) {
            listAdapter.add(checklog);
            mMsgListView.smoothScrollToPosition(listAdapter.getCount() - 1);
        }  
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
}
