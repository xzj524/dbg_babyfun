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
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.database.TemperatureInfoEnumClass;
import com.aizi.yingerbao.database.YingerbaoDatabase;
import com.aizi.yingerbao.deviceinterface.AsyncDeviceFactory;
import com.aizi.yingerbao.fragment.SimpleCalendarDialogFragment;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.synctime.DataTime;
import com.aizi.yingerbao.utility.PrivateParams;
import com.aizi.yingerbao.utility.Utiliy;
import com.aizi.yingerbao.view.TopBarView;
import com.aizi.yingerbao.view.TopBarView.onTitleBarClickListener;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

public class TemperatureActivity extends Activity implements onTitleBarClickListener{
    
    private static final String TAG = TemperatureActivity.class.getSimpleName();
    LineChart mTemperatureChart;
    Button mTempButton;
    TextView mTempValue;
    boolean mTempStart = false;
    boolean mIsTempMeasuring = false;
    
    TopBarView mTopView;

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
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        
        mTempStart = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        
        mTopView = (TopBarView) findViewById(R.id.xiaohuhutopbar);
        mTopView.setClickListener(this);
        
        mTemperatureChart = (LineChart) findViewById(R.id.temperature_linechart);
        mTempButton = (Button) findViewById(R.id.control_temp_button);
        mTempButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                try {
                    if (Utiliy.isBluetoothConnected(getApplicationContext())) {
                        if (!mTempStart) {
                            if (!mIsTempMeasuring) {
                                mTempStart = true;
                                mTempButton.setText(R.string.action_measure_temp);
                                AsyncDeviceFactory.getInstance(getApplicationContext()).getRealTimeTempData();
                                mIsTempMeasuring = true;
                                
                                new Handler().postDelayed(new Runnable() {
                                    
                                    @Override
                                    public void run() {
                                        mIsTempMeasuring = false; 
                                    }
                                }, 10000);
                                
                            } else {
                                Toast.makeText(getApplicationContext(), "10秒内请勿重复读取！", Toast.LENGTH_SHORT).show();
                            }
                           
                        } else {
                            mTempStart = false;
                            mTempButton.setText(R.string.action_start_temp);
                        }
                    } else {
                        showNormalDialog();
                    }
                    
                } catch (Exception e) {
                    SLog.e(TAG, e);
                }
                
                
                
