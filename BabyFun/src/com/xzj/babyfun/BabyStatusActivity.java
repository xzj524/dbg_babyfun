package com.xzj.babyfun;

import java.util.ArrayList;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.xzj.babyfun.chart.BarChartFragment;
import com.xzj.babyfun.chart.SleepyChart;
import com.xzj.babyfun.chart.SleepyChart.SetTextViewListener;
import com.xzj.babyfun.chart.SleepyPieChart;
import com.xzj.babyfun.receiver.BabyStatusReceiver;
import com.xzj.babyfun.receiver.BabyStatusReceiver.DataStatusInteraction;
import com.xzj.babyfun.service.BluetoothService;
import com.xzj.babyfun.ui.component.main.BabyRealTimeStatusFragment;
import com.xzj.babyfun.utility.Utiliy;

public class BabyStatusActivity extends Activity implements DataStatusInteraction, SetTextViewListener{
    
    BabyRealTimeStatusFragment realTimeStatusFragment;
    BarChartFragment babyChartFragment;
    SleepyChart sleepyChartFragment;
    SleepyPieChart sleepyPieChartFragmentChart;
    int tempValue = 0;
    int humitValue = 0;
    int sleepValue = 0;
    
    ImageView mImageViewbigline;
    ImageView mImageViewsmalline;
    
    TextView mBabyStatus;
    TextView mBabyPoint;
    
    MyHandler mHandler;
    MyThread m ;
    int count = 0;
    AlphaAnimation appearAnimation;
    AlphaAnimation disappearAnimation;
    FragmentManager mFragmentMan;
    
    ArrayList<Integer> mSleepValues = new ArrayList<Integer>();
    
    static int[] mColors = new int[] { Color.rgb(137, 230, 81), Color.rgb(240, 240, 30),//  
        Color.rgb(89, 199, 250), Color.rgb(250, 104, 104), Color.rgb(4, 158, 255) }; // 自定义颜色 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_status);
        
        BabyStatusReceiver babyStatusReceiver = new BabyStatusReceiver();
        babyStatusReceiver.setDataStatusInteractionListener(this);
        
 
        
        mFragmentMan = getFragmentManager();
        sleepyChartFragment = (SleepyChart) mFragmentMan.findFragmentById(R.id.babySleepChartFragment);
        
        sleepyPieChartFragmentChart
            = (SleepyPieChart) mFragmentMan.findFragmentById(R.id.babySleepPieChartFragment);
        
        
        if (Utiliy.mSleepList.size() == 0) {
            Utiliy.mSleepList.add(0); 
        }

        appearAnimation = new AlphaAnimation(0, 1);  
        appearAnimation.setDuration(1000);  
  
        disappearAnimation = new AlphaAnimation(1, 0);  
        disappearAnimation.setDuration(1000); 
    }
    
    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    @Override
    public void setData(Intent intent) {
        // TODO Auto-generated method stub
        //Toast.makeText(getApplicationContext(), " 使能notification", Toast.LENGTH_SHORT).show();
        int dataType = intent.getIntExtra(BluetoothService.EXTRA_TYPE, 0);
        
       /* if (dataType == BluetoothService.DATA_TYPE_TEMP_HUMIT) {
               tempValue = intent.getIntExtra(BluetoothService.EXTRA_DATA_TEMP, 0);
               humitValue = intent.getIntExtra(BluetoothService.EXTRA_DATA_HUMIT, 0);
               realTimeStatusFragment.setTemperature(tempValue);
               realTimeStatusFragment.setHumit(humitValue);
         } else if (dataType == BluetoothService.DATA_TYPE_PM25) {
            // pm25Value = intent.getIntExtra(UartService.EXTRA_DATA_PM25, 0);
         } else if (dataType == BluetoothService.DATA_TYPE_SLEEP) {
              sleepValue = intent.getIntExtra(BluetoothService.EXTRA_DATA_SLEEP, 0);
              Utiliy.mSleepList.add(sleepValue);
              LineData data = sleepyChartFragment.getData(sleepValue);
              if (data != null) {
                  sleepyChartFragment.setupChart(data, mColors[4]);
              }
              Log.e("Babyfun", "sleepvalue = " + sleepValue);
              
              int Value = Math.abs(Math.abs(sleepValue - 50) * 2 - 100);
              //ValsSleep.add(new Entry(Math.abs(sleepValue * 2 - 100), i));
              
              if (Value < 30) {
                mBabyStatus.setText("宝宝清醒");
              
            } else if (Value >= 30 && Value < 60) {
                mBabyStatus.setText("宝宝入睡");
            } else if (Value >= 60 && Value < 90) {
                mBabyStatus.setText("宝宝浅睡");
            } else if (Value >= 90 && Value < 100) {
                mBabyStatus.setText("宝宝深睡");
            } 
              
              mBabyPoint.setText(Value + " 分");
             
         }*/
        
       
        
        
        
        
        
        //realTimeStatusFragment.setPM25(pm25Value);
    }
   
    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) { // 判断接收的消息
            case 0:
                mImageViewbigline.startAnimation(appearAnimation);
                mImageViewbigline.setVisibility(View.VISIBLE);
                
                mImageViewsmalline.startAnimation(disappearAnimation);
                mImageViewsmalline.setVisibility(View.INVISIBLE);
                //MyHandle.this.iv.setImageResource(R.drawable.photo1);
                break;
            case 1:
                mImageViewbigline.startAnimation(disappearAnimation);
                mImageViewbigline.setVisibility(View.INVISIBLE);
                
                mImageViewsmalline.startAnimation(appearAnimation);  
                mImageViewsmalline.setVisibility(View.VISIBLE);
               // MyHandle.this.iv.setImageResource(R.drawable.photo2);
                break;

            }
            super.handleMessage(msg);
        }
    }
    
    class MyThread implements Runnable {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            while (true) {
                Message msg = mHandler.obtainMessage();
                msg.what = count++ % 2;
                mHandler.sendMessage(msg); // 发送消息
                try {
                    Thread.sleep(1000);// 停止2秒
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                }
            }
        }

    @Override
    public void SetBabyStatus(String str) {
        // TODO Auto-generated method stub
        mBabyStatus.setText(str);
    }

    @Override
    public void SetBabyPoint(String str) {
        // TODO Auto-generated method stub
        mBabyPoint.setText(str);
        
    }

    @Override
    public void SetPieChart(float sober, float fallsleep, float lightsleep, float deepsleep) {
        // TODO Auto-generated method stub
        mFragmentMan = getFragmentManager();
        if (mFragmentMan != null) {
            sleepyPieChartFragmentChart
            = (SleepyPieChart) mFragmentMan.findFragmentByTag("piechart");
        
            sleepyChartFragment 
            = (SleepyChart) mFragmentMan.findFragmentById(R.id.babySleepChartFragment);
        }
       
       
        sleepyPieChartFragmentChart.setPieChart(sober, fallsleep, lightsleep, deepsleep);
    }
   
}
