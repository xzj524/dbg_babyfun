package com.aizi.huxibao;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import com.aizi.yingerbao.R;

public class EmergencyPhoneNumberActivity extends Activity {
    
    ViewGroup mEmergency120Group;
    ViewGroup mEmergencyHospital;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_phone_number);
        
        mEmergency120Group = (ViewGroup) findViewById(R.id.emergency_120_view);
        mEmergency120Group.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

               dialPhoneNumber("120");
            }
        });
        
        mEmergencyHospital = (ViewGroup) findViewById(R.id.emergency_hospital_view);
        mEmergencyHospital.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
              // dialPhoneNumber("01059616161");
                Intent intent;
                try {
                    intent = Intent.getIntent("intent://map/place/search?" +
                    		"query=医院&radius=1000&region=北京" +
                    		"&src=yourCompanyName|yourAppName#Intent;" +
                    		"scheme=bdapp;package=com.baidu.BaiduMap;end");
                    startActivity(intent); //启动调用
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }  
                
            }
        });
    }
    
    public void dialPhoneNumber(String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phoneNumber));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