                // 首先检测蓝牙是否连接
                /*if (mTempStart) {
                    mTempStart = false;
                    if (mTimer != null) {
                        mTimer.purge();
                        mTimer.cancel();
                    }   
                    mTempButton.setText(R.string.action_start_temp);
                } else {
                    if (Utiliy.isBluetoothConnected(getApplicationContext())) {
                        if (!mTempStart) {
                            mTempStart = true;
                            mTimer = new Timer(true);
                            TimerTask task = new TimerTask(){  
                                public void run() {  
                                AsyncDeviceFactory.getInstance(getApplicationContext()).getRealTimeTempData();
                              }  
                           };  
                            mTimer.schedule(task,1000, 3000); 
                            mTempButton.setText(R.string.action_stop);
                        } else {
                            mTempStart = false;
                            if (mTimer != null) {
                                mTimer.purge();
                                mTimer.cancel();
                            } 
                            mTempButton.setText(R.string.action_start_temp);
                        }
                    } else {
                        showNormalDialog();
                    }
                }*/
                
            }
        });
        
        mTempValue = (TextView) findViewById(R.id.tempvalue);
        
        DataTime dataTime = new DataTime();
        dataTime.year = PrivateParams.getSPInt(getApplicationContext(), Constant.DATA_DATE_YEAR, 0);
        dataTime.month = PrivateParams.getSPInt(getApplicationContext(), Constant.DATA_DATE_MONTH, 0);
        dataTime.day = PrivateParams.getSPInt(getApplicationContext(), Constant.DATA_DATE_DAY, 0);
        updateTempStatus(dataTime );
        EventBus.getDefault().register(this);
    }
    
    
    public void onEventMainThread(Intent intent) { 
        String action = intent.getAction();
        if (Constant.DATA_REALTIME_TEMPERATURE.equals(action)) {
            // 获取实时温度值
           String str = intent.getStringExtra("realtime_temperature"); 
           mTempValue.setText(str);
           mTempStart = false;
           mTempButton.setText(R.string.action_start_temp);
        }
    }
    
    private void updateTempStatus(DataTime dataTime) {
        if (yValsTem.size() > 0) {
            yValsTem.clear();
        }
        
        int year =  dataTime.year;
        int month = dataTime.month;
        int day = dataTime.day;
        
        if (year == 0 || month == 0 || day == 0) {
            Calendar calendar = Calendar.getInstance();   
            year = calendar.get(Calendar.YEAR);      
            month = calendar.get(Calendar.MONTH) + 1;     
            day = calendar.get(Calendar.DAY_OF_MONTH);   
        } 
        
        List<TemperatureInfoEnumClass> temperatureinfos 
            = YingerbaoDatabase.getTemperatureInfoEnumClassList(getApplicationContext(), year, month, day);
        
        
       for (int i = 0; i < 144; i++) {
           if (i < temperatureinfos.size()) {
               float tempvalue = Float.parseFloat(temperatureinfos.get(i).getTemperatureValue());
               //temperatureinfos.get(i).g
               
               SLog.e(TAG, "tempvalue from database = " + tempvalue);
               yValsTem.add(new Entry(tempvalue -10, i));
           }/* else {
               yValsTem.add(new Entry(0, i));
           }*/
           
       }
        
       /* for (int j = 0; j < 1440; j++) {
            for (int i = 0; i < temperatureinfos.size(); i++) {
                if (j == temperatureinfos.get(i).getTemperatureMinute()) {
                    yValsTem.add(new Entry(Float.parseFloat(temperatureinfos.get(i).getTemperatureValue()), j));
                }
                
            }
        }*/
        
        if (xVals.size() > 0) {
            xVals.clear();
        }  
        for (int i = 0; i < 144; i++) {
            xVals.add(i + "");
        }
        
/*        for (int i = 0; i < 24; i++) {
            if (i < 6) {
                yValsTem.add(new Entry((float) (70 - (Math.random() * 10)), i));
            } else if (i > 5 && i < 12) {
                yValsTem.add(new Entry((float) (50 - (Math.random() * 10)), i));
            }else if (i > 11 && i < 18) {
                yValsTem.add(new Entry((float) (60 - (Math.random() * 10)), i));
            }else if (i > 17 && i < 24) {
                yValsTem.add(new Entry((float) (70 - (Math.random() * 10)), i));
            }
        }*/
        
        
      
        
        LineDataSet TemperatureSet = new LineDataSet(yValsTem, null);
        TemperatureSet.setDrawCubic(true);
        TemperatureSet.setDrawValues(false);
        TemperatureSet.setDrawCircles(false);
        TemperatureSet.setColor(Color.RED);
        
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(TemperatureSet);
        LineData data = new LineData(xVals, dataSets);
        setupChart(data, mColors[4]);
    }
    
 // 设置显示的样式  
    public void setupChart(LineData data, int color) {  
        // if enabled, the chart will always start at zero on the y-axis  
   
        XAxis xAxis = mTemperatureChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(false);
        //xAxis.setGridColor(Color.RED);
        
        
        YAxis leftAxis = mTemperatureChart.getAxisLeft();  //得到图表的左侧Y轴实例
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawLabels(false);
        leftAxis.setAxisMaxValue(40); // 设置Y轴最大值
        leftAxis.setAxisMinValue(25);// 设置Y轴最小值。
        leftAxis.setStartAtZero(true);   //设置图表起点从0开始
        
        YAxis rightAxis = mTemperatureChart.getAxisRight();  //得到图表的右侧Y轴实例
        rightAxis.setDrawAxisLine(true);
        rightAxis.setDrawLabels(false);
        rightAxis.setAxisMaxValue(40); // 设置Y轴最大值
        rightAxis.setAxisMinValue(25);// 设置Y轴最小值。
        rightAxis.setStartAtZero(true);   //设置图表起点从0开始
  
        // no description text  
        mTemperatureChart.setDescription("");// 数据描述  
        mTemperatureChart.setNoDataText(getApplicationContext().getResources().getString(R.string.date_no_data));
     
        // enable / disable grid background  
        mTemperatureChart.setDrawGridBackground(true); // 是否显示表格颜色  
       
        // enable touch gestures  
        mTemperatureChart.setTouchEnabled(false); // 设置是否可以触摸  
  
        mTemperatureChart.setDoubleTapToZoomEnabled(true);//设置双击不进行缩放
        //mTemperatureChart.set
  
        // if disabled, scaling can be done on x- and y-axis separately  
        mTemperatureChart.setPinchZoom(false);//   
        // enable scaling and dragging  
        mTemperatureChart.setDragEnabled(false);// 是否可以拖拽  
        mTemperatureChart.setScaleEnabled(false);// 是否可以缩放  
  
        mTemperatureChart.setBackgroundColor(color);// 设置背景  
        // add data  
        mTemperatureChart.setData(data); // 设置数据  
    
        // get the legend (only possible after setting data)  
        Legend l = mTemperatureChart.getLegend(); // 设置标示，就是那个一组y的value的  
        l.setEnabled(false);
        // animate calls invalidate()...  
        mTemperatureChart.animateX(2000); // 立即执行的动画,x轴  
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
    
    public void onEventMainThread(DataTime dataTime) { 
        updateTempStatus(dataTime);
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
