package com.qiao.bmob.listener;

import android.view.MotionEvent;
import android.view.View;

import com.qiao.bmob.utils.LogUtil;

/**
 * Created by Qiao on 2017/5/15.
 */

public abstract class OnDoubleTapListener implements View.OnTouchListener {
    private static final int SPAN = 300;
    private long mLastUpTime = 0;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        LogUtil.d("event:" + event.getAction() + " mLastUpTime:" + mLastUpTime);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (System.currentTimeMillis() - mLastUpTime > SPAN) {
                    mLastUpTime = 0;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mLastUpTime == 0) {
                    mLastUpTime = System.currentTimeMillis();
                } else {
                    if (System.currentTimeMillis() - mLastUpTime <= SPAN) {
                        onDoubleTap(v);
                    }
                    mLastUpTime = 0;
                }
                break;
        }
        return false;
    }

    public abstract void onDoubleTap(View v);

}
