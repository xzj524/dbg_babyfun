package com.aizi.yingerbao.chart;

import java.util.ArrayList;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.aizi.yingerbao.BabyBreathActivity;
import com.aizi.yingerbao.BabyExplainActivity;
import com.aizi.yingerbao.R;
import com.aizi.yingerbao.SleepAnalysisActivity;
import com.aizi.yingerbao.utility.SleepyInfo;

public class SleepyPieChart extends Fragment{
    
    ViewGroup mSleepEfficiency;
    ViewGroup mSleepBreathStop;
    ViewGroup mSleepLightSleep;
    static PieChart mPieChart;
    private static final String TAG = SleepyPieChart.class.getSimpleName();
    static int[] mTextColors = new int[] {
        Color.rgb(110, 215, 217),
        Color.rgb(53, 199, 202),
        Color.rgb(255, 185, 188),
        Color.rgb(233, 103, 39)
    };
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        
        View pieChartView = inflater.inflate(R.layout.sleepy_pie_chart, container, false);
        mPieChart = (PieChart) pieChartView.findViewById(R.id.sleepypiechart);
        
        mSleepEfficiency = (ViewGroup) pieChartView.findViewById(R.id.sleepefficiency);
        mSleepBreathStop = (ViewGroup) pieChartView.findViewById(R.id.breathstop);
        mSleepLightSleep = (ViewGroup) pieChartView.findViewById(R.id.lightsleeps);
        mSleepEfficiency.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity().getApplicationContext(), BabyExplainActivity.class);
                intent.putExtra("title", "睡眠效率");
                intent.putExtra("content", "睡眠效率是指除清醒外的睡眠时间占躺床上睡觉总时间的百发比。");
                startActivity(intent);
            }   
        });
        
        mSleepBreathStop.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity().getApplicationContext(), BabyExplainActivity.class);
                intent.putExtra("title", "呼吸暂停");
                intent.putExtra("content", "呼吸暂停问题");
                startActivity(intent);
            }   
        });
 
        mSleepLightSleep.setOnClickListener(new View.OnClickListener() {
     
     @Override
     public void onClick(View v) {
         // TODO Auto-generated method stub
         Intent intent = new Intent(getActivity().getApplicationContext(), SleepAnalysisActivity.class);
         intent.putExtra("title", "睡眠分析");
         intent.putExtra("content", "睡眠分析问题");
         startActivity(intent);
     }   
 });
        
        
        //initPieChart();
        return pieChartView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void initPieChart() {
        // TODO Auto-generated method stub
        
       // PieData mPieData = getPieData(4, 100);    
        //showChart(mPieChart, mPieData); 
        
    }
    
    private void showChart(PieChart pieChart, PieData pieData) {    
        pieChart.setHoleColorTransparent(true);    

        pieChart.setHoleRadius(40f);  //半径    
        pieChart.setTransparentCircleRadius(0f); // 半透明圈    
        //pieChart.setHoleRadius(0)  //实心圆    
    
    
        // mChart.setDrawYValues(true);    
        pieChart.setDrawCenterText(true);  //饼状图中间可以添加文字    
    
        pieChart.setDrawHoleEnabled(true);    
    
        pieChart.setRotationAngle(90); // 初始旋转角度    
    
        // draws the corresponding description value into the slice    
        // mChart.setDrawXValues(true);    
    
        // enable rotation of the chart by touch    
        pieChart.setRotationEnabled(false); // 可以手动旋转    
    
        // display percentage values    
        pieChart.setUsePercentValues(true);  //显示成百分比    
        // mChart.setUnit(" €");    
        // mChart.setDrawUnitsInChart(true);    
    
        // add a selection listener    
//      mChart.setOnChartValueSelectedListener(this);    
        // mChart.setTouchEnabled(false);    
   
//      mChart.setOnAnimationListener(this); 
        pieChart.setDescription(null);
        pieChart.setCenterText("宝宝睡眠分布");  //饼状图中间的文字 
        pieChart.setCenterTextColor(Color.WHITE);
    
        //设置数据    
        pieChart.setData(pieData);     
        Legend mLegend = pieChart.getLegend();  //设置比例图    
        mLegend.setEnabled(false);
            
        pieChart.animateXY(1000, 1000);  //设置动画       
    }    
    
    /**  
     *   
     * @param count 分成几部分  
     * @param range  
     */    
    private PieData getPieData(float sober, float fallsleep, float lightsleep, float deepsleep) {    
            
        ArrayList<String> xValues = new ArrayList<String>();  //xVals用来表示每个饼块上的内容    
     
        
        xValues.add("清醒");
        xValues.add("入睡");
        xValues.add("浅睡");
        xValues.add("深睡");

        ArrayList<Entry> yValues = new ArrayList<Entry>();  //yVals用来表示封装每个饼块的实际数据    
    
        // 饼图数据    
        /**  
         * 将一个饼形图分成四部分， 四部分的数值比例为14:14:34:38  
         * 所以 14代表的百分比就是14%   
         */    
        float quarterly1 = sober;    
        float quarterly2 = fallsleep;    
        float quarterly3 = lightsleep;    
        float quarterly4 = deepsleep;    
    
        yValues.add(new Entry(quarterly1, 0));    
        yValues.add(new Entry(quarterly2, 1));    
        yValues.add(new Entry(quarterly3, 2));    
        yValues.add(new Entry(quarterly4, 3));    
    
        //y轴的集合    
        PieDataSet pieDataSet = new PieDataSet(yValues, null);    
        pieDataSet.setSliceSpace(0f); //设置个饼状图之间的距离    
        
    
        ArrayList<Integer> colors = new ArrayList<Integer>();    
    
        // 饼图颜色    
        colors.add(mTextColors[0]); 
        colors.add(mTextColors[1]); 
        colors.add(mTextColors[2]); 
        colors.add(mTextColors[3]);    

        pieDataSet.setColors(colors);    
    
        DisplayMetrics metrics = getResources().getDisplayMetrics();    
        float px = 5 * (metrics.densityDpi / 160f);    
        pieDataSet.setSelectionShift(px); // 选中态多出的长度    
    
        PieData pieData = new PieData(xValues, pieDataSet);    
        pieData.setValueFormatter(new PercentFormatter());
        pieData.setValueTextSize(10);
        pieData.setValueTextColor(Color.WHITE);
        return pieData;    
    }
    
    
    public void onEvent(SleepyInfo event) {  
        
        /*// String msg = "onEventMainThread收到了消息：" + event.getMsg();  
         Log.d("harvic", msg);  
        // tv.setText(msg);  */
        // Toast.makeText(this, "enventbus write bytes", Toast.LENGTH_SHORT).show(); 
        // mService.writeBaseRXCharacteristic(event.getByte());
        
        setPieChart(event.sober, event.fallsleep, event.lightsleep, event.deepsleep);
     } 
    
    

    public void setPieChart(float sober, float fallsleep, float lightsleep, float deepsleep) {
        // TODO Auto-generated method stub
      PieData mPieData = getPieData(sober, fallsleep, lightsleep, deepsleep);    
      showChart(mPieChart, mPieData); 
    }    

}
