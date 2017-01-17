package com.aizi.yingerbao;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class BrowserShowActivity extends Activity {
    
    /** log tag. */
    protected static final String TAG = "BrowserShowActivity";
    
    private static String mWeidianUrl = "https://weidian.com/item.html?itemID=2019786672";
    
    WebView mWebView;
    TextView mTitleText;
    
    PopupWindow mPopupWindow;
    RelativeLayout mMainLayout;
    RelativeLayout mErrorView;
    ProgressBar mProgressBar;
    
    String mUrl;
    String mFinalUrl;
    JSONArray mTeljsonArray = new JSONArray();
    long mPushStartTime;
    long mEndTime;
    long mPushFinishTime;
    long mBoolawStartTime;
    long mBoolawEndTime;
    long mZhiDaEndTime;
    int mPageFinishTimes;
    boolean mIsErrorPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_browser_show);
        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        initTitle();
        initWebView();
        initOverFlowMenu();
        initErrorPage();
        
       
        Map<String, String> additionalHttpHeaders = new HashMap<String, String>();
        additionalHttpHeaders.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
        mWebView.requestFocus();
        mWebView.loadUrl(mWeidianUrl, additionalHttpHeaders);
        mUrl = mWeidianUrl;
        if (isNetworkAvailable(getApplicationContext())) {
            hideErrorPage();
        } else {
            Toast.makeText(getApplicationContext(), "网络不可用,请检查网络状态！", Toast.LENGTH_SHORT).show();
            showErrorPage();
        }
        mPushStartTime = System.currentTimeMillis();
    }
    
    
    private void initOverFlowMenu() {
        LinearLayout overflowLayout = new LinearLayout(getApplicationContext());
        overflowLayout.setOrientation(LinearLayout.VERTICAL);

        Button reloadBtn = new Button(getApplicationContext());
        reloadBtn.setText("刷新");
        reloadBtn.setTextColor(0xff000000);
        reloadBtn.setBackgroundColor(0xfff2f2f2);
        reloadBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(0xffd6d6d6);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundColor(0xfff2f2f2);
                }
                return false;
            }
        });
        reloadBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (isNetworkAvailable(getApplicationContext())) {
                    mWebView.reload();
                    mPopupWindow.dismiss();
                } else {
                    Toast.makeText(getApplicationContext(), "网络不可用,请检查网络状态！", Toast.LENGTH_SHORT).show();
                }

            }
        });

        LinearLayout divisionLine = new LinearLayout(getApplicationContext());
        LinearLayout.LayoutParams divisionParams = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, dip2px(1));
        divisionLine.setBackgroundColor(0xff8c8c8c);

        Button closeBtn = new Button(getApplicationContext());
        closeBtn.setText("关闭");
        closeBtn.setTextColor(0xff000000);
        closeBtn.setBackgroundColor(0xfff2f2f2);
        closeBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(0xffd6d6d6);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundColor(0xfff2f2f2);
                }
                return false;
            }
        });
        closeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                mEndTime = System.currentTimeMillis();
                mPopupWindow.dismiss();
                mWebView.destroy();
                finish();
            }
        });

        LinearLayout.LayoutParams overflowParams = new LinearLayout.LayoutParams(dip2px(120), dip2px(40));
        overflowLayout.addView(reloadBtn, overflowParams);
        overflowLayout.addView(divisionLine, divisionParams);
        overflowLayout.addView(closeBtn, overflowParams);

        mPopupWindow = new PopupWindow(overflowLayout, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
        mPopupWindow.setTouchable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
    }

    private void initWebView() {
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    mProgressBar.setVisibility(View.INVISIBLE);
                } else {
                    if (View.INVISIBLE == mProgressBar.getVisibility()) {
                        mProgressBar.setVisibility(View.VISIBLE);
                    }
                    mProgressBar.setProgress(newProgress);
                }

                if (mIsErrorPage) {
                    mWebView.clearHistory();
                }
                super.onProgressChanged(view, newProgress);
            }


            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                return super.onJsAlert(view, url, message, result);
            }

        });

        mWebView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                mUrl = url;
                if (url.startsWith("mailto:") || url.startsWith("geo:") || url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);

                    JSONObject jsonStrs = new JSONObject();
                    try {
                        jsonStrs.put("number", getNum(url));
                        jsonStrs.put("timestamp", System.currentTimeMillis());

                        mTeljsonArray.put(jsonStrs);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (url.startsWith("push://push.baidu.com")) {
                        showErrorPage();
                    } else {
                        hideErrorPage();
                        view.loadUrl(url);
                        if (url.contains("3508281") && url.contains("m.baidu.com")) {
                            mPushFinishTime = System.currentTimeMillis();
                        } else if (url.contains("3508281") && url.contains("m.boolaw.com")) {
                            mPageFinishTimes += 1;
                            if (mPageFinishTimes == 1) {
                                mBoolawStartTime = System.currentTimeMillis();
                            }
                        }
                    }
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (url.contains("3508281")) {
                    if (url.contains("m.boolaw.com")) {
                        mBoolawEndTime = System.currentTimeMillis();
                    } else if (url.contains("m.baidu.com")) {
                        mZhiDaEndTime = System.currentTimeMillis();
                    }
                }
                super.onPageFinished(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                mWebView.clearHistory();
                showErrorPage();
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });
    }
    
    private String getNum(String str) {
        String str2 = "";
        str = str.trim();
        if (str != null && !"".equals(str)) {
            for (int i = 0; i < str.length(); i++) {
                if (str.charAt(i) >= 48 && str.charAt(i) <= 57) {
                    str2 += str.charAt(i);
                }
            }
        }
        return str2;
    }

    @SuppressLint("NewApi")
    private void initTitle() {
        RelativeLayout titleRelLayout = new RelativeLayout(getApplicationContext());
        titleRelLayout.setGravity(RelativeLayout.CENTER_HORIZONTAL);

    /*    Button btnBack = new Button(getApplicationContext());
        BitmapDrawable backdrawable = null;
        try {
            InputStream in = getClass().getResourceAsStream("/assets/btn_back.png");
            backdrawable = new BitmapDrawable(BitmapFactory.decodeStream(in));
        } catch (Exception e) {
            Log.e(TAG, "error " + e.getMessage());
        }

        backdrawable.setBounds(0, 0, dip2px(20), dip2px(28));
        btnBack.setCompoundDrawables(backdrawable, null, null, null); // 设置左图标
        btnBack.setText("返回");
        btnBack.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        btnBack.setTextColor(0xff000000);
        btnBack.setBackgroundColor(0xffffffff);
        btnBack.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(0xffdbdbdb);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundColor(0xffffffff);
                }
                return false;
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                } else {
                    mEndTime = System.currentTimeMillis();
                    mWebView.destroy();
                    finish();
                }
            }
        });

        RelativeLayout.LayoutParams backParams =
                new RelativeLayout.LayoutParams(dip2px(80), ViewGroup.LayoutParams.WRAP_CONTENT);

        titleRelLayout.addView(btnBack, backParams);
        RelativeLayout.LayoutParams textParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);
        mTitleText = new TextView(this);
        mTitleText.setLayoutParams(textParams);
        mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        mTitleText.setTextColor(0xff000000);
        mTitleText.setPadding(dip2px(30), 0, 0, 0);
        mTitleText.setEllipsize(TruncateAt.END);
        mTitleText.setEms(10);
        mTitleText.setSingleLine();
        mTitleText.setMaxWidth(500);
        textParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        textParams.addRule(RelativeLayout.CENTER_VERTICAL);
        titleRelLayout.addView(mTitleText, textParams);

        BitmapDrawable overflowdrawable = null;
        try {
            InputStream in = getClass().getResourceAsStream("/assets/overflow.png");
            overflowdrawable = new BitmapDrawable(BitmapFactory.decodeStream(in));
        } catch (Exception e) {
            Log.e(TAG, "error " + e.getMessage());
        }

        final BitmapDrawable overflowdra = overflowdrawable;

        Button btnOverflow = new Button(this);
        btnOverflow.setBackgroundDrawable(overflowdrawable);
        btnOverflow.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundColor(0xffdbdbdb);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    v.setBackgroundDrawable(overflowdra);
                }
                return false;
            }
        });

        btnOverflow.setOnClickListener(new View.OnClickListener() {

            @SuppressLint("NewApi")
            @Override
            public void onClick(View v) {
                mPopupWindow.showAsDropDown(v, dip2px(60), dip2px(10));
            }
        });

        RelativeLayout.LayoutParams btnParams =
                new RelativeLayout.LayoutParams(dip2px(40), ViewGroup.LayoutParams.MATCH_PARENT);
        btnParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        titleRelLayout.addView(btnOverflow, btnParams);

        titleRelLayout.setBackgroundColor(0xffffffff);
        RelativeLayout.LayoutParams overflowParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(40));

        titleRelLayout.setLayoutParams(overflowParams);
        titleRelLayout.setId(1);*/

        RelativeLayout.LayoutParams progressbarParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(2));

        mProgressBar = new ProgressBar(getApplicationContext(), null, android.R.attr.progressBarStyleHorizontal);
        progressbarParams.addRule(RelativeLayout.BELOW, 1);
        mProgressBar.setBackgroundColor(0xffffffff);
        mProgressBar.setId(2);

        mMainLayout = new RelativeLayout(getApplicationContext());
        mWebView = new WebView(getApplicationContext());
        mWebView.removeJavascriptInterface("searchBoxJavaBridge_");
        mWebView.removeJavascriptInterface("accessibility");
        mWebView.removeJavascriptInterface("accessibilityTraversal");
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        mWebView.setWebViewClient(new WebViewClient());
        mWebView.getSettings().setUserAgentString("BCCS_SDK/3.0");
        RelativeLayout.LayoutParams mWebViewParams =
                new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
        mWebViewParams.addRule(RelativeLayout.BELOW, 2);

        mMainLayout.addView(titleRelLayout);
        mMainLayout.addView(mProgressBar, progressbarParams);
        mMainLayout.addView(mWebView, mWebViewParams);
        setContentView(mMainLayout);
    }
    
    
    public boolean isNetworkAvailable(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    protected void showErrorPage() {
        mWebView.setVisibility(View.INVISIBLE);
        mErrorView.setVisibility(View.VISIBLE);
        mIsErrorPage = true;
    }

    protected void hideErrorPage() {
        mErrorView.setVisibility(View.INVISIBLE);
        mWebView.setVisibility(View.VISIBLE);
        mIsErrorPage = false;
    }

    protected void initErrorPage() {
        if (mErrorView == null) {
            mErrorView = new RelativeLayout(getApplicationContext());
            TextView errorText = new TextView(getApplicationContext());
            errorText.setText("访问错误,请稍后重试");
            errorText.setTextColor(0xffd1d1d1);
            errorText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
            errorText.setPadding(0, 0, 0, dip2px(10));
            Button button = new Button(getApplicationContext());
            button.setId(3);
            button.setText("重新加载");
            button.setTextColor(0xff000000);
            button.setBackgroundColor(0xffd1d1d1);
            button.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // TODO Auto-generated method stub
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        v.setBackgroundColor(0xffdbdbdb);
                    } else if (event.getAction() == MotionEvent.ACTION_UP) {
                        v.setBackgroundColor(0xffd1d1d1);
                    }
                    return false;
                }
            });
            RelativeLayout.LayoutParams reloadParams =
                    new RelativeLayout.LayoutParams(300, ViewGroup.LayoutParams.WRAP_CONTENT);
            reloadParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            reloadParams.addRule(RelativeLayout.CENTER_VERTICAL);

            mErrorView.addView(button, reloadParams);

            RelativeLayout.LayoutParams notifyParams =
                    new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            notifyParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            notifyParams.addRule(RelativeLayout.ABOVE, 3);

            mErrorView.addView(errorText, notifyParams);
            button.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (isNetworkAvailable(getApplicationContext())) {
                        Map<String, String> additionalHttpHeaders = new HashMap<String, String>();
                        additionalHttpHeaders.put("Content-Type", "application/x-www-form-urlencoded;charset=utf-8");
                        mWebView.clearView();
                        mWebView.loadUrl(mUrl, additionalHttpHeaders);
                        hideErrorPage();
                    } else {
                        Toast.makeText(getApplicationContext(), "网络不可用,请检查网络状态！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            mErrorView.setOnClickListener(null);
            mErrorView.setBackgroundColor(0xfffafafa);
            RelativeLayout webParentView = (RelativeLayout) mWebView.getParent();
            RelativeLayout.LayoutParams lp =
                    new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

            webParentView.addView(mErrorView, 0, lp);
        }
    }

    public int dip2px(float dipValue) {
        final float scale = getApplicationContext().getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            } else {
                mEndTime = System.currentTimeMillis();
                mWebView.destroy();
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }
/*
    public static int getVersion() {
        return VERSION;
    }*/
    
}
