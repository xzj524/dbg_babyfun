package com.aizi.xiaohuhu.login;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.aizi.xiaohuhu.R;
import com.aizi.xiaohuhu.logging.SLog;
import com.aizi.xiaohuhu.userdatabase.UserAccountDataBase;
import com.aizi.xiaohuhu.userdatabase.UserAccountInfo;
import com.aizi.xiaohuhu.view.TopBarView;
import com.aizi.xiaohuhu.view.TopBarView.onTitleBarClickListener;

public class RegisterActivity extends Activity implements onTitleBarClickListener{
    
    private static final String TAG = RegisterActivity.class.getSimpleName();
    

    // UI references.
    private AutoCompleteTextView mRegiserPhoneView;
    private EditText mPasswordView;
    private EditText mCheckCodeView;
    private Button mSendCheckCodeButton;
    private Button mRegisterButton;
    EventHandler mEventHandler;
    // 国家号码规则
    private HashMap<String, String> mCountryRules;
    
    private TopBarView topbar;  
    private TimeCount time;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        topbar = (TopBarView) findViewById(R.id.registertopbar);
        topbar.setClickListener(this);
        
        time = new TimeCount(60000, 1000);//构造CountDownTimer对象
        mRegiserPhoneView = (AutoCompleteTextView) findViewById(R.id.register_phonenumber);
        //populateAutoComplete();
        mCheckCodeView = (EditText) findViewById(R.id.register_check_code_text);
        mSendCheckCodeButton = (Button) findViewById(R.id.send_check_code);
        mPasswordView = (EditText) findViewById(R.id.register_password_text);
        mRegisterButton = (Button) findViewById(R.id.phone_register_button);
        
        
        SMSSDK.initSDK(this, "16cac73c0585e", "5a43a8be5eaf2786403d854f39ce28f1");
        mEventHandler = new EventHandler(){
            
            @Override
            public void afterEvent(int event, int result, Object data) {
 
               if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                //提交验证码成功
                    SLog.e(TAG, " check code is right");
                    if (!UserAccountDataBase.checkUserAccountAndPassword(getApplicationContext(),
                            mRegiserPhoneView.getText().toString(), mPasswordView.getText().toString())) {
                        UserAccountInfo useraccountinfo = new UserAccountInfo();
                        useraccountinfo.mUserAccountName = mRegiserPhoneView.getText().toString();
                        useraccountinfo.mUserAccountInfoPassWord = mPasswordView.getText().toString();
                        useraccountinfo.mUserAccountPosition = "beijing";
                        useraccountinfo.mUserAccountTimestamp = System.currentTimeMillis();
                        long res = UserAccountDataBase.insertUserAccountInfo(getApplicationContext(), 
                                useraccountinfo );  
                        if (res != -1) {
                            Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), "该手机号已经注册过", Toast.LENGTH_SHORT).show();
                    }
                    
                   new Thread(new Runnable() {
                    
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            Thread.sleep(1000);
                            finish();
                        } catch (InterruptedException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }).start();
                    
                
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    SLog.e(TAG, " get check code ");
                //获取验证码成功
                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                //返回支持发送验证码的国家列表
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        onCountryListGot((ArrayList<HashMap<String,Object>>) data);
                    } 
                    
                    SLog.e(TAG, " get support country code");
                } 
              }else{                                                                 
                 ((Throwable)data).printStackTrace(); 
              }
            } 
        }; 
        
        SMSSDK.registerEventHandler(mEventHandler); //注册短信回调
        mSendCheckCodeButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if (UserAccountDataBase.checkUserAccount(getApplicationContext(),
                        mRegiserPhoneView.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "该手机号已注册过,请更换号码或直接登录", 
                            Toast.LENGTH_SHORT).show();
                } else {
                    SMSSDK.getVerificationCode("86", mRegiserPhoneView.getText().toString());
                    SLog.e(TAG, "mRegiserPhoneView = " + mRegiserPhoneView.getText().toString());
                    time.start();//开始计时
                }
            }
        });

    
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                   // attemptLogin();
                    return true;
                }
                return false;
            }
        });
        
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mPasswordView.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "请输入注册密码", Toast.LENGTH_SHORT).show();
                } else if (mPasswordView.getText().toString().length() <= 4) {
                    Toast.makeText(getApplicationContext(), "密码太短了", Toast.LENGTH_SHORT).show();
                } else {
                    SMSSDK.submitVerificationCode("86", mRegiserPhoneView.getText().toString(), 
                            mCheckCodeView.getText().toString());
                }               
            }
        });
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
        // 回归页面初始化操作
       // initPage();
    }
    
    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        SMSSDK.unregisterEventHandler(mEventHandler);
    }

    @Override
    public void onBackClick() {
        finish();
    }

    @Override
    public void onRightClick() {
     
    }
    
    
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }
        @Override
        public void onFinish() {//计时完毕时触发
            mSendCheckCodeButton.setText("重新验证");
            mSendCheckCodeButton.setClickable(true);
        }
        @Override
        public void onTick(long millisUntilFinished){//计时过程显示
            mSendCheckCodeButton.setClickable(false);
            mSendCheckCodeButton.setText(millisUntilFinished /1000+"秒");
        }
        }
}
