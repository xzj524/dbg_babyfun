package com.xzj.babyfun;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

public class BabyFunActivity extends Activity {
    
    private static final String TAG = BabyFunActivity.class.getSimpleName();
    
    BluetoothAdapter mAdapter;
    Button mSearchBtnButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_fun);
        
        Intent enabler=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enabler,10);//同startActivity(enabler);  启动蓝牙
        
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mReceiver, filter);
        
        mAdapter= BluetoothAdapter.getDefaultAdapter();
      /*  if(!mAdapter.isEnabled()){
        	Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	//startActivityForResult(enabler, REQUEST_ENABLE);
        	      // mAdapter.enable();
        	Toast.makeText(getApplicationContext(), "蓝牙使能 ", Toast.LENGTH_SHORT).show();
        	}else {
        		Toast.makeText(getApplicationContext(), "蓝牙禁用", Toast.LENGTH_SHORT).show();
			}*/
        
        mSearchBtnButton = (Button) findViewById(R.id.findbtn);
        mSearchBtnButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                
            }
        });
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            Toast.makeText(getApplicationContext(), "resultcode = " + resultCode, Toast.LENGTH_SHORT).show();
        }
    }
    
    BroadcastReceiver mReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent
                        .getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                
                if (device.getBondState() != BluetoothDevice.BOND_BONDED){
                    Toast.makeText(getApplicationContext(), "find device:" + device.getName()
                            + device.getAddress(), Toast.LENGTH_SHORT).show();
    
                }
            }else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                setTitle("搜索完成");
           
            }
        }
        
    };
    
    
    private void search() { //开启蓝牙和设置设备可见时间
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (!adapter.isEnabled()) {
            adapter.enable();
        }
        Intent enable = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        enable.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 3600); //3600为蓝牙设备可见时间
        startActivity(enable);
       /* Intent searchIntent = new Intent(this, ComminuteActivity.class);
        startActivity(searchIntent);*/
    }
}
