package com.aizi.xiaohuhu.view;

import com.aizi.xiaohuhu.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TopBarView extends RelativeLayout implements OnClickListener{
    
    private ImageView backView;  
    private ImageView rightView;  
    private ImageView calendarView;
    private TextView titleView;  
   
    private String titleTextStr;   
    private int titleTextSize ;  
    private int  titleTextColor ;  
   
    private Drawable leftImage ;  
    private Drawable rightImage ;  
    private Drawable calendarImage ;  

    public TopBarView(Context context, AttributeSet attrs) {
        this(context, attrs, R.style.AppTheme);
    }

    public TopBarView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }
    
    public TopBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getConfig(context, attrs);    
        initView(context);
    }
 
    /**  
     * 从xml中获取配置信息  
     */   
    private void getConfig(Context context, AttributeSet attrs) {  
        //TypedArray是一个数组容器用于存放属性值    
        TypedArray ta = context.obtainStyledAttributes(attrs,R.styleable.Topbar);    
           
        int count = ta.getIndexCount();  
        for(int i = 0;i<count;i++)  
        {  
            int attr = ta.getIndex(i);    
            switch (attr)    
            {    
            case R.styleable.Topbar_titleText:    
                titleTextStr = ta.getString(R.styleable.Topbar_titleText);      
                break;    
            case R.styleable.Topbar_titleColor:    
                // 默认颜色设置为黑色    
                titleTextColor = ta.getColor(attr, Color.BLACK);    
                break;    
            case R.styleable.Topbar_titleSize:    
                // 默认设置为16sp，TypeValue也可以把sp转化为px    
                titleTextSize = ta.getDimensionPixelSize(attr, (int) TypedValue.applyDimension(    
                        TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics()));    
                break;    
            case R.styleable.Topbar_leftBtn:     
                leftImage = ta.getDrawable(R.styleable.Topbar_leftBtn);    
                break;   
            case R.styleable.Topbar_rightBtn:    
                rightImage = ta.getDrawable(R.styleable.Topbar_rightBtn);   
                break;   
            case R.styleable.Topbar_calendarBtn:    
                calendarImage = ta.getDrawable(R.styleable.Topbar_calendarBtn);   
                break;   
            }   
        }  
   
        //用完务必回收容器    
        ta.recycle();   
       
 }  
   
   
 private void initView(Context context) {  
     View layout = LayoutInflater.from(context).inflate(R.layout.topbar,   
             this,true);  
          
        backView = (ImageView) layout.findViewById(R.id.back_image);  
        titleView = (TextView) layout.findViewById(R.id.text_title);  
        rightView = (ImageView) layout.findViewById(R.id.right_image);
        calendarView = (ImageView) layout.findViewById(R.id.calendar_image);
        
        backView.setOnClickListener(this);  
        rightView.setOnClickListener(this);  
        calendarView.setOnClickListener(this);
          
        if(null != leftImage){
            backView.setImageDrawable(leftImage);  
        }
        
        if(null != rightImage) {
            rightView.setImageDrawable(rightImage);  
        }
        
        if (null != calendarImage) {
            calendarView.setImageDrawable(calendarImage);
        }
        
        if(null != titleTextStr) {  
         titleView.setText(titleTextStr);  
         titleView.setTextSize(titleTextSize);  
         titleView.setTextColor(titleTextColor);  
        }  
    } 
 
 public void setTitle(String title) {
    titleView.setText(title);
}
 
 private onTitleBarClickListener onMyClickListener;  
 
 /** 
  * 设置按钮点击监听接口 
  * @param callback 
  */  
 public void setClickListener(onTitleBarClickListener listener) {  
     this.onMyClickListener = listener;  
 }  

 /** 
  * 导航栏点击监听接口 
  */  
 public static interface onTitleBarClickListener{  
     /** 
      * 点击返回按钮回调 
      */  
     void onBackClick();  

     void onRightClick(); 
     
     void onCalendarClick();
 }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        int id = v.getId();  
        switch(id)  
        {  
        case R.id.back_image:  
            if(null != onMyClickListener)  
            onMyClickListener.onBackClick();  
            break;  
        case R.id.right_image:  
            if(null != onMyClickListener)  
            onMyClickListener.onRightClick();  
            break;  
        case R.id.calendar_image:  
            if(null != onMyClickListener)  
            onMyClickListener.onCalendarClick();  
            break;  
        }    
    }  

}
