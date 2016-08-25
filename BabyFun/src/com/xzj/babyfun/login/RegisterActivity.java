package com.xzj.babyfun.login;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.xzj.babyfun.BabyBreathActivity;
import com.xzj.babyfun.R;
import com.xzj.babyfun.logging.SLog;

public class RegisterActivity extends Activity {
    
    private static final String TAG = RegisterActivity.class.getSimpleName();
    
   // private UserRegisterTask mRegisterTask = null;

    // UI references.
    private AutoCompleteTextView mRegiserPhoneView;
    private EditText mPasswordView;
    private EditText mCheckCodeView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mSendCheckCodeButton;
    EventHandler mEventHandler;
    // 国家号码规则
    private HashMap<String, String> mCountryRules;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        
        SMSSDK.initSDK(this, "15cc6f2034e7d", "027c72f4185b80d6f1d2e49be748b98f");
        mEventHandler = new EventHandler(){
            
            @Override
            public void afterEvent(int event, int result, Object data) {
 
               if (result == SMSSDK.RESULT_COMPLETE) {
                //回调完成
                if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                //提交验证码成功
                }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    SLog.e(TAG, " get check code ");
                //获取验证码成功
                }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
                //返回支持发送验证码的国家列表
                    if (result == SMSSDK.RESULT_COMPLETE) {
                        onCountryListGot((ArrayList<HashMap<String,Object>>) data);
                    } 
                    
                } 
              }else{                                                                 
                 ((Throwable)data).printStackTrace(); 
          }
      } 
   }; 
       SMSSDK.registerEventHandler(mEventHandler); //注册短信回调
        
        mRegiserPhoneView = (AutoCompleteTextView) findViewById(R.id.register_phonenumber);
        //populateAutoComplete();
        mCheckCodeView = (EditText) findViewById(R.id.register_check_code_text);
        mSendCheckCodeButton = (Button) findViewById(R.id.send_check_code);
        mSendCheckCodeButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                SMSSDK.getVerificationCode("86", mRegiserPhoneView.getText().toString());
                SLog.e(TAG, "mRegiserPhoneView = " + mRegiserPhoneView.getText().toString());
               // SMSSDK.getVerificationCode("86", "18811130187");
            }
        });

        mPasswordView = (EditText) findViewById(R.id.register_password_text);
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
}
