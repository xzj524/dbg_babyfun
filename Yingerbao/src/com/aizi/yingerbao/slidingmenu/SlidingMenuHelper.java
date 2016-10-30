package com.aizi.yingerbao.slidingmenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.aizi.yingerbao.BabyBreathActivity;
import com.aizi.yingerbao.ConnectDeviceActivity;
import com.aizi.yingerbao.BabyStatusActivity;
import com.aizi.yingerbao.CriticalActivity;
import com.aizi.yingerbao.R;
import com.aizi.yingerbao.TestActivity;
import com.aizi.yingerbao.YingerBaoActivity;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.deviceinterface.AsyncDeviceFactory;
import com.aizi.yingerbao.login.LoginActivity;
import com.aizi.yingerbao.sleepdatabase.BreathStopInfo;
import com.aizi.yingerbao.sleepdatabase.SleepInfoDatabase;
import com.aizi.yingerbao.userdatabase.UserAccountDataBase;
import com.aizi.yingerbao.userdatabase.UserAccountInfo;
import com.aizi.yingerbao.utility.PrivateParams;
import com.aizi.yingerbao.utility.Utiliy;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

public class SlidingMenuHelper {
    
    private static final String TAG = SlidingMenuHelper.class.getSimpleName();
    
    private Activity mActivity;
    ViewGroup mMessageCenterViewGroup;
    ViewGroup mBabyBreathViewGroup;
    ViewGroup mBabySleepViewGroup;
    ViewGroup mSettingsViewGroup;
    ViewGroup mSyncDataViewGroup;
    
    ViewGroup mUserAccountGroup;
    ViewGroup mQuitUserAccountGroup;
    ViewGroup mDataTestGroup;
    ViewGroup mSearchGroup;
    

    private SlidingMenu mSlidingMenu;

    public SlidingMenuHelper(Activity activity) {
        mActivity = activity;
    }
    
    
    public void initSlidingMenu() {  
        
        mSlidingMenu = new SlidingMenu(mActivity);
        mSlidingMenu.setMode(SlidingMenu.LEFT);  
       // 设置触摸屏幕的模式  
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        
        mSlidingMenu.setShadowWidthRes(R.dimen.shadow_width);  
       // 设置滑动菜单视图的宽度  
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);  
       // 设置渐入渐出效果的值  
        mSlidingMenu.setFadeDegree(0.35f);  
        mSlidingMenu.attachToActivity(mActivity, SlidingMenu.SLIDING_CONTENT);  
       //为侧滑菜单设置布局  
        mSlidingMenu.setMenu(R.layout.slidingmenu); 
        
        mUserAccountGroup = (ViewGroup) findViewById(R.id.useraccount);
        mUserAccountGroup.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(mActivity.getApplicationContext(), LoginActivity.class);
                mActivity.startActivity(intent);
            }
        });
        
       /* mMessageCenterViewGroup = (ViewGroup) findViewById(R.id.message_center_view);
        mMessageCenterViewGroup.setOnClickListener(new View.OnClickListener() {
         
         @Override
         public void onClick(View v) {
             // TODO Auto-generated method stub
             Intent intent = new Intent(mActivity.getApplicationContext(), CriticalActivity.class);
             mActivity.startActivity(intent);
         }
     });*/
        
      /*  mBabyBreathViewGroup = (ViewGroup) findViewById(R.id.baby_breath_view);
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
     });*/
        
        mSyncDataViewGroup = (ViewGroup) findViewById(R.id.baby_syncdata_view);
        mSyncDataViewGroup.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                
      /*          if (!UserAccountDataBase.checkUserAccountAndPassword(mActivity.getApplicationContext(),
                        "18811130187", "123456")) {
                    UserAccountInfo useraccountinfo = new UserAccountInfo();
                    useraccountinfo.mUserAccountName = "18811130187";
                    useraccountinfo.mUserAccountInfoPassWord = "123456";
                    useraccountinfo.mUserAccountTimestamp = System.currentTimeMillis();
                    useraccountinfo.mUserAccountPosition = "beijing";
                    UserAccountDataBase.insertUserAccountInfo(mActivity.getApplicationContext(),
                            useraccountinfo );
                }*/
                
                Intent intent = new Intent(mActivity.getApplicationContext(), YingerBaoActivity.class);
                mActivity.startActivity(intent);
                
               // AsyncDeviceFactory.getInstance(mActivity.getApplicationContext()).getBreathStopInfo();
                
                
                
                
                
             /*   TemperatureInfo temperatureinfo = new TemperatureInfo();
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
                
                SleepInfoDatabase.insertSleepInfo(mActivity.getApplicationContext(), sleepInfo);*/
            }
        });
        
        
        mSearchGroup = (ViewGroup) findViewById(R.id.baby_search_device);
        mSearchGroup.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(mActivity.getApplicationContext(), ConnectDeviceActivity.class);
                mActivity.startActivity(intent);
            }
        });
       
        
        
        mDataTestGroup = (ViewGroup) findViewById(R.id.baby_data_test);
        mDataTestGroup.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(mActivity.getApplicationContext(), TestActivity.class);
                mActivity.startActivity(intent);
                
                //Utiliy.logToFile("12345456767899");
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
