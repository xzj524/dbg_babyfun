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
        	//�����Ի�����ʾ�û��Ǻ��
        	Intent enabler = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        	//startActivityForResult(enabler, REQUEST_ENABLE);
        	      //������ʾ��ǿ�д�
        	      // mAdapter.enable();
        	Toast.makeText(getApplicationContext(), "������ʹ��", Toast.LENGTH_SHORT).show();
        	}else {
        		Toast.makeText(getApplicationContext(), "����ʹ��", Toast.LENGTH_SHORT).show();
			}
    }
}
