package com.xzj.babyfun.ui.component.main;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xzj.babyfun.BabyBreathActivity;
import com.xzj.babyfun.R;

public class BabyBreathIndicateFragment extends Fragment{
    
    private View mBabyBreathView;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View babybreathIndicateView = inflater.inflate(R.layout.baby_breath_indicate, container, false);
        mBabyBreathView = babybreathIndicateView.findViewById(R.id.babybreathindcate);
       
        mBabyBreathView.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub       
                Intent intent = new Intent(getActivity(), BabyBreathActivity.class);
               // Intent intent = new Intent(getActivity(), TestActivity.class);
                startActivity(intent); 
                
                getActivity().overridePendingTransition(R.anim.main_special_activity_open_enter, R.anim.main_special_activity_open_exit);
            }
        });
        
        return babybreathIndicateView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

}
