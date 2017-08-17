package com.visionvera.bmob.view;

import android.content.Context;
import android.util.AttributeSet;

import com.facebook.drawee.view.SimpleDraweeView;
import com.visionvera.bmob.utils.DensityUtil;

public class SquareImageView extends SimpleDraweeView {

    int mSize;

    public SquareImageView(Context context) {
        this(context, null);
    }

    public SquareImageView(Context context, int size) {
        super(context);
        this.mSize = size;
    }

    public SquareImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        int screenWidth = DensityUtil.getScreenWidth();
        mSize = (screenWidth - DensityUtil.dip2px(4)) / 3;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(mSize, mSize);
    }

}
