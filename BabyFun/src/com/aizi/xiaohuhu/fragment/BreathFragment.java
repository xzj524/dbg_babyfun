package com.aizi.xiaohuhu.fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aizi.xiaohuhu.R;
import com.aizi.xiaohuhu.breath.BabyBreath;
import com.aizi.xiaohuhu.constant.Constant;
import com.aizi.xiaohuhu.deviceinterface.AsyncDeviceFactory;
import com.aizi.xiaohuhu.logging.SLog;
import com.aizi.xiaohuhu.sleepdatabase.BreathInfoEnumClass;
import com.aizi.xiaohuhu.sleepdatabase.SleepInfoDatabase;
import com.aizi.xiaohuhu.utility.PrivateParams;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;

import de.greenrobot.event.EventBus;

public class BreathFragment extends Fragment{
    
    private static final String TAG = BreathFragment.class.getSimpleName();
    static ArrayList<BarEntry> yVals = new ArrayList<BarEntry>(); 
    static ArrayList<String> xVals = new ArrayList<String>();
    static Typeface mTf; // 自定义显示字体  
    
    static LineChart mBreathChart;
    BarChart mBreathStopTimes;
    private TextView mBreathFreqData;
    private TextView mBreathRealValue;
    
    int mPreValue = 5;
    long mLastBreathTime;
    long mBreathPeriod;
    int mBreathFreq;
    boolean mBreatfreq = false;
    boolean mBreatStart = false;
    Timer mTimer;
    
    Button mControlBreathBtn;
    
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
    LineData mData = new LineData();
    int xValCount = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View breathView = inflater.inflate(R.layout.activity_tab_breath, container,false); 
        mBreathChart = (LineChart) breathView.findViewById(R.id.breath_line_chart);
        mBreathStopTimes = (BarChart) breathView.findViewById(R.id.breath_stop_barchart);
        mControlBreathBtn = (Button) breathView.findViewById(R.id.control_breath_button);
        
        mControlBreathBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (!mBreatStart) {
                    mBreatStart = true;
                    mTimer = new Timer(true);
                    TimerTask task = new TimerTask(){  
                        public void run() {  
                        Message message = new Message();      
                        message.what = 1; 
                        mPreValue = (int) (Math.random() * 30);
                        message.arg1 = mPreValue;
                        
                        long curtime = System.currentTimeMillis();
                        if (mLastBreathTime == 0) {
                            mLastBreathTime = curtime;
                        } else {
                            mBreathPeriod = curtime - mLastBreathTime;
                            mLastBreathTime = curtime;
                        }
                        mHandler.sendMessage(message);    
                      }  
                   };  
                    mTimer.schedule(task,1000, 1300); 
                    
                    AsyncDeviceFactory.getInstance(getActivity().getApplicationContext()).startSendBreathData();
                    mControlBreathBtn.setText(R.string.action_stop_breath);
                } else {
                    mBreatStart = false;
                    mTimer.purge();
                    mTimer.cancel();
                    AsyncDeviceFactory.getInstance(getActivity().getApplicationContext()).stopSendBreathData();
                    mControlBreathBtn.setText(R.string.action_start_breath);
                }
            }
        });
        
        EventBus.getDefault().register(this);
        mBreathFreqData = (TextView)breathView.findViewById(R.id.breath_frequence_data);
        mBreathRealValue = (TextView)breathView.findViewById(R.id.breath_value_data);
        
        initBreathChart();
        LineData initData = initData(50);
        setupChart(initData, mColors[5]);
        
        initBreathStopBarChart(mBreathStopTimes);
        loadBreathStopBarChartData(mBreathStopTimes);
        return breathView;
    }
    
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        
        mBreatStart = false;
        mTimer.purge();
        mTimer.cancel();
        AsyncDeviceFactory.getInstance(getActivity().getApplicationContext()).stopSendBreathData();
        mControlBreathBtn.setText(R.string.action_start_breath);
    }
    
    
   
   final Handler mHandler = new Handler(){  
       public void handleMessage(Message msg) {  
            switch (msg.what) {      
                case 1:      
                    updateBreathWave(msg.arg1);
                    if (mBreatfreq) {
                       mBreatfreq = false;
                       if (mBreathPeriod > 0) {
                           mBreathFreq = (int)((60 * 1000) / mBreathPeriod); 
                           mBreathFreqData.setText(mBreathFreq + "");
                       }
                   }
                    break;      
                }      
                super.handleMessage(msg);  
           }    
       };  
       
       private void updateBreathWave(int preValue) {
           // TODO Auto-generated method stub
           for (int i = 0; i < 2; i++) {
               if (i == 0) {
                   generateNewWave(5); 
               } else if (i == 1) {
                   generateNewWave(preValue); 
               } 
           }
       }
       
    
    public void onEventMainThread(final ArrayList<BabyBreath> breaths) { 
        long curtime = System.currentTimeMillis();
        SLog.e(TAG, "breath receivre data curtime = " + curtime);
        if (mBreatStart) {
            if (mLastBreathTime == 0) {
                mLastBreathTime = curtime;
            } else {
                mBreathPeriod = curtime - mLastBreathTime;
                mLastBreathTime = curtime;
            }
            mBreatfreq = true;
            if (breaths.size() == 1) {
                mPreValue = breaths.get(0).mBreathValue + 100;
                SLog.e(TAG, "BabyBreathActivity receive REAL BREATH DATA " + mPreValue);
            }
        }
       
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
            yValsBreath.add(new Entry(5, i));
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
        leftAxis.setAxisMaxValue(150); // 设置Y轴最大值
        leftAxis.setAxisMinValue(0);// 设置Y轴最小值。
        leftAxis.setStartAtZero(false);   //设置图表起点从0开始
        //leftAxis.enableGridDashedLine(10f, 10f, 0f); //设置横向表格为虚线
        leftAxis.setDrawLabels(false);
        leftAxis.setGridColor(Color.WHITE);
        
        //右侧Y轴设置
        YAxis rightAxis = mBreathChart.getAxisRight();
        rightAxis.setAxisMaxValue(150); // 设置Y轴最大值
        rightAxis.setAxisMinValue(0);// 设置Y轴最小值。
        rightAxis.setStartAtZero(false);   //设置图表起点从0开始
        rightAxis.setDrawLabels(false);

        mBreathChart.setDrawGridBackground(false); // 是否显示表格颜色 
        mBreathChart.setGridBackgroundColor(mColors[5]);
        
        mBreathChart.setDrawBorders(false);
        //mBreathChart.setBorderPositions(new BorderPosition[] { BorderPosition.BOTTOM, BorderPosition.LEFT });// 设置图标边框
        mBreathChart.setBorderColor(Color.WHITE);
        mBreathChart.setBorderWidth(2);
        
        mBreathChart.setDescription("");
       
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
        breathLegend.setForm(LegendForm.SQUARE);// 样式  
        breathLegend.setFormSize(6f);// 字体  
        breathLegend.setTextColor(Color.WHITE);// 颜色  
        breathLegend.setTypeface(mTf);// 字体  
        mBreathChart.animateX(100); // 立即执行的动画,x轴  
    }
   
   
   /**
    * 加载并设置柱形图的数据
    * @param chart
    */
   private void loadBreathStopBarChartData(BarChart chart) {
       //所有数据点的集合
       //ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
       
       int year =  PrivateParams.getSPInt(getActivity().getApplicationContext(), Constant.DATA_DATE_YEAR, 0);
       int month =  PrivateParams.getSPInt(getActivity().getApplicationContext(), Constant.DATA_DATE_MONTH, 0);
       int day =  PrivateParams.getSPInt(getActivity().getApplicationContext(), Constant.DATA_DATE_DAY, 0);
       
       if (year == 0 || month == 0 || day == 0) {
           Time time = new Time("GMT+8");       
           time.setToNow();      
           year = time.year;      
           month = time.month;      
           day = time.monthDay;  
       } 
       
       List<BreathInfoEnumClass> breathInfoEnumClasses = 
               SleepInfoDatabase.getBreathInfoEnumClassList(getActivity().getApplicationContext(), 
               year, month, day);
       
       ArrayList<BarEntry> entries = getBarEntry(breathInfoEnumClasses);
      
       
    /*   for (int i = 0; i < 24; i++) {
           if (i > 13 && i < 18) {
               int yVlue = (int) (Math.random() * 30);
               entries.add(new BarEntry(yVlue, i));
           }
          
       }*/
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

   private ArrayList<BarEntry> getBarEntry(List<BreathInfoEnumClass> breathInfoEnumClasses) {
       ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
       int h0 = 0;
       int h1 = 0;
       int h2 = 0;
       int h3 = 0;
       int h4 = 0;
       int h5 = 0;
       int h6 = 0;
       int h7 = 0;
       int h8 = 0;
       int h9 = 0;
       int h10 = 0;
       int h11 = 0;
       int h12 = 0;
       int h13 = 0;
       int h14 = 0;
       int h15 = 0;
       int h16 = 0;
       int h17 = 0;
       int h18 = 0;
       int h19 = 0;
       int h20 = 0;
       int h21 = 0;
       int h22 = 0;
       int h23 = 0;
       
       
       
       for (BreathInfoEnumClass breathInfoEnumClass : breathInfoEnumClasses) {
            int tempHour = breathInfoEnumClass.getBreathHour();
            switch (tempHour) {
            case 0:
                h0++;
                break;
            case 1:
                h1++;
                break;
            case 2:
                h2++;
                break;
            case 3:
                h3++;
                break;
            case 4:
                h4++;
                break;
            case 5:
                h5++;
                break;
            case 6:
                h6++;
                break;
            case 7:
                h7++;
                break;
            case 8:
                h8++;
                break;
            case 9:
                h9++;
                break;
            case 10:
                h10++;
                break;
            case 11:
                h11++;
                break;
            case 12:
                h12++;
                break;
            case 13:
                h13++;
                break;
            case 14:
                h14++;
                break;
            case 15:
                h15++;
                break;
            case 16:
                h16++;
                break;
            case 17:
                h17++;
                break;
            case 18:
                h18++;
                break;
            case 19:
                h19++;
                break;
            case 20:
                h20++;
                break;
            case 21:
                h21++;
                break;
            case 22:
                h22++;
                break;
            case 23:
                h23++;
                break;

            default:
                break;
            }
       }
       
       
    entries.add(new BarEntry(h0, 0));
    entries.add(new BarEntry(h1, 1));
    entries.add(new BarEntry(h2, 2));
    entries.add(new BarEntry(h3, 3));
    entries.add(new BarEntry(h4, 4));
    entries.add(new BarEntry(h5, 5));
    entries.add(new BarEntry(h6, 6));
    entries.add(new BarEntry(h7, 7));
    entries.add(new BarEntry(h8, 8));
    entries.add(new BarEntry(h9, 9));
    entries.add(new BarEntry(h10, 10));
    entries.add(new BarEntry(h11, 11));
    entries.add(new BarEntry(h12, 12));
    entries.add(new BarEntry(h13, 13));
    entries.add(new BarEntry(h14, 14));
    entries.add(new BarEntry(h15, 15));
    entries.add(new BarEntry(h16, 16));
    entries.add(new BarEntry(h17, 17));
    entries.add(new BarEntry(h18, 18));
    entries.add(new BarEntry(h19, 19));
    entries.add(new BarEntry(h20, 20));
    entries.add(new BarEntry(h21, 21));
    entries.add(new BarEntry(h22, 22));
    entries.add(new BarEntry(h23, 23));
    
    return entries;
}

/**
    * 设置柱形图的样式
    * @param chart
    */
   private void initBreathStopBarChart(BarChart chart) {
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
//       rightAxis.setDrawAxisLine(false);//右侧坐标轴线
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
