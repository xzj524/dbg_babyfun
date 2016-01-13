/*
 * Copyright (C) 2014 Baidu Inc. All rights reserved.
 */
package com.xzj.babyfun.ui.component.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.xzj.babyfun.R;

/**
 * 
 * @author panxu
 * @since 2014-1-27
 */

public class RouterStatusFragment extends Fragment {

    public static final int ABNORMAL_REQUEST_CODE = 1;

    /** 路由名称 */
    private TextView mRouterNameTextView;

    /** 速度 */
    private TextView mRouterSpeedTextView;

    /** 远程还是本地 */
    private TextView mRemoteOrLocalTextView;

    /** 检查网络 */
    private TextView mCheckNetworkConnectingTextView;

    /** 连接后的状态 成功还是失败 */
    private TextView mConnectedStatusTextView;

    /** 连接时候的progress */
    private ImageView mProgressImageView;

    /** 正在连接动画 */
    private boolean mIsConnectingAnimation;

    /** 适配器 提供数据 */
//    private RouterAdapter mRouterAdapter;

    /** 正在连接的view group */
    private ViewGroup mConnectingInfoViewGroup;

    /** 正在检查的image view */
    private ImageView mCheckingItemImageView;

    /** 连接完成的状态view gourp */
    private ViewGroup mConnectedStatusViewGroup;

    /** 连接状态标识image view */
    private ImageView mConnectedStatusIndicatorImageView;

    /** 设置的image view */
    private ImageView mSettingImageView;

    /** 正在检测的状态 */
    private CheckingState mCurrentState;

    /** 检测状态结果 */
    private int[] mCheckStates;

    /** UI handler */
    private final Handler mUIHandler = new Handler(Looper.getMainLooper());

    /** 是否显示了百度登陆 */
    private boolean mIsBaiduLoginShow;

    /** 是否destory */
    private boolean mIsDestroyed;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        mIsDestroyed = false;

        mCurrentState = CheckingState.IDEL;

     /*   mRouterAdapter = new RouterAdapter(getActivity(), this);
        mRouterAdapter.setCheckingListener(this);
        mRouterAdapter.onCreate(getActivity(), savedInstanceState);

        new UpgradeFactoryXlink().createEngine().autoCheck(new AutoCheckListener(this));*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View babyfunStatusView = inflater.inflate(R.layout.babyfun_status_fragment, container, false);
        

        mCheckNetworkConnectingTextView = (TextView) babyfunStatusView.findViewById(R.id.checkingNetworkTextView);

        mProgressImageView = (ImageView) babyfunStatusView.findViewById(R.id.progressImageView);
        mProgressImageView.setOnClickListener(new UpdateStatusOnclickListener());

        mConnectingInfoViewGroup = (ViewGroup) babyfunStatusView.findViewById(R.id.connectInfoLayout);
        mCheckingItemImageView = (ImageView) babyfunStatusView.findViewById(R.id.checkingItemImageView);

        mConnectedStatusViewGroup = (ViewGroup) babyfunStatusView.findViewById(R.id.connectedStatusLayout);
        mConnectedStatusIndicatorImageView = (ImageView) babyfunStatusView
                .findViewById(R.id.connectedStatusIndicatorImageView);

        mConnectedStatusTextView = (TextView) babyfunStatusView.findViewById(R.id.connectedStatusTextView);

 

   

        return babyfunStatusView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mCurrentState == CheckingState.IDEL) {
            mCurrentState = CheckingState.CHECKING;
            startConnecting();
        }
    }

    /**
     * 设置路由名称
     * 
     * @param name
     *            路由名称
     */
    public void setRouterName(String name) {
        mRouterNameTextView.setText(name);
    }


    /**
     * 设置远程还是直连
     * 
     * @param isLocal
     *            是否是直连
     */
    public void setRemoteOrLocal(boolean isLocal) {
        if (isLocal) {
            mRemoteOrLocalTextView.setText(R.string.local);
        } else {
            mRemoteOrLocalTextView.setText(R.string.remote);
        }
    }

    /**
     * 开始检查连接
     */
    public void startConnecting() {

        if (!mIsConnectingAnimation) {
            mIsConnectingAnimation = true;

            mProgressImageView.setImageResource(R.drawable.main_checking_status_progress);

            Animation progressAnimation = AnimationUtils.loadAnimation(getActivity(),
                    R.anim.connecting_router_rotate_animation);

            mProgressImageView.startAnimation(progressAnimation);

            mConnectedStatusViewGroup.setVisibility(View.GONE);

            mConnectingInfoViewGroup.setVisibility(View.VISIBLE);

        }
    }

 

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
       
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mIsDestroyed = true;


        super.onDestroy();
    }
    
    /**
     * 更新状态的OnClick
     * 
     * @author panxu
     * @since 2014-8-8
     */
    private class UpdateStatusOnclickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
         //   doUpdateStatusClick();
        }
    }

  

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK && requestCode == ABNORMAL_REQUEST_CODE) {
           
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    /**
     * 检查的状态
     * 
     * @author panxu
     * @since 2014-8-8
     */
    private static enum CheckingState {
        IDEL, // idel
        CHECKING, // 正在检查
        FATAL_ROUTER_NOT_CONNECT, // 无法连接路由
        FAIL // 失败
    }


    public CheckingState getCurrentState() {
        return mCurrentState;
    }

}
