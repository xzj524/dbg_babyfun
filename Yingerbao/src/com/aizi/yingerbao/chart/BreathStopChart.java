package com.aizi.yingerbao.chart;

import java.util.ArrayList;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.aizi.yingerbao.R;

public class BreathStopChart extends Fragment{
    
    BarChart mBreathStopTimes;
    
    static ArrayList<BarEntry> yVals = new ArrayList<BarEntry>(); 
    static ArrayList<String> xVals = new ArrayList<String>();

    private static final String TAG = BreathStopChart.class.getSimpleName();
    
    static Typeface mTf; // 自定义显示字体  
    static int[] mColors = new int[] { 
            Color.rgb(4, 158, 255),
            Color.rgb(240, 240, 30),  
            Color.rgb(89, 199, 250), 
            Color.rgb(250, 104, 104),
            Color.rgb(222, 182, 180)}; // 自定义颜色 
    static int[] mTextColors = new int[] {
        Color.rgb(9, 79, 55),
        Color.rgb(13, 89, 116),
        Color.rgb(16, 51, 116),
        Color.rgb(14, 39, 90)
    };
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View breathstopChartView = inflater.inflate(R.layout.breath_stop_time, container, false);
        mBreathStopTimes = (BarChart) breathstopChartView.findViewById(R.id.breathstopbarchart);
       
        initBarChart(mBreathStopTimes);
        loadBarChartData(mBreathStopTimes);
        return breathstopChartView;
    }
    
    /**
     * 加载并设置柱形图的数据
     * @param chart
     */
    private void loadBarChartData(BarChart chart) {
        //所有数据点的集合
        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        
        for (int i = 0; i < 24; i++) {
            if (i > 13 && i < 18) {
                int yVlue = (int) (Math.random() * 30);
                entries.add(new BarEntry(yVlue, i));
            }
           
        }
        //柱形数据的集合
        BarDataSet mBarDataSet = new BarDataSet(entries,"barDataSet");
        mBarDataSet.setBarSpacePercent(20f);
        mBarDataSet.setHighLightAlpha(100);//设置点击后高亮颜色透明度
        mBarDataSet.setHighLightColor(Color.GRAY);
        
        //mBarDataSet.setColors(mColors);
        mBarDataSet.setColor(mColors[0]);
        //BarData表示挣个柱形图的数据
        BarData mBarData = new BarData(getXAxisShowLable(),mBarDataSet);
        chart.setData(mBarData);
        chart.animateY(1500);//设置动画
    }

    /**
     * 设置柱形图的样式
     * @param chart
     */
    private void initBarChart(BarChart chart) {
        chart.setDescription("");
        chart.setDrawGridBackground(false);//设置网格背景
        chart.setScaleEnabled(true);//设置缩放
        chart.setDoubleTapToZoomEnabled(false);//设置双击不进行缩放
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            
            @Override
            public void onValueSelected(Entry arg0, int arg1, Highlight arg2) {
                // TODO Auto-generated method stub
                //Toast.makeText(getActivity(), "value = " + arg0.getVal() + " index = " + arg0.getXIndex(), Toast.LENGTH_SHORT).show();
                //arg0.getVal();
            }
            
            @Override
            public void onNothingSelected() {
                // TODO Auto-generated method stub
                
            }
        });

        //设置X轴
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);

        //获得左侧侧坐标轴
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setLabelCount(5);

        //设置右侧坐标轴
        YAxis rightAxis = chart.getAxisRight();
//        rightAxis.setDrawAxisLine(false);//右侧坐标轴线
        rightAxis.setDrawLabels(false);//右侧坐标轴数组Lable
        rightAxis.setLabelCount(5);
        
        Legend mLegend = chart.getLegend(); // 设置标示，就是那个一组y的value的  
        mLegend.setEnabled(false);
    }
    
    private ArrayList<String> getXAxisShowLable() {
        ArrayList<String> m = new ArrayList<String>();
        for (int i = 0; i < 24; i++) {
            m.add(i+1+"");
        }
        return m;
    }

}
