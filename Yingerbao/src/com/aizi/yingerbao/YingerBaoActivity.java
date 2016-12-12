package com.aizi.yingerbao;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aizi.yingerbao.adapter.FragmentAdapter;
import com.aizi.yingerbao.bluttooth.BluetoothApi;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.fragment.BreathFragment;
import com.aizi.yingerbao.fragment.SimpleCalendarDialogFragment;
import com.aizi.yingerbao.fragment.SleepFragment;
import com.aizi.yingerbao.fragment.TemperatureFragment;
import com.aizi.yingerbao.slidingmenu.SlidingMenuHelper;
import com.aizi.yingerbao.synctime.DataTime;
import com.aizi.yingerbao.utility.PrivateParams;
import com.aizi.yingerbao.utility.Utiliy;
import com.aizi.yingerbao.view.TopBarView;
import com.aizi.yingerbao.view.TopBarView.onTitleBarClickListener;

import de.greenrobot.event.EventBus;

public class YingerBaoActivity extends FragmentActivity  implements onTitleBarClickListener{
    
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
    private View mBreathTab;
    private View mTemperatureTab;
    SlidingMenuHelper mSlidingMenuHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_xiao_huhu);
        
        /*UpdateHelper.getInstance().init(getApplicationContext(), Color.parseColor("#0A93DB"));
        UpdateHelper.getInstance().setDebugMode(true);
        long intervalMillis = 100 * 1000L;           //第一次调用startUpdateSilent出现弹窗后，如果10秒内进行第二次调用不会查询更新
        UpdateHelper.getInstance().autoUpdate(getPackageName(), false, intervalMillis);
*/        
        topbar = (TopBarView) findViewById(R.id.xiaohuhutopbar);
        topbar.setClickListener(this);
        
        int year =  PrivateParams.getSPInt(getApplicationContext(), Constant.DATA_DATE_YEAR, 0);
        int month =  PrivateParams.getSPInt(getApplicationContext(), Constant.DATA_DATE_MONTH, 0);
        int day =  PrivateParams.getSPInt(getApplicationContext(), Constant.DATA_DATE_DAY, 0);
        
        if (year == 0 || month == 0 || day == 0) {
            Calendar calendar = Calendar.getInstance();   
            year = calendar.get(Calendar.YEAR);      
            month = calendar.get(Calendar.MONTH) + 1;     
            day = calendar.get(Calendar.DAY_OF_MONTH);   
        } 
        
        topbar.setTitle( year + "年" + month + "月" + day + "日");
        
        EventBus.getDefault().register(this);
        
        mSlidingMenuHelper = new SlidingMenuHelper(this);
        mSlidingMenuHelper.initSlidingMenu();
        
        BluetoothApi.getInstance(getApplicationContext());
        
        findById();  
        init();  
        initTabLineWidth();
        
        if (!Utiliy.isBluetoothReady(getApplicationContext())) {
            //Utiliy.showNormalDialog(this);
        }
        
    }
    
    private void findById() {  
        mTabBreathTv = (TextView) this.findViewById(R.id.id_breath_tv);  
        mTabTemperatureTv = (TextView) this.findViewById(R.id.id_temperature_tv);  
        //mTabSleepTv = (TextView) this.findViewById(R.id.id_sleep_tv);  
        mTabLineIv = (ImageView) this.findViewById(R.id.id_tab_line_iv);  
        mPageVp = (ViewPager) this.findViewById(R.id.id_page_vp);  
        
        mBreathTab = (View) findViewById(R.id.id_tab_breath_ll);
        mTemperatureTab = (View) findViewById(R.id.id_tab_temperature_ll);
    }  
  
    private void init() {  
        mBreathFg = new BreathFragment();  
        mTemperatureFg = new TemperatureFragment();  
        mSleepFg = new SleepFragment();  
        
        mFragmentList.add(mBreathFg);  
        mFragmentList.add(mTemperatureFg);  
        //mFragmentList.add(mSleepFg);   
        mFragmentAdapter = new FragmentAdapter(  
                this.getSupportFragmentManager(), mFragmentList);  
        mPageVp.setAdapter(mFragmentAdapter);  
        mPageVp.setCurrentItem(0);  
        
        mBreathTab.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mPageVp.setCurrentItem(0); 
            }
        });
        
        mTemperatureTab.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mPageVp.setCurrentItem(1); 
            }
        });
        
        
  
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
  
                //SLog.e("offset:", offset + "");  
                /** 
                 * 利用currentIndex(当前所在页面)和position(下一个页面)以及offset来 
                 * 设置mTabLineIv的左边距 滑动场景： 
                 * 记3个页面, 
                 * 从左到右分别为0,1,2  
                 * 0->1; 1->2; 2->1; 1->0 
                 */  
  
                if (currentIndex == 0 && position == 0)// 0->1  
                {  
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 2) + currentIndex  
                            * (screenWidth / 2));  
  
                } else if (currentIndex == 1 && position == 0) // 1->0  
                {  
                    lp.leftMargin = (int) (-(1 - offset)  
                            * (screenWidth * 1.0 / 2) + currentIndex  
                            * (screenWidth / 2));  
  
                } else if (currentIndex == 1 && position == 1) // 1->2  
                {  
                    lp.leftMargin = (int) (offset * (screenWidth * 1.0 / 2) + currentIndex  
                            * (screenWidth / 2));  
                } /*else if (currentIndex == 2 && position == 1) // 2->1  
                {  
                    lp.leftMargin = (int) (-(1 - offset)  
                            * (screenWidth * 1.0 / 3) + currentIndex  
                            * (screenWidth / 3));  
                }  */
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
/*                case 2:  
                    mTabSleepTv.setTextColor(Color.BLUE);  
                    break;  */
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
        //lp.width = screenWidth / 3; 
        lp.width = screenWidth / 2;  
        mTabLineIv.setLayoutParams(lp);  
    }  
  
    /** 
     * 重置颜色 
     */  
    private void resetTextView() {  
        mTabBreathTv.setTextColor(Color.BLACK);  
        mTabTemperatureTv.setTextColor(Color.BLACK);  
       // mTabSleepTv.setTextColor(Color.BLACK);  
    }

    @Override
    public void onBackClick() {
        mSlidingMenuHelper.showMenu();
    }

    @Override
    public void onRightClick() {
    }

    @Override
    public void onCalendarClick() {
        SimpleCalendarDialogFragment mFragment = new SimpleCalendarDialogFragment();
        mFragment.show(getFragmentManager(), "simple-calendar");
        
    }  
    
    public void onEventMainThread(DataTime dataTime) {  
        //Date date = new Date(timemili);
        int year = dataTime.year;
        int month = dataTime.month;
        int day = dataTime.day;
        
        topbar.setTitle(year+"年"+month+"月"+day+"日");
    } 
    
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        PrivateParams.setSPInt(getApplicationContext(), Constant.BLUETOOTH_IS_READY, 0);
        BluetoothApi.getInstance(getApplicationContext()).mBluetoothService.disconnect();
    }

}
