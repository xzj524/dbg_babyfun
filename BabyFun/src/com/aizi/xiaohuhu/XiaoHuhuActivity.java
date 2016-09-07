package com.aizi.xiaohuhu;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aizi.xiaohuhu.adapter.FragmentAdapter;
import com.aizi.xiaohuhu.fragment.BreathFragment;
import com.aizi.xiaohuhu.fragment.SleepFragment;
import com.aizi.xiaohuhu.fragment.TemperatureFragment;
import com.aizi.xiaohuhu.logging.SLog;
import com.aizi.xiaohuhu.view.TopBarView;
import com.aizi.xiaohuhu.view.TopBarView.onTitleBarClickListener;

public class XiaoHuhuActivity extends FragmentActivity  implements onTitleBarClickListener{
    
    
    private List<Fragment> mFragmentList = new ArrayList<Fragment>();  
    private FragmentAdapter mFragmentAdapter;  
      
    private ViewPager mPageVp;  
    /** 
     * Tab显示内容TextView 
     */  
    private TextView mTabBreathTv, mTabTemperatureTv, mTabSleepTv;  
    /** 
     * Tab的那个引导线 
     */  
    private ImageView mTabLineIv;  
    /** 
     * Fragment 
     */  
    private BreathFragment mBreathFg;  
    private TemperatureFragment mTemperatureFg;  
    private SleepFragment mSleepFg;  
    /** 
     * ViewPager的当前选中页 
     */  
    private int currentIndex;  
    /** 
     * 屏幕的宽度 
     */  
    private int screenWidth;  
    
    private  TopBarView topbar;  

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiao_huhu);
        
        topbar = (TopBarView) findViewById(R.id.topbar);
        topbar.setClickListener(this);  
        
        findById();  
        init();  
        initTabLineWidth();
    }
    
    private void findById() {  
        mTabBreathTv = (TextView) this.findViewById(R.id.id_breath_tv);  
        mTabTemperatureTv = (TextView) this.findViewById(R.id.id_temperature_tv);  
        mTabSleepTv = (TextView) this.findViewById(R.id.id_sleep_tv);  
        mTabLineIv = (ImageView) this.findViewById(R.id.id_tab_line_iv);  
        mPageVp = (ViewPager) this.findViewById(R.id.id_page_vp);  
    }  
  
    private void init() {  
        mBreathFg = new BreathFragment();  
        mTemperatureFg = new TemperatureFragment();  
        mSleepFg = new SleepFragment();  
        
        mFragmentList.add(mBreathFg);  
        mFragmentList.add(mTemperatureFg);  
        mFragmentList.add(mSleepFg);   
        mFragmentAdapter = new FragmentAdapter(  
                this.getSupportFragmentManager(), mFragmentList);  
        mPageVp.setAdapter(mFragmentAdapter);  
        mPageVp.setCurrentItem(0);  
  
        mPageVp.setOnPageChangeListener(new OnPageChangeListener() {  
  
            /** 
             * state滑动中的状态 有三种状态（0，1，2） 1：正在滑动 2：滑动完毕 0：什么都没做。 
             */  
            @Override  
            public void onPageScrollStateChanged(int state) {  
  
            }  
  
            /** 
             * position :当前页面，及你点击滑动的页面 offset:当前页面偏移的百分比 
             * offsetPixels:当前页面偏移的像素位置 
             */  
            @Override  
            public void onPageScrolled(int position, float offset,  
                    int offsetPixels) {  
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv  
                        .getLayoutParams();  
  
                SLog.e("offset:", offset + "");  
                /** 
                 * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来 
                 * 设置mTabLineIv的左边距 滑动场景： 
                 * 记3个页面, 
                 * 从左到右分别为0,1,2  
                 * 0->1; 1->2; 2->1; 1->0 
                 */  
  
                if (currentIndex == 0 && position == 0)// 0->1  
                {  
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currentIndex  
                            * (screenWidth / 3));  
  
                } else if (currentIndex == 1 && position == 0) // 1->0  
                {  
                    lp.leftMargin = (int) (-(1 - offset)  
                            * (screenWidth * 1.0 / 3) + currentIndex  
                            * (screenWidth / 3));  
  
                } else if (currentIndex == 1 && position == 1) // 1->2  
                {  
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 3) + currentIndex  
                            * (screenWidth / 3));  
                } else if (currentIndex == 2 && position == 1) // 2->1  
                {  
                    lp.leftMargin = (int) (-(1 - offset)  
                            * (screenWidth * 1.0 / 3) + currentIndex  
                            * (screenWidth / 3));  
                }  
                mTabLineIv.setLayoutParams(lp);  
            }  
  
            @Override  
            public void onPageSelected(int position) {  
                resetTextView();  
                switch (position) {  
                case 0:  
                    mTabBreathTv.setTextColor(Color.BLUE);  
                    break;  
                case 1:  
                    mTabTemperatureTv.setTextColor(Color.BLUE);  
                    break;  
                case 2:  
                    mTabSleepTv.setTextColor(Color.BLUE);  
                    break;  
                }  
                currentIndex = position;  
            }  
        });  
  
    }  
  
    /** 
     * 设置滑动条的宽度为屏幕的1/3(根据Tab的个数而定) 
     */  
    private void initTabLineWidth() {  
        DisplayMetrics dpMetrics = new DisplayMetrics();  
        getWindow().getWindowManager().getDefaultDisplay()  
                .getMetrics(dpMetrics);  
        screenWidth = dpMetrics.widthPixels;  
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabLineIv  
                .getLayoutParams();  
        lp.width = screenWidth / 3;  
        mTabLineIv.setLayoutParams(lp);  
    }  
  
    /** 
     * 重置颜色 
     */  
    private void resetTextView() {  
        mTabBreathTv.setTextColor(Color.BLACK);  
        mTabTemperatureTv.setTextColor(Color.BLACK);  
        mTabSleepTv.setTextColor(Color.BLACK);  
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onRightClick() {
    }  
}
