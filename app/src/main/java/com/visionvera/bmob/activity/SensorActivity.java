package com.visionvera.bmob.activity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.visionvera.bmob.R;
import com.visionvera.bmob.base.BaseActivity;
import com.visionvera.bmob.listener.PressEffectTouchListener;
import com.visionvera.bmob.utils.DateUtil;
import com.visionvera.bmob.utils.LogUtil;
import com.visionvera.bmob.utils.ResUtil;
import com.visionvera.bmob.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class SensorActivity extends BaseActivity implements View.OnClickListener {
    private TextView sensor_time_tv;
    private TextView sensor_count_tv;
    private ImageView sensor_start_iv;
    private ImageView sensor_stop_iv;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private boolean mIsPaused = true;
    private Subscription mSubscription;
    private long mStart;
    private long mDuration;
    private int mCount;
    private boolean mIsNear;

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
            ToastUtil.showToast("sensor TYPE_PROXIMITY not found");
        }
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitleBar(getString(R.string.title_sensor));
        sensor_time_tv = (TextView) findViewById(R.id.sensor_time_tv);
        sensor_count_tv = (TextView) findViewById(R.id.sensor_count_tv);
        sensor_start_iv = (ImageView) findViewById(R.id.sensor_start_iv);
        sensor_stop_iv = (ImageView) findViewById(R.id.sensor_stop_iv);

        sensor_start_iv.setOnClickListener(this);
        sensor_stop_iv.setOnClickListener(this);

        sensor_start_iv.setOnTouchListener(new PressEffectTouchListener());
        sensor_stop_iv.setOnTouchListener(new PressEffectTouchListener());
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sensor_start_iv) {
            if (mIsPaused) {
                start();
            } else {
                pause();
            }
        } else if (v.getId() == R.id.sensor_stop_iv) {
            stop();
        }
    }

    private void stop() {
        if (mSensorManager != null && mSensor != null) {
            mSensorManager.unregisterListener(mSensorEventListener, mSensor);
        }
        if (mSubscription != null && !mSubscription.isUnsubscribed()) {
            mSubscription.unsubscribe();
            mSubscription = null;
        }
        mDuration = 0;
        mCount = 0;
        sensor_time_tv.setText(DateUtil.getTimeString(mDuration));
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
                    .subscribe(new Action1<Long>() {
                        @Override
                        public void call(Long aLong) {
                            long duration = System.currentTimeMillis() - mStart;
                            sensor_time_tv.setText(DateUtil.getTimeString(mDuration + duration));
                        }
                    });
        }
        mIsPaused = false;
    }
}
