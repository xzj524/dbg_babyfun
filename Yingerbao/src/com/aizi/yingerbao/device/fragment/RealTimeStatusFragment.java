package com.aizi.yingerbao.device.fragment;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizi.yingerbao.R;
import com.aizi.yingerbao.deviceinterface.AsyncDeviceFactory;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.utility.PrivateParams;

import de.greenrobot.event.EventBus;

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
        
      mTemperatureTextView = (TextView) realtimeStatusView.findViewById(R.id.temperaturevalues123);
      mHumitTextView = (TextView) realtimeStatusView.findViewById(R.id.humidityvalues);
      
      Timer timer = new Timer(true);      
      EventBus.getDefault().register(this);
      
      return realtimeStatusView;
    }
    
    
    TimerTask task = new TimerTask(){  
        public void run() {  
            int connected =  PrivateParams.getSPInt(getActivity().getApplicationContext(), "connectedbluetooth", 0);
   if (connected == 1) {
       AsyncDeviceFactory.getInstance(getActivity()
               .getApplicationContext()).getRealTimeData();
   }
          
      }  
   };  

    
    //Container Activity must implement this interface  
    public interface OnStatusSelectedListener{  
        public void onStatusSelected(int touchid);  
    }  
    
    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        

    }
    
    public void setTemperature(String tempString) {
        SLog.e(TAG, "tempString = " + tempString);
      //  mTemperatureTextView.setText(tempString);
        
    }
    
    public void setHumit(int value) {
        mHumitTextView.setText(""+value);
        
    }
    
    public void setPM25(int value) {
        mPM25TextView.setText(""+value);
        
    }
    
    public void onEventMainThread(String str) {
        setTemperature(str);
    }
    
    public void onEvent(String str) {
        
    }
    
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
