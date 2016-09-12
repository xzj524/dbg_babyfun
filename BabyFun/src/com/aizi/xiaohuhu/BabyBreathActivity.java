package com.aizi.xiaohuhu;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.aizi.xiaohuhu.breath.BabyBreath;
import com.aizi.xiaohuhu.chart.BreathChart;
import com.aizi.xiaohuhu.deviceinterface.AsyncDeviceFactory;
import com.aizi.xiaohuhu.logging.SLog;
import com.aizi.xiaohuhu.view.TopBarView;
import com.aizi.xiaohuhu.view.TopBarView.onTitleBarClickListener;

import de.greenrobot.event.EventBus;

public class BabyBreathActivity extends Activity implements OnClickListener, onTitleBarClickListener{
    private static final String TAG = BabyBreathActivity.class.getSimpleName();

    private FragmentManager mFragmentMan;
    private BreathChart breathchartFragment;
    private TextView mBreathFreqData;
    private TextView mBreathRealValue;
    int mPreValue = 5;
    long mLastBreathTime;
    long mBreathPeriod;
    int mBreathFreq;
    Timer timer;
    int isbreath = 0;
    
    TopBarView mTopBarView;
    
    boolean breatfreq = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_baby_breath);
        
        mTopBarView = (TopBarView) findViewById(R.id.breathtopbar);
        mTopBarView.setClickListener(this);
               
        mFragmentMan = getFragmentManager();
        breathchartFragment = (BreathChart) mFragmentMan.findFragmentById(R.id.babybreathChartFragment);     
        timer = new Timer(true);
        
        mBreathFreqData = (TextView)findViewById(R.id.breathfrequencedata);
        mBreathRealValue = (TextView)findViewById(R.id.breathvaluedata);
        
        timer.schedule(task,1000, 1300); 
        AsyncDeviceFactory.getInstance(getApplicationContext()).startSendBreathData();
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        timer.cancel();
        super.onDestroy();
        AsyncDeviceFactory.getInstance(getApplicationContext()).stopSendBreathData();
        SLog.e(TAG, "stop send breath data");
    }
    
    private void updateBreathWave(int preValue) {
        // TODO Auto-generated method stub
        for (int i = 0; i < 2; i++) {
            if (i == 0) {
                breathchartFragment.generateNewWave(5); 
            } else if (i == 1) {
                breathchartFragment.generateNewWave(preValue); 
            } 
        }
    }
    
    final Handler mHandler = new Handler(){  
            public void handleMessage(Message msg) {  
                 switch (msg.what) {      
                     case 1:      
                         updateBreathWave(msg.arg1);
                         if (breatfreq) {
                            breatfreq = false;
                            if (mBreathPeriod > 0) {
                                mBreathFreq = (int)((60 * 1000) / mBreathPeriod); 
                                mBreathFreqData.setText(mBreathFreq + "");
                            }
                        }
                       
                         break;      
                     }      
                     super.handleMessage(msg);  
                }    
            };  
            
            
     TimerTask task = new TimerTask(){  
           public void run() {  
           Message message = new Message();      
           message.what = 1; 
           message.arg1 = mPreValue;
           mPreValue = 5;
           
           //mPreValue = (int) Math.abs((Math.random() * 30));
           
           long curtime = System.currentTimeMillis();
           if (mLastBreathTime == 0) {
               mLastBreathTime = curtime;
           } else {
               mBreathPeriod = curtime - mLastBreathTime;
               mLastBreathTime = curtime;
           }
           mHandler.sendMessage(message);    
         }  
      };  
    
    public void onEventMainThread(final ArrayList<BabyBreath> breaths) { 
        long curtime = System.currentTimeMillis();
        SLog.e(TAG, "curtime = " + curtime);
        if (mLastBreathTime == 0) {
            mLastBreathTime = curtime;
        } else {
            mBreathPeriod = curtime - mLastBreathTime;
            mLastBreathTime = curtime;
        }
        breatfreq = true;
        //SLog.e(TAG, "breaths size = ")
        if (breaths.size() == 1) {
            mPreValue = breaths.get(0).mBreathValue;
            mBreathRealValue.setText(mPreValue+"");
            SLog.e(TAG, "BabyBreathActivity receive REAL BREATH DATA " + mPreValue);
        } /*else if (breaths.size() == 2) {
            mPreValue = breaths.get(0).mBreathValue;
            int timelen = breaths.get(1).mBreathTime - breaths.get(0).mBreathTime;
            Handler mHandler = new Handler(getMainLooper());
            mHandler.postDelayed(new Runnable() {
                
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    mPreValue = breaths.get(1).mBreathValue;
                }
            }, timelen * 1000);
        }*/
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        
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
    
}
