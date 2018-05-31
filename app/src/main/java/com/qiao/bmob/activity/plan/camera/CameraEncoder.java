package com.qiao.bmob.activity.plan.camera;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;

import com.qiao.bmob.activity.plan.VideoConfig;
import com.qiao.bmob.utils.LogUtil;
import com.qiao.bmob.utils.TextUtil;
import com.qiao.bmob.utils.ToastUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.ByteBuffer;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by Qiao on 2017/7/26.
 */

public class CameraEncoder {
    public static final int STATE_STOPPED = 0;
    public static final int STATE_RECORDING = 1;
    public static final int STATE_END = 2;

    @IntDef({STATE_STOPPED, STATE_RECORDING, STATE_END})
    @Retention(RetentionPolicy.SOURCE)
    @interface STATE {
    }

    @STATE
    private int mState;

    private static final String AUDIO_MIME_TYPE = MediaFormat.MIMETYPE_AUDIO_AAC;
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private static final int BIT_RATE = 128 * 1024; // bps
    private static final String FILE_PATH = "camera_encoder.mp4";
    private MediaCodec mAudioEncoder;
    private int mAudioTrackIndex = -1;
    private AudioRecord mAudioRecord;
    private int mBufferSize;


    private static final String TAG = "CameraEncoder";
    private static final String MIME_TYPE_VIDEO = MediaFormat.MIMETYPE_VIDEO_AVC;
    private final VideoConfig mVideoConfig;

    private MediaCodec mVideoEncoder;
    private MediaMuxer mMediaMuxer;
    private int mVideoTrackIndex = -1;
    private volatile byte[] mYuvData;
    private final LinkedBlockingQueue<byte[]> mPCMData = new LinkedBlockingQueue<>();
    private int mTrackCount;
    private Thread mAudioThread;
    private Thread mVideoThread;

    public CameraEncoder(VideoConfig videoConfig) {
        mVideoConfig = videoConfig;
        init();
    }

