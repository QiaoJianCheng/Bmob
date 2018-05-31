package com.qiao.bmob.activity.plan.camera;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaCodec;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.qiao.bmob.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * Created by Qiao on 2017/9/4.
 */

public class AudioEncoder {
    private static final String MIME_TYPE = MediaFormat.MIMETYPE_AUDIO_AAC;
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.MIC;
    private static final int SAMPLE_RATE = 44100;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;
    private int mAudioSessionId;
    private MediaCodec mEncoder;
    private MediaMuxer mMediaMuxer;
    private boolean mIsRecording;
    private long mStartTime;
    private int mTrackIndex;
    private AudioRecord mAudioRecord;

    public AudioEncoder() {
        init();
    }

    private void init() {
        int minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        mAudioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, minBufferSize);
        mAudioRecord.startRecording();
        mAudioSessionId = mAudioRecord.getAudioSessionId();

        MediaFormat audioFormat = MediaFormat.createAudioFormat(MediaFormat.MIMETYPE_AUDIO_AAC, SAMPLE_RATE, 1);
        audioFormat.setInteger(MediaFormat.KEY_BIT_RATE, 128000);
        audioFormat.setInteger(MediaFormat.KEY_AUDIO_SESSION_ID, mAudioSessionId);
//        audioFormat.setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC);
        try {
            mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
            mEncoder.configure(audioFormat, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mEncoder.setCallback(new MediaCodec.Callback() {

                @Override
                public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {
                    if (index >= 0) {
                        long current = System.nanoTime() / 1000;
                        mStartTime = mStartTime == 0 ? current : mStartTime;
                        ByteBuffer inputBuffer = codec.getInputBuffer(index);
                        if (inputBuffer != null) {
                            mAudioRecord.read(inputBuffer, minBufferSize);
                            codec.queueInputBuffer(index, 0, inputBuffer.limit(), current - mStartTime, 0);
                        }
                    }
                }

                @Override
                public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                    if (index >= 0) {
                        ByteBuffer outputBuffer = codec.getOutputBuffer(index);
                        if (outputBuffer != null) {
                            mMediaMuxer.writeSampleData(index, outputBuffer, info);
                        }
                    }
                }

                @Override
                public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
                    LogUtil.e(e);
                }

                @Override
                public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
                    mTrackIndex = mMediaMuxer.addTrack(format);
                    mMediaMuxer.start();
                }
            });
            mEncoder.start();
            File file = new File(Environment.getExternalStorageDirectory(), "audio_encoder.mp3");
            mMediaMuxer = new MediaMuxer(file.getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mAudioRecord != null) {
            mAudioRecord.stop();
            mAudioRecord.release();
            mAudioRecord = null;
        }
        if (mMediaMuxer != null) {
            mMediaMuxer.stop();
            mMediaMuxer.release();
            mMediaMuxer = null;
        }
    }

    private class EncodeThread implements Runnable {
        @Override
        public void run() {
            while (mIsRecording) {

            }
        }
    }
}
