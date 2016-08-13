package com.xzj.babyfun.ui.component.main;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xzj.babyfun.R;

/**
 * 
 * @author xuzejun
 * @since 2016-3-5
 */
public class BabyRealTimeStatusFragment extends Fragment{

    private static final String TAG = BabyRealTimeStatusFragment.class.getSimpleName();
    
    /** 温度 view */
    private LinearLayout mTemptureView;

    /** 湿度 view */
    private LinearLayout mHumidityView;
   
    /** 睡眠 view */
    private LinearLayout mSleepView;
    
    private static TextView mTemperatureTextView;
    private TextView mHumitTextView;
   // private TextView mPM25TextView;
   
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View realtimeStatusView = inflater.inflate(R.layout.baby_realtime_status_fragment, container, false);
        
     // mTemptureView = (LinearLayout) realtimeStatusView.findViewById(R.id.babyenvironment1);
      
      mTemperatureTextView = (TextView) realtimeStatusView.findViewById(R.id.babytemperaturevalues);
      mHumitTextView = (TextView) realtimeStatusView.findViewById(R.id.babyhumidityvalues);
     
      return realtimeStatusView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
     
    
    
    public static void setTemperature(String tempString) {
        mTemperatureTextView.setText(tempString);
        
    }
    
    public void setHumit(int value) {
        mHumitTextView.setText(""+value);
        
    }
}
