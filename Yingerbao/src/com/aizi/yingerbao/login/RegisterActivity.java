package com.aizi.yingerbao.login;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.utils.SMSLog;

import com.aizi.yingerbao.R;
import com.aizi.yingerbao.logging.SLog;
import com.aizi.yingerbao.userdatabase.UserAccountDataBase;
import com.aizi.yingerbao.userdatabase.UserAccountInfo;
import com.aizi.yingerbao.view.TopBarView;
import com.aizi.yingerbao.view.TopBarView.onTitleBarClickListener;

public class RegisterActivity extends Activity implements onTitleBarClickListener, android.os.Handler.Callback{
    
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
    
    private TopBarView mRegisterTopbar;  
    private TimeCount time;
    private static boolean mIsRegisterSucceed = false;
    
    // MOB SDK APPKEY
    private static String APPKEY = "18320567edb8c";
    // MOB SDK APPSECRET
    private static String APPSECRET = "099b55f2d0f7a8897a6fd1b70e0d4b55";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        mRegisterTopbar = (TopBarView) findViewById(R.id.registertopbar);
        mRegisterTopbar.setClickListener(this);
        
        SMSSDK.initSDK(this, APPKEY, APPSECRET);
      
        initSDK();
        //SMSSDK.registerEventHandler(mEventHandler); //注册短信回调
        time = new TimeCount(60000, 1000);//构造CountDownTimer对象
        mRegiserPhoneView = (AutoCompleteTextView) findViewById(R.id.register_phonenumber);
        mCheckCodeView = (EditText) findViewById(R.id.register_check_code_text);
        mSendCheckCodeButton = (Button) findViewById(R.id.send_check_code);
        mPasswordView = (EditText) findViewById(R.id.register_password_text);
        mRegisterButton = (Button) findViewById(R.id.phone_register_button);
        
        
        mSendCheckCodeButton.setOnClickListener(new View.OnClickListener() {   
            @Override
            public void onClick(View v) {
                String phonenum = mRegiserPhoneView.getText().toString();
                if (!TextUtils.isEmpty(phonenum)) {
                    if (UserAccountDataBase.checkUserAccount(getApplicationContext(),
                            phonenum)) {
                        Toast.makeText(getApplicationContext(), "该手机号已注册过,请更换号码或直接登录", 
                                Toast.LENGTH_SHORT).show();
                    } else {
                        SMSSDK.getVerificationCode("86", phonenum); // 获取验证码
                        SLog.e(TAG, "mRegiserPhoneView = " + phonenum);
                        time.start();//开始计时
                    }
                }
            }
        });
        
       /* mEventHandler = new EventHandler(){
            @Override
            public void afterEvent(int event, int result, Object data) {
                SLog.e(TAG, "checkcode return result  = " + result + " event = " + event);
               if (result == SMSSDK.RESULT_COMPLETE) {
                 //回调完成
                 if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                    //提交验证码成功
                     SLog.e(TAG, "EVENT_SUBMIT_VERIFICATION_CODE succeed!");
                     signup();
                     new Thread(new Runnable() {
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
                       }).start();
                } else if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE){
                    //获取验证码成功
                    SLog.e(TAG, " get check code succeed"); 
                }
              } else if (result == SMSSDK.RESULT_ERROR) { // 请求验证码失败
                
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
*/        
    
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    return true;
                }
                return false;
            }
        });
        
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String phone = mRegiserPhoneView.getText().toString();
                String passcode = mPasswordView.getText().toString();
                String checkcode = mCheckCodeView.getText().toString();
                SLog.e(TAG, "click registerbutton  phone = " + phone
                        + " passcode = " + passcode
                        + " checkcode = " + checkcode);
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getApplicationContext(), "请输入手机号码", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(checkcode)) {
                    Toast.makeText(getApplicationContext(), "请输入验证码", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(passcode)) {
                    Toast.makeText(getApplicationContext(), "请输入注册密码", Toast.LENGTH_SHORT).show();
                } else if (passcode.length() <= 4) {
                    Toast.makeText(getApplicationContext(), "密码太短了", Toast.LENGTH_SHORT).show();
                } else {  
                    SLog.e(TAG, "submitVerificationCode phone = " + phone
                            + " passcode = " + passcode
                            + " checkcode = " + checkcode);

                    SMSSDK.submitVerificationCode("86", phone, checkcode);
                }               
            }
        });
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
    
    
    private void signup() {
        try {
            final UserAccountInfo useraccountinfo = new UserAccountInfo();
            String phone = mRegiserPhoneView.getText().toString();
            String password = mPasswordView.getText().toString();
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
            useraccountinfo.signUp(new SaveListener<UserAccountInfo>() {

                @Override
                public void done(UserAccountInfo useinfo, BmobException e) {
                    if (e == null) {
                        Toast.makeText(getApplicationContext(), "注册成功", Toast.LENGTH_SHORT).show();
                        UserAccountDataBase.insertUserAccountInfo(getApplicationContext(), 
                                useraccountinfo);  
                        mIsRegisterSucceed = true;
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
                        mIsRegisterSucceed = false;
                    }
                }
            });
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
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

    @Override
    public void onCalendarClick() {
        
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
                 signup();
                 new Thread(new Runnable() {
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
                    }).start();
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
}
