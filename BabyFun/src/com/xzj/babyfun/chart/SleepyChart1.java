package com.xzj.babyfun.chart;

import com.github.mikephil.charting.charts.LineChart;
import com.xzj.babyfun.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SleepyChart1 extends Fragment{
    
    static LineChart mSleepyChart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View sleepyChartView = inflater.inflate(R.layout.sleepy_chart, container, false);
        mSleepyChart = (LineChart) sleepyChartView.findViewById(R.id.sleepychart);
        
        return sleepyChartView;
        //return super.onCreateView(inflater, container, savedInstanceState);
        
    }
}
