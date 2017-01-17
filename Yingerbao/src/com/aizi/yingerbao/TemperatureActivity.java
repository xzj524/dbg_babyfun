package com.aizi.yingerbao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import cn.qqtheme.framework.picker.NumberPicker;

import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.database.TemperatureDataInfo;
import com.aizi.yingerbao.database.YingerbaoDatabase;
import com.aizi.yingerbao.deviceinterface.DeviceFactory;
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
import com.github.mikephil.charting.utils.ValueFormatter;
import com.umeng.analytics.MobclickAgent;

import de.greenrobot.event.EventBus;

public class TemperatureActivity extends Activity implements onTitleBarClickListener{
    
    private static final String TAG = TemperatureActivity.class.getSimpleName();
    LineChart mTemperatureChart;
    Button mTempButton;
    Button mSetTempAlarmValueButton;
    TextView mTempValue;
    TextView mTempDate;
    TextView mTempAlarmValueTextView;
    ImageView mCalendarView;
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
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        mTempStart = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temperature);
        
        EventBus.getDefault().register(this);
        
        mTopView = (TopBarView) findViewById(R.id.xiaohuhutopbar);
        mTopView.setClickListener(this);
        
        mTempDate = (TextView)findViewById(R.id.temperaturedate);
        mCalendarView = (ImageView) findViewById(R.id.temperature_calendar);
        mCalendarView.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                SimpleCalendarDialogFragment mFragment = new SimpleCalendarDialogFragment();
                mFragment.show(getFragmentManager(), "simple-calendar");
            }
        });
        
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
                                DeviceFactory.getInstance(getApplicationContext()).getRealTimeTempData();
                                mTempValue.setText("--");
                                mIsTempMeasuring = true;
                                
                                new Handler().postDelayed(new Runnable() {
                                    
                                    @Override
                                    public void run() {
                                        mIsTempMeasuring = false; 
                                        if (mTempStart) { // 如果10秒钟没有返回温度值，则恢复按钮
                                            mTempStart = false;
                                            mTempButton.setText(R.string.action_start_temp);
                                        }
                                    }
                                }, 10000);
                                
                            } else {
                                Toast.makeText(getApplicationContext(), "10秒内请勿重复读取！", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            if (!mIsTempMeasuring) {
                                mTempStart = false;
                                mTempButton.setText(R.string.action_start_temp);
                            }
                        }
                    } else {
                        showNormalDialog();
                    }
                    
                } catch (Exception e) {
                    SLog.e(TAG, e);
                }
            }
        });
        
        mSetTempAlarmValueButton = (Button) findViewById(R.id.changealarmvalue);
        mSetTempAlarmValueButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                NumberPicker picker = new NumberPicker(TemperatureActivity.this);
                picker.setAnimationStyle(R.style.Animation_CustomPopup);
                picker.setOffset(1);//偏移量
                picker.setRange(30, 43, 0.5);//数字范围
                picker.setSelectedItem(37.5);
                picker.setLabel("℃");
                picker.setTitleText("报警温度值");
                picker.setOnNumberPickListener(new NumberPicker.OnNumberPickListener() {
                    @Override
                    public void onNumberPicked(int index, Number item) {
                        SLog.e(TAG, "index=" + index + ", item=" + item.doubleValue());
                        double tempalarmvalue = item.doubleValue();
                        mTempAlarmValueTextView.setText(""+tempalarmvalue);
                        DeviceFactory.getInstance(getApplicationContext())
                                .setTemperatureAlarmConfig(tempalarmvalue, 0);
                        PrivateParams.setSPString(getApplicationContext(), 
                                Constant.DATA_TEMP_ALARM_VALUE_NEW, ""+tempalarmvalue);
                    }
                });
                picker.show();
            }
        });
        
        mTempValue = (TextView) findViewById(R.id.tempvalue);
        mTempAlarmValueTextView = (TextView) findViewById(R.id.temperaturealarmvalue);
        
        String newalarmString = PrivateParams.getSPString(getApplicationContext(), Constant.DATA_TEMP_ALARM_VALUE_NEW);
        if (!TextUtils.isEmpty(newalarmString)) {
            mTempAlarmValueTextView.setText(newalarmString);
        } else {
            String oldalarmString = PrivateParams.getSPString(getApplicationContext(), Constant.DATA_TEMP_ALARM_VALUE_OLD);
            if (!TextUtils.isEmpty(oldalarmString)) {
                mTempAlarmValueTextView.setText(oldalarmString);
            } else {
                mTempAlarmValueTextView.setText("37.5");
            }
        }
        initXValues();   
        initTempChart();
        
        if (yValsTem.size() > 0) {
            yValsTem.clear();
        }
  
        drawTempChart(yValsTem , false);
        Utiliy.initCurrentDataDate(getApplicationContext());
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DataTime dataTime = new DataTime();
        dataTime.year = year;
        dataTime.month = month;
        dataTime.day = day;
        updateTempStatus(dataTime);
    }
    
    
    private void initTempChart() {
        XAxis xAxis = mTemperatureChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawLabels(true);   
        xAxis.setDrawGridLines(true);
        xAxis.setDrawAxisLine(true);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setTextSize(10);
        
        YAxis leftAxis = mTemperatureChart.getAxisLeft();  //得到图表的左侧Y轴实例
        leftAxis.setDrawAxisLine(true);
        leftAxis.setDrawLabels(true);
        leftAxis.setAxisMaxValue(40); // 设置Y轴最大值
        leftAxis.setAxisMinValue(10);// 设置Y轴最小值。
        leftAxis.setStartAtZero(false);   //设置图表起点从0开始
        
        leftAxis.setLabelCount(6);
        leftAxis.setDrawTopYLabelEntry(true);
        leftAxis.setTextSize(14);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setValueFormatter(new ValueFormatter() {
            
            @Override
            public String getFormattedValue(float arg0) {
                return (int)arg0 + "℃";
            }
        });
        
        YAxis rightAxis = mTemperatureChart.getAxisRight();  //得到图表的右侧Y轴实例
        rightAxis.setDrawAxisLine(true);
        rightAxis.setDrawLabels(false);
        rightAxis.setAxisMaxValue(40); // 设置Y轴最大值
        rightAxis.setAxisMinValue(10);// 设置Y轴最小值。
        rightAxis.setStartAtZero(false);   //设置图表起点从0开始
        rightAxis.setLabelCount(6);
  
        // no description text  
        mTemperatureChart.setDescription("历史采集温度");// 数据描述  
        mTemperatureChart.setDescriptionColor(Color.WHITE);
        mTemperatureChart.setNoDataText(getApplicationContext().getResources().getString(R.string.date_no_temp_data));
      
        // enable / disable grid background  
        mTemperatureChart.setDrawGridBackground(false); // 是否显示表格颜色  
       
        // enable touch gestures  
        mTemperatureChart.setTouchEnabled(true); // 设置是否可以触摸  
  
        mTemperatureChart.setDoubleTapToZoomEnabled(false);//设置双击不进行缩放
        //mTemperatureChart.set
  
        // if disabled, scaling can be done on x- and y-axis separately  
        mTemperatureChart.setPinchZoom(false);//   
        // enable scaling and dragging  
        mTemperatureChart.setDragEnabled(true);// 是否可以拖拽  
        mTemperatureChart.setScaleEnabled(true);// 是否可以缩放  
        mTemperatureChart.setSelected(true);
        mTemperatureChart.setDragDecelerationEnabled(false);
        
        Legend l = mTemperatureChart.getLegend(); // 设置标示，就是那个一组y的value的  
        l.setEnabled(false);
    }

    private void initXValues() {
        if (xVals.size() > 0) {
            xVals.clear();
        }  
        
        int xval = 0;
        for (int i = 0; i < 144; i++) {
            if (i > 0) {
                if ((i+1) % 6 == 0) {
                    xval = (i+1) / 6;
                    xVals.add(xval + "点");
                } else {
                    xval = 0;
                    xVals.add("");
                }
            } else {
                xval = 0;
                xVals.add(xval + "点");
            }
        }
    }

    public void onEventMainThread(Intent intent) { 
        String action = intent.getAction();
        if (Constant.DATA_REALTIME_TEMPERATURE.equals(action)) {
            // 获取实时温度值
            if (intent.hasExtra("error_type")) {
                int errtype = intent.getIntExtra("error_type", 0);
                if (errtype == 1) { // 
                    Toast.makeText(getApplicationContext(), "数据错误！", Toast.LENGTH_SHORT).show();
                } else if (errtype == 2) {
                    Toast.makeText(getApplicationContext(), "温度值超出合理范围！", Toast.LENGTH_SHORT).show();
                } else {
                    String str = intent.getStringExtra("realtime_temperature"); 
                    mTempValue.setText(str);
                }
                mTempStart = false; 
            }
            mTempButton.setText(R.string.action_start_temp);
        }
    }
    
    @SuppressWarnings("unchecked")
    private void updateTempStatus(DataTime dataTime) {
        

        int year =  dataTime.year;
        int month = dataTime.month;
        int day = dataTime.day;
        
        if (year == 0 || month == 0 || day == 0) {
            Calendar calendar = Calendar.getInstance();   
            year = calendar.get(Calendar.YEAR);      
            month = calendar.get(Calendar.MONTH) + 1;     
            day = calendar.get(Calendar.DAY_OF_MONTH);   
        } 
        
        mTempDate.setText(year + "年" + month + "月" + day + "日");
        
        new FreshChartTask().execute(year, month, day);
        
      /*  new Thread(new Runnable() {
            
            @Override
            public void run() {
                synchronized (this) {
                    List<TemperatureDataInfo> temperatureinfos 
                    = YingerbaoDatabase.getTemperatureInfoEnumClassList(getApplicationContext(), year, month, day);
                
                    if (temperatureinfos.size() > 0) {
                        Collections.sort(temperatureinfos, new Comparator() {
                            public int compare(Object a, Object b) {
                                int one = ((TemperatureDataInfo) a).getTemperatureMinute();
                                int two = ((TemperatureDataInfo) b).getTemperatureMinute();
                                return one - two;
                            }
                        });
                        
                       
                        for (int i = 0; i < 144; i++) {
                            boolean isIntempinfo = false;
                            for (TemperatureDataInfo tempeinfo : temperatureinfos) {
                                int tempmin = tempeinfo.getTemperatureMinute()/10;
                                if (tempmin == i+1) {
                                    float tempvalue = Float.parseFloat(tempeinfo.getTemperatureValue());
                                    yValsTem.add(new Entry(tempvalue, i));
                                    isIntempinfo = true;
                                    break;
                                }
                            }
                            
                            if (!isIntempinfo) {
                                yValsTem.add(new Entry(0, i));
                            }
                        }
                    }
        
                    LineDataSet tempSet = new LineDataSet(yValsTem, null);
                    tempSet.setDrawCubic(true);
                    tempSet.setDrawValues(false);
                    tempSet.setDrawCircles(false);
                    tempSet.setColor(Color.WHITE);
                    tempSet.setFillColor(Color.WHITE);
                    tempSet.setFillAlpha(100);
                    tempSet.setDrawFilled(true);
             
                    ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
                    dataSets.add(tempSet);
                    LineData data = new LineData(xVals, dataSets);
                    setupChart(data, mColors[4]);
                }
            }
        }).start();*/
    }
    
    
    
    
 // 设置显示的样式  
    public void setupChart(LineData data, int color) {  
        // add data  
        mTemperatureChart.setData(data); // 设置数据  
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
    
    
    
    private class FreshChartTask extends AsyncTask<Integer, Integer, List<TemperatureDataInfo>> {
        
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            
            mTemperatureChart.setNoDataText(getApplicationContext().getResources().getString(R.string.date_initing_data));
            if (yValsTem.size() > 0) {
                yValsTem.clear();
            }
            drawTempChart(yValsTem, true);
            
        }

        @Override
        protected List<TemperatureDataInfo> doInBackground(Integer... params) {
            int year = params[0];
            int month = params[1];
            int day = params[2];
            List<TemperatureDataInfo> temperatureinfos 
            = YingerbaoDatabase.getTemperatureInfoEnumClassList(getApplicationContext(), year, month, day);
            Collections.sort(temperatureinfos, new Comparator() {
                public int compare(Object a, Object b) {
                    int one = ((TemperatureDataInfo) a).getTemperatureMinute();
                    int two = ((TemperatureDataInfo) b).getTemperatureMinute();
                    return one - two;
                }
            });
            return temperatureinfos;
        }
 
        @Override
        protected void onPostExecute(List<TemperatureDataInfo> temperatureinfos ) {
            super.onPostExecute(temperatureinfos);
            if (yValsTem.size() > 0) {
                yValsTem.clear();
            }
            if (temperatureinfos.size() > 0) {
                for (int i = 0; i < 144; i++) {
                    boolean isIntempinfo = false;
                    for (TemperatureDataInfo tempeinfo : temperatureinfos) {
                        int tempmin = tempeinfo.getTemperatureMinute()/10;
                        if (tempmin == i+1) {
                            float tempvalue = Float.parseFloat(tempeinfo.getTemperatureValue());
                            yValsTem.add(new Entry(tempvalue, i));
                            isIntempinfo = true;
                            break;
                        }
                    }
                    
                    if (!isIntempinfo) {
                        yValsTem.add(new Entry(0, i));
                    }
                }
            }
            
            drawTempChart(yValsTem, false);
        }

    }
    
    private void drawTempChart(ArrayList<Entry> yVals, boolean isload) {
        
        if (yVals.size() == 0 && !isload) {
            mTemperatureChart.setNoDataText(getApplicationContext().getResources().getString(R.string.date_no_temp_data));
        }

        LineDataSet tempSet = new LineDataSet(yVals, null);
        tempSet.setDrawCubic(true);
        tempSet.setDrawValues(false);
        tempSet.setDrawCircles(false);
        tempSet.setColor(Color.WHITE);
        tempSet.setFillColor(Color.WHITE);
        tempSet.setFillAlpha(100);
        tempSet.setDrawFilled(true);
      
 
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
        dataSets.add(tempSet);
        LineData data = new LineData(xVals, dataSets);
        setupChart(data, mColors[4]);
    }
    
    /*     for (int i = 0; i < 144; i++) {
    if (i < temperatureinfos.size()) {
        float tempvalue = Float.parseFloat(temperatureinfos.get(i).getTemperatureValue());
        if (tempvalue != 0.255 && tempvalue != 1.255) {
            int tempmin = temperatureinfos.get(i).mTemperatureMinute/10;
            yValsTem.add(new Entry(tempvalue -10, tempmin));
            xVals.add(tempmin + "");
            
            
            SLog.e(TAG, "tempvalue from database = " + tempvalue 
                    + " min = " + tempmin);
        }
    } 
}  

if (xVals.size() > 0) {
    xVals.clear();
}  
for (int i = 0; i < 144; i++) {
    xVals.add(i + "");
}*/

/*        for (int i = 97; i < 109; i++) {
    yValsTem.add(new Entry(10, i));
}*/
}
