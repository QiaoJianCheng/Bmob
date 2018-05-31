package com.qiao.bmob.listener;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by Qiao on 2016/11/22.
 * 通过改变按钮的alpha值达到视觉上的按压效果
 */

public class PressEffectTouchListener implements View.OnTouchListener {
    private Handler mHandler = new Handler();

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(final View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                v.setAlpha(0.5f);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        v.setAlpha(1.0f);
                    }
                }, 100);
                break;
        }
        return false;
    }
}
