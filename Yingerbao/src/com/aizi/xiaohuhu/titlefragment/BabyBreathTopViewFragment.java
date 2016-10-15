package com.aizi.xiaohuhu.titlefragment;

import com.aizi.yingerbao.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class BabyBreathTopViewFragment extends Fragment{
   // ImageView backView;
 
    @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View breathTopView = inflater.inflate(R.layout.baby_breath_top_title, container, false);
       /* backView = (ImageView) breathTopView.findViewById(R.id.breathbackbtn);
        backView.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                getActivity().finish();
            }
        });*/
        return breathTopView;
        //return super.onCreateView(inflater, container, savedInstanceState);
    }
}
