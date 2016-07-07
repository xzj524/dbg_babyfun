package com.xzj.babyfun.ui.component.main;

import android.app.Activity;
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
public class RealTimeStatusFragment extends Fragment{

    private static final String TAG = RealTimeStatusFragment.class.getSimpleName();
    
    /** 温度 view */
    private LinearLayout mTemptureView;

    /** 湿度 view */
    private LinearLayout mHumidityView;
    
    private TextView mTemperatureTextView;
    private TextView mHumitTextView;
    private TextView mPM25TextView;
    
    OnStatusSelectedListener mListener;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View realtimeStatusView = inflater.inflate(R.layout.realtime_status_fragment, container, false);
        
      mTemptureView = (LinearLayout) realtimeStatusView.findViewById(R.id.environment1);

      
  /*    mTemptureView.setOnClickListener(new View.OnClickListener() {
          
          @Override
          public void onClick(View v) {
              // TODO Auto-generated method stub
              Toast.makeText(v.getContext(), "点击温度", Toast.LENGTH_SHORT).show();
              mListener.onStatusSelected(1);
          }
      });*/
      
      mTemperatureTextView = (TextView) realtimeStatusView.findViewById(R.id.temperaturevalues);
      mHumitTextView = (TextView) realtimeStatusView.findViewById(R.id.humidityvalues);
      
      return realtimeStatusView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
    
    
    //Container Activity must implement this interface  
    public interface OnStatusSelectedListener{  
        public void onStatusSelected(int touchid);  
    }  
    
    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        
        mListener =(OnStatusSelectedListener)activity;  
    }
    
    public void setTemperature(int value) {
        mTemperatureTextView.setText(""+value);
        
    }
    
    public void setHumit(int value) {
        mHumitTextView.setText(""+value);
        
    }
    
    public void setPM25(int value) {
        mPM25TextView.setText(""+value);
        
    }
}
