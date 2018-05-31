package com.qiao.bmob.view;

import android.content.Context;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.qiao.bmob.R;
import com.qiao.bmob.utils.DensityUtil;
import com.qiao.bmob.utils.ResUtil;

import java.lang.reflect.Field;

/**
 * Created by Qiao on 2017/4/18.
 */

public class DragParentLayout extends FrameLayout {
    private static final String TAG = DragParentLayout.class.getSimpleName();

    private static final float RELEASE_VELOCITY_LIMIT = 2000.0f;
    private static final float RELEASE_LEFT_LIMIT = 0.3f;
    private static final int EDGE_SIZE = (int) ResUtil.getDimen(R.dimen.x100);
    private static final int SHADOW_SIZE = (int) ResUtil.getDimen(R.dimen.x100);
    private ViewDragHelper mDragHelper;
    private View mDragView;
    private int mLeft;
    private int mScreenWidth;
    private boolean mIsFromEdge;


    public DragParentLayout(Context context) {
        this(context, null);
    }

    public DragParentLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DragParentLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDragHelper = ViewDragHelper.create(this, 1.0f, mCallback);
        mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        try {
            Field mEdgeSize = mDragHelper.getClass().getDeclaredField("mEdgeSize");
            mEdgeSize.setAccessible(true);
            mEdgeSize.setInt(mDragHelper, EDGE_SIZE);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
        mScreenWidth = DensityUtil.getScreenWidth();
    }

    @Override
    public void addView(View child, int index) {
        super.addView(child, index);
        child.setElevation(100);
        mDragView = child;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        final int action = MotionEventCompat.getActionMasked(ev);
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
            mDragHelper.cancel();
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            mIsFromEdge = ev.getRawX() < EDGE_SIZE;
        }
        mDragHelper.processTouchEvent(ev);
        return true;
    }

    private ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            return mDragView == child && mIsFromEdge;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            return left;
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            mDragHelper.captureChildView(mDragView, pointerId);
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            mLeft = left;
            mDragView.setElevation((mScreenWidth - mLeft) / 10);
            if (mLeft == mScreenWidth) {
                if (mOnActivityListener != null) {
                    mOnActivityListener.onActivityFinish();
                }
            }
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (xvel > RELEASE_VELOCITY_LIMIT) {
                smoothToFinish(releasedChild);
            } else {
                if (mLeft > RELEASE_LEFT_LIMIT * mScreenWidth) {
                    smoothToFinish(releasedChild);
                } else {
                    smoothToClose(releasedChild);
                }
            }
        }
    };

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void smoothToFinish(View releasedChild) {
        if (mDragHelper.smoothSlideViewTo(releasedChild, mScreenWidth, 0)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void smoothToClose(View releasedChild) {
        if (mDragHelper.smoothSlideViewTo(releasedChild, 0, 0)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    public interface OnActivityListener {
        void onActivityFinish();
    }

    private OnActivityListener mOnActivityListener;

    public void setOnActivityListener(OnActivityListener onActivityListener) {
        mOnActivityListener = onActivityListener;
    }
}
