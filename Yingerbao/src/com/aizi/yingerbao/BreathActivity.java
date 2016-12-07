package com.aizi.yingerbao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.aizi.yingerbao.breath.BabyBreath;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.database.BreathInfoEnumClass;
import com.aizi.yingerbao.database.BreathStopInfo;
import com.aizi.yingerbao.database.YingerbaoDatabase;
import com.aizi.yingerbao.deviceinterface.DeviceFactory;
import com.aizi.yingerbao.fragment.SimpleCalendarDialogFragment;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.synctime.DataTime;
import com.aizi.yingerbao.utility.PrivateParams;
import com.aizi.yingerbao.utility.Utiliy;
import com.aizi.yingerbao.view.TopBarView;
import com.aizi.yingerbao.view.TopBarView.onTitleBarClickListener;
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
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

public class BreathActivity extends Activity implements onTitleBarClickListener{
    
    private static final String TAG = BreathActivity.class.getSimpleName();
    static ArrayList<BarEntry> yVals = new ArrayList<BarEntry>(); 
    static ArrayList<String> xVals = new ArrayList<String>();
    static Typeface mTf; // 自定义显示字体  
    
    static LineChart mBreathChart;
    BarChart mBreathStopChart;
    private TextView mBreathFreqData;
    
    int mBreValue = 5;
    long mLastBreathTime;
    long mBreathPeriod;
    int mBreathFreq;
    long mBreathTimeforlast;
    boolean mBreatStart = false;
    Timer mTimer;
    
    boolean mIsBreathSet = false;
    
    Button mControlBreathBtn;
    
    private  TopBarView mBreathTopbar;
    
    static int[] mColors = new int[] {
        Color.rgb(137, 230, 81), 
        Color.rgb(240, 240, 30), 
        Color.rgb(89, 199, 250), 
        Color.rgb(250, 104, 104), 
        Color.rgb(4, 158, 255),
        Color.rgb(222, 182, 180),
        Color.rgb(241, 158, 194)}; // 自定义颜色 
    
