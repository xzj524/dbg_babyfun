package com.aizi.yingerbao.login;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.aizi.yingerbao.R;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.userdatabase.UserAccountInfo;
import com.aizi.yingerbao.view.TopBarView;
import com.aizi.yingerbao.view.TopBarView.onTitleBarClickListener;

public class ResetPasswordActivity extends Activity  implements onTitleBarClickListener{
    
    private static final String TAG = ResetPasswordActivity.class.getSimpleName();
    
    TopBarView topBarView;
    Button mSendCheckCode;
    Button mResetCode;
    EventHandler mEventHandler;
    
    // 国家号码规则
    private HashMap<String, String> mCountryRules;
    
    private AutoCompleteTextView mResetPhoneView;
    private EditText mResetPasswordView;
    private EditText mResetCheckCodeView;
    private TimeCount time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        topBarView = (TopBarView) findViewById(R.id.topbar);
        topBarView.setClickListener(this);
        
        SMSSDK.registerEventHandler(mEventHandler); //注册短信回调
        mResetPhoneView = (AutoCompleteTextView) findViewById(R.id.resetphonenumber);
        mResetPasswordView = (EditText) findViewById(R.id.reset_password);
        mResetCheckCodeView = (EditText) findViewById(R.id.reset_check_code_text);
        
        mSendCheckCode = (Button) findViewById(R.id.reset_send_check_code);
        mSendCheckCode.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                String phonenum = mResetPhoneView.getText().toString();
                if (!TextUtils.isEmpty(phonenum)) {
                    SMSSDK.getVerificationCode("86", phonenum);
                    SLog.e(TAG, "mResetPhoneView = " + phonenum);
                    time.start();//开始计时
                }
                
            }
        });
        
        mResetCode = (Button) findViewById(R.id.reset_passcode_button);
        mResetCode.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mResetPasswordView.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                } else if (mResetPasswordView.getText().toString().length() <= 4) {
                    Toast.makeText(getApplicationContext(), "密码太短了", Toast.LENGTH_SHORT).show();
                } else {
                    SMSSDK.submitVerificationCode("86", mResetPhoneView.getText().toString(), 
                            mResetCheckCodeView.getText().toString());
                }
            }
        });
        
        
        mEventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                // TODO Auto-generated method stub
                super.afterEvent(event, result, data);
                
                if (result == SMSSDK.RESULT_COMPLETE) {
                    if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                         UserAccountInfo useraccountinfo = (UserAccountInfo) UserAccountInfo.getCurrentUser();
                         useraccountinfo.setPassword(mResetPasswordView.getText().toString());
                         useraccountinfo.update(new UpdateListener() {
                            
                            @Override
                            public void done(BmobException e) {
                                if (e == null) {
                                    Toast.makeText(getApplicationContext(), "重置密码成功", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "重置密码失败", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });   
                    } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                        SLog.e(TAG, " get check code ");
                        //获取验证码成功
                    }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                        //返回支持发送验证码的国家列表
                        if (result == SMSSDK.RESULT_COMPLETE) {
                            onCountryListGot((ArrayList<HashMap<String,Object>>) data);
                        } 
                    } 
                } else{                  
                    Toast.makeText(getApplicationContext(), "获取验证码失败或者验证失败", 
                            Toast.LENGTH_SHORT).show();
                    ((Throwable)data).printStackTrace(); 
                }
            }
        };
        
    }
    
    
    private void onCountryListGot(ArrayList<HashMap<String, Object>> countries) {
        // 解析国家列表
        for (HashMap<String, Object> country : countries) {
            String code = (String) country.get("zone");
            String rule = (String) country.get("rule");
            if (TextUtils.isEmpty(code) || TextUtils.isEmpty(rule)) {
                continue;
            }
            SLog.e(TAG, "country rules  code =  " + code + " rule = " + rule );

            if (mCountryRules == null) {
                mCountryRules = new HashMap<String, String>();
            }
            mCountryRules.put(code, rule);
        }
    }

    @Override
    public void onBackClick() {
        // TODO Auto-generated method stub
        finish();
    }

    @Override
    public void onRightClick() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onCalendarClick() {
        // TODO Auto-generated method stub
        
    }
    
    
    
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }
        @Override
        public void onFinish() {//计时完毕时触发
            mSendCheckCode.setText("重新验证");
            mSendCheckCode.setClickable(true);
        }
        @Override
        public void onTick(long millisUntilFinished){//计时过程显示
            mSendCheckCode.setClickable(false);
            mSendCheckCode.setText(millisUntilFinished /1000+"秒");
        }
    }

}
