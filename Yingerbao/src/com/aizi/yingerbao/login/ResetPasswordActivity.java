package com.aizi.yingerbao.login;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.aizi.yingerbao.R;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.userdatabase.UserAccountDataBase;
import com.aizi.yingerbao.userdatabase.UserAccountInfo;
import com.aizi.yingerbao.view.TopBarView;
import com.aizi.yingerbao.view.TopBarView.onTitleBarClickListener;

public class ResetPasswordActivity extends Activity  implements onTitleBarClickListener, Callback{
    
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
                    /*Toast.makeText(getApplicationContext(), "获取验证码失败或者验证失败", 
                            Toast.LENGTH_SHORT).show();
                    ((Throwable)data).printStackTrace(); */
                    
                    try {
                        Throwable throwable = (Throwable) data;
                        JSONObject object = new JSONObject(throwable.getMessage());
                        String des = object.optString("detail");
                        int status = object.optInt("status");
                        switch (status) {
                          case 456:
                          case 457:
                              Toast.makeText(getApplicationContext(), "手机号码错误", Toast.LENGTH_SHORT).show();
                              break;
                          case 463:
                          case 464:
                          case 465:
                              Toast.makeText(getApplicationContext(), "获取验证码次数超限", Toast.LENGTH_SHORT).show();
                              break;
                          default:
                              Toast.makeText(getApplicationContext(), "获取验证码失败或者验证失败", 
                                      Toast.LENGTH_SHORT).show();
                              break;
                        }
                    } catch (Exception e) {
                        SLog.e(TAG, e);
                    }
                    
                    
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
    
    private void initSDK() {
        try {
            
            final Handler handler = new Handler(this);
            EventHandler eventHandler = new EventHandler() {
                public void afterEvent(int event, int result, Object data) {
                    Message msg = new Message();
                    msg.arg1 = event;
                    msg.arg2 = result;
                    msg.obj = data;
                    handler.sendMessage(msg);
                }
            };
    
            SMSSDK.registerEventHandler(eventHandler); // ע����Żص�
    
        } catch (Exception e) {
            e.printStackTrace();
        }
    
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



    @Override
    public boolean handleMessage(Message msg) {
        int event = msg.arg1;
        int result = msg.arg2;
        Object data = msg.obj;
         if (result == SMSSDK.RESULT_COMPLETE) {
                System.out.println("--------result"+event);
             if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                 //Toast.makeText(getApplicationContext(), "成功验证", Toast.LENGTH_SHORT).show();
                 resetpasscode();
                /* new Thread(new Runnable() {
                     @Override
                     public void run() {
                         try {
                             if (mIsRegisterSucceed) {
                                 Thread.sleep(1000);
                                 SLog.e(TAG, "RegisterActivity finish");
                                 finish();
                             }
                         } catch (Exception e) {
                             SLog.e(TAG, e);
                         }
                     }
                    }).start();*/
             }else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                 //Toast.makeText(getApplicationContext(), "获取验证码成功", Toast.LENGTH_SHORT).show();
                
             }else if (event ==SMSSDK.EVENT_GET_SUPPORTED_COUNTRIES){
             } 
           
           } else {    
                int status = 0; 
                try {
                    ((Throwable) data).printStackTrace();
                    Throwable throwable = (Throwable) data;
    
                    JSONObject object = new JSONObject(throwable.getMessage());
                    String des = object.optString("detail");
                    status = object.optInt("status");
                    if (!TextUtils.isEmpty(des)) {
                        Toast.makeText(getApplicationContext(), des, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                } catch (Exception e) {
                    SLog.e(TAG, e);
                }
            
       }
        return false;
    }


    private void resetpasscode() {
        // TODO Auto-generated method stub
        try {
            String phone = mResetPhoneView.getText().toString();
            String password = mResetPasswordView.getText().toString();
            final UserAccountInfo useraccountinfo = (UserAccountInfo) UserAccountInfo.getCurrentUser();
            if (useraccountinfo.getMobilePhoneNumber().equals(phone)) {
                
            }
            
            if (TextUtils.isEmpty(phone) || TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), 
                        "请输入正确的手机号码或密码", Toast.LENGTH_SHORT).show();
                return;
            }
            useraccountinfo.setUsername(phone);
            useraccountinfo.setMobilePhoneNumber(phone);
            useraccountinfo.setPassword(password);
            useraccountinfo.setUserPassWord(password);
            useraccountinfo.mUserTimestamp = System.currentTimeMillis();
           /* useraccountinfo.update(new SaveListener<UserAccountInfo>() {

                @Override
                public void done(UserAccountInfo useinfo, BmobException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                        UserAccountDataBase.insertUserAccountInfo(getApplicationContext(), 
                                useraccountinfo);  
                        //mIsRegisterSucceed = true;
                    } else {
                        SLog.e(TAG, "errorcode = " + e.getErrorCode()
                                + " errormsg = " + e.getMessage());
                        
                        if (e.getErrorCode() == 202) { // already token
                            Toast.makeText(getApplicationContext(),
                                    "已经注册过，请重新登录", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), 
                                    "注册失败，请检查网络连接", Toast.LENGTH_SHORT).show();
                        }
                        //mIsRegisterSucceed = false;
                    }
                }
            });*/
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }

}
