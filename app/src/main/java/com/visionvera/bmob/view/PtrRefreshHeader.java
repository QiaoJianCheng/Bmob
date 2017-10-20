package com.visionvera.bmob.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.visionvera.bmob.R;
import com.visionvera.bmob.utils.FrescoUtil;
import com.visionvera.bmob.utils.ResUtil;

import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrUIHandler;
import in.srain.cube.views.ptr.indicator.PtrIndicator;

public class PtrRefreshHeader extends FrameLayout implements PtrUIHandler {

    private RotateAnimation mFlipAnimation;
    private RotateAnimation mReverseFlipAnimation;
    private TextView mTextView;
    private ImageView mRotateView;
    private View mProgressBar;
    private SimpleDraweeView mAdView;
    private static final String AD_URL = "http://wx1.sinaimg.cn/large/c1a9d02cly1ff25bmgprxj20zk0hsact.jpg";
//    private static final String AD_URL = "http://wx4.sinaimg.cn/large/c1a9d02cly1ff26r4fa6uj21hc0u0t9v.jpg";

    public PtrRefreshHeader(Context context) {
        super(context);
        initViews();
    }

    public PtrRefreshHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews();
    }

    public PtrRefreshHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    protected void initViews() {
        buildAnimation();

        View header = LayoutInflater.from(getContext()).inflate(R.layout.header_ptr_default, this);
        mTextView = (TextView) header.findViewById(R.id.ptr_classic_header_text);
        mRotateView = (ImageView) header.findViewById(R.id.ptr_classic_header_rotate_view);
        mProgressBar = header.findViewById(R.id.ptr_classic_header_rotate_view_progressbar);
        mAdView = (SimpleDraweeView) header.findViewById(R.id.ptr_classic_header_ad_sdv);
        FrescoUtil.display(mAdView, AD_URL, (int) ResUtil.getDimen(R.dimen.x1080), (int) ResUtil.getDimen(R.dimen.x500));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    private void buildAnimation() {
        mFlipAnimation = new RotateAnimation(0, 180, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mFlipAnimation.setInterpolator(new LinearInterpolator());
        int rotateAniTime = 150;
        mFlipAnimation.setDuration(rotateAniTime);
        mFlipAnimation.setFillAfter(true);

        mReverseFlipAnimation = new RotateAnimation(-180, 0, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        mReverseFlipAnimation.setInterpolator(new LinearInterpolator());
        mReverseFlipAnimation.setDuration(rotateAniTime);
        mReverseFlipAnimation.setFillAfter(true);
    }

    private void resetView() {
        hideRotateView();
        mProgressBar.setVisibility(INVISIBLE);
        mTextView.setText(R.string.header_pull_to_refresh);
    }

    private void hideRotateView() {
        mRotateView.clearAnimation();
        mRotateView.setVisibility(INVISIBLE);
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        resetView();
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        mProgressBar.setVisibility(INVISIBLE);
        mRotateView.setVisibility(VISIBLE);
        mTextView.setText(R.string.header_pull_to_refresh);
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        hideRotateView();
        mProgressBar.setVisibility(VISIBLE);
        mTextView.setText(R.string.header_refreshing);
    }

    @Override
    @SuppressLint("CommitPrefEdits")
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        hideRotateView();
        mProgressBar.setVisibility(INVISIBLE);
        mRotateView.setVisibility(VISIBLE);
        mTextView.setText(R.string.header_pull_to_refresh);
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {

        final int mOffsetToRefresh = frame.getOffsetToRefresh();
        final int currentPos = ptrIndicator.getCurrentPosY();
        final int lastPos = ptrIndicator.getLastPosY();

        if (currentPos < mOffsetToRefresh && lastPos >= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                if (mRotateView != null) {
                    mRotateView.clearAnimation();
                    mRotateView.startAnimation(mReverseFlipAnimation);
                }
                if (mTextView != null) mTextView.setText(R.string.header_pull_to_refresh);
            }
        } else if (currentPos > mOffsetToRefresh && lastPos <= mOffsetToRefresh) {
            if (isUnderTouch && status == PtrFrameLayout.PTR_STATUS_PREPARE) {
                if (mRotateView != null) {
                    mRotateView.clearAnimation();
                    mRotateView.startAnimation(mFlipAnimation);
                }
                if (mTextView != null) mTextView.setText(R.string.header_release_to_refresh);
            }
        }
    }
}
