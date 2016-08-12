package com.xzj.babyfun;

import com.xzj.babyfun.utility.Utiliy;


import android.app.Activity;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class CriticalActivity extends Activity {
    
    Button mFever;
    Button mBreathAbnormal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_critical);
        
        mFever = (Button) findViewById(R.id.fever);
        mBreathAbnormal = (Button) findViewById(R.id.breathabnormal);
        
        mFever.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Utiliy.showFeverNotification(getApplicationContext(), 
                        "孩子发烧了！！", "孩子发烧了，请及时就医。", null);
               
               /* PendingIntent pendingIntent3 = PendingIntent.getActivity(this, 0,  
                        new Intent(this, MainActivity.class), 0);  
                // 通过Notification.Builder来创建通知，注意API Level  
                // API16之后才支持  
                Notification notify3 = new Notification.Builder(this)  
                        .setSmallIcon(R.drawable.ic_launcher)  
                        .setTicker("TickerText:" + "您有新短消息，请注意查收！")  
                        .setContentTitle("Notification Title")  
                        .setContentText("This is the notification message")  
                        .setContentIntent(pendingIntent3).setNumber(1).build(); // 需要注意build()是在API  
                                                                                // level16及之后增加的，API11可以使用getNotificatin()来替代  
                notify3.flags |= Notification.FLAG_AUTO_CANCEL; // FLAG_AUTO_CANCEL表明当通知被用户点击时，通知将被清除。  
                manager.notify(0, notify3);// 步骤4：通过通知管理器来发起通知。如果id不同，则每click，在status哪里增加一个提示  
                */
            }
        });
        
        mBreathAbnormal.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Utiliy.showBreathNotification(getApplicationContext(), 
                        "孩子呼吸停滞！！", "孩子呼吸停滞，请及时处理。", null);
            }
        });
    }
}
