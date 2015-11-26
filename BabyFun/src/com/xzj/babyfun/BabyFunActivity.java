package com.xzj.babyfun;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class BabyFunActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_fun);
        
        BluetoothAdapter mAdapter= BluetoothAdapter.getDefaultAdapter();
        if(!mAdapter.isEnabled()){
        	//弹出对话框提示用户是后打开
        	Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	//startActivityForResult(enabler, REQUEST_ENABLE);
        	      //不做提示，强行打开
        	      // mAdapter.enable();
        	Toast.makeText(getApplicationContext(), "蓝牙不使能", Toast.LENGTH_SHORT).show();
        	}else {
        		Toast.makeText(getApplicationContext(), "蓝牙使能", Toast.LENGTH_SHORT).show();
			}
    }
}
