package com.qiao.bmob.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.qiao.bmob.R;
import com.qiao.bmob.utils.DensityUtil;
import com.qiao.bmob.utils.FrescoUtil;
import com.qiao.bmob.utils.ResUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Qiao on 2017/2/10.
 */

public class GridPhotoView extends FrameLayout {
    private ArrayList<String> mUrls = new ArrayList<>();
    private int mItemSize;
    private int mWidth;
    private int mSpace;

    public GridPhotoView(Context context) {
        this(context, null);
    }

    public GridPhotoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GridPhotoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSpace = (int) ResUtil.getDimen(R.dimen.y20);
        mItemSize = (int) (DensityUtil.getScreenWidth() - ResUtil.getDimen(R.dimen.x360)) / 3;
        mWidth = (int) (DensityUtil.getScreenWidth() - ResUtil.getDimen(R.dimen.x320));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int childCount = getChildCount();
        if (childCount == 0) {
            heightMeasureSpec = 0;
        } else if (childCount == 1) {
            heightMeasureSpec = mItemSize * 2 + mSpace;
        } else if (childCount < 4) {
            heightMeasureSpec = mItemSize;
        } else if (childCount < 7) {
            heightMeasureSpec = mItemSize * 2 + mSpace;
        } else {
            heightMeasureSpec = mItemSize * 3 + mSpace * 2;
        }
        setMeasuredDimension(mWidth, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childCount = getChildCount();
        if (childCount == 1) {
            int rightBottom = mItemSize * 2 + mSpace;
            getChildAt(0).layout(0, 0, rightBottom, rightBottom);
        } else if (childCount == 4) {
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                childAt.layout(i % 2 * (mItemSize + mSpace), i / 2 * (mItemSize + mSpace), i % 2 * (mItemSize + mSpace) + mItemSize, i / 2 * (mItemSize + mSpace) + mItemSize);
            }
        } else {
            for (int i = 0; i < childCount; i++) {
                View childAt = getChildAt(i);
                childAt.layout(i % 3 * (mItemSize + mSpace), i / 3 * (mItemSize + mSpace), i % 3 * (mItemSize + mSpace) + mItemSize, i / 3 * (mItemSize + mSpace) + mItemSize);
            }
        }
    }

    public void setUrls(List<String> urls) {
        mUrls.clear();
        removeAllViews();
        if (urls == null || urls.size() == 0) return;
        mUrls.addAll(urls);
        if (mUrls.size() > 0) {
            for (int i = 0; i < mUrls.size(); i++) {
                String url = mUrls.get(i);
                addUrl(url);
            }
            invalidate();
        }
    }

    public void addUrl(String url) {
        mUrls.add(url);
        SquareImageView squareImageView = new SquareImageView(getContext(), mItemSize);
        int position = getChildCount();
        addView(squareImageView);
        GenericDraweeHierarchyBuilder builder = new GenericDraweeHierarchyBuilder(getResources())
                .setPressedStateOverlay(ResUtil.getDrawable(R.color.colorBlack40))
                .setPlaceholderImage(R.color.colorLine);
        squareImageView.setHierarchy(builder.build());
        FrescoUtil.display(squareImageView, url, mItemSize, mItemSize);
        squareImageView.setOnClickListener(v -> {
            if (mOnItemClickListener != null) {
                mOnItemClickListener.onClick(position, mUrls);
            }
        });
    }

    public interface OnItemClickListener {
        void onClick(int position, ArrayList<String> urls);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.mOnItemClickListener = onItemClickListener;
    }
}
