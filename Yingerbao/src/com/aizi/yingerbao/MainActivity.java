package com.aizi.yingerbao;

import java.util.ArrayList;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import com.aizi.yingerbao.R;

public class MainActivity extends Activity implements OnClickListener, OnPageChangeListener {

    // 定义ViewPager对象
    private ViewPager mViewPager;
    // 定义ViewPager适配器
    private ViewPagerAdapter mVpAdapter;
    // 定义一个ArrayList来存放View
    private ArrayList<View> mViews;
    // 引导图片资源
    private static final int[] pics = { R.drawable.guide1, R.drawable.guide2, R.drawable.guide3, R.drawable.guide4 };
    // 底部小点的图片
    private ImageView[] points;
    // 记录当前选中位置
    private int currentIndex;
    
    Button mStartButton;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        mStartButton = (Button) findViewById(R.id.start);
        mStartButton.setVisibility(View.INVISIBLE);
        mStartButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                finish();
                Intent intent = new Intent(mContext, SplashActivity.class);
                startActivity(intent);
            }
        });
        
        SharedPreferences  preferences = getSharedPreferences("StartStatus", Context.MODE_PRIVATE);  
        //判断是不是首次登录，  
        if (preferences.getBoolean("firststart", true)) {  
             Editor editor = preferences.edit();  
             //将登录标志位设置为false，下次登录时不在显示首次登录界面  
             editor.putBoolean("firststart", false);  
             editor.commit();  
             initView();
             initData();
        }else {
            Intent intent = new Intent(mContext, SplashActivity.class);
            startActivity(intent); 
            finish(); 
        }
        
       
    }

    /**
     * 初始化组件
     */
    private void initView() {
        // 实例化ArrayList对象
        mViews = new ArrayList<View>();
        // 实例化ViewPager
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        // 实例化ViewPager适配器
        mVpAdapter = new ViewPagerAdapter(mViews);
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 定义一个布局并设置参数
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        // 初始化引导图片列表
        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            // 防止图片不能填满屏幕
            iv.setScaleType(ScaleType.FIT_XY);
            // 加载图片资源
            iv.setImageResource(pics[i]);
            mViews.add(iv);
        }

        // 设置数据
        mViewPager.setAdapter(mVpAdapter);
        // 设置监听
        mViewPager.setOnPageChangeListener(this);

        // 初始化底部小点
        initPoint();
    }

    /**
     * 初始化底部小点
     */
    private void initPoint() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);

        points = new ImageView[pics.length];

        // 循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            // 得到一个LinearLayout下面的每一个子元素
            points[i] = (ImageView) linearLayout.getChildAt(i);
            // 默认都设为灰色
            points[i].setEnabled(true);
            // 给每个小点设置监听
            points[i].setOnClickListener(this);
            // 设置位置tag，方便取出与当前位置对应
            points[i].setTag(i);
        }

        // 设置当面默认的位置
        currentIndex = 0;
        // 设置为白色，即选中状态
        points[currentIndex].setEnabled(false);
    }

    /**
     * 设置当前页面的位置
     */
    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        mViewPager.setCurrentItem(position);
    }

    /**
     * 设置当前的小点的位置
     */
    private void setCurDot(int positon) {
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
            return;
        }
        points[positon].setEnabled(false);
        points[currentIndex].setEnabled(true);

        currentIndex = positon;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPageSelected(int arg0) {
        // TODO Auto-generated method stub
        setCurDot(arg0);
        Log.e("click", "click = " + arg0);
        if (arg0 == 3) {
            mStartButton.setVisibility(View.VISIBLE);
        }else {
            mStartButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int position = (Integer) v.getTag();
        setCurView(position);
        Log.e("click", "click = " + position);
      
    }
}
