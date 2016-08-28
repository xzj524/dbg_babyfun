package com.aizi.xiaohuhu.titlefragment;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.aizi.xiaohuhu.CalendarActivity;
import com.aizi.xiaohuhu.R;
import com.aizi.xiaohuhu.dialog.CalendarDialog;

public class BabyStatusTopViewFragment extends Fragment{
    
    ImageView backView;
    ImageView calendarView;
    
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
        
        calendarView = (ImageView) babystatusView.findViewById(R.id.plusbtn);
        calendarView.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                
                Toast.makeText(getActivity(), "zidingyi dialog", Toast.LENGTH_SHORT).show();
                
                Intent intent = new Intent(getActivity().getApplicationContext(), CalendarActivity.class);
                startActivity(intent);
                
                /*      CalendarDialog.Builder builder 
                = new CalendarDialog.Builder(getActivity());
                
                builder.setMessage("这个就是自定义的提示框");  
                builder.setTitle("提示");  
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {  
                    public void onClick(DialogInterface dialog, int which) {  
                        dialog.dismiss();  
                        //设置你的操作事项  
                    }  
                });  
          
                builder.setNegativeButton("取消",  
                        new android.content.DialogInterface.OnClickListener() {  
                            public void onClick(DialogInterface dialog, int which) {  
                                dialog.dismiss();  
                            }  
                        });  
          
                builder.create().show();*/
            }
        });
        
        return babystatusView;
      //  return super.onCreateView(inflater, container, savedInstanceState);
    }

}
