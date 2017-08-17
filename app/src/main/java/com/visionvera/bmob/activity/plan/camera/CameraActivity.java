package com.visionvera.bmob.activity.plan.camera;

import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.visionvera.bmob.R;
import com.visionvera.bmob.activity.plan.camera.opengl.VideoDisplayFrame;
import com.visionvera.bmob.activity.plan.camera.opengl.VideoImage;
import com.visionvera.bmob.base.BaseActivity;
import com.visionvera.bmob.listener.PressEffectTouchListener;
import com.visionvera.bmob.utils.DateFormatUtil;
import com.visionvera.bmob.utils.LogUtil;
import com.visionvera.bmob.utils.PermissionUtil;
import com.visionvera.bmob.utils.ToastUtil;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;


public class CameraActivity extends BaseActivity implements View.OnClickListener {
    private CameraView camera_preview;
    private VideoImage camera_play_view;
    private TextView timer_tv;
    private ImageView shot_iv;
    private ImageView switch_iv;
    private Subscription mSubscription;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_camera);

        PermissionUtil.requestCameraPermission(this, 100);
        PermissionUtil.requestRecordAudioPermission(this, 101);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        camera_preview = (CameraView) findViewById(R.id.camera_preview);
        camera_play_view = (VideoImage) findViewById(R.id.camera_play_view);
        timer_tv = (TextView) findViewById(R.id.timer_tv);
        shot_iv = (ImageView) findViewById(R.id.shot_iv);
        switch_iv = (ImageView) findViewById(R.id.switch_iv);

        shot_iv.setOnClickListener(this);
        switch_iv.setOnClickListener(this);

        shot_iv.setOnTouchListener(new PressEffectTouchListener());
        switch_iv.setOnTouchListener(new PressEffectTouchListener());
        camera_preview.setCameraListener(new CameraView.CameraListener() {
            @Override
            public void onCameraOpen(int cameraId, Camera camera) {
            }

            @Override
            public void onCameraRelease() {
                timer_tv.setText("00:00");
                if (mSubscription != null && !mSubscription.isUnsubscribed()) {
                    mSubscription.unsubscribe();
                    mSubscription = null;
                }
            }

            @Override
            public void onOrientationChanged(int orientation) {
                LogUtil.d("orientation: " + orientation);
            }

            @Override
            public void onPreviewFrame(byte[] data) {
                Log.d(TAG, "onPreviewFrame:" + data.length);
//                byte[] tmp = data.clone();
//                rotateYUV240SP(tmp, data, 1280, 720);
                if (mIsRecording) {
                    VideoDisplayFrame vdFrame = new VideoDisplayFrame(data, 0, data.length, 1280, 720, System.currentTimeMillis());
                    camera_play_view.Play(vdFrame);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private boolean mIsRecording;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.shot_iv) {
            if (mIsRecording) {
                ToastUtil.showToast("结束录制");
                timer_tv.setText("00:00");
                if (mSubscription != null && !mSubscription.isUnsubscribed()) {
                    mSubscription.unsubscribe();
                    mSubscription = null;
                }
            } else {
                ToastUtil.showToast("开始录制");
                final long start = System.currentTimeMillis();
                if (mSubscription == null) {
                    mSubscription = Observable.interval(0, 10, TimeUnit.MILLISECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<Long>() {
                                @Override
                                public void call(Long aLong) {
                                    long current = System.currentTimeMillis();
                                    timer_tv.setText(DateFormatUtil.getFormattedDuration(current - start));
                                }
                            });
                }
                startPlay();
            }
            mIsRecording = !mIsRecording;
        } else if (v.getId() == R.id.switch_iv) {
            camera_preview.switchCamera();
        }
    }

    private void startPlay() {
//        camera_play_view.setVideoPath(mVideoRecorder.getFile());
    }

    // 顺时针旋转90度：一般后置摄像头使用
    public static void rotateYUV240SP(byte[] src, byte[] dst, int srcWidth, int srcHeight) {
        if (false) {
            int nWidth = 0, nHeight = 0;
            int wh = 0;
            int uvHeight = 0;
            if (srcWidth != nWidth || srcHeight != nHeight) {
                nWidth = srcWidth;
                nHeight = srcHeight;
                wh = srcWidth * srcHeight;
                uvHeight = srcHeight >> 1;// uvHeight = height / 2
            }

            // 旋转Y
            int k = 0;
            for (int i = 0; i < srcWidth; i++) {
                int nPos = 0;
                for (int j = 0; j < srcHeight; j++) {
                    dst[k] = src[nPos + i];
                    k++;
                    nPos += srcWidth;
                }
            }

            for (int i = 0; i < srcWidth; i += 2) {
                int nPos = wh;
                for (int j = 0; j < uvHeight; j++) {
                    dst[k] = src[nPos + i];
                    dst[k + 1] = src[nPos + i + 1];
                    k += 2;
                    nPos += srcWidth;
                }
            }

        } else {
            int nWidth = 0, nHeight = 0;
            int wh = 0;
            int uvHeight = 0;
            if (srcWidth != nWidth || srcHeight != nHeight) {
                nWidth = srcWidth;
                nHeight = srcHeight;
                wh = srcWidth * srcHeight;
                uvHeight = srcHeight >> 1; // uvHeight = height / 2
            }

            int k = 0;
            for (int i = 0; i < srcWidth; i++) {
                int nPos = 0;
                for (int j = 0; j < srcHeight; j++) {
                    dst[k + (srcHeight - j - 1)] = src[nPos + i];
                    nPos += srcWidth;
                }
                k += srcHeight;
            }

            for (int i = 0; i < srcWidth; i += 2) {
                int nPos = wh;
                for (int j = 0; j < uvHeight; j++) {
                    dst[k + srcHeight - j - j - 2] = src[nPos + i];
                    dst[k + srcHeight - j - j - 1] = src[nPos + i + 1];
                    nPos += srcWidth;
                }
                k += srcHeight;
            }
            return;
        }

        return;

    }

    // 逆时针旋转90度：一般前置摄像头使用
    public static void YUV420spRotateNegative90(byte[] src, byte[] dst, int srcWidth, int srcHeight) {
        if (false) {
            int nWidth = 0, nHeight = 0;
            int wh = 0;
            int uvHeight = 0;
            if (srcWidth != nWidth || srcHeight != nHeight) {
                nWidth = srcWidth;
                nHeight = srcHeight;
                wh = srcWidth * srcHeight;
                uvHeight = srcHeight >> 1;// uvHeight = height / 2
            }
            // 旋转Y
            int k = 0;
            for (int i = 0; i < srcWidth; i++) {
                int nPos = srcWidth - 1;
                for (int j = 0; j < srcHeight; j++) {
                    dst[k] = src[nPos - i];
                    k++;
                    nPos += srcWidth;
                }
            }
            for (int i = 0; i < srcWidth; i += 2) {
                int nPos = wh + srcWidth - 1;
                for (int j = 0; j < uvHeight; j++) {
                    dst[k] = src[nPos - i - 1];
                    dst[k + 1] = src[nPos - i];
                    k += 2;
                    nPos += srcWidth;
                }
            }

        } else {
            int nWidth = 0, nHeight = 0;
            int wh = 0;
            int uvHeight = 0;
            if (srcWidth != nWidth || srcHeight != nHeight) {
                nWidth = srcWidth;
                nHeight = srcHeight;
                wh = srcWidth * srcHeight;
                uvHeight = srcHeight >> 1; // uvHeight = height / 2
            }

            // rotate Y
            int k = 0;
            for (int i = 0; i < srcWidth; i++) {
                int nPos = srcWidth - 1;
                for (int j = 0; j < srcHeight; j++) {
                    dst[k] = src[nPos - i];
                    k++;
                    nPos += srcWidth;
                }
            }

            // rotate UV
            for (int i = 0; i < srcWidth; i += 2) {
                int nPos = wh + srcWidth - 1;
                for (int j = 0; j < uvHeight; j++) {
                    dst[k] = src[nPos - i - 1];
                    dst[k + 1] = src[nPos - i];
                    k += 2;
                    nPos += srcWidth;
                }
            }
            return;
        }
    }

}
