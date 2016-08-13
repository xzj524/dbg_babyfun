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
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.xzj.babyfun.R;
import com.xzj.babyfun.utility.SleepyInfo;
import com.xzj.babyfun.utility.Utiliy;

public class SleepyChart extends Fragment{
    
    static LineChart mChart;
    private static final String TAG = SleepyChart.class.getSimpleName();
    
    static Typeface mTf; // 自定义显示字体  
    static int[] mColors = new int[] { Color.rgb(4, 158, 255), Color.rgb(240, 240, 30),//  
            Color.rgb(89, 199, 250), Color.rgb(250, 104, 104) ,Color.rgb(153, 134, 117)}; // 自定义颜色 
    static int[] mTextColors = new int[] {
        Color.rgb(9, 79, 55),
        Color.rgb(13, 89, 116),
        Color.rgb(16, 51, 116),
        Color.rgb(14, 39, 90)
    };

    TextView mTextSober;
    TextView mTextFallSleep;
    TextView mTextLightSleep;
    TextView mTextDeepSleep;
    
    TextView mSoberText;
    TextView mFallSleepText;
    TextView mLightSleepText;
    TextView mDeepSleepText;
    
    TextView mSoberPercent;
    TextView mFallSleepPercent;
    TextView mLightSleepPercent;
    TextView mDeepSleepPercent;
    
    float mSoberCount;
    float mFallSleepCount;
    float mLightSleepCount;
    float mDeepSleepCount;
    
    
    static ArrayList<Entry> yValsNoSleep = new ArrayList<Entry>();
    static ArrayList<Entry> yValsShadowSleep = new ArrayList<Entry>();
    static ArrayList<Entry> yValsDeepSleep = new ArrayList<Entry>();
    static ArrayList<Entry> yValsSleep = new ArrayList<Entry>();
    
    static ArrayList<String> xVals = new ArrayList<String>();
    static LineDataSet NoSleepySet = new LineDataSet(yValsNoSleep, "清醒");
    static LineDataSet ShadowSleepySet = new LineDataSet(yValsShadowSleep, "浅睡");
    static LineDataSet DeepSleepySet = new LineDataSet(yValsDeepSleep, "深睡");
    
    static int mDatalength = 0;
    
    SetTextViewListener mListener;
    
    public interface SetTextViewListener{  
        public void SetBabyStatus(String str);  
        public void SetBabyPoint(String str);
        public void SetPieChart(float sober, float fallsleep,
                float lightsleep, float deepsleep);
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
        mChart = (LineChart) lineChartView.findViewById(R.id.sleeplinechart);
        mTextSober = (TextView) lineChartView.findViewById(R.id.sleepylist1);
        mTextFallSleep = (TextView) lineChartView.findViewById(R.id.sleepylist2);
        mTextLightSleep = (TextView) lineChartView.findViewById(R.id.sleepylist3);
        mTextDeepSleep = (TextView) lineChartView.findViewById(R.id.sleepylist4);
        
        mSoberText = (TextView) lineChartView.findViewById(R.id.sobertext);
        mFallSleepText = (TextView) lineChartView.findViewById(R.id.fallsleeptext);
        mLightSleepText = (TextView) lineChartView.findViewById(R.id.lightsleeptext);
        mDeepSleepText = (TextView) lineChartView.findViewById(R.id.deepsleeptext);
        
        mSoberPercent = (TextView) lineChartView.findViewById(R.id.soberpercent);
        mFallSleepPercent = (TextView) lineChartView.findViewById(R.id.fallsleeppercent);
        mLightSleepPercent = (TextView) lineChartView.findViewById(R.id.lightsleeppercent);
        mDeepSleepPercent = (TextView) lineChartView.findViewById(R.id.deepsleeppercent);
        
        initColors();
        initDataSet();
        initSleepStatus();
        
        for (int i = 0; i < 48; i++) {
            float slpvalue = yValsSleep.get(i).getVal();
            
            if (slpvalue < 25) {
                mDeepSleepCount++;
            } else if (slpvalue >= 25 && slpvalue < 50) {
                mLightSleepCount++;
            } else if (slpvalue >= 50 && slpvalue < 75) {
                mFallSleepCount++;
            } else if (slpvalue >= 75 && slpvalue < 100) {
                mSoberCount++;
            }
        }
        
        setSleepyPercent(mSoberCount, mFallSleepCount, mLightSleepCount, mDeepSleepCount);
        
        
      //  EventBus.getDefault().register(this);
        return lineChartView;
    }
    
    public void onEvent() {
        
    }
    
    private void initColors() {
        mTextSober.setTextColor(mTextColors[0]);
        mTextFallSleep.setTextColor(mTextColors[1]);
        mTextLightSleep.setTextColor(mTextColors[2]);
        mTextDeepSleep.setTextColor(mTextColors[3]);
        
        mSoberText.setTextColor(mTextColors[0]);
        mFallSleepText.setTextColor(mTextColors[1]);
        mLightSleepText.setTextColor(mTextColors[2]);
        mDeepSleepText.setTextColor(mTextColors[3]);
    }
    
