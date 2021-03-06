package com.aizi.yingerbao.login;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

import com.aizi.yingerbao.R;
import com.aizi.yingerbao.logging.SLog;
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
    private EditText mOldPasswordView;
    private EditText mNewCheckCodeView;
    private TimeCount time;
    private static boolean mIsResetPasswordSucceed = false;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);
        topBarView = (TopBarView) findViewById(R.id.topbar);
        topBarView.setClickListener(this);
        
        time = new TimeCount(60000, 1000);//构造CountDownTimer对象
        
        //SMSSDK.registerEventHandler(mEventHandler); //注册短信回调
        initSDK();
        
        mOldPasswordView = (EditText) findViewById(R.id.user_old_password);
        mNewCheckCodeView = (EditText) findViewById(R.id.user_new_password);
       /* mResetPhoneView = (AutoCompleteTextView) findViewById(R.id.resetphonenumber);
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
        });*/
        
        mResetCode = (Button) findViewById(R.id.reset_passcode_button);
        mResetCode.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                
                resetpasscode();
               /* if (TextUtils.isEmpty(mResetPasswordView.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "请输入密码", Toast.LENGTH_SHORT).show();
                } else if (mResetPasswordView.getText().toString().length() <= 4) {
                    Toast.makeText(getApplicationContext(), "密码太短了", Toast.LENGTH_SHORT).show();
                } else {
                    SMSSDK.submitVerificationCode("86", mResetPhoneView.getText().toString(), 
                            mResetCheckCodeView.getText().toString());
                }*/
            }
        });  
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterEventHandler(mEventHandler);
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
    
            SMSSDK.registerEventHandler(eventHandler);
    
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
             if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                 resetpasscode();
                 new Thread(new Runnable() {
                     @Override
                     public void run() {
                         try {
                             if (mIsResetPasswordSucceed) {
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
        try {
            String oldpasscode = mOldPasswordView.getText().toString();
            String newpasscode = mNewCheckCodeView.getText().toString();
            if (TextUtils.isEmpty(oldpasscode) 
                    || TextUtils.isEmpty(oldpasscode)) {
                Toast.makeText(getApplicationContext(), 
                        "请输入正确的密码", Toast.LENGTH_SHORT).show();
                return;
            }
            
            BmobUser.updateCurrentUserPassword(oldpasscode, newpasscode, new UpdateListener() {

                @Override
                public void done(BmobException e) {
                    if(e==null){
                        SLog.e(TAG,  "reset passcode, please check ");
                        Toast.makeText(getApplicationContext(), "重置密码成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        SLog.e(TAG,  "reset error =  " + e.getMessage() 
                                + " errorcode = " + e.getErrorCode());
                        Toast.makeText(getApplicationContext(), "重置密码失败 ", Toast.LENGTH_SHORT).show();
                    }
                }

            });
            
            
          /*  final String email = "xzj524@126.com";
            BmobUser.resetPasswordByEmail(email, new UpdateListener() {

                @Override
                public void done(BmobException e) {
                    if(e==null){
                        //toast("重置密码请求成功，请到" + email + "邮箱进行密码重置操作");
                        SLog.e(TAG,  "reset passcode，please check " + email);
                    }else{
                        SLog.e(TAG,  "reset error =  " + e.getMessage() 
                                + " errorcode = " + e.getErrorCode());
                        //toast("失败:" + e.getMessage());
                    }
                }
            });*/
            
           /* BmobQuery<BmobUser> query = new BmobQuery<BmobUser>();
            query.addWhereEqualTo("username", phone);
            query.findObjects(new FindListener<BmobUser>() {
                @Override
                public void done(List<BmobUser> object,BmobException e) {
                    if(e == null){
                        Toast.makeText(getApplicationContext(), 
                                "查询到用户个数 " + object.size()
                                + "objectID = " + object.get(0).getObjectId()
                                + "username = " + object.get(0).getUsername(), Toast.LENGTH_SHORT).show();
                        
                        SLog.e(TAG,  "查询到用户个数 " + object.size()
                                + " objectID = " + object.get(0).getObjectId()
                                + " username = " + object.get(0).getUsername());
                        
                        if (object.size() > 0) {
                            BmobUser newUser = new BmobUser();
                            newUser.setPassword(password);
                            newUser.update(object.get(0).getObjectId(),new UpdateListener() {
                                @Override
                                public void done(BmobException e) {
                                    if(e == null){
                                        Toast.makeText(getApplicationContext(), "重置密码成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "重置密码失败 " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        SLog.e(TAG,  "reset error =  " + e.getMessage() 
                                                + " errorcode = " + e.getErrorCode());
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(getApplicationContext(), "无账户信息，请注册", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), 
                                "查询用户失败", Toast.LENGTH_SHORT).show();
                    }
                }
            });*/
            
            
           /* final BmobUser useraccountinfo = UserAccountInfo.getCurrentUser();
            if (useraccountinfo != null) {
                if (!useraccountinfo.getMobilePhoneNumber().equals(phone)) {
                    Toast.makeText(getApplicationContext(), 
                            "请输入正确的手机号码或密码", Toast.LENGTH_SHORT).show();
                    return;
                }
                
                useraccountinfo.setUsername(phone);
                useraccountinfo.setMobilePhoneNumber(phone);
                useraccountinfo.setPassword(password);
                useraccountinfo.update(new UpdateListener() {
                    
                    @Override
                    public void done(BmobException arg0) {
                        if (arg0 == null) {
                            Toast.makeText(getApplicationContext(), "重置密码成功", Toast.LENGTH_SHORT).show();
                            mIsResetPasswordSucceed = true;
                        } else {
                            Toast.makeText(getApplicationContext(), "重置密码失败", Toast.LENGTH_SHORT).show();
                            mIsResetPasswordSucceed = false;
                        }
                    }
                });
            } else { // 手机上获取不到用户信息             
                UserAccountInfo useraccount = new UserAccountInfo();
                useraccount.setUsername(phone);
                useraccount.setMobilePhoneNumber(phone);
                useraccount.setPassword(password);
                useraccount.update(new UpdateListener() {
                    
                    @Override
                    public void done(BmobException arg0) {
                        if (arg0 == null) {
                            Toast.makeText(getApplicationContext(), "重置密码成功", Toast.LENGTH_SHORT).show();
                            mIsResetPasswordSucceed = true;
                        } else {
                            Toast.makeText(getApplicationContext(), "重置密码失败", Toast.LENGTH_SHORT).show();
                            mIsResetPasswordSucceed = false;
                        }
                    }
                });
            } */
        } catch (Exception e) {
            SLog.e(TAG, e);
        }
    }

}
