package com.visionvera.bmob.activity.plan.camera;

import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Environment;

import com.visionvera.bmob.utils.ToastUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by Qiao on 2017/3/10.
 */

public class VideoRecorder implements MediaRecorder.OnInfoListener, MediaRecorder.OnErrorListener {
    private MediaRecorder mMediaRecorder;
    private boolean mIsRecording;
    private int mCameraId;
    private Camera mCamera;
    private File mFile = new File(Environment.getExternalStorageDirectory(), "aaaaaaaaaaaaaaaaa.mp4");
    private int mOrientation = 90;
    private LSS mLSS;
    private Thread mReceiveThread;

    public VideoRecorder(Camera camera, int cameraId) {
        mCameraId = cameraId;
        mCamera = camera;
    }

    public void startRecord() {
        new Thread() {
            @Override
            public void run() {
                if (mFile.exists()) mFile.delete();
                try {
                    mLSS = new LSS("VideoRecorder");
                } catch (IOException e) {
                    e.printStackTrace();
                    ToastUtil.warnToast("init LSS error.");
                    return;
                }

                if (mCamera == null || mIsRecording) return;
                try {
                    mCamera.unlock();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
                if (mMediaRecorder == null) {
                    mMediaRecorder = new MediaRecorder();
                } else {
                    mMediaRecorder.reset();
                }
                mMediaRecorder.setCamera(mCamera);
                // 这两项需要放在setOutputFormat之前
//                mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);

                // Set output file format
                mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);

                // 这两项需要放在setOutputFormat之后
//                mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);

                mMediaRecorder.setVideoEncodingBitRate(500 * 1024); // 500 Kbps
//                mMediaRecorder.setAudioEncodingBitRate(128 * 1024); // 128 Kbps
                mMediaRecorder.setVideoFrameRate(30);
                mMediaRecorder.setVideoSize(1280, 720);
                mMediaRecorder.setOrientationHint(mCameraId == CameraView.CAMERA_FACING_BACK ? mOrientation : (mOrientation + 180) % 360);

                mMediaRecorder.setOutputFile(getFile());
//                mMediaRecorder.setOutputFile(mLSS.getFileDescriptor());
                mMediaRecorder.setOnInfoListener(VideoRecorder.this);
                mMediaRecorder.setOnErrorListener(VideoRecorder.this);
                try {
                    mMediaRecorder.prepare();
                    mMediaRecorder.start();
                    mIsRecording = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    release();
                }
                mReceiveThread = new Thread() {
                    @Override
                    public void run() {
                        try {
//                            receiveThread();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                mReceiveThread.start();
            }
        }.start();
    }

    public void stopRecord() {
        if (mMediaRecorder != null && mIsRecording) {
            try {
                mMediaRecorder.stop();
            } catch (RuntimeException e) {
                e.printStackTrace();
                ToastUtil.warnToast("recording time is too short !");
                if (mFile != null && mFile.exists()) {
                    mFile.delete();
                }
            }
            release();
        }
    }

    public void release() {
        if (mLSS != null) {
            mLSS.dispose();
        }
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mIsRecording = false;
        }
    }

    public boolean isRecording() {
        return mIsRecording;
    }

    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    @Override
    public void onInfo(MediaRecorder mr, int what, int extra) {

    }

    @Override
    public void onError(MediaRecorder mr, int what, int extra) {

    }

    public String getFile() {
        if (mFile.exists()) mFile.delete();
        return mFile.getAbsolutePath();
    }
}
