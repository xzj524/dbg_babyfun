package com.xzj.babyfun;

import java.util.ArrayList;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.xzj.babyfun.chart.BreathChart;
import com.xzj.babyfun.ui.component.main.RouterStatusFragment;

import android.R.integer;
import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class BabyBreathActivity extends Activity {
    
    private Button mFreshButton;
    private FragmentManager mFragmentMan;
    private BreathChart breathchartFragment;
    int preValue = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_breath);
        mFreshButton = (Button) findViewById(R.id.breathbtn);
        mFreshButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
               // breathchartFragment.freshChart();
                preValue = (int) (Math.random() * 100);
                breathchartFragment.generateNewWave(preValue++);
                
                
            }
        });
        
        mFragmentMan = getFragmentManager();
        breathchartFragment = (BreathChart) mFragmentMan.findFragmentById(R.id.babybreathChartFragment);
    }
}
