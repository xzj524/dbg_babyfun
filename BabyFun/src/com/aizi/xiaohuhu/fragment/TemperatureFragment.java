package com.aizi.xiaohuhu.fragment;

import java.util.ArrayList;

import com.aizi.xiaohuhu.R;
import com.aizi.xiaohuhu.chart.TemperatureChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TemperatureFragment extends Fragment{
    
    private static final String TAG = TemperatureChart.class.getSimpleName();
    LineChart mTemperatureChart;

    static Typeface mTf; // 自定义显示字体  
    static int[] mColors = new int[] { Color.rgb(4, 158, 255), Color.rgb(240, 240, 30),//  
            Color.rgb(89, 199, 250), Color.rgb(250, 104, 104) ,Color.rgb(153, 134, 117)}; // 自定义颜色 
    static int[] mTextColors = new int[] {
        Color.rgb(9, 79, 55),
        Color.rgb(13, 89, 116),
        Color.rgb(16, 51, 116),
        Color.rgb(14, 39, 90)
    };
    
    static ArrayList<Entry> yValsTem = new ArrayList<Entry>();
    static ArrayList<String> xVals = new ArrayList<String>();


   @Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    View temperatureView = inflater.inflate(R.layout.activity_tab_temperature, container,false); 
    mTemperatureChart = (LineChart) temperatureView.findViewById(R.id.temperature_linechart);
    initTempStatus();
    return temperatureView;
}
   @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }
   
   private void initTempStatus() {
       if (yValsTem.size() > 0) {
           yValsTem.clear();
       }
       for (int i = 0; i < 24; i++) {
           if (i < 6) {
               yValsTem.add(new Entry((float) (70 - (Math.random() * 10)), i));
           } else if (i > 5 && i < 12) {
               yValsTem.add(new Entry((float) (50 - (Math.random() * 10)), i));
           }else if (i > 11 && i < 18) {
               yValsTem.add(new Entry((float) (60 - (Math.random() * 10)), i));
           }else if (i > 17 && i < 24) {
               yValsTem.add(new Entry((float) (70 - (Math.random() * 10)), i));
           }
           
       }
       
       
       if (xVals.size() > 0) {
           xVals.clear();
       }  
       for (int i = 0; i < 24; i++) {
           xVals.add(i + "");
       }
       
       LineDataSet SleepySet = new LineDataSet(yValsTem, null);
       SleepySet.setDrawCubic(true);
       SleepySet.setDrawValues(false);
       SleepySet.setDrawCircles(false);
       
       ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
       dataSets.add(SleepySet);
       LineData data = new LineData(xVals, dataSets);
       setupChart(data, mColors[4]);
   }
   
// 设置显示的样式  
   public void setupChart(LineData data, int color) {  
       // if enabled, the chart will always start at zero on the y-axis  
  
       XAxis xAxis = mTemperatureChart.getXAxis();
       xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
       
       
       YAxis leftAxis = mTemperatureChart.getAxisLeft();  //得到图表的左侧Y轴实例
       leftAxis.setDrawAxisLine(true);
       leftAxis.setDrawLabels(false);
       leftAxis.setAxisMaxValue(100); // 设置Y轴最大值
       leftAxis.setAxisMinValue(0);// 设置Y轴最小值。
       leftAxis.setStartAtZero(true);   //设置图表起点从0开始
       
       YAxis rightAxis = mTemperatureChart.getAxisRight();  //得到图表的右侧Y轴实例
       rightAxis.setDrawAxisLine(true);
       rightAxis.setDrawLabels(false);
       rightAxis.setAxisMaxValue(100); // 设置Y轴最大值
       rightAxis.setAxisMinValue(0);// 设置Y轴最小值。
       rightAxis.setStartAtZero(true);   //设置图表起点从0开始
 
       // no description text  
       mTemperatureChart.setDescription("");// 数据描述  
      
    
       // enable / disable grid background  
       mTemperatureChart.setDrawGridBackground(false); // 是否显示表格颜色  
      
       // enable touch gestures  
       mTemperatureChart.setTouchEnabled(true); // 设置是否可以触摸  
 
       mTemperatureChart.setDoubleTapToZoomEnabled(false);//设置双击不进行缩放
 
       // if disabled, scaling can be done on x- and y-axis separately  
       mTemperatureChart.setPinchZoom(false);//   
     
 
       mTemperatureChart.setBackgroundColor(color);// 设置背景  
       // add data  
       mTemperatureChart.setData(data); // 设置数据  
   
       // get the legend (only possible after setting data)  
       Legend l = mTemperatureChart.getLegend(); // 设置标示，就是那个一组y的value的  
       l.setEnabled(false);
       // animate calls invalidate()...  
       mTemperatureChart.animateX(2000); // 立即执行的动画,x轴  
   }  
}
