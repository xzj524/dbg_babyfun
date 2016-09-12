package com.aizi.xiaohuhu.chart;

import java.util.ArrayList;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.aizi.xiaohuhu.R;

public class BreathChart extends Fragment{
    
    static LineChart mBreathChart;
    
    private static final String TAG = BreathChart.class.getSimpleName();
    
    static Typeface mTf; // 自定义显示字体  
    static int[] mColors = new int[] {
        Color.rgb(137, 230, 81), 
        Color.rgb(240, 240, 30),//  
        Color.rgb(89, 199, 250), 
        Color.rgb(250, 104, 104), 
        Color.rgb(4, 158, 255),
        Color.rgb(222, 182, 180) }; // 自定义颜色 
    
    static ArrayList<Entry> yValsBreath = new ArrayList<Entry>();
    static ArrayList<String> xValsBreathTime = new ArrayList<String>();
    static LineDataSet BreathSet = new LineDataSet(yValsBreath, "清醒");
    
    static ArrayList<String> xVals = new ArrayList<String>(); //X轴数据
    LineData mData = new LineData();
    int xValCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View breathChartView = inflater.inflate(R.layout.breath_chart, container, false);
        mBreathChart = (LineChart) breathChartView.findViewById(R.id.breathchart);
        initBreathChart();
        LineData initData = initData(50);
        setupChart(initData, mColors[5]);
        return breathChartView;
    }
    
    public void freshChart() {
        // TODO Auto-generated method stub
        if (mData != null) {
            mData = initData(5);
            setupChart(mData, mColors[5]);
        }
    }
    
    public ArrayList<Entry> generateNewyVals(int val){
        
        ArrayList<Entry> tempValue = new ArrayList<Entry>();
        float entryval = 0;
        
        if (yValsBreath.size() <= 0) {
            return null;
        }
        for (int i = 1; i < yValsBreath.size(); i++) {
            Entry tmpEntry = yValsBreath.get(i);
            entryval = tmpEntry.getVal();
            tempValue.add(new Entry(entryval, i-1));
        }
        
        tempValue.add(new Entry(val, yValsBreath.size()-1));
        return tempValue;
    }
    
    public void generateNewWave(int val) {
        // TODO Auto-generated method stub   
        yValsBreath = generateNewyVals(val);
        if (yValsBreath != null) {
            //BreathSet.
            LineDataSet SleepySet = new LineDataSet(yValsBreath, null);
            setupLineDataSet(SleepySet);
            ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
            dataSets.add(SleepySet);    
            if (xVals.size() > 0) {
                xVals.clear();
            }  
            for (int i = 0; i < yValsBreath.size(); i++) {
                xVals.add(i + "");
            }
            LineData data = new LineData(xVals, dataSets);    
            setupChart(data, mColors[5]);
        }   
    }
    
    private void setupLineDataSet(LineDataSet DataSet) {
        // TODO Auto-generated method stub
        DataSet.setLineWidth(1.75f); // 线宽  
        DataSet.setColor(Color.WHITE);// 显示颜色  
        DataSet.setHighLightColor(Color.WHITE); // 高亮的线的颜色  
        DataSet.setDrawCubic(true);
        DataSet.setDrawCircles(false);
        DataSet.setDrawValues(false);
    }

    private LineData initData(int count) {
        
        if (xVals.size() > 0) {
            xVals.clear();
        }  
        for (int i = 0; i < count; i++) {
            xVals.add(i + "");
        }
        
        if (yValsBreath.size() > 0) {
            yValsBreath.clear();
        }
        
        for (int i = 0; i < count; i++) {
            yValsBreath.add(new Entry(0, i));
        }

        LineDataSet SleepySet = new LineDataSet(yValsBreath, null);
        setupLineDataSet(SleepySet);
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(SleepySet);
        LineData data = new LineData(xVals, dataSets);
        return data;
    }
    
    // 设置显示的样式  
    public static void setupChart(LineData data, int color) {  
      
        mBreathChart.clear();
        // add data  
        mBreathChart.setData(data); // 设置数据  
    }  

    private void initBreathChart() {
        
        //X轴设置
        XAxis xAxis = mBreathChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setEnabled(false);
        xAxis.setGridColor(Color.WHITE);

        //左侧Y轴设置
        YAxis leftAxis = mBreathChart.getAxisLeft();  //得到图表的左侧Y轴实例
        leftAxis.setDrawAxisLine(true);
        //leftAxis.setAxisLineColor(Color.RED);
        leftAxis.setAxisMaxValue(30); // 设置Y轴最大值
        leftAxis.setAxisMinValue(0);// 设置Y轴最小值。
        leftAxis.setStartAtZero(false);   //设置图表起点从0开始
//        leftAxis.enableGridDashedLine(10f, 10f, 0f); //设置横向表格为虚线
        leftAxis.setDrawLabels(false);
        leftAxis.setGridColor(Color.WHITE);
        
        //右侧Y轴设置
        YAxis rightAxis = mBreathChart.getAxisRight();
        rightAxis.setAxisMaxValue(30); // 设置Y轴最大值
        rightAxis.setAxisMinValue(0);// 设置Y轴最小值。
        rightAxis.setStartAtZero(false);   //设置图表起点从0开始
        rightAxis.setDrawLabels(false);
       
  
        // no description text  
        mBreathChart.setDescription("");// 数据描述  
        // 如果没有数据的时候，会显示这个，类似listview的emtpyview  
        //mBreathChart.setNoDataTextDescription("You need to provide data for the chart.");   
        // enable / disable grid background  

        mBreathChart.setDrawGridBackground(false); // 是否显示表格颜色 
        mBreathChart.setGridBackgroundColor(mColors[5]);
        
        mBreathChart.setDrawBorders(false);
    //    mBreathChart.setBorderPositions(new BorderPosition[] { BorderPosition.BOTTOM, BorderPosition.LEFT });// 设置图标边框
        mBreathChart.setBorderColor(Color.WHITE);
        mBreathChart.setBorderWidth(2);
       
        // enable touch gestures  
        mBreathChart.setTouchEnabled(false); // 设置是否可以触摸  
  
        // enable scaling and dragging  
        mBreathChart.setDragEnabled(false);// 是否可以拖拽  
        mBreathChart.setScaleEnabled(false);// 是否可以缩放  
  
        // if disabled, scaling can be done on x- and y-axis separately  
        mBreathChart.setPinchZoom(false);    
        
        mBreathChart.setBackgroundColor(mColors[5]);// 设置背景  
        
        // get the legend (only possible after setting data)  
        Legend breathLegend = mBreathChart.getLegend(); // 设置标示，就是那个一组y的value的  

        // modify the legend ...  
        breathLegend.setForm(LegendForm.SQUARE);// 样式  
        breathLegend.setFormSize(6f);// 字体  
        breathLegend.setTextColor(Color.WHITE);// 颜色  
        breathLegend.setTypeface(mTf);// 字体  
        // animate calls invalidate()...  
        mBreathChart.animateX(100); // 立即执行的动画,x轴  
    }

  
}