    static ArrayList<Entry> yValsBreath = new ArrayList<Entry>();
    static ArrayList<String> xValsBreathTime = new ArrayList<String>();
    static LineDataSet BreathSet = new LineDataSet(yValsBreath, "清醒");
    LineData mData = new LineData();
    int xValCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_breath);
        
        mBreathTopbar = (TopBarView) findViewById(R.id.breathtopbar);
        mBreathTopbar.setClickListener(this);
        
        mBreathChart = (LineChart) findViewById(R.id.breath_line_chart);
        mBreathStopChart = (BarChart) findViewById(R.id.breath_stop_barchart);
        mControlBreathBtn = (Button) findViewById(R.id.control_breath_button);
        
        mControlBreathBtn.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (mBreatStart) {
                    mBreatStart = false;
                    if (mTimer != null) {
                        mTimer.purge();
                        mTimer.cancel(); 
                    }
                    DeviceFactory.getInstance(getApplicationContext()).stopSendBreathData();
                    mControlBreathBtn.setText(R.string.action_start_breath);
                } else {
                    // 首先检测蓝牙是否连接
                    if (Utiliy.isBluetoothConnected(getApplicationContext())) {
                        if (!mBreatStart) {
                            mBreatStart = true;
                            mTimer = new Timer(true);
                            TimerTask task = new TimerTask(){  
                                public void run() {  
                                Message message = new Message();      
                                message.what = 1; 
                                if (mIsBreathSet) {
                                    mIsBreathSet = false;
                                    message.arg1 = mBreValue;
                                } else {
                                    mIsBreathSet = true;
                                    message.arg1 = 5;
                                }
                                
                                message.arg2 = mBreathFreq;
                                mHandler.sendMessage(message);  
                                
                                /*updateBreathWave(mBreValue);
                                mBreValue = 5;
                                mBreathFreqData.setText(mBreathFreq + "");*/
                              }  
                           };  
                            mTimer.schedule(task,1000, 1300); 
                            
                            DeviceFactory.getInstance(getApplicationContext()).startSendBreathData();
                            mControlBreathBtn.setText(R.string.action_stop);
                        } else {
                            mBreatStart = false;
                            if (mTimer != null) {
                                mTimer.purge();
                                mTimer.cancel();
                            }  
                            DeviceFactory.getInstance(getApplicationContext()).stopSendBreathData();
                            mControlBreathBtn.setText(R.string.action_start_breath);
                        }
                    } else {
                       showNormalDialog();
                    }
                }
            }
        });
        
        EventBus.getDefault().register(this);
        mBreathFreqData = (TextView)findViewById(R.id.breath_frequence_data);
        
        initBreathChart();
        LineData initData = initData(50);
        setupRealTimeBreathChart(initData, mColors[5]);
       
        initBreathStopBarChart();
        DataTime dataTime = new DataTime();
        dataTime.year = PrivateParams.getSPInt(getApplicationContext(), Constant.DATA_DATE_YEAR, 0);
        dataTime.month = PrivateParams.getSPInt(getApplicationContext(), Constant.DATA_DATE_MONTH, 0);
        dataTime.day = PrivateParams.getSPInt(getApplicationContext(), Constant.DATA_DATE_DAY, 0);
        updateBreathStopBarChartData(dataTime);
    }
    
    public void showNormalDialog(){
        
        final AlertDialog.Builder normalDialog = 
            new AlertDialog.Builder(this);
        normalDialog.setIcon(R.drawable.yingerbao_96);
        normalDialog.setTitle("连接设备");
        normalDialog.setMessage("设备未连接，是否连接设备,\n请先摇动设备保证能够正确连接。");
        normalDialog.setPositiveButton("确定", 
            new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(getApplicationContext(), ConnectDeviceActivity.class);
                startActivity(intent);
            }
        });
        normalDialog.setNegativeButton("取消", 
            new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //...To-do
            }
        });
        // 显示
        normalDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DeviceFactory.getInstance(getApplicationContext()).stopSendBreathData();
        EventBus.getDefault().unregister(this);
        if (mTimer != null) {
            mTimer.purge();
            mTimer.cancel();
        }

    }
    

    final Handler mHandler = new Handler(){  
        public void handleMessage(Message msg) {  
             switch (msg.what) {      
                 case 1:      
                     updateBreathWave(msg.arg1);
                     mBreValue = 5;
                     mBreathFreqData.setText(msg.arg2 + "");
                    /* if (mBreatfreq) {
                        mBreatfreq = false;
                        if (mBreathPeriod > 0) {
                            mBreathFreq = (int)((60 * 1000) / mBreathPeriod); 
                            mBreathFreqData.setText(mBreathFreq + "");
                        }
                    }*/
                     break;      
                 }      
                 super.handleMessage(msg);  
            }    
        };  
        
        private void updateBreathWave(int preValue) {
            for (int i = 0; i < 2; i++) {
                if (i == 0) {
                    generateNewWave(5); 
                } else if (i == 1) {
                    generateNewWave(preValue); 
                } 
            }
        }
        
     
     public void onEventMainThread(final BabyBreath breaths) { 

         mBreValue = (int) (breaths.mBreathValue + (60 + Math.random() * 10));
         mBreathFreq = breaths.mBreathFreq;
         mBreathTimeforlast = breaths.mBreathTime;
         
         SLog.e(TAG, "breath receivre data  mBreValue = " + mBreValue 
                 + " mBreathFreq = " + mBreathFreq
                 + " mBreathTimeforlast = " + mBreathTimeforlast);
     }
     
     public void onEventMainThread(DataTime dataTime) { 
         updateBreathStopBarChartData(dataTime);
     }
     
     
     public void freshChart() {
         if (mData != null) {
             mData = initData(5);
             setupRealTimeBreathChart(mData, mColors[5]);
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
             setupRealTimeBreathChart(data, mColors[5]);
         }   
     }
     
     private void setupLineDataSet(LineDataSet DataSet) {
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
     public static void setupRealTimeBreathChart(LineData data, int color) {   
         try {
             mBreathChart.clear();
             mBreathChart.setData(data); // 设置数据  
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
        
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
         leftAxis.setAxisMaxValue(100); // 设置Y轴最大值
         leftAxis.setAxisMinValue(0);// 设置Y轴最小值。
         leftAxis.setStartAtZero(false);   //设置图表起点从0开始
         //leftAxis.enableGridDashedLine(10f, 10f, 0f); //设置横向表格为虚线
         leftAxis.setDrawLabels(false);
         leftAxis.setGridColor(Color.WHITE);
         
         //右侧Y轴设置
         YAxis rightAxis = mBreathChart.getAxisRight();
         rightAxis.setAxisMaxValue(100); // 设置Y轴最大值
         rightAxis.setAxisMinValue(0);// 设置Y轴最小值。
         rightAxis.setStartAtZero(false);   //设置图表起点从0开始
         rightAxis.setDrawLabels(false);

         mBreathChart.setDrawGridBackground(false); // 是否显示表格颜色 
        // mBreathChart.setGridBackgroundColor(mColors[5]);
         
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
         
         //mBreathChart.setBackgroundColor(mColors[5]);// 设置背景  
         
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
    private void updateBreathStopBarChartData(DataTime dataTime) {
        
        int year = dataTime.year;
        int month = dataTime.month;
        int day = dataTime.day;
        
        if (year == 0 || month == 0 || day == 0) {
            Calendar calendar = Calendar.getInstance();   
            year = calendar.get(Calendar.YEAR);      
            month = calendar.get(Calendar.MONTH) + 1;     
            day = calendar.get(Calendar.DAY_OF_MONTH);   
        } 
        
   /*     BreathStopInfo breathinfo = new BreathStopInfo();
        breathinfo.mBreathYear = year;
        breathinfo.mBreathMonth = month;
        breathinfo.mBreathDay = day;*/
        
       // breathinfo.mBreathHour = 5;
        
     /*   for (int i = 0; i < 3; i++) {
            breathinfo.mBreathMinute = 5+i;
            YingerbaoDatabase.insertBreathInfo(getApplicationContext(), breathinfo);
        }*/
        
        
      /*  for (int i = 0; i < 24; i++) {
             breathinfo.mBreathHour = i;
         
             if (i == 5) {
                 breathinfo.mBreathMinute = i;
             }else {
                 breathinfo.mBreathMinute = 0;
             }
             YingerbaoDatabase.insertBreathInfo(getApplicationContext(), breathinfo);
        }
*/
        
        
        List<BreathInfoEnumClass> breathInfoEnumClasses = 
                YingerbaoDatabase.getBreathInfoEnumClassList(getApplicationContext(), 
                year, month, day);
        
        ArrayList<BarEntry> entries = getBarEntry(breathInfoEnumClasses);
       
        //柱形数据的集合
        BarDataSet mBarDataSet = new BarDataSet(entries,"barDataSet");
        mBarDataSet.setBarSpacePercent(20f);
        mBarDataSet.setHighLightAlpha(100);//设置点击后高亮颜色透明度
        mBarDataSet.setHighLightColor(Color.GRAY);
        
        mBarDataSet.setColor(mColors[6]);
        //BarData表示挣个柱形图的数据
        BarData mBarData = new BarData(getXAxisShowLable(),mBarDataSet);
        mBreathStopChart.setData(mBarData);
        mBreathStopChart.animateY(1500);//设置动画
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
        
        
     if (h0 != 0) {
         entries.add(new BarEntry(h0, 0));
     }
     
     if (h1 != 0) {
         entries.add(new BarEntry(h1, 1));
     }
     
     if (h2 != 0) {
         entries.add(new BarEntry(h2, 2));
     }
     
     if (h3 != 0) {
         entries.add(new BarEntry(h3, 3));
     }
     
     if (h4 != 0) {
         entries.add(new BarEntry(h4, 4));
     }
     
     if (h5 != 0) {
         entries.add(new BarEntry(h5, 5));
     }
     
     if (h6 != 0) {
         entries.add(new BarEntry(h6, 6));
     }
     
     if (h7 != 0) {
         entries.add(new BarEntry(h7, 7));
     }
     
     if (h8 != 0) {
         entries.add(new BarEntry(h8, 8));
     }
     
     if (h9 != 0) {
         entries.add(new BarEntry(h9, 9));
     }
     
     if (h10 != 0) {
         entries.add(new BarEntry(h10, 10));
     }
     
     if (h11 != 0) {
         entries.add(new BarEntry(h11, 11));
     }
     
     if (h12 != 0) {
         entries.add(new BarEntry(h12, 12));
     }
     
     
     if (h13 != 0) {
         entries.add(new BarEntry(h13, 13));
     }
     
     if (h14 != 0) {
         entries.add(new BarEntry(h14, 14));
     }
     
     if (h15 != 0) {
         entries.add(new BarEntry(h15, 15));
     }
     
     if (h16 != 0) {
         entries.add(new BarEntry(h16, 16));
     }
     
     if (h17 != 0) {
         entries.add(new BarEntry(h17, 17));
     }
     
     if (h18 != 0) {
         entries.add(new BarEntry(h18, 18));
     }
     
     if (h19 != 0) {
         entries.add(new BarEntry(h19, 19));
     }

     
     if (h20 != 0) {
         entries.add(new BarEntry(h20, 20));
     }

     
     if (h21 != 0) {
         entries.add(new BarEntry(h21, 21));
     }

     
     if (h22 != 0) {
         entries.add(new BarEntry(h22, 22));
     }

     
     if (h23 != 0) {
         entries.add(new BarEntry(h23, 23));
     }
          
     return entries;
 }

 /**
     * 设置柱形图的样式
     * @param chart
     */
    private void initBreathStopBarChart() {
        mBreathStopChart.setDescription("");
        mBreathStopChart.setNoDataText(getApplicationContext().getResources().getString(R.string.date_no_data));
        mBreathStopChart.setDrawGridBackground(false);//设置网格背景
        mBreathStopChart.setScaleEnabled(true);//设置缩放
        mBreathStopChart.setDoubleTapToZoomEnabled(false);//设置双击不进行缩放
        mBreathStopChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            
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
        XAxis xAxis = mBreathStopChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置X轴的位置
        
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);

        //获得左侧侧坐标轴
        YAxis leftAxis = mBreathStopChart.getAxisLeft();
        leftAxis.setLabelCount(5);
        leftAxis.setAxisMaxValue(10); // 设置Y轴最大值
        leftAxis.setAxisMinValue(0);// 设置Y轴最小值。

        //设置右侧坐标轴
        YAxis rightAxis = mBreathStopChart.getAxisRight();
//        rightAxis.setDrawAxisLine(false);//右侧坐标轴线
        rightAxis.setDrawLabels(false);//右侧坐标轴数组Lable
        rightAxis.setLabelCount(5);
        rightAxis.setAxisMaxValue(10); // 设置Y轴最大值
        rightAxis.setAxisMinValue(0);// 设置Y轴最小值。
        
        Legend mLegend = mBreathStopChart.getLegend(); // 设置标示，就是那个一组y的value的  
        mLegend.setEnabled(false);
    }
    
    private ArrayList<String> getXAxisShowLable() {
        ArrayList<String> m = new ArrayList<String>();
        for (int i = 0; i < 24; i++) {
            m.add(i+1+"");
        }
        return m;
    }

    @Override
    public void onBackClick() {
        // TODO Auto-generated method stub
        finish();
    }

    @Override
    public void onRightClick() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onCalendarClick() {
        // TODO Auto-generated method stub
        SimpleCalendarDialogFragment mFragment = new SimpleCalendarDialogFragment();
        mFragment.show(getFragmentManager(), "simple-calendar");

    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        MobclickAgent.onResume(this);
    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        MobclickAgent.onPause(this);
    }
    
    

}
