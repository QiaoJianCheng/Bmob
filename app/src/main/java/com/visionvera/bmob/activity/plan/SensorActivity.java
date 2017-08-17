package com.visionvera.bmob.activity.plan;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.visionvera.bmob.R;
import com.visionvera.bmob.base.BaseActivity;
import com.visionvera.bmob.dialog.CommonDialog;
import com.visionvera.bmob.listener.PressEffectTouchListener;
import com.visionvera.bmob.utils.DateFormatUtil;
import com.visionvera.bmob.utils.LogUtil;
import com.visionvera.bmob.utils.ResUtil;
import com.visionvera.bmob.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class SensorActivity extends BaseActivity {
    private TextView sensor_time_tv;
    private TextView sensor_count_tv;
    private ImageView sensor_start_iv;
    private ImageView sensor_stop_iv;
    private TextView sensor_sport1_tv;
    private TextView sensor_sport2_tv;
    private TextView sensor_sport3_tv;
    private TextView sensor_sport4_tv;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private boolean mIsPaused = true;
    private Subscription mSubscription;
    private long mStart;
    private long mDuration;
    private int mCount;
    private boolean mIsNear;
    private int mSport;

    @Override
    protected void setContentView() {
        setStatusBarColor(R.color.colorThemeRed, false);
        setContentView(R.layout.activity_senser);
    }

    @Override
    protected void initData() {
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        if (mSensor == null) {
            ToastUtil.warnToast("sensor TYPE_PROXIMITY not found");
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitleBar(getString(R.string.title_sensor));
        sensor_time_tv = (TextView) findViewById(R.id.sensor_time_tv);
        sensor_count_tv = (TextView) findViewById(R.id.sensor_count_tv);
        sensor_start_iv = (ImageView) findViewById(R.id.sensor_start_iv);
        sensor_stop_iv = (ImageView) findViewById(R.id.sensor_stop_iv);

        sensor_sport1_tv = (TextView) findViewById(R.id.sensor_sport1_tv);
        sensor_sport2_tv = (TextView) findViewById(R.id.sensor_sport2_tv);
        sensor_sport3_tv = (TextView) findViewById(R.id.sensor_sport3_tv);
        sensor_sport4_tv = (TextView) findViewById(R.id.sensor_sport4_tv);

        sensor_start_iv.setOnClickListener(v -> startClick());
        sensor_stop_iv.setOnClickListener(v -> stop(null));
        sensor_sport1_tv.setOnClickListener(v -> {
            mSport = 1;
            stop(v);
        });
        sensor_sport2_tv.setOnClickListener(v -> {
            mSport = 2;
            stop(v);
        });
        sensor_sport3_tv.setOnClickListener(v -> {
            mSport = 3;
            stop(v);
        });
        sensor_sport4_tv.setOnClickListener(v -> {
            mSport = 4;
            stop(v);
        });

        sensor_start_iv.setOnTouchListener(new PressEffectTouchListener());
        sensor_stop_iv.setOnTouchListener(new PressEffectTouchListener());
        sensor_sport1_tv.setOnTouchListener(new PressEffectTouchListener());
        sensor_sport2_tv.setOnTouchListener(new PressEffectTouchListener());
        sensor_sport3_tv.setOnTouchListener(new PressEffectTouchListener());
        sensor_sport4_tv.setOnTouchListener(new PressEffectTouchListener());
    }

    private SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
                float maximumRange = event.sensor.getMaximumRange();
                String value = "";
                if (event.values[0] < maximumRange) {
                    mIsNear = true;
                } else {
                    if (mIsNear && !mIsPaused) {
                        mCount++;
                        mIsNear = false;
                        sensor_count_tv.setText(String.valueOf(mCount));
                        if (mSport == 1) {
                            sensor_sport1_tv.setText(String.format("仰卧起坐: %s 个", mCount));
                        } else if (mSport == 2) {
                            sensor_sport2_tv.setText(String.format("俯卧撑: %s 个", mCount));
                        }
                    }
                }
                LogUtil.d(TAG, "accuracy=" + event.accuracy + " values=" + value);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void startClick() {
        if (mIsPaused) {
            start();
        } else {
            pause();
        }
    }

    private void stop(View v) {
        animateView(v);
        if (mSensorManager != null && mSensor != null) {
            mSensorManager.unregisterListener(mSensorEventListener, mSensor);
        }
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
        mDuration = 0;
        mCount = 0;
        sensor_time_tv.setText(DateFormatUtil.getFormattedDuration(mDuration));
        sensor_count_tv.setText(String.valueOf(0));
        sensor_start_iv.setImageResource(R.drawable.start);
        sensor_start_iv.animate().translationX(0).setDuration(150).start();
        sensor_stop_iv.animate().translationX(0).alpha(0).setDuration(150).start();
        mIsPaused = true;
    }

    private void pause() {
        if (mSensorManager != null && mSensor != null) {
            mSensorManager.unregisterListener(mSensorEventListener, mSensor);
        }
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
        sensor_start_iv.setImageResource(R.drawable.start);
        mDuration += System.currentTimeMillis() - mStart;
        mIsPaused = true;
    }

    private void start() {
        if (mSensorManager != null && mSensor != null) {
            mSensorManager.registerListener(mSensorEventListener, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        sensor_start_iv.setImageResource(R.drawable.pause);
        sensor_start_iv.animate().translationX(ResUtil.getDimen(R.dimen.x200)).setDuration(150).start();
        sensor_stop_iv.animate().translationX(-ResUtil.getDimen(R.dimen.x200)).alpha(1).setDuration(150).start();
        mStart = System.currentTimeMillis();
        if (mSubscription == null) {
            mSubscription = Observable.interval(0, 10, TimeUnit.MILLISECONDS)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(aLong -> {
                        long duration = System.currentTimeMillis() - mStart;
                        String format = DateFormatUtil.getFormattedDuration(mDuration + duration);
                        sensor_time_tv.setText(format);
                        if (mSport == 3) {
                            sensor_sport3_tv.setText(String.format("高抬腿: %s", format));
                        } else if (mSport == 4) {
                            sensor_sport4_tv.setText(String.format("平板撑: %s", format));
                        }
                    });
        }
        mIsPaused = false;
    }

    private void animateView(View view) {
        sensor_sport1_tv.clearAnimation();
        sensor_sport2_tv.clearAnimation();
        sensor_sport3_tv.clearAnimation();
        sensor_sport4_tv.clearAnimation();
        if (view == null) return;
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.2f);
        anim.setDuration(800);
        anim.setRepeatCount(Animation.INFINITE);
        anim.setRepeatMode(Animation.REVERSE);
        view.startAnimation(anim);
    }

    @Override
    public void onBackPressed() {
        new CommonDialog(this)
                .setTitle("确认退出?")
                .setMsg("未完成的进度将丢失")
                .setOnButtonClickListener(new CommonDialog.OnButtonClickListener() {
                    @Override
                    public void onConfirm() {
                        SensorActivity.super.onBackPressed();
                    }

                    @Override
                    public void onCancel() {

                    }
                })
                .show();
    }
}
