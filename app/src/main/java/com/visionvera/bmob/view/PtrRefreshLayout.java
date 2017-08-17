package com.visionvera.bmob.view;

import android.content.Context;
import android.util.AttributeSet;

import com.visionvera.bmob.R;
import com.visionvera.bmob.utils.ResUtil;

import in.srain.cube.views.ptr.PtrFrameLayout;

public class PtrRefreshLayout extends PtrFrameLayout {

    private PtrRefreshHeader mPtrRefreshHeader;

    public PtrRefreshLayout(Context context) {
        this(context, null);
    }

    public PtrRefreshLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PtrRefreshLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initViews();
    }

    private void initViews() {
        setDurationToClose(500);
        setDurationToCloseHeader(500);
        setKeepHeaderWhenRefresh(true);
        setPullToRefresh(false);
        setRatioOfHeaderHeightToRefresh(110.0f / 710);
        setResistance(1.7f);
        int x110 = (int) ResUtil.getDimen(R.dimen.x110);
        setOffsetToKeepHeaderWhileLoading(x110);

        mPtrRefreshHeader = new PtrRefreshHeader(getContext());
        setHeaderView(mPtrRefreshHeader);
        addPtrUIHandler(mPtrRefreshHeader);
    }

    public PtrRefreshHeader getHeader() {
        return mPtrRefreshHeader;
    }
}
