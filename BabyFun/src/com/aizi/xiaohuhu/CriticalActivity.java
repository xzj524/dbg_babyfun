package com.aizi.xiaohuhu;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import u.aly.da;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aizi.xiaohuhu.deviceinterface.AsyncDeviceFactory;
import com.aizi.xiaohuhu.fragment.SimpleCalendarDialogFragment;
import com.aizi.xiaohuhu.utility.Utiliy;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

public class CriticalActivity extends Activity {
    
    Button mFever;
    Button mBreathAbnormal;
    
    Button mGetNoSyncData;
    Button mGetAllData;
    Button mStartBreathData;
    Button mStopBreathData;
    Button mGetBreathHistoryData;
    Button mGetRealTimeData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_critical);
        
        mFever = (Button) findViewById(R.id.fever);
        mBreathAbnormal = (Button) findViewById(R.id.breathabnormal);
        
        mFever.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Utiliy.showFeverNotification(getApplicationContext(), 
                        "孩子发烧了！！", "孩子发烧了，请及时就医。", null);
            }
        });
        
        mBreathAbnormal.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Utiliy.showBreathNotification(getApplicationContext(), 
                        "孩子呼吸停滞！！", "孩子呼吸停滞，请及时处理。", null);
            }
        });
        
        mGetNoSyncData = (Button) findViewById(R.id.getnosyncdata);
        mGetNoSyncData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).getAllNoSyncInfo();
            }
        });
        
        mGetAllData = (Button) findViewById(R.id.getallsyncdata);
        mGetAllData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).getAllSyncInfo();
            }
        });
        
        mStartBreathData = (Button) findViewById(R.id.startbreathdata);
        mStartBreathData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).startSendBreathData();
            }
        });
        
        mStopBreathData = (Button) findViewById(R.id.stopbreathdata);
        mStopBreathData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).stopSendBreathData();
            }
        });
        
        mGetBreathHistoryData = (Button) findViewById(R.id.getbreathhistorydata);
        mGetBreathHistoryData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(getApplicationContext()).getBreathStopInfo();
            }
        });
        
        
        mGetRealTimeData = (Button) findViewById(R.id.gettempdata);
        mGetRealTimeData.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //AsyncDeviceFactory.getInstance(getApplicationContext()).getBodyTemperature();
                
                new SimpleCalendarDialogFragment().show(getFragmentManager(), "test-simple-calendar");
            }
        });
    }
    
   /* private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    
    public static class SimpleCalendarDialogFragment extends DialogFragment implements OnDateSelectedListener {

        private TextView textView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.dialog_calendar, container, false);
        }

        @SuppressLint("NewApi")
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            textView = (TextView) view.findViewById(R.id.textView);

            MaterialCalendarView widget = (MaterialCalendarView) view.findViewById(R.id.calendarView);

            widget.setOnDateChangedListener(this);
            Date date = new Date(System.currentTimeMillis());
            widget.setDateSelected(date , true);
            widget.refreshDrawableState();
            
        }

        @Override
        public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
            textView.setText(FORMATTER.format(date.getDate()));
            
        }
    }*/
}
