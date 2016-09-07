package com.aizi.xiaohuhu.fragment;

import com.aizi.xiaohuhu.R;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TemperatureFragment extends Fragment{

   @Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    // TODO Auto-generated method stub
       
    View temperatureView = inflater.inflate(R.layout.activity_tab_temperature, container,false); 
    return temperatureView;
}
   @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
    }
}
