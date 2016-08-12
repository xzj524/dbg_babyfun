package com.xzj.babyfun;

import android.app.Activity;
import android.os.Bundle;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.cloud.CloudSearchResult;
import com.baidu.mapapi.map.MapView;

public class EmergencyMapActivity extends Activity {
    
    MapView mMapView = null; 

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext()); 
        setContentView(R.layout.activity_emergency_map);
        
        mMapView = (MapView) findViewById(R.id.bmapView);
        
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mMapView.onDestroy();
    }
    
    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        
        mMapView.onResume();
    }
    
    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        mMapView.onPause();
    }
    
    public void onGetSearchResult(CloudSearchResult result, int error) {  
        //在此处理相应的检索结果  
    }
}