    private void init() {
        if (!avcCodecSupported()) {
            ToastUtil.showToast("不支持硬件编码");
            return;
        }
        File file = new File(Environment.getExternalStorageDirectory(), FILE_PATH);
        file.delete();
        try {
            MediaFormat videoFormat = MediaFormat.createVideoFormat(MIME_TYPE_VIDEO, mVideoConfig.width, mVideoConfig.height);
            videoFormat.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible);
            videoFormat.setInteger(MediaFormat.KEY_BIT_RATE, mVideoConfig.bitrate);
            videoFormat.setInteger(MediaFormat.KEY_FRAME_RATE, mVideoConfig.fps);
            videoFormat.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
            mVideoEncoder = MediaCodec.createEncoderByType(MIME_TYPE_VIDEO);
            mVideoEncoder.configure(videoFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            mBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT) * 2;
            mAudioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, mBufferSize);
            mAudioRecord.startRecording();
            MediaFormat audioFormat = MediaFormat.createAudioFormat(AUDIO_MIME_TYPE, SAMPLE_RATE, 1);
            audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, BIT_RATE);
            audioFormat.setInteger(MediaFormat.KEY_AUDIO_SESSION_ID, mAudioRecord.getAudioSessionId());
            audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
            audioFormat.setInteger(MediaFormat.KEY_CHANNEL_MASK, AUDIO_FORMAT);
            audioFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, mBufferSize);
            mAudioEncoder = MediaCodec.createEncoderByType(AUDIO_MIME_TYPE);
            mAudioEncoder.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);

            mState = STATE_RECORDING;
            mVideoEncoder.start();
            mAudioEncoder.start();
            mMediaMuxer = new MediaMuxer(file.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
            mAudioThread = new Thread(new EncodeThread(), "EncodeThread");
            mAudioThread.start();
            mVideoThread = new Thread(new EncodeThread2(), "EncodeThread2");
            mVideoThread.start();
            new Thread(new ReadThread(), "ReadThread").start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class ReadThread implements Runnable {
        @Override
        public void run() {
//            数据量Byte=采样频率Hz ×（采样位数/8）× 声道数 × 时间s
            while (mState == STATE_RECORDING) {
                byte[] buffer = new byte[mBufferSize];
                int read = mAudioRecord.read(buffer, 0, mBufferSize);
                long duration = 1_000_000L * read / SAMPLE_RATE / 2;
                ByteArrayOutputStream bytes = new ByteArrayOutputStream(read + 8);
                DataOutputStream data = new DataOutputStream(bytes);
                try {
                    data.writeLong(duration);
                    data.write(buffer, 0, read);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mPCMData.offer(bytes.toByteArray());
            }
        }
    }

    private class EncodeThread implements Runnable {
        private byte[] buffer;
        private volatile long mTimestamp;

        @Override
        public void run() {
            if (true)
                throw new RuntimeException("HEHEHE");
            ENCODER:
            while (true) {
                if (buffer == null) {
                    buffer = mPCMData.poll();
                }
                if (mMediaMuxer == null) break;
                if (buffer != null && mState != STATE_STOPPED) {
                    int index = mAudioEncoder.dequeueInputBuffer(-1);
                    if (index >= 0) {
                        ByteBuffer inputBuffer = mAudioEncoder.getInputBuffer(index);
                        if (inputBuffer != null) {
                            if (mState == STATE_RECORDING) {
                                DataInputStream in = new DataInputStream(new ByteArrayInputStream(buffer));
                                try {
                                    long duration = in.readLong();
                                    byte[] bytes = new byte[in.available()];
                                    in.readFully(bytes);
                                    inputBuffer.clear();
                                    inputBuffer.position(0);
                                    inputBuffer.put(bytes);
                                    mAudioEncoder.queueInputBuffer(index, 0, bytes.length, mTimestamp, 0);
                                    mTimestamp += duration;
                                    buffer = null;
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            } else if (mState == STATE_END) {
                                mAudioEncoder.queueInputBuffer(index, 0, 0, mTimestamp, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                mState = STATE_STOPPED;
                            }
                        }
                    }
                }
                MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                int index2 = mAudioEncoder.dequeueOutputBuffer(info, 0);
                if (index2 == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    mAudioTrackIndex = mMediaMuxer.addTrack(mAudioEncoder.getOutputFormat());
                    mTrackCount++;
                    if (mTrackCount == 2) {
                        mMediaMuxer.start();
                        mTrackCount++;
                        LogUtil.e("mMediaMuxer.start");
                    }
                }
                while (index2 >= 0) {
                    if (info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                        break ENCODER;
                    }
                    ByteBuffer outputBuffer = mAudioEncoder.getOutputBuffer(index2);
                    if (outputBuffer != null && info.size > 0) {
                        if (mTrackCount >= 3) {
                            outputBuffer.position(info.offset);
                            outputBuffer.limit(info.offset + info.size);
                            byte[] dst = new byte[info.size];
                            outputBuffer.get(dst);
                            LogUtil.e("writeSampleData=" + info.presentationTimeUs);
                            mMediaMuxer.writeSampleData(mAudioTrackIndex, outputBuffer, info);
                        }
                    }
                    mAudioEncoder.releaseOutputBuffer(index2, false);
                    index2 = mAudioEncoder.dequeueOutputBuffer(info, 0);
                }
            }
            LogUtil.e("stop");
            if (mAudioEncoder != null) {
                mAudioEncoder.stop();
                mAudioEncoder.release();
                mAudioEncoder = null;
            }
            if (mAudioRecord != null) {
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
            }
        }
    }

    private class EncodeThread2 implements Runnable {
        private long mStartTime;
        private long mLastTime;

        @Override
        public void run() {
            ENCODER:
            while (true) {
                if (mYuvData == null) continue;
                if (mMediaMuxer == null) break;
                if (mState != STATE_STOPPED) {
                    long current = System.nanoTime() / 1000;
                    if (current - mLastTime < 1_000_000L / mVideoConfig.fps) {
                        continue;
                    }
                    if (mStartTime == 0) {
                        mStartTime = current;
                    }
                    mLastTime = current;
                    int index = mVideoEncoder.dequeueInputBuffer(-1);
                    if (index >= 0) {
                        ByteBuffer inputBuffer = mVideoEncoder.getInputBuffer(index);
                        if (inputBuffer != null) {
                            if (mState == STATE_RECORDING) {
                                byte[] i420 = convertYV12ToI420(mYuvData, mVideoConfig.width, mVideoConfig.height);
                                inputBuffer.clear();
                                inputBuffer.put(i420);
                                mVideoEncoder.queueInputBuffer(index, 0, i420.length, current - mStartTime, 0);
                            } else if (mState == STATE_END) {
                                mVideoEncoder.queueInputBuffer(index, 0, 0, current - mStartTime, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                                mState = STATE_STOPPED;
                            }
                        }
                    }
                }
                MediaCodec.BufferInfo info = new MediaCodec.BufferInfo();
                int index2 = mVideoEncoder.dequeueOutputBuffer(info, 0);
                if (index2 == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    mVideoTrackIndex = mMediaMuxer.addTrack(mVideoEncoder.getOutputFormat());
                    mMediaMuxer.setOrientationHint(90);
                    mTrackCount++;
                    if (mTrackCount == 2) {
                        mMediaMuxer.start();
                        LogUtil.e("mMediaMuxer.start");
                        mTrackCount++;
                    }
                }
                while (index2 >= 0) {
                    if (info.flags == MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                        LogUtil.e(TAG, "FLAG_CONFIG");
                    } else if (info.flags == MediaCodec.BUFFER_FLAG_KEY_FRAME) {
                        LogUtil.e(TAG, "FLAG_KEY");
                    } else if (info.flags == MediaCodec.BUFFER_FLAG_END_OF_STREAM) {
                        LogUtil.e(TAG, "FLAG_END");
                        break ENCODER;
                    }
                    ByteBuffer outputBuffer = mVideoEncoder.getOutputBuffer(index2);
                    if (outputBuffer != null) {
                        outputBuffer.position(0);
                        if (mTrackCount >= 3) {
                            LogUtil.e(TAG, "FLAG_NORMAL");
                            mMediaMuxer.writeSampleData(mVideoTrackIndex, outputBuffer, info);
                        }
                    }
                    mVideoEncoder.releaseOutputBuffer(index2, false);
                    index2 = mVideoEncoder.dequeueOutputBuffer(info, 0);
                }
            }
            LogUtil.e(TAG, "stop2");
            if (mVideoEncoder != null) {
                mVideoEncoder.stop();
                mVideoEncoder.release();
                mVideoEncoder = null;
            }
            if (mMediaMuxer != null) {
                mMediaMuxer.stop();
                mMediaMuxer.release();
                mMediaMuxer = null;
            }
        }
    }

    public void start() {
    }

    public void release() {
        mState = STATE_END;
        mVideoThread.interrupt();
        mAudioThread.interrupt();
    }

    private long startTimestamp;
    private long lastTimestamp;

    private long timestamp() {
        long current = System.nanoTime() / 1000;
        if (startTimestamp == 0) {
            startTimestamp = current;
        }
        long timestamp = current - startTimestamp;
        if (timestamp < lastTimestamp) {
            return timestamp();
        }
        lastTimestamp = timestamp;
        return timestamp;
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
                if (TextUtil.equalsIgnoreCase(type, MIME_TYPE_VIDEO)) {
                    return true;
                }
            }
        }
        return false;
    }

    //yv12 -> yuv420p / yvu -> mYuvData
    private byte[] convertYV12ToI420(@NonNull byte[] yv12bytes, int width, int height) {
        byte[] i420bytes = new byte[yv12bytes.length];
        System.arraycopy(yv12bytes, 0, i420bytes, 0, width * height);
        System.arraycopy(yv12bytes, width * height + width * height / 4, i420bytes, width * height, width * height / 4);
        System.arraycopy(yv12bytes, width * height, i420bytes, width * height + width * height / 4, width * height / 4);
        return i420bytes;
    }

    public boolean isRecording() {
        return mState != STATE_STOPPED;
    }
}
