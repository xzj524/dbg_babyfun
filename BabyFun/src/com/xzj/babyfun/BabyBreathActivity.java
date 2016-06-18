package com.xzj.babyfun;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.xzj.babyfun.breath.BabyBreath;
import com.xzj.babyfun.chart.BreathChart;
import com.xzj.babyfun.deviceinterface.AsyncDeviceFactory;
import com.xzj.babyfun.logging.SLog;

import de.greenrobot.event.EventBus;

public class BabyBreathActivity extends Activity {
    
    private Button mFreshButton;
    private Button mStartButton;
    private FragmentManager mFragmentMan;
    private BreathChart breathchartFragment;
    int mPreValue = 10;
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_baby_breath);
        mFreshButton = (Button) findViewById(R.id.breathbtn);
        mFreshButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
               // breathchartFragment.freshChart();
               //  mPreValue = (int) Math.abs((Math.random() * 100));
                //updateBreathWave(mPreValue);
                AsyncDeviceFactory.getInstance(getApplicationContext()).startSendBreathData();
            }
        });
        
        mStartButton = (Button) findViewById(R.id.breathstartbtn);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                timer.schedule(task,1000, 1000); //延时1000ms后执行，1000ms执行一次
            }
        });
        
        mFragmentMan = getFragmentManager();
        breathchartFragment = (BreathChart) mFragmentMan.findFragmentById(R.id.babybreathChartFragment);
       
        timer = new Timer(true);
       
    }
    
    private void updateBreathWave(int preValue) {
        // TODO Auto-generated method stub
        for (int i = 0; i < 2; i++) {
            if (i == 0 ) {
                breathchartFragment.generateNewWave(5); 
            } else if (i == 1) {
                breathchartFragment.generateNewWave(preValue); 
            } 
            
        }
    }
    
    final Handler handler = new Handler(){  
            public void handleMessage(Message msg) {  
                 switch (msg.what) {      
                     case 1:      
                     updateBreathWave(msg.arg1);
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
           handler.sendMessage(message);    
         }  
      };  
    
    public void onEvent(final ArrayList<BabyBreath> breaths) { 
        SLog.e("breathtest", "BabyBreathActivity receive REAL BREATH DATA");
        if (breaths.size() == 1) {
            mPreValue = breaths.get(0).mBreathValue;
            SLog.e("breathtest", "BabyBreathActivity receive REAL BREATH DATA " + mPreValue);
        } else if (breaths.size() == 2) {
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
  
        }
       /* for (BabyBreath breath:breaths) {
            mPreValue = breath.mBreathValue;
            for (int i = 0; i < 2; i++) {
                if (i == 0 ) {
                    breathchartFragment.generateNewWave(10); 
                } else if (i == 1) {
                    breathchartFragment.generateNewWave(mPreValue); 
                } 
                
            }
        }*/
    }
    
}
