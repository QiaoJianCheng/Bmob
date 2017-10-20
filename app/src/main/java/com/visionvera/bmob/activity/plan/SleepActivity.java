package com.visionvera.bmob.activity.plan;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;

import com.visionvera.bmob.R;
import com.visionvera.bmob.utils.DateFormatUtil;
import com.visionvera.bmob.utils.ToastUtil;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

public class SleepActivity extends AppCompatActivity {
    private RadioGroup time_rg;
    private SeekBar seek_bar;
    private TimePicker time_picker;
    private Button time_ok;
    private TextView time_count;
    private Subscription mSubscribe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sleep);

        time_rg = (RadioGroup) findViewById(R.id.time_rg);
        seek_bar = (SeekBar) findViewById(R.id.seek_bar);
        time_picker = (TimePicker) findViewById(R.id.time_picker);
        time_ok = (Button) findViewById(R.id.time_ok);
        time_count = (TextView) findViewById(R.id.time_count);

        time_ok.setOnClickListener(this::ok);

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ToastUtil.showToast(String.format("%s分钟后", (progress + 1) * 15));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        time_picker.setIs24HourView(true);
        activeManager();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void ok(View v) {
        Calendar calendar = Calendar.getInstance();
        long current = System.currentTimeMillis();
        if (time_rg.getCheckedRadioButtonId() == R.id.time1_rb) {
            calendar.setTimeInMillis(current + (seek_bar.getProgress() + 1) * 15 * 60 * 1000);
        } else {
            int hour = time_picker.getHour();
            int minute = time_picker.getMinute();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
        }
        long delay = calendar.getTimeInMillis() - current;
        if (delay <= 1000) {
            ToastUtil.showToast("小于1秒");
            return;
        }
        if (mSubscribe != null) {
            mSubscribe.unsubscribe();
            mSubscribe = null;
        }
        ToastUtil.showToast(String.format("将在 %s:%s (%s分钟后)锁定屏幕", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), (delay / 1000 + 1) / 60));
        mSubscribe = Observable.interval(1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {
                    if (aLong >= delay / 1000) {
                        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
                        ComponentName componentName = new ComponentName(getApplicationContext(), AdminReceiver.class);
                        if (dpm.isAdminActive(componentName)) {
                            dpm.lockNow();
                            android.os.Process.killProcess(android.os.Process.myPid());
                        } else {
                            activeManager();
                        }
                    } else {
                        time_count.setText(DateFormatUtil.getFormattedDuration(delay - aLong * 1000));
                    }
                });

    }

    private void activeManager() {
        ComponentName componentName = new ComponentName(getApplicationContext(), AdminReceiver.class);
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "一键锁屏");
        startActivity(intent);
    }
}
