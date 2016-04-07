package com.xzj.babyfun.ui.component.main;

import com.xzj.babyfun.R;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class BabyStatusTopViewFragment extends Fragment{
    
    ImageView backView;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        View babystatusView = inflater.inflate(R.layout.babystatuspagetoptitle, container, false);
        backView = (ImageView) babystatusView.findViewById(R.id.backbtn);
        backView.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                getActivity().finish();
            }
        });
        return babystatusView;
      //  return super.onCreateView(inflater, container, savedInstanceState);
    }

}
