package com.aizi.yingerbao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.deviceinterface.DeviceFactory;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.utility.Utiliy;
import com.aizi.yingerbao.view.CircleButton;

import de.greenrobot.event.EventBus;

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
import android.widget.ListView;
import android.widget.Toast;

public class ManufatureTestActivity extends Activity {
    private static final String TAG = ManufatureTestActivity.class.getSimpleName();
    
    Button mClearLog;
    private ListView mMsgListView;
    private ArrayAdapter<String> listAdapter;
    
    CircleButton mCircleButtonTest;
    CircleButton mCircleButtonUpdate;
    
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
        
        mClearLog = (Button) findViewById(R.id.manu_btn_clearlog);
        mClearLog.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
             listAdapter.clear();
            }
        });
        
        mCircleButtonTest = (CircleButton) findViewById(R.id.manufature_test);
        mCircleButtonUpdate = (CircleButton) findViewById(R.id.update_rom_test);
        
        mCircleButtonTest.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (Utiliy.isBluetoothConnected(getApplicationContext())) {
                    String time = new SimpleDateFormat("yyyy-MM-dd ").format(new Date());
                    Calendar calendar = Calendar.getInstance();
                    String curCheckTime = "[" + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                            + calendar.get(Calendar.MINUTE) + ":"
                            + calendar.get(Calendar.SECOND)
                            + "]: ";
                    String checklog = curCheckTime + " 正在进行自动化测试，请稍后。。。";
                   
                        if (!mIsMeasuring) {
                            DeviceFactory.getInstance(getApplicationContext()).manufactureTestCommand();  
                            listAdapter.add(checklog);
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
                       
                   
                    
                    /*DeviceFactory.getInstance(getApplicationContext()).manufactureTestCommand();
                    String time = new SimpleDateFormat("yyyy-MM-dd ").format(new Date());
                    Calendar calendar = Calendar.getInstance();
                    String curCheckTime = "[" + calendar.get(Calendar.HOUR_OF_DAY) + ":"
                            + calendar.get(Calendar.MINUTE) + ":"
                            + calendar.get(Calendar.SECOND)
                            + "]: ";
                    String checklog = curCheckTime + " 正在进行自动化测试，请稍后。。。";
                    listAdapter.add(checklog);*/
                } else {
                    showNormalDialog();
                }
            }
        });
        mCircleButtonUpdate.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (Utiliy.isBluetoothConnected(getApplicationContext())) {
                    DeviceFactory.getInstance(getApplicationContext()).updateDeviceRom();
                }
            }
        });
        
        EventBus.getDefault().register(this);
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    
  public void onEventMainThread(Intent event) {  
        
        String time = new SimpleDateFormat("yyyy-MM-dd ").format(new Date());
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
}
