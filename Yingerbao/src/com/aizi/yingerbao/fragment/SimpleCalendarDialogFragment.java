package com.aizi.yingerbao.fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.aizi.yingerbao.R;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.synctime.DataTime;
import com.aizi.yingerbao.utility.PrivateParams;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import de.greenrobot.event.EventBus;

public class SimpleCalendarDialogFragment extends DialogFragment implements OnDateSelectedListener {
    private static final DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
    private static final String TAG = SimpleCalendarDialogFragment.class.getSimpleName();
    
    long mTodayTimeMilis = 0;
    long mOldTimeMilis = 0;
    Button mBackTodayBtn;
    Date mDate = null;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        
        return inflater.inflate(R.layout.dialog_calendar, container, false);

    }
    
    @SuppressLint("NewApi")
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final MaterialCalendarView widget = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        mBackTodayBtn = (Button) view.findViewById(R.id.backtotody);
        widget.setOnDateChangedListener(this);
        int year = PrivateParams.getSPInt(getActivity().getApplicationContext(), Constant.DATA_DATE_YEAR, 0);
        int month = PrivateParams.getSPInt(getActivity().getApplicationContext(), Constant.DATA_DATE_MONTH, 0);
        int day = PrivateParams.getSPInt(getActivity().getApplicationContext(), Constant.DATA_DATE_DAY, 0);
        
        if (year == 0 || month == 0 || day == 0) {
            mOldTimeMilis = System.currentTimeMillis();
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String start= year + "-" + month + "-" + day;
            //得到毫秒数
            try {
                mOldTimeMilis=sdf.parse(start).getTime();
            } catch (java.text.ParseException e) {
                SLog.e(TAG, e);
            }
        }
        
        mDate = new Date(mOldTimeMilis);
        setWidgetDate(widget, mDate);

        mBackTodayBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                widget.setDateSelected(mDate , false);
                widget.refreshDrawableState();
                
                mTodayTimeMilis = System.currentTimeMillis();
                Date date = new Date(mTodayTimeMilis);
                setWidgetDate(widget, date);
               
                CalendarDay calendarDay = new CalendarDay(date);
                setPrivateCalendarDay(getActivity().getApplicationContext(), calendarDay);
                postDataTime(calendarDay);               
            }
        });
    }

    private void setWidgetDate(MaterialCalendarView widget, Date date) {
        widget.setDateSelected(date , true);
        widget.refreshDrawableState();
        widget.setCurrentDate(date);
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, 
            boolean selected) {
        setPrivateCalendarDay(getActivity().getApplicationContext(), date);
        postDataTime(date);
        dismiss();
    }
    

    private void postDataTime(CalendarDay date) {
        // TODO Auto-generated method stub
        DataTime dataTime = new DataTime();
        dataTime.year = date.getYear();
        dataTime.month = date.getMonth() + 1;
        dataTime.day = date.getDay();
        EventBus.getDefault().post(dataTime);  
    }

    private void setPrivateCalendarDay(Context context, CalendarDay date) {
        
        PrivateParams.setSPInt(context, Constant.DATA_DATE_YEAR, date.getYear());
        PrivateParams.setSPInt(context, Constant.DATA_DATE_MONTH, date.getMonth() + 1);
        PrivateParams.setSPInt(context, Constant.DATA_DATE_DAY, date.getDay());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

}
