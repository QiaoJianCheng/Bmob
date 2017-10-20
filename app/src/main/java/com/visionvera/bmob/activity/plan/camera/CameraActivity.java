package com.visionvera.bmob.activity.plan.camera;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.visionvera.bmob.R;
import com.visionvera.bmob.activity.plan.VideoConfig;
import com.visionvera.bmob.base.BaseActivity;
import com.visionvera.bmob.listener.PressEffectTouchListener;
import com.visionvera.bmob.utils.PermissionUtil;
import com.visionvera.bmob.utils.ToastUtil;


public class CameraActivity extends BaseActivity implements View.OnClickListener {
    private CameraView camera_preview;
    private TextView timer_tv;
    private ImageView shot_iv;
    private ImageView switch_iv;
    private CameraEncoder mCameraEncoder;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_camera);

        PermissionUtil.requestCameraPermission(this, 100);
        PermissionUtil.requestRecordAudioPermission(this, 101);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        camera_preview = (CameraView) findViewById(R.id.camera_preview);
        timer_tv = (TextView) findViewById(R.id.timer_tv);
        shot_iv = (ImageView) findViewById(R.id.shot_iv);
        switch_iv = (ImageView) findViewById(R.id.switch_iv);

        shot_iv.setOnClickListener(this);
        switch_iv.setOnClickListener(this);

        shot_iv.setOnTouchListener(new PressEffectTouchListener());
        switch_iv.setOnTouchListener(new PressEffectTouchListener());
        camera_preview.setCameraListener(new CameraView.CameraListener() {
            @Override
            public void onCameraOpen(VideoConfig videoConfig) {
            }

            @Override
            public void onYUVCallback(byte[] yuv, VideoConfig videoConfig) {
                if (mCameraEncoder != null && mCameraEncoder.isRecording()) {
                    mCameraEncoder.encode(yuv);
                }
            }
        });
    }

    @Override
    protected void loadData(boolean showLoading) {
        super.loadData(showLoading);
        camera_preview.setConfig(VideoConfig.getCameraConfigHD());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.shot_iv) {
            startPlay();
        } else if (v.getId() == R.id.switch_iv) {
            VideoConfig videoConfig = camera_preview.getVideoConfig();
            videoConfig.source = videoConfig.source == VideoConfig.SOURCE_CAMERA_BACK ? VideoConfig.SOURCE_CAMERA_FRONT : VideoConfig.SOURCE_CAMERA_BACK;
            camera_preview.setConfig(videoConfig);
        }
    }

    private void startPlay() {
        if (mCameraEncoder != null && mCameraEncoder.isRecording()) {
            ToastUtil.showToast("结束录制");
            mCameraEncoder.release();
            mCameraEncoder = null;
            camera_preview.lockAutoExposure(false);
        } else {
            ToastUtil.showToast("开始录制");
            if (mCameraEncoder != null) mCameraEncoder.release();
            mCameraEncoder = new CameraEncoder(camera_preview.getVideoConfig());
            mCameraEncoder.start();
            camera_preview.lockAutoExposure(true);
        }
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
