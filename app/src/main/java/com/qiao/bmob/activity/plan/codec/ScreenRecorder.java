package com.qiao.bmob.activity.plan.codec;

import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.MediaRecorder;
import android.media.projection.MediaProjection;
import android.os.Environment;

import com.qiao.bmob.activity.plan.VideoConfig;
import com.qiao.bmob.utils.DensityUtil;

import java.io.File;
import java.io.IOException;

/**
 * Created by Qiao on 2017/8/22.
 */

public class ScreenRecorder {
    private final MediaProjection mMediaProjection;
    private final VideoConfig mVideoConfig;
    private MediaRecorder mMediaRecorder;
    private VirtualDisplay mVirtualDisplay;

    public ScreenRecorder(MediaProjection mediaProjection, VideoConfig videoConfig) {
        mMediaProjection = mediaProjection;
        mVideoConfig = videoConfig;
        init();
    }

    private void init() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.SURFACE);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(new File(Environment.getExternalStorageDirectory(), "screen.mp4").getAbsolutePath());
        mMediaRecorder.setVideoSize(mVideoConfig.width, mVideoConfig.height);
        mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        mMediaRecorder.setVideoEncodingBitRate(mVideoConfig.bitrate); // 可选，默认200Kbps
        mMediaRecorder.setVideoFrameRate(mVideoConfig.fps); // 必须
        try {
            mMediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen_recorder", mVideoConfig.width, mVideoConfig.height, DensityUtil.getDensity(), DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, mMediaRecorder.getSurface(), null, null);
        mMediaRecorder.start();
    }

    public void stop() {
        mMediaRecorder.stop();
        mMediaRecorder.release();
        mVirtualDisplay.release();
    }
}
