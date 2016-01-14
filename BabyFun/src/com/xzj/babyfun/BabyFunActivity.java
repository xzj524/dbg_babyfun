package com.xzj.babyfun;

import java.util.ArrayList;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class BabyFunActivity extends Activity {
    
    private static final String TAG = BabyFunActivity.class.getSimpleName();
    
    BluetoothAdapter mAdapter;
    Button mSearchBtnButton;
    
    LineChart[] mCharts = new LineChart[4]; // 4条数据  
    Typeface mTf; // 自定义显示字体  
    int[] mColors = new int[] { Color.rgb(137, 230, 81), Color.rgb(240, 240, 30),//  
            Color.rgb(89, 199, 250), Color.rgb(250, 104, 104) }; // 自定义颜色 
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_fun);
        
        LineChart chart = (LineChart) findViewById(R.id.chart);
        // 生产数据  
        LineData data = getData(7, 100); 
        
        setupChart(chart, data, mColors[0]);  
        
        Intent enabler=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enabler,10);//鍚宻tartActivity(enabler);  鍚姩钃濈墮
        
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
        
        mAdapter= BluetoothAdapter.getDefaultAdapter();
      /*  if(!mAdapter.isEnabled()){
        	Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	//startActivityForResult(enabler, REQUEST_ENABLE);
        	      // mAdapter.enable();
        	Toast.makeText(getApplicationContext(), "钃濈墮浣胯兘 ", Toast.LENGTH_SHORT).show();
        	}else {
        		Toast.makeText(getApplicationContext(), "钃濈墮绂佺敤", Toast.LENGTH_SHORT).show();
			}*/
        
        mSearchBtnButton = (Button) findViewById(R.id.findbtn);
        mSearchBtnButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            Toast.makeText(getApplicationContext(), "resultcode = " + resultCode, Toast.LENGTH_SHORT).show();
        }
    }
    
    BroadcastReceiver mReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                
                if (device.getBondState() != BluetoothDevice.BOND_BONDED){
                    Toast.makeText(getApplicationContext(), "find device:" + device.getName()
                            + device.getAddress(), Toast.LENGTH_SHORT).show();
    
                }
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setTitle("鎼滅储瀹屾垚");
           
            }
        }
        
    };
    
    
    private void search() { //寮�鍚摑鐗欏拰璁剧疆璁惧鍙鏃堕棿
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            adapter.enable();
        }
        Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        enable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600); //3600涓鸿摑鐗欒澶囧彲瑙佹椂闂�
        startActivity(enable);
       /* Intent searchIntent = new Intent(this, ComminuteActivity.class);
        startActivity(searchIntent);*/
    }
    
    // 设置显示的样式  
    void setupChart(LineChart chart, LineData data, int color) {  
        // if enabled, the chart will always start at zero on the y-axis  
    	
   
  
        // no description text  
        chart.setDescription("");// 数据描述  
        // 如果没有数据的时候，会显示这个，类似listview的emtpyview  
        chart.setNoDataTextDescription("You need to provide data for the chart.");  
  
        // enable / disable grid lines  
       
        // mChart.setDrawHorizontalGrid(false);  
        //  
        // enable / disable grid background  
        chart.setDrawGridBackground(false); // 是否显示表格颜色  
       
        // enable touch gestures  
        chart.setTouchEnabled(true); // 设置是否可以触摸  
  
        // enable scaling and dragging  
        chart.setDragEnabled(true);// 是否可以拖拽  
        chart.setScaleEnabled(true);// 是否可以缩放  
  
        // if disabled, scaling can be done on x- and y-axis separately  
        chart.setPinchZoom(false);//   
  
        chart.setBackgroundColor(color);// 设置背景  
  
    
  
        // add data  
        chart.setData(data); // 设置数据  
  
        // get the legend (only possible after setting data)  
        Legend l = chart.getLegend(); // 设置标示，就是那个一组y的value的  
  
        // modify the legend ...  
        // l.setPosition(LegendPosition.LEFT_OF_CHART);  
        l.setForm(LegendForm.CIRCLE);// 样式  
        l.setFormSize(6f);// 字体  
        l.setTextColor(Color.WHITE);// 颜色  
        l.setTypeface(mTf);// 字体  
  
  
  
        // animate calls invalidate()...  
        chart.animateX(2500); // 立即执行的动画,x轴  
    }  
  
    // 生成一个数据，  
    LineData getData(int count, float range) {  
        ArrayList<String> xVals = new ArrayList<String>();
        String[] mMonths = {"1","2","44","5","6","7","8","9"};
        for (int i = 0; i < count; i++) {  
           
			// x轴显示的数据，这里默认使用数字下标显示  
            xVals.add(mMonths [i % 12]);  
        }  
  
        // y轴的数据  
        ArrayList<Entry> yVals = new ArrayList<Entry>();  
        for (int i = 0; i < count; i++) {  
            float val = (float) (Math.random() * range) + 3;  
            yVals.add(new Entry(val, i));  
        }  
  
        // create a dataset and give it a type  
        // y轴的数据集合  
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");  
        // set1.setFillAlpha(110);  
        // set1.setFillColor(Color.RED);  
  
        set1.setLineWidth(1.75f); // 线宽  
        set1.setCircleSize(3f);// 显示的圆形大小  
        set1.setColor(Color.WHITE);// 显示颜色  
        set1.setCircleColor(Color.WHITE);// 圆形的颜色  
        set1.setHighLightColor(Color.WHITE); // 高亮的线的颜色  
  
        ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();  
        dataSets.add(set1); // add the datasets  
  
        // create a data object with the datasets  
        LineData data = new LineData(xVals, dataSets);  
  
        return data;  
    } 
}
