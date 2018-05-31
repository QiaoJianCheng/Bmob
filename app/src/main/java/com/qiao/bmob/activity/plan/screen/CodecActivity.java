package com.qiao.bmob.activity.plan.screen;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;

import com.qiao.bmob.R;
import com.qiao.bmob.activity.plan.VideoConfig;
import com.qiao.bmob.base.BaseActivity;
import com.qiao.bmob.dialog.DrawerDialog;
import com.qiao.bmob.utils.PermissionUtil;
import com.qiao.bmob.utils.ToastUtil;

public class CodecActivity extends BaseActivity {

    private static final int REQUEST_CODE_RECORD_SCREEN = 1;
    private static final int REQUEST_CODE_RECORD_AUDIO = 2;
    private MediaProjectionManager mMediaProjectionManager;
    private MediaProjection mMediaProjection;
    private ScreenRecorder mScreenRecorder;
    private SeekBar seek_bar;
    private CheckBox checkbox;
    private int ratio = 4;
    private ScreenEncoder mScreenEncoder;


    @Override
    protected void setContentView() {
        setStatusBarColor(R.color.colorThemeRed, false);
        setContentView(R.layout.activity_codec);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        seek_bar = findViewById(R.id.seek_bar);
        checkbox = findViewById(R.id.checkbox);

        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                ToastUtil.showToast("bitrate=" + progress);
                ratio = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void permission(View view) {
        permission();
    }

    private void permission() {
        if (!PermissionUtil.hasRecordAudioPermission()) {
            PermissionUtil.requestRecordAudioPermission(this, REQUEST_CODE_RECORD_AUDIO);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mMediaProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            if (mMediaProjection == null) {
                Intent intent = mMediaProjectionManager.createScreenCaptureIntent();
                startActivityForResult(intent, REQUEST_CODE_RECORD_SCREEN);
            } else {
                start();
            }
        } else {
            ToastUtil.showToast("仅 Android 5.0 及以上系统支持录屏");
        }
    }

    private void start() {
        new DrawerDialog(this)
                .addDrawer("1/1")
                .addDrawer("1/2")
                .addDrawer("1/4")
                .setOnItemClickedListener(position -> {
                    VideoConfig videoConfig;
                    if (position == 1) {
                        videoConfig = VideoConfig.getScreenConfigOriginal(ratio);
                    } else if (position == 2) {
                        videoConfig = VideoConfig.getScreenConfigHalf(ratio);
                    } else {
                        videoConfig = VideoConfig.getScreenConfigQuarter(ratio);
                    }
                    if (checkbox.isChecked()) {
                        if (mScreenEncoder != null) return;
                        mScreenEncoder = new ScreenEncoder(mMediaProjection, videoConfig);
                        mScreenEncoder.start();
                    } else {
                        if (mScreenRecorder != null) return;
                        mScreenRecorder = new ScreenRecorder(mMediaProjection, videoConfig);
                        mScreenRecorder.start();
                    }
                    ToastUtil.showToast("开始录屏");
//                    Intent intent = new Intent(Intent.ACTION_MAIN);
//                    intent.addCategory(Intent.CATEGORY_HOME);
//                    startActivity(intent);
                })
                .show();
    }

    public void stop(View view) {
        if (mScreenRecorder != null) {
            ToastUtil.showToast("停止录屏");
            mScreenRecorder.stop();
            mScreenRecorder = null;
        }
        if (mScreenEncoder != null) {
            ToastUtil.showToast("停止录屏");
            mScreenEncoder.release();
            mScreenEncoder = null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_RECORD_SCREEN) {
            mMediaProjection = mMediaProjectionManager.getMediaProjection(resultCode, data);
            if (mMediaProjection == null) {
                ToastUtil.showToast("录屏权限被拒绝");
                return;
            }
            start();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_RECORD_AUDIO) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permission();
            } else {
                ToastUtil.showToast("permission declined");
            }
        }
    }
}