    private void setSleepyPercent(float sober, float fallsleep, float lighsleep, float deepsleep){
        mSoberPercent.setText(Math.round((sober/48) * 100) + "%");
        mFallSleepPercent.setText(Math.round((fallsleep/48) * 100) + "%");
        mLightSleepPercent.setText(Math.round((lighsleep/48) * 100) + "%");
        mDeepSleepPercent.setText(Math.round((deepsleep/48) * 100) + "%");
        
        mListener.SetPieChart(Math.round((sober/48) * 100), 
                Math.round((fallsleep/48) * 100),
                Math.round((lighsleep/48) * 100),
                Math.round((deepsleep/48) * 100));
        
        SleepyInfo slpInfo = new SleepyInfo(Math.round((sober/48) * 100), 
                Math.round((fallsleep/48) * 100),
                Math.round((lighsleep/48) * 100),
                Math.round((deepsleep/48) * 100));
        
      //  EventBus.getDefault().post(slpInfo);
        
    }

    private void initSleepStatus() {
        if (yValsSleep.size() > 0) {
            yValsSleep.clear();
        }
        for (int i = 0; i < 48; i++) {
            
            if (i < 6) {
                yValsSleep.add(new Entry((float) (80 - (Math.random() * 10)), i));
            } else if (i > 5 && i < 12) {
                yValsSleep.add(new Entry((float) (75 - (Math.random() * 5)), i));
            } else if (i > 11 && i < 18) {
                yValsSleep.add(new Entry((float) (56 - (Math.random() * 5)), i));
            } else if (i > 17 && i < 30) {
                yValsSleep.add(new Entry((float) (20 - (Math.random() * 10)), i));
            } else if (i > 29 && i < 36) {
                yValsSleep.add(new Entry((float) (48 - (Math.random() * 10)), i));
            }else if (i > 35 && i < 42) {
                yValsSleep.add(new Entry((float) (64 - (Math.random() * 5)), i));
            } else if (i > 41 && i < 48) {
                yValsSleep.add(new Entry((float) (78 - (Math.random() * 10)), i));
            }
            
            
         /*   if (i < 12) {
                yValsSleep.add(new Entry((float) (100 - (Math.random() * 10)), i));
            } else if (i > 11 && i < 24) {
                yValsSleep.add(new Entry((float) (30 - (Math.random() * 10)), i));
            }else if (i > 23 && i < 36) {
                yValsSleep.add(new Entry((float) (70 - (Math.random() * 10)), i));
            }else if (i > 35 && i < 48) {
                yValsSleep.add(new Entry((float) (90 - (Math.random() * 10)), i));
            }
            
*/        }
        
        
        if (xVals.size() > 0) {
            xVals.clear();
        }  
        for (int i = 0; i < 48; i++) {
            xVals.add(i + "");
        }
        
        LineDataSet SleepySet = new LineDataSet(yValsSleep, null);
        SleepySet.setDrawCubic(true);
        SleepySet.setDrawValues(false);
        SleepySet.setDrawCircles(false);
        
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(SleepySet);
        LineData data = new LineData(xVals, dataSets);
        setupChart(data, mColors[4]);
    }

    // 设置显示的样式  
    public static void setupChart(LineData data, int color) {  
        // if enabled, the chart will always start at zero on the y-axis  
   
        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(false);
        xAxis.setSpaceBetweenLabels(6);
        
        
        YAxis leftAxis = mChart.getAxisLeft();  //得到图表的左侧Y轴实例
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawLabels(false);
        leftAxis.setAxisMaxValue(100); // 设置Y轴最大值
        leftAxis.setAxisMinValue(0);// 设置Y轴最小值。
        leftAxis.setStartAtZero(true);   //设置图表起点从0开始
        
        YAxis rightAxis = mChart.getAxisRight();  //得到图表的右侧Y轴实例
        rightAxis.setDrawAxisLine(true);
        rightAxis.setDrawLabels(false);
        rightAxis.setAxisMaxValue(100); // 设置Y轴最大值
        rightAxis.setAxisMinValue(0);// 设置Y轴最小值。
        rightAxis.setStartAtZero(true);   //设置图表起点从0开始
   
  
        // no description text  
        mChart.setDescription("");// 数据描述  
       
     
        // enable / disable grid background  
        mChart.setDrawGridBackground(false); // 是否显示表格颜色  
       mChart.setBorderColor(mColors[0]);
       mChart.setGridBackgroundColor(mColors[0]);
        // enable touch gestures  
        mChart.setTouchEnabled(false); // 设置是否可以触摸  
  
        // enable scaling and dragging  
        mChart.setDragEnabled(false);// 是否可以拖拽  
        mChart.setScaleEnabled(false);// 是否可以缩放  
  
        // if disabled, scaling can be done on x- and y-axis separately  
        mChart.setPinchZoom(false);//   

        mChart.setBackgroundColor(color);// 设置背景  
        // add data  
        mChart.setData(data); // 设置数据  
    
        // get the legend (only possible after setting data)  
        Legend l = mChart.getLegend(); // 设置标示，就是那个一组y的value的  
        l.setEnabled(false);
        // animate calls invalidate()...  
        mChart.animateX(2000); // 立即执行的动画,x轴  
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
            if (yValsSleep.size() > 0) {
                yValsSleep.clear();
            }
            for (int i = 0; i < mDatalength; i++) {
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
            SleepySet.setDrawCubic(true);
            SleepySet.setDrawValues(false);
            SleepySet.setDrawCircles(false);
            
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
        dataSet.setDrawCircles(false);
        
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
