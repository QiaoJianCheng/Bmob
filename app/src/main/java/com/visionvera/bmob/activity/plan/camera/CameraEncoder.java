package com.visionvera.bmob.activity.plan.camera;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.visionvera.bmob.activity.plan.VideoConfig;
import com.visionvera.bmob.utils.LogUtil;
import com.visionvera.bmob.utils.TextUtil;
import com.visionvera.bmob.utils.ToastUtil;

import java.io.File;
import java.nio.ByteBuffer;

/**
 * Created by Qiao on 2017/7/26.
 */

public class CameraEncoder {
    private static final String TAG = "CameraEncoder";
    private static final String MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC;
    private final VideoConfig mVideoConfig;

    private MediaCodec mEncoder;
    private boolean mIsRecording;
    private MediaMuxer mMediaMuxer;
    private int mTrackIndex = -1;
    private volatile byte[] mYuvData;
    private volatile long mStartTime;

    public CameraEncoder(VideoConfig videoConfig) {
        mVideoConfig = videoConfig;
        init();
    }

    private void init() {
        if (!avcCodecSupported()) {
            ToastUtil.showToast("不支持硬件编码");
            return;
        }
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mVideoConfig.width, mVideoConfig.height);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
        format.setInteger(MediaFormat.KEY_BIT_RATE, mVideoConfig.bitrate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, mVideoConfig.fps);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        try {
            mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
            mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mEncoder.setCallback(new MediaCodec.Callback() {
                @Override
                public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                    if (index >= 0) {
                        ByteBuffer inputBuffer = codec.getInputBuffer(index);
                        if (inputBuffer != null) {
//                            byte[] i420 = convertYV12ToI420(mYuvData, mVideoConfig.width, mVideoConfig.height);
                            inputBuffer.clear();
                            inputBuffer.put(mYuvData);
                            codec.queueInputBuffer(index, 0, mYuvData.length, System.nanoTime() / 1000 - mStartTime, 0);
                        }
                    }
                }

                @Override
                public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                    if (index >= 0) {
                        ByteBuffer encodedData = codec.getOutputBuffer(index);
                        if (encodedData != null && info.size > 0) {
                            mMediaMuxer.writeSampleData(mTrackIndex, encodedData, info);
                        }
                        codec.releaseOutputBuffer(index, false);
                    }
                }

                @Override
                public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
                    LogUtil.e(e);
                }

                @Override
                public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
                    mTrackIndex = mMediaMuxer.addTrack(format);
                    mMediaMuxer.setOrientationHint(90);
                    mMediaMuxer.start();
                }
            });
            mMediaMuxer = new MediaMuxer(new File(Environment.getExternalStorageDirectory(), "camera_encoder.mp4").getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void start() {
        if (mIsRecording) return;
        mStartTime = System.nanoTime() / 1000;
        mIsRecording = true;
        mEncoder.start();
    }

    public void release() {
        mIsRecording = false;
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mMediaMuxer != null) {
            try {
                if (mTrackIndex != -1) {
                    mMediaMuxer.stop();
                    mMediaMuxer.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
            mMediaMuxer = null;
        }
    }

    public void encode(byte[] yuv) {
        mYuvData = yuv;
    }

    private boolean avcCodecSupported() {
        MediaCodecInfo[] codecInfos = new MediaCodecList(MediaCodecList.ALL_CODECS).getCodecInfos();
        for (MediaCodecInfo codecInfo : codecInfos) {
            if (!codecInfo.isEncoder()) {
                continue;
            }
            String[] types = codecInfo.getSupportedTypes();
            for (String type : types) {
                if (TextUtil.equalsIgnoreCase(type, MIME_TYPE)) {
                    return true;
                }
            }
        }
        return false;
    }

    //yv12 -> yuv420p / yvu -> mYuvData
    private byte[] convertYV12ToI420(byte[] yv12bytes, int width, int height) {
        byte[] i420bytes = new byte[yv12bytes.length];
        System.arraycopy(yv12bytes, 0, i420bytes, 0, width * height);
        System.arraycopy(yv12bytes, width * height + width * height / 4, i420bytes, width * height, width * height / 4);
        System.arraycopy(yv12bytes, width * height, i420bytes, width * height + width * height / 4, width * height / 4);
        return i420bytes;
    }

    public boolean isRecording() {
        return mIsRecording;
    }
}
