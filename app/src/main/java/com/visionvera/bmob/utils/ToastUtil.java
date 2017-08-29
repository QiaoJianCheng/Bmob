package com.visionvera.bmob.utils;

import android.content.Context;
import android.graphics.PixelFormat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.visionvera.bmob.R;
import com.visionvera.bmob.global.App;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Qiao on 2016/12/16.
 */

public class ToastUtil {
    private static Toast toast;
    private static WindowManager mWindowManager;
    private static WindowManager.LayoutParams mParams;
    private static final LinkedBlockingQueue<String> mTextQueue = new LinkedBlockingQueue<>();
    private static final Object mLock = new Object();
    private static Thread mQueueThread;

    /**
     * 强大的吐司，没有通知权限也能弹的土司
     */
    public static void showToast(final String text) {
        App.post(() -> {
            if (NotificationManagerCompat.from(App.getContext()).areNotificationsEnabled()) {
                showNormal(text);
                stop();
            } else {
                showWithoutPermission(text);
            }
        });
    }

    private static void showNormal(String text) {
        if (toast == null) {
            toast = new Toast(App.getContext());
        }
        TextView textView = new TextView(App.getContext());
        textView.setText(text);
        textView.setTextColor(ResUtil.getColor(R.color.colorWhite));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResUtil.getDimen(R.dimen.textSizeSmall));
        textView.setBackgroundResource(R.drawable.shape_toast_warning);
        toast.setView(textView);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

    private static void showWithoutPermission(String text) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) App.getContext().getSystemService(Context.WINDOW_SERVICE);
        }
        if (mParams == null) {
            mParams = new WindowManager.LayoutParams();
            mParams.format = PixelFormat.TRANSPARENT;
            mParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            mParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            mParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            mParams.y = DensityUtil.dip2px(50);
            mParams.windowAnimations = android.R.style.Animation_Toast;
            mParams.type = WindowManager.LayoutParams.TYPE_TOAST;
            mParams.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
        }
        if (mQueueThread != null)
            mQueueThread.interrupt();
        mTextQueue.offer(text);
        synchronized (mLock) {
            mLock.notify();
        }
        if (mQueueThread == null) {
            mEnable = true;
            mQueueThread = new Thread(new QueueThread(), "QueueThread");
            mQueueThread.start();
        }
    }

    private static boolean mEnable = true;

    private static void stop() {
        if (mEnable) {
            mEnable = false;
            mTextQueue.clear();
            synchronized (mLock) {
                mLock.notify();
            }
            mQueueThread = null;
        }
    }

    private static class QueueThread implements Runnable {
        @Override
        public void run() {
            while (mEnable) {
                String text = mTextQueue.poll();
                if (text != null) {
                    TextView textView = new TextView(App.getContext());
                    textView.setText(text);
                    textView.setTextColor(ResUtil.getColor(R.color.colorWhite));
                    textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, ResUtil.getDimen(R.dimen.textSizeSmall));
                    textView.setBackgroundResource(R.drawable.shape_toast_normal);
                    App.post(() -> mWindowManager.addView(textView, mParams));
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        LogUtil.d(e.getMessage());
                    }
                    App.post(() -> mWindowManager.removeViewImmediate(textView));
                } else {
                    synchronized (mLock) {
                        try {
                            mLock.wait();
                        } catch (InterruptedException e) {
                            LogUtil.d(e.getMessage());
                        }
                    }
                }
            }
        }
    }
}
