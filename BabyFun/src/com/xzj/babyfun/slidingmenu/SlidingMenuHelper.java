package com.xzj.babyfun.slidingmenu;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.xzj.babyfun.CriticalActivity;
import com.xzj.babyfun.R;

public class SlidingMenuHelper {
    private Activity mActivity;

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
        
        ViewGroup mMessageCenterViewGroup = (ViewGroup) findViewById(R.id.message_center_view);
        mMessageCenterViewGroup.setOnClickListener(new View.OnClickListener() {
         
         @Override
         public void onClick(View v) {
             // TODO Auto-generated method stub
             Intent intent = new Intent(mActivity.getApplicationContext(), CriticalActivity.class);
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


}
