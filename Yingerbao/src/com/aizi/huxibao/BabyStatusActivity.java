package com.aizi.huxibao;

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

import com.aizi.yingerbao.R;
import com.aizi.xiaohuhu.chart.BarChartFragment;
import com.aizi.xiaohuhu.chart.SleepyChart;
import com.aizi.xiaohuhu.chart.SleepyChart.SetTextViewListener;
import com.aizi.xiaohuhu.chart.SleepyPieChart;
import com.aizi.xiaohuhu.deviceinterface.AsyncDeviceFactory;
import com.aizi.xiaohuhu.logging.SLog;
import com.aizi.xiaohuhu.receiver.BabyStatusReceiver;
import com.aizi.xiaohuhu.receiver.BabyStatusReceiver.DataStatusInteraction;
import com.aizi.xiaohuhu.service.BluetoothService;
import com.aizi.xiaohuhu.ui.component.main.BabyRealTimeStatusFragment;
import com.aizi.xiaohuhu.utility.Utiliy;

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
        
        //AsyncDeviceFactory.getInstance(getApplicationContext()).getAllNoSyncInfo();
        AsyncDeviceFactory.getInstance(getApplicationContext()).getAllSyncInfo();
        SLog.e("BabyStatusActivity", "getAllSyncInfo");
        
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
