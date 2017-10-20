package com.visionvera.bmob.activity.plan.camera;


import android.content.Context;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;

import com.visionvera.bmob.activity.plan.VideoConfig;
import com.visionvera.bmob.utils.ToastUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * Created by Qiao on 2017/3/10.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    public static final int CAMERA_FACING_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    public static final int CAMERA_FACING_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private Camera mCamera;
    private SurfaceHolder mSurfaceHolder;
    private CameraListener mCameraListener;
    private VideoConfig mVideoConfig;

    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setKeepScreenOn(true);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    public Camera open() {
        if (mVideoConfig == null) {
            ToastUtil.showToast("Null VideoConfig! ");
            return null;
        }
        if (mCamera != null) {
            try {
                mCamera.reconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        release();
        try {
            mCamera = Camera.open(mVideoConfig.source);
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.setDisplayOrientation(mVideoConfig.orientation);
        } catch (RuntimeException | IOException e) {
            e.printStackTrace();
            ToastUtil.showToast("相机权限被拒绝");
        }
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewFormat(ImageFormat.YV12);
            parameters.setPreviewSize(mVideoConfig.width, mVideoConfig.height);
            List<Integer> supportedFPS = parameters.getSupportedPreviewFrameRates();
            Collections.sort(supportedFPS);
            parameters.setPreviewFrameRate(supportedFPS.get(supportedFPS.size() - 1));
            if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(parameters);
            final VideoConfig videoConfig = mVideoConfig;
            Camera.Size previewSize = parameters.getPreviewSize();
            byte[] buffer = new byte[previewSize.height * previewSize.width * 3 / 2];
            mCamera.addCallbackBuffer(buffer);
            mCamera.setPreviewCallbackWithBuffer((data, camera) -> {
                if (mCameraListener != null) {
                    mCameraListener.onYUVCallback(data, videoConfig);
                }
                camera.addCallbackBuffer(buffer);
            });
            mCamera.startPreview();
            if (mCameraListener != null) {
                mCameraListener.onCameraOpen(videoConfig);
            }
            resizeView();
        } catch (RuntimeException e) {
            e.printStackTrace();
            ToastUtil.showToast("相机参数错误");
        }
        return mCamera;
    }

    private void resizeView() {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        params.setMargins(0, 0, 0, 0);
        params.width = FrameLayout.LayoutParams.MATCH_PARENT;
        params.height = FrameLayout.LayoutParams.MATCH_PARENT;
        setLayoutParams(params);
        int width, height;
        FrameLayout parent = (FrameLayout) getParent();
        width = parent.getWidth();
        height = parent.getHeight();
        final VideoConfig videoConfig = mVideoConfig;
        if (videoConfig.orientation == VideoConfig.ORIENTATION_90 || videoConfig.orientation == VideoConfig.ORIENTATION_180) {
            if (height * 1.0f / width < videoConfig.width * 1.0f / videoConfig.height) {
                params.leftMargin = params.rightMargin = (int) ((width - height * videoConfig.height * 1.0f / videoConfig.width) / 2);
            } else {
                params.topMargin = params.bottomMargin = (int) ((height - width * videoConfig.width * 1.0f / videoConfig.height) / 2);
            }
        } else {
            if (height * 1.0f / width < videoConfig.height * 1.0f / videoConfig.width) {
                params.leftMargin = params.rightMargin = (int) ((width - height * videoConfig.width * 1.0f / videoConfig.height) / 2);
            } else {
                params.topMargin = params.bottomMargin = (int) ((height - width * videoConfig.height * 1.0f / videoConfig.width) / 2);
            }
        }
        setLayoutParams(params);
    }

    public void release() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public final void surfaceCreated(SurfaceHolder holder) {
        open();
    }

    @Override
    public final void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public final void surfaceDestroyed(SurfaceHolder holder) {
        release();
    }

    public VideoConfig getVideoConfig() {
        return mVideoConfig;
    }

    public void setConfig(VideoConfig config) {
        mVideoConfig = config;
        open();
    }

    public interface CameraListener {
        void onCameraOpen(VideoConfig videoConfig);

        void onYUVCallback(byte[] yuv, VideoConfig videoConfig);
    }

    public void setCameraListener(CameraListener cameraListener) {
        mCameraListener = cameraListener;
    }

    public void lockAutoExposure(boolean lock) {
        if (mCamera != null) {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setAutoExposureLock(lock);
            parameters.setAutoWhiteBalanceLock(lock);
            if (lock) {
                if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_AUTO))
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
            } else {
                if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO))
                    parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(parameters);
        }
    }
}
