package com.aizi.xiaohuhu.fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.aizi.xiaohuhu.R;
import com.aizi.xiaohuhu.constant.Constant;
import com.aizi.xiaohuhu.synctime.DataTime;
import com.aizi.xiaohuhu.utility.PrivateParams;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import de.greenrobot.event.EventBus;

public class SimpleCalendarDialogFragment extends DialogFragment implements OnDateSelectedListener {
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    private static final String TAG = SimpleCalendarDialogFragment.class.getSimpleName();
    
    long mTimeMilis = 0;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        return inflater.inflate(R.layout.dialog_calendar, container, false);

    }
    
    @SuppressLint("NewApi")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        MaterialCalendarView widget = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        //EventBus.getDefault().register(this);
        widget.setOnDateChangedListener(this);
        //Date date = new Date(System.currentTimeMillis());
        int year = PrivateParams.getSPInt(getActivity().getApplicationContext(), Constant.DATA_DATE_YEAR, 0);
        int month = PrivateParams.getSPInt(getActivity().getApplicationContext(), Constant.DATA_DATE_MONTH, 0);
        int day = PrivateParams.getSPInt(getActivity().getApplicationContext(), Constant.DATA_DATE_DAY, 0);
        
        if (year == 0 || month == 0 || day == 0) {
            mTimeMilis = System.currentTimeMillis();
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String start= year + "-" + (month+1) + "-" + day;
            //得到毫秒数
                try {
                    mTimeMilis=sdf.parse(start).getTime();
                } catch (java.text.ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
        }
        Date date = new Date(mTimeMilis);
        widget.setDateSelected(date , true);
        widget.refreshDrawableState();
        widget.setCurrentDate(date);
        
        
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        // TODO Auto-generated method stub
        PrivateParams.setSPInt(getActivity().getApplicationContext(), Constant.DATA_DATE_YEAR, date.getYear());
        PrivateParams.setSPInt(getActivity().getApplicationContext(), Constant.DATA_DATE_MONTH, date.getMonth());
        PrivateParams.setSPInt(getActivity().getApplicationContext(), Constant.DATA_DATE_DAY, date.getDay());
        
        DataTime dataTime = new DataTime();
        dataTime.year = date.getYear();
        dataTime.month = date.getMonth();
        dataTime.day = date.getDay();
        EventBus.getDefault().post(dataTime);
        
        dismiss();
      
    }
    
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        //EventBus.getDefault().unregister(this);
    }

}
