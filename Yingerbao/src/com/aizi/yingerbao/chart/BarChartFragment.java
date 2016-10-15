package com.aizi.yingerbao.chart;

import java.util.ArrayList;


import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.aizi.yingerbao.R;

public class BarChartFragment extends Fragment{
    
    static BarChart mBarChart;
    static Typeface mTf; // 自定义显示字体
   // static int[] mColors = new int[]{} ;
    ArrayList<Integer> mColors = new ArrayList<Integer>();
    
    static ArrayList<BarEntry> yVals = new ArrayList<BarEntry>(); 
    static ArrayList<String> xVals = new ArrayList<String>();
    static int mDatalength = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View barChartView = inflater.inflate(R.layout.barchart_fragment, container, false);
        mBarChart = (BarChart) barChartView.findViewById(R.id.barchart);
        setBarChart(mBarChart);
        loadBarChartData(mBarChart);
        
        //mBarChart.
        
        return barChartView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
    
    // 设置显示的样式  
    public static void setupChart(BarData data, int color) {  
        // if enabled, the chart will always start at zero on the y-axis  
        
   
        XAxis xAxis = mBarChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        
        YAxis leftAxis = mBarChart.getAxisLeft();  //得到图表的左侧Y轴实例
        leftAxis.setAxisMaxValue(100); // 设置Y轴最大值
        leftAxis.setAxisMinValue(0);// 设置Y轴最小值。
        leftAxis.setStartAtZero(false);   //设置图表起点从0开始
        //leftAxis.enableGridDashedLine(10f, 10f, 0f); //设置横向表格为虚线
        
        YAxis rightAxis = mBarChart.getAxisRight();
        rightAxis.setDrawAxisLine(false);//右侧坐标轴线
        rightAxis.setDrawLabels(false);//右侧坐标轴数组Lable
        //rightAxis.setDrawLabels(false);
/*       rightAxis.setAxisMaxValue(40); // 设置Y轴最大值
        rightAxis.setAxisMinValue(0);// 设置Y轴最小值。
*/        
       // rightAxis.s;   //设置图表起点从0开始    
  
        // no description text  
        mBarChart.setDescription("");// 数据描述  
        // 如果没有数据的时候，会显示这个，类似listview的emtpyview  
        mBarChart.setNoDataTextDescription("You need to provide data for the chart.");  
           // enable / disable grid background  
        mBarChart.setDrawGridBackground(false); // 是否显示表格颜色  
       
        // enable touch gestures  
        mBarChart.setTouchEnabled(true); // 设置是否可以触摸  
  
        // enable scaling and dragging  
        mBarChart.setDragEnabled(false);// 是否可以拖拽  
        mBarChart.setScaleEnabled(false);// 是否可以缩放  
  
        // if disabled, scaling can be done on x- and y-axis separately  
        mBarChart.setPinchZoom(false);//   
  
        mBarChart.setBackgroundColor(color);// 设置背景  
        // add data  
        mBarChart.setData(data); // 设置数据  
        // get the legend (only possible after setting data)  
        Legend l = mBarChart.getLegend(); // 设置标示，就是那个一组y的value的  
  
        // modify the legend ...  
        // l.setPosition(LegendPosition.LEFT_OF_CHART);  
        l.setForm(LegendForm.CIRCLE);// 样式  
        l.setFormSize(6f);// 字体  
        l.setTextColor(Color.WHITE);// 颜色  
        l.setTypeface(mTf);// 字体  

        // animate calls invalidate()...  
        mBarChart.animateX(100); // 立即执行的动画,x轴  
    }  
  
    // 生成一个数据，  
    public static BarData getData(int count) {  
        
        //String[] mMonths = {"1","2","44","5","6","7","8","9"};
        for (int i = 0; i < count; i++) {
            yVals.add(new BarEntry(i, i)); 
        }
        
        for (int i = 0; i < count; i++) {
            xVals.add(i + ""); 
        }
        
       
       // mDatalength++;
        // create a dataset and give it a type  
        // y轴的数据集合  
        BarDataSet set1 = new BarDataSet(yVals, "睡眠");  
        set1.setColor(Color.WHITE);// 显示颜色  
        set1.setHighLightColor(Color.WHITE); // 高亮的线的颜色  
  
        ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();  
        dataSets.add(set1); // add the datasets  
  
        // create a data object with the datasets  
        BarData data = new BarData(xVals, dataSets);  
  
        return data;  
    }
    
    
    /**
     * 加载并设置柱形图的数据
     * @param chart
     */
    private void loadBarChartData(BarChart chart) {
        //所有数据点的集合
        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        
        for (int i = 0; i < 12; i++) {
            
        }
        for (int i = 0; i < 4; i++) {
            int yVlue = (int) (Math.random() * 70) + 30;
            entries.add(new BarEntry(yVlue, i));
            if (yVlue > 50) {
                mColors.add(Color.rgb(255, 0, 0)); 
            } else {
                mColors.add(Color.rgb(255, 255, 255));    
            }
        }
        //柱形数据的集合
        BarDataSet mBarDataSet = new BarDataSet(entries,"barDataSet");
        mBarDataSet.setBarSpacePercent(20f);
        mBarDataSet.setHighLightAlpha(100);//设置点击后高亮颜色透明度
        mBarDataSet.setHighLightColor(Color.GRAY);
        
       /* mColors.add(Color.rgb(205, 205, 205));    
        mColors.add(Color.rgb(114, 188, 223));    
        mColors.add(Color.rgb(255, 123, 124));    
        mColors.add(Color.rgb(57, 135, 200));   */
        mBarDataSet.setColors(mColors);
        //BarData表示挣个柱形图的数据
        BarData mBarData = new BarData(getXAxisShowLable(),mBarDataSet);
        chart.setData(mBarData);
        chart.animateY(1500);//设置动画
    }

    /**
     * 设置柱形图的样式
     * @param chart
     */
    private void setBarChart(BarChart chart) {
        chart.setDescription("Glan");
        chart.setDrawGridBackground(false);//设置网格背景
        chart.setScaleEnabled(true);//设置缩放
        chart.setDoubleTapToZoomEnabled(false);//设置双击不进行缩放
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            
            @Override
            public void onValueSelected(Entry arg0, int arg1, Highlight arg2) {
                // TODO Auto-generated method stub
                Toast.makeText(getActivity(), "value = " + arg0.getVal() + " index = " + arg0.getXIndex(), Toast.LENGTH_SHORT).show();
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
//        xAxis.setTypeface(mTf);//设置字体
        
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);

        //获得左侧侧坐标轴
        YAxis leftAxis = chart.getAxisLeft();
//        leftAxis.setTypeface(mTf);
        leftAxis.setLabelCount(5);
//        leftAxis.setAxisLineWidth(1.5f);

        //设置右侧坐标轴
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawAxisLine(false);//右侧坐标轴线
        rightAxis.setDrawLabels(false);//右侧坐标轴数组Lable
//        rightAxis.setTypeface(mTf);
//        rightAxis.setLabelCount(5);
//        rightAxis.setDrawGridLines(false);
    }
    
    private ArrayList<String> getXAxisShowLable() {
        ArrayList<String> m = new ArrayList<String>();
        m.add("9");
        m.add("11");
        m.add("13");
        m.add("15");
        m.add("17");
        m.add("19");
        m.add("21");
        m.add("23");
        m.add("1");
        m.add("3");
        m.add("5");
        m.add("7");
        m.add("9");
        return m;
    }

}
