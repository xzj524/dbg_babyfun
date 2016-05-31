package com.xzj.babyfun.chart;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
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
import com.xzj.babyfun.R;
import com.xzj.babyfun.utility.Utiliy;

public class SleepyChart extends Fragment{
    
    static LineChart mChart;
    private static final String TAG = SleepyChart.class.getSimpleName();
    
    static Typeface mTf; // 自定义显示字体  
    static int[] mColors = new int[] { Color.rgb(137, 230, 81), Color.rgb(240, 240, 30),//  
            Color.rgb(89, 199, 250), Color.rgb(250, 104, 104) }; // 自定义颜色 
    
    /*static ArrayList<Entry> yVals1 = new ArrayList<Entry>(); 
    static ArrayList<Entry> yVals2 = new ArrayList<Entry>(); */
    static ArrayList<Entry> yValsNoSleep = new ArrayList<Entry>();
    static ArrayList<Entry> yValsShadowSleep = new ArrayList<Entry>();
    static ArrayList<Entry> yValsDeepSleep = new ArrayList<Entry>();
    static ArrayList<Entry> yValsSleep = new ArrayList<Entry>();
    
 //   static ArrayList<ArrayList<Entry>> yArrayList = new ArrayList<ArrayList<Entry>>();
    
    static ArrayList<String> xVals = new ArrayList<String>();
  //  static ArrayList<Integer> chartType = new ArrayList<Integer>();
    
    static LineDataSet NoSleepySet = new LineDataSet(yValsNoSleep, "清醒");
    static LineDataSet ShadowSleepySet = new LineDataSet(yValsShadowSleep, "浅睡");
    static LineDataSet DeepSleepySet = new LineDataSet(yValsDeepSleep, "深睡");
    
   // static LineDataSet SleepySet1 = new LineDataSet(yVals1, "温度");  
   // static LineDataSet SleepySet2 = new LineDataSet(yVals2, "温度"); 
    static int mDatalength = 0;
    
    SetTextViewListener mListener;
    
    public interface SetTextViewListener{  
        public void SetBabyStatus(String str);  
        public void SetBabyPoint(String str);
    } 
    
    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        
        mListener =(SetTextViewListener)activity; 
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        
        View lineChartView = inflater.inflate(R.layout.chart_fragment, container, false);
        mChart = (LineChart) lineChartView.findViewById(R.id.linechart);
       // mChart.
        initDataSet();
        
