package com.visionvera.bmob.activity.plan.camera;


import android.content.Context;
import android.hardware.Camera;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.OrientationEventListener;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.visionvera.bmob.utils.ToastUtil;

import java.io.IOException;

/**
 * Created by Qiao on 2017/3/10.
 */

public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    public static final int CAMERA_FACING_BACK = Camera.CameraInfo.CAMERA_FACING_BACK;
    public static final int CAMERA_FACING_FRONT = Camera.CameraInfo.CAMERA_FACING_FRONT;

    private Camera mCamera;
    private int mCameraID;
    private SurfaceHolder mSurfaceHolder;
    private OrientationEventListener mOrientationEventListener;
    private CameraListener mCameraListener;
    private int mCurrentOrientation;


    public CameraView(Context context) {
        this(context, null);
    }

    public CameraView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }

    public Camera open() {
        return open(CAMERA_FACING_BACK);
    }

    public Camera open(int cameraID) {
        if (mCamera != null) {
            try {
                mCamera.reconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        release();
        mCameraID = cameraID;
        mCamera = Camera.open(cameraID);
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mCamera.setDisplayOrientation(90);
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            parameters.setPreviewSize(1280, 720);
            if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            mCamera.setParameters(parameters);
            mCamera.setPreviewCallback(new Camera.PreviewCallback() {
                @Override
                public void onPreviewFrame(byte[] data, Camera camera) {
                    if (mCameraListener != null) {
                        mCameraListener.onPreviewFrame(data);
                    }
                }
            });
            mCamera.startPreview();
            if (mCameraListener != null) {
                mCameraListener.onCameraOpen(mCameraID, mCamera);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            ToastUtil.warnToast("不支持的分辨率");
        }
        return mCamera;
    }

    private void release() {
        if (mCameraListener != null) {
            mCameraListener.onCameraRelease();
        }
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        open();
        if (mOrientationEventListener == null) {
            mOrientationEventListener = new OrientationEventListener(getContext(), SensorManager.SENSOR_DELAY_NORMAL) {
                @Override
                public void onOrientationChanged(int orientation) {
                    orientation = (orientation + 45) / 90 * 90 % 360;
                    if (mCameraListener != null && mCurrentOrientation != orientation) {
                        mCurrentOrientation = orientation;
                        mCameraListener.onOrientationChanged(mCurrentOrientation);
                    }
                }
            };
        }
        if (mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.enable();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        release();
        if (mOrientationEventListener != null && mOrientationEventListener.canDetectOrientation()) {
            mOrientationEventListener.disable();
        }
    }

    public void switchCamera() {
        mCameraID = (mCameraID + 1) % 2;
        open(mCameraID);
    }

    public interface CameraListener {
        void onCameraOpen(int cameraId, Camera camera);

        void onCameraRelease();

        void onOrientationChanged(int orientation);

        void onPreviewFrame(byte[] data);
    }

    public void setCameraListener(CameraListener cameraListener) {
        mCameraListener = cameraListener;
    }
}
