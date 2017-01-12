package com.aizi.yingerbao;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.Toast;

public class CalendarActivity extends Activity {
    
    CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);
        
        calendarView = (CalendarView) findViewById(R.id.calendartest);
        calendarView.setBackgroundColor(Color.rgb(137, 230, 81));
        
        calendarView.setOnDateChangeListener(new OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month,
                    int dayOfMonth) {
                // TODO Auto-generated method stub
                String date = year + "年" + month + "月" + dayOfMonth + "日";
                Toast.makeText(getApplicationContext(), date, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
