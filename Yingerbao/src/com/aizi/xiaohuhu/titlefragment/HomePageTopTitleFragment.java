package com.aizi.xiaohuhu.titlefragment;

import com.aizi.yingerbao.R;
import com.aizi.xiaohuhu.ui.component.main.RealTimeStatusFragment.OnStatusSelectedListener;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class HomePageTopTitleFragment extends Fragment {
    
    ImageView settingView;
    ImageView plusView;
    OnButtonClickedListener mListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View HomePageTopView = inflater.inflate(R.layout.homepagetoptitle, container, false);
        
        settingView = (ImageView) HomePageTopView.findViewById(R.id.settingbtn);
        //plusView = (ImageView) HomePageTopView.findViewById(R.id.plusbtn);
        
        settingView.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                mListener.OnButtonClicked(2);
            }
        });
        return HomePageTopView;
    }
    
    
    //Container Activity must implement this interface  
    public interface OnButtonClickedListener{  
        public void OnButtonClicked(int touchid);  
    } 
    
    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        mListener = (OnButtonClickedListener)activity;
    }
}
