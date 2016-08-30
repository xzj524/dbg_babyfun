package com.aizi.xiaohuhu.slidingmenu;

import android.app.Activity;

import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.aizi.xiaohuhu.BabyBreathActivity;
import com.aizi.xiaohuhu.BabyStatusActivity;
import com.aizi.xiaohuhu.CriticalActivity;
import com.aizi.xiaohuhu.R;
import com.aizi.xiaohuhu.deviceinterface.AsyncDeviceFactory;
import com.aizi.xiaohuhu.logging.SLog;
import com.aizi.xiaohuhu.login.LoginActivity;
import com.aizi.xiaohuhu.sleepdatabase.BreathStopInfo;
import com.aizi.xiaohuhu.sleepdatabase.SleepInfo;
import com.aizi.xiaohuhu.sleepdatabase.SleepInfoDatabase;
import com.aizi.xiaohuhu.sleepdatabase.TemperatureInfo;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class SlidingMenuHelper {
    
    private static final String TAG = SlidingMenuHelper.class.getSimpleName();
    
    private Activity mActivity;
    ViewGroup mMessageCenterViewGroup;
    ViewGroup mBabyBreathViewGroup;
    ViewGroup mBabySleepViewGroup;
    ViewGroup mSettingsViewGroup;
    ViewGroup mSyncDataViewGroup;

    private SlidingMenu mSlidingMenu;

    public SlidingMenuHelper(Activity activity) {
        mActivity = activity;
    }
    
    
    public void initSlidingMenu() {  
        
        mSlidingMenu = new SlidingMenu(mActivity);
        mSlidingMenu.setMode(SlidingMenu.LEFT);  
       // 设置触摸屏幕的模式  
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);  
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);  
       // 设置滑动菜单视图的宽度  
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);  
       // 设置渐入渐出效果的值  
        mSlidingMenu.setFadeDegree(0.35f);  
        mSlidingMenu.attachToActivity(mActivity, SlidingMenu.SLIDING_CONTENT);  
       //为侧滑菜单设置布局  
        mSlidingMenu.setMenu(R.layout.slidingmenu); 
        
        mMessageCenterViewGroup = (ViewGroup) findViewById(R.id.message_center_view);
        mMessageCenterViewGroup.setOnClickListener(new View.OnClickListener() {
         
         @Override
         public void onClick(View v) {
             // TODO Auto-generated method stub
             Intent intent = new Intent(mActivity.getApplicationContext(), CriticalActivity.class);
             mActivity.startActivity(intent);
         }
     });
        
        mBabyBreathViewGroup = (ViewGroup) findViewById(R.id.baby_breath_view);
        mBabyBreathViewGroup.setOnClickListener(new View.OnClickListener() {
         
         @Override
         public void onClick(View v) {
             // TODO Auto-generated method stub
             Intent intent = new Intent(mActivity.getApplicationContext(), BabyBreathActivity.class);
             mActivity.startActivity(intent);
         }
     });
        
        mBabySleepViewGroup = (ViewGroup) findViewById(R.id.baby_sleep_view);
        mBabySleepViewGroup.setOnClickListener(new View.OnClickListener() {
         
         @Override
         public void onClick(View v) {
             // TODO Auto-generated method stub
             Intent intent = new Intent(mActivity.getApplicationContext(), BabyStatusActivity.class);
             mActivity.startActivity(intent);
         }
     });
        
        mSettingsViewGroup = (ViewGroup) findViewById(R.id.baby_settings_view);
        mSettingsViewGroup.setOnClickListener(new View.OnClickListener() {
         
         @Override
         public void onClick(View v) {
             // TODO Auto-generated method stub
             Intent intent = new Intent(mActivity.getApplicationContext(), LoginActivity.class);
             mActivity.startActivity(intent);
             AsyncDeviceFactory.getInstance(mActivity.getApplicationContext()).getAllNoSyncInfo();
         }
     });
        
        mSyncDataViewGroup = (ViewGroup) findViewById(R.id.baby_syncdata_view);
        mSyncDataViewGroup.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                AsyncDeviceFactory.getInstance(mActivity.getApplicationContext()).getBreathStopInfo();
                
                BreathStopInfo breathStopInfo = new BreathStopInfo();
                breathStopInfo.mBreathYear = 2016;
                breathStopInfo.mBreathMonth = 8;
                breathStopInfo.mBreathDay = 27;
                breathStopInfo.mBreathHour = 20;
                breathStopInfo.mBreathMinute = 36;
                breathStopInfo.mBreathSecond = 0;
                breathStopInfo.mBreathDuration = 10;
                breathStopInfo.mBreathIsAlarm = 1;
                breathStopInfo.mBreathTimestamp = System.currentTimeMillis();
                
                SleepInfoDatabase.insertBreathInfo(mActivity.getApplicationContext(), breathStopInfo);
                
                TemperatureInfo temperatureinfo = new TemperatureInfo();
                temperatureinfo.mTemperatureTimestamp = System.currentTimeMillis();
                temperatureinfo.mTemperatureValue = "35.24";
                temperatureinfo.mTemperatureYear = 2016;
                temperatureinfo.mTemperatureMonth = 8;
                temperatureinfo.mTemperatureDay = 28;
                temperatureinfo.mTemperatureMinute = 16;
                
                SleepInfoDatabase.insertTemperatureInfo(mActivity.getApplicationContext(), temperatureinfo);
                SLog.e(TAG, "insertTemperatureInfo");
                
                SleepInfo sleepInfo = new SleepInfo();
                sleepInfo.mSleepYear = 2016;
                sleepInfo.mSleepMonth = 8;
                sleepInfo.mSleepDay = 28;
                sleepInfo.mSleepMinute = 10;
                sleepInfo.mSleepTimestamp = System.currentTimeMillis();
                sleepInfo.mSleepValue = 100;
                
                SleepInfoDatabase.insertSleepInfo(mActivity.getApplicationContext(), sleepInfo);
            }
        });
        
    }  
    
    
    /**
     * Finds a view that was identified by the id attribute from the XML that was processed in onCreate(Bundle).
     * 
     * @param id the resource id of the desired view
     * @return The view if found or null otherwise.
     */
    public View findViewById(int id) {
        View v;
        if (mSlidingMenu != null) {
            v = mSlidingMenu.findViewById(id);
            if (v != null)
                return v;
        }
        return null;
    }

    
    /**
     * Gets the SlidingMenu associated with this activity.
     *
     * @return the SlidingMenu associated with this activity.
     */
    public SlidingMenu getSlidingMenu() {
        return mSlidingMenu;
    }
    
    /**
     * Toggle the SlidingMenu. If it is open, it will be closed, and vice versa.
     */
    public void toggle() {
        mSlidingMenu.toggle();
    }

    /**
     * Close the SlidingMenu and show the content view.
     */
    public void showContent() {
        mSlidingMenu.showContent();
    }

    /**
     * Open the SlidingMenu and show the menu view.
     */
    public void showMenu() {
        mSlidingMenu.showMenu();
    }

    /**
     * Open the SlidingMenu and show the secondary menu view. Will default to the regular menu
     * if there is only one.
     */
    public void showSecondaryMenu() {
        mSlidingMenu.showSecondaryMenu();
    }


}