        return lineChartView;
    }
    
    // 设置显示的样式  
    public static void setupChart(LineData data, int color) {  
        // if enabled, the chart will always start at zero on the y-axis  
   
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setEnabled(false);
        
        
        YAxis leftAxis = mChart.getAxisLeft();  //得到图表的左侧Y轴实例
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawLabels(true);
        //leftAxis.setAxisLineColor(Color.RED);
        leftAxis.setAxisMaxValue(100); // 设置Y轴最大值
        leftAxis.setAxisMinValue(0);// 设置Y轴最小值。
        leftAxis.setStartAtZero(false);   //设置图表起点从0开始
        leftAxis.enableGridDashedLine(10f, 10f, 0f); //设置横向表格为虚线
        
        
        YAxis rightAxis = mChart.getAxisRight();
        //rightAxis.setDrawLabels(false);
        rightAxis.setAxisMaxValue(100); // 设置Y轴最大值
        rightAxis.setAxisMinValue(0);// 设置Y轴最小值。
        rightAxis.setStartAtZero(false);   //设置图表起点从0开始
       
  
        // no description text  
        mChart.setDescription("");// 数据描述  
        // 如果没有数据的时候，会显示这个，类似listview的emtpyview  
        mChart.setNoDataTextDescription("You need to provide data for the chart.");  
   
        
  
        // enable / disable grid lines  
       
        // mChart.setDrawHorizontalGrid(false);  
        //  
        // enable / disable grid background  
        mChart.setDrawGridBackground(false); // 是否显示表格颜色  
       
        // enable touch gestures  
        mChart.setTouchEnabled(true); // 设置是否可以触摸  
  
        // enable scaling and dragging  
        mChart.setDragEnabled(true);// 是否可以拖拽  
        mChart.setScaleEnabled(true);// 是否可以缩放  
  
        // if disabled, scaling can be done on x- and y-axis separately  
        mChart.setPinchZoom(false);//   
      
  
        mChart.setBackgroundColor(color);// 设置背景  
        // add data  
        mChart.setData(data); // 设置数据  
    
        // get the legend (only possible after setting data)  
        Legend l = mChart.getLegend(); // 设置标示，就是那个一组y的value的  
        
       // ArrayList<String> strlist = new ArrayList<String>();
        
       // strlist.add("hh");
       // strlist.add("ee");
  
        // modify the legend ...  
       // l.setPosition(LegendPosition.LEFT_OF_CHART);  
        l.setForm(LegendForm.SQUARE);// 样式  
        l.setFormSize(6f);// 字体  
        l.setTextColor(Color.WHITE);// 颜色  
        l.setTypeface(mTf);// 字体  
       // l.setLabels(strlist);
        // animate calls invalidate()...  
        mChart.animateX(100); // 立即执行的动画,x轴  
    }  
  
    // 生成一个数据，  
    public static LineData getData(int count) {  
        LineDataSet lineDataSet;
        
        Log.e(TAG, "  count = " + count);
       // int sleepValue = Math.abs(Math.abs(count - 50) * 2 - 100);
        
        if (Utiliy.mSleepList.size() > 24) {
            Utiliy.mSleepList.clear();
            Utiliy.mSleepList.add(0);
        }
        mDatalength = Utiliy.mSleepList.size();
            /*mDatalength++;
            yValsSleep.add(new Entry(sleepValue, mDatalength));
            xVals.add(mDatalength + "");*/
            if (yValsSleep.size() > 0) {
                yValsSleep.clear();
            }
            for (int i = 0; i < mDatalength; i++) {
                //int sleepvalue = Utiliy.mSleepList.get(i);
                int sleepValue = Math.abs(Utiliy.mSleepList.get(i) - 50);
                yValsSleep.add(new Entry(Math.abs(sleepValue * 2 - 100), i));
            }
            if (xVals.size() > 0) {
                xVals.clear();
            }  
            for (int i = 0; i < mDatalength; i++) {
                xVals.add(i + "");
            }
            
            LineDataSet SleepySet = new LineDataSet(yValsSleep, null);
            //SleepySet.setLineWidth(1.75f); // 线宽  
            //SleepySet.setCircleSize(3f);// 显示的圆形大小  
            //SleepySet.setColor(Color.WHITE);// 显示颜色  
            //SleepySet.setCircleColor(Color.WHITE);// 圆形的颜色  
            //SleepySet.setHighLightColor(Color.WHITE); // 高亮的线的颜色  
            //NoSleepySet.setFillColor(Color.rgb(205, 205, 205));  
            
            //SleepySet.setFillAlpha(255);
           // SleepySet.setDrawFilled(true);
            SleepySet.setDrawCubic(true);
            SleepySet.setDrawValues(false);
            
            ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
            dataSets.add(SleepySet);
            LineData data = new LineData(xVals, dataSets);
            
      
             
            return data;  
     
    }
    
    private static LineDataSet getDataSet(int type, ArrayList<Entry> Vals) {
        LineDataSet dataSet = null;
        switch (type) {
        case 0:
            dataSet = new LineDataSet(Vals, "清醒");
            dataSet.setFillColor(Color.RED);
            break;
        case 1:
            dataSet = new LineDataSet(Vals, "浅睡");
            dataSet.setFillColor(Color.YELLOW);
            break;
        case 2:
            dataSet = new LineDataSet(Vals, "深睡");
            dataSet.setFillColor(Color.GREEN);
            break;

        default:
            break;
        }
        
        dataSet.setLineWidth(1.75f); // 线宽  
        dataSet.setCircleSize(3f);// 显示的圆形大小  
        dataSet.setColor(Color.WHITE);// 显示颜色  
        dataSet.setCircleColor(Color.WHITE);// 圆形的颜色  
        dataSet.setHighLightColor(Color.WHITE); // 高亮的线的颜色  
        //NoSleepySet.setFillColor(Color.rgb(205, 205, 205));  
        
        dataSet.setFillAlpha(255);
        dataSet.setDrawFilled(true);
        dataSet.setDrawCubic(true);
        dataSet.setDrawValues(false);
        
        return dataSet;
    }
    
    private void initDataSet(){
        NoSleepySet.setLineWidth(1.75f); // 线宽  
        NoSleepySet.setCircleSize(3f);// 显示的圆形大小  
        NoSleepySet.setColor(Color.WHITE);// 显示颜色  
        NoSleepySet.setCircleColor(Color.WHITE);// 圆形的颜色  
        NoSleepySet.setHighLightColor(Color.WHITE); // 高亮的线的颜色  
        //NoSleepySet.setFillColor(Color.rgb(205, 205, 205));  
        NoSleepySet.setFillColor(Color.RED);
        NoSleepySet.setFillAlpha(255);
        NoSleepySet.setDrawFilled(true);
        NoSleepySet.setDrawCubic(true);
        NoSleepySet.setDrawValues(false);
   
        
        ShadowSleepySet.setLineWidth(1.75f); // 线宽  
        ShadowSleepySet.setCircleSize(3f);// 显示的圆形大小  
        ShadowSleepySet.setColor(Color.WHITE);// 显示颜色  
        ShadowSleepySet.setCircleColor(Color.WHITE);// 圆形的颜色  
        ShadowSleepySet.setHighLightColor(Color.WHITE); // 高亮的线的颜色  
        //ShadowSleepySet.setFillColor(Color.rgb(114, 188, 223));  
        ShadowSleepySet.setFillColor(Color.YELLOW);
        ShadowSleepySet.setFillAlpha(255);
        ShadowSleepySet.setDrawFilled(true);
        ShadowSleepySet.setDrawCubic(true);
        ShadowSleepySet.setDrawValues(false);
        
        DeepSleepySet.setLineWidth(1.75f); // 线宽  
        DeepSleepySet.setCircleSize(3f);// 显示的圆形大小  
        DeepSleepySet.setColor(Color.WHITE);// 显示颜色  
        DeepSleepySet.setCircleColor(Color.WHITE);// 圆形的颜色  
        DeepSleepySet.setHighLightColor(Color.WHITE); // 高亮的线的颜色  
       // DeepSleepySet.setFillColor(Color.rgb(255, 123, 124));  
        DeepSleepySet.setFillColor(Color.GREEN);
        DeepSleepySet.setFillAlpha(255);
        DeepSleepySet.setDrawFilled(true);
        DeepSleepySet.setDrawCubic(true);
        DeepSleepySet.setDrawValues(false);
    }

}
