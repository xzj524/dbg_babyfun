package com.aizi.yingerbao.slidingmenu;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aizi.yingerbao.AboutActivity;
import com.aizi.yingerbao.ConnectDeviceActivity;
import com.aizi.yingerbao.R;
import com.aizi.yingerbao.TestActivity;
import com.aizi.yingerbao.constant.Constant;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.login.LoginActivity;
import com.aizi.yingerbao.utility.PrivateParams;
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
    
    ViewGroup mAboutGroup;
    
    TextView mUserAccountTextView;
    

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
        
        mUserAccountTextView = (TextView) findViewById(R.id.useraccount);
        
        String useraccount = PrivateParams.getSPString(mActivity, Constant.AIZI_USER_ACCOUNT);
        
        if (!TextUtils.isEmpty(useraccount)) {
            mUserAccountTextView.setText(useraccount);
            SLog.e(TAG, "useraccount = " + useraccount);
        } else {
            mUserAccountTextView.setText("用户登录");
            SLog.e(TAG, "useraccount = " + "用户登录");
        }
        
        mUserAccountGroup = (ViewGroup) findViewById(R.id.user);
        mUserAccountGroup.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity.getApplicationContext(), LoginActivity.class);
                mActivity.startActivity(intent);
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
        
        mAboutGroup = (ViewGroup) findViewById(R.id.appabout);
        mAboutGroup.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity.getApplicationContext(), AboutActivity.class);
                mActivity.startActivity(intent);
            }
        });
       
        
        
        mDataTestGroup = (ViewGroup) findViewById(R.id.baby_data_test);
        mDataTestGroup.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity.getApplicationContext(), TestActivity.class);
                mActivity.startActivity(intent);
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
    
    public void setUserAccount(String useraccount) {
        mUserAccountTextView.setText(useraccount);
    }
}
