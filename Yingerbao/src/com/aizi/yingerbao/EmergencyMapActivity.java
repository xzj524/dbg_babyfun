package com.aizi.yingerbao;

import android.app.Activity;
import android.os.Bundle;
import com.aizi.yingerbao.R;

public class EmergencyMapActivity extends Activity {
    
 //   MapView mMapView = null; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
   //     SDKInitializer.initialize(getApplicationContext()); 
        setContentView(R.layout.activity_emergency_map);
        
     //   mMapView = (MapView) findViewById(R.id.bmapView);
        
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
     //   mMapView.onDestroy();
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        
      //  mMapView.onResume();
    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
     //   mMapView.onPause();
    }
    
}
