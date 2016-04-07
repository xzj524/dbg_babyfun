package com.xzj.babyfun.ui.component.main;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.xzj.babyfun.BabyStatusActivity;
import com.xzj.babyfun.R;

public class BabyStatusIndicateFragment extends Fragment{
    
    TextView mBabyStatus;
    private View mBabyStatuView;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View babystatusIndicateView = inflater.inflate(R.layout.baby_status_indicate, container, false);
      
        mBabyStatuView = babystatusIndicateView.findViewById(R.id.babystatusindcate);
        //mBabyStatus = (TextView) babystatusIndicateView.findViewById(R.id.babysleep);
        
        mBabyStatuView.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(getActivity(), BabyStatusActivity.class);
                //intent.putExtra("deviceid", mDeviceId);
                /*bundle.putInt("temp", tempValue);
                bundle.putInt("humit", humitValue);*/
                /*intent.putExtra("temp", getArguments().getInt("temp"));
                intent.putExtra("humit", getArguments().getInt("humit"));*/
                startActivity(intent);
               // Toast.makeText(getActivity().getApplicationContext(), " 宝宝睡觉状态", Toast.LENGTH_SHORT).show();
            }
        });
        return babystatusIndicateView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }

}