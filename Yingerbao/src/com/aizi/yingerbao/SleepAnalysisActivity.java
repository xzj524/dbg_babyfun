package com.aizi.yingerbao;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.aizi.yingerbao.R;

public class SleepAnalysisActivity extends Activity {
    
    ViewGroup mSleepTimeGroup;
    ViewGroup mSleepNoenough;
    ViewGroup mSleepHardGroup;
    ViewGroup mSleepWakeGroup;
    ViewGroup mSleepDistributeGroup;
    ViewGroup mSleepEficienceGroup;
    
    public static final Short SLEEPTIME = 1;
    public static final Short SLEEPNOTENOUGH = 2;
    public static final Short SLEEPHARD = 3;
    public static final Short SLEEPWAKE = 4;
    public static final Short SLEEPDISTRIBUTION = 5;
    public static final Short SLEEPEFICIENCE = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep_analysis);
        
        mSleepTimeGroup = (ViewGroup) findViewById(R.id.sleep_time_view);
        mSleepTimeGroup.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), SleepProblemActivity.class);
                intent.putExtra("problem_type", SLEEPTIME);
                intent.putExtra("title", "实际睡眠时长过短");
                intent.putExtra("fator", "睡眠时间少于正常睡眠时长");
                intent.putExtra("explain", "睡眠不足会使人感到精神不济，反应迟钝，记忆力衰退。");
                intent.putExtra("comment", "一般成年人正常睡眠时间在7、8小时左右。");
                startActivity(intent);
            }
        });
        
        mSleepNoenough = (ViewGroup) findViewById(R.id.sleep_noenough_view);
        mSleepNoenough.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), SleepProblemActivity.class);
                intent.putExtra("problem_type", SLEEPNOTENOUGH);
                intent.putExtra("title", "深睡眠不足");
                intent.putExtra("fator", "深睡眠时间不足");
                intent.putExtra("explain", "缺乏有效的深睡眠会导致精神萎靡、头痛、反胃、肌肉酸痛。");
                intent.putExtra("comment", "睡前六小时不要喝咖啡和浓茶，尽量不要饮酒。");
                startActivity(intent);
            }
        });
        
        mSleepHardGroup = (ViewGroup) findViewById(R.id.sleep_hard_view);
        mSleepHardGroup.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), SleepProblemActivity.class);
                intent.putExtra("problem_type", SLEEPHARD);
                intent.putExtra("title", "难以入睡");
                intent.putExtra("fator", "超过15分钟无法入眠");
                intent.putExtra("explain", "超过15分钟无法入眠。");
                intent.putExtra("comment", "入眠时间较长");
                startActivity(intent);
            }
        });
        
        mSleepWakeGroup = (ViewGroup) findViewById(R.id.sleep_wake_view);
        mSleepWakeGroup.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), SleepProblemActivity.class);
                intent.putExtra("problem_type", SLEEPWAKE);
                intent.putExtra("title", "睡觉易醒");
                intent.putExtra("fator", "睡眠中清醒了2次");
                intent.putExtra("explain", "睡眠中中途清醒次数过多，表示夜间睡眠不安稳，大多处于浅睡眠状态，导致精神疲倦");
                intent.putExtra("comment", "建议每天保持适量运动。");
                startActivity(intent);
            }
        });
        
        mSleepDistributeGroup = (ViewGroup) findViewById(R.id.sleep_distribute_view);
        mSleepDistributeGroup.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), SleepProblemActivity.class);
                intent.putExtra("problem_type", SLEEPDISTRIBUTION);
                intent.putExtra("title", "良性睡眠分布");
                intent.putExtra("fator", "深睡眠和中睡眠部分的比例分布不合理");
                intent.putExtra("explain", "良性睡眠是指睡眠过程中中睡眠和深睡眠部分，此部分是身体机能进行自我恢复的关键时期。");
                intent.putExtra("comment", "睡前六小时不要喝咖啡和浓茶，尽量不要饮酒。");
                startActivity(intent);
            }
        });
        
        mSleepEficienceGroup = (ViewGroup) findViewById(R.id.sleep_effcience_view);
        mSleepEficienceGroup.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getApplicationContext(), SleepProblemActivity.class);
                intent.putExtra("problem_type", SLEEPEFICIENCE);
                intent.putExtra("title", "睡眠效率");
                intent.putExtra("fator", "睡眠效率有待提升");
                intent.putExtra("explain", "睡眠效率是指除清醒外的睡眠时间占床上睡觉总时间的百发比。");
                intent.putExtra("comment", "建议尝试在两周内，每天都在规定的时间起床，每晚感到疲倦就上床休息。");
                startActivity(intent);
            }
        });
    }
}
