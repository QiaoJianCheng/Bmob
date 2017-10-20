package com.visionvera.bmob.activity.plan.codec;

import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaCodecList;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.media.projection.MediaProjection;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import com.visionvera.bmob.activity.plan.VideoConfig;
import com.visionvera.bmob.global.App;
import com.visionvera.bmob.utils.DensityUtil;
import com.visionvera.bmob.utils.LogUtil;
import com.visionvera.bmob.utils.TextUtil;
import com.visionvera.bmob.utils.ToastUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.ByteBuffer;

/**
 * Created by Qiao on 2017/7/26.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ScreenEncoder {
    private static final String TAG = "ScreenEncoder";
    private static final String MIME_TYPE = MediaFormat.MIMETYPE_VIDEO_AVC;
    private final VideoConfig mVideoConfig;

    private MediaCodec mEncoder;
    private final MediaCodec.BufferInfo mBufferInfo = new MediaCodec.BufferInfo();
    private static final int TIMEOUT_US = 10000;
    private VirtualDisplay mVirtualDisplay;
    private MediaProjection mMediaProjection;
    private boolean mIsRecording;
    private MediaMuxer mMediaMuxer;
    private long mLastTime;

    public ScreenEncoder(MediaProjection mediaProjection, VideoConfig videoConfig) {
        mMediaProjection = mediaProjection;
        mVideoConfig = videoConfig;
        init();
    }

    private void init() {
        if (!avcCodecSupported()) {
            ToastUtil.showToast("不支持硬件编码");
            return;
        }
        MediaFormat format = MediaFormat.createVideoFormat(MIME_TYPE, mVideoConfig.width, mVideoConfig.height);
        format.setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatSurface);
        format.setInteger(MediaFormat.KEY_BIT_RATE, mVideoConfig.bitrate);
        format.setInteger(MediaFormat.KEY_FRAME_RATE, mVideoConfig.fps);
        format.setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1);
        try {
            mEncoder = MediaCodec.createEncoderByType(MIME_TYPE);
            mEncoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE);
            mEncoder.setCallback(new MediaCodec.Callback() {
                @Override
                public void onInputBufferAvailable(@NonNull MediaCodec codec, int index) {

                }

                @Override
                public void onOutputBufferAvailable(@NonNull MediaCodec codec, int index, @NonNull MediaCodec.BufferInfo info) {
                    if (mIsRecording && mMediaMuxer != null) {
                        if (index >= 0 && info.flags != MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                            long current = System.nanoTime() / 1000;
                            ByteBuffer encodedData = codec.getOutputBuffer(index);
                            if (encodedData != null && info.size > 0) {
                                boolean isKeyframe = (encodedData.get(4) & 0x0F) == 5;
//                                if (isKeyframe /*|| current - mLastTime > 1000000 / mVideoConfig.fps*/) {
                                mMediaMuxer.writeSampleData(mTrackIndex, encodedData, info);
                                mLastTime = current;
//                                }
                            }
                            codec.releaseOutputBuffer(index, false);
                        }
                    }
                }

                @Override
                public void onError(@NonNull MediaCodec codec, @NonNull MediaCodec.CodecException e) {
                    LogUtil.e(e);
                }

                @Override
                public void onOutputFormatChanged(@NonNull MediaCodec codec, @NonNull MediaFormat format) {
                    if (mIsRecording && mMediaMuxer != null) {
                        mTrackIndex = mMediaMuxer.addTrack(format);
                        mMediaMuxer.start();
                    }
                }
            });
            mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen_encoder", mVideoConfig.width, mVideoConfig.height, DensityUtil.getDensity(), DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, mEncoder.createInputSurface(), null, null);
            mMediaMuxer = new MediaMuxer(new File(Environment.getExternalStorageDirectory(), "screen_encoder.mp4").getAbsolutePath(), MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void start() {
        if (mIsRecording) return;
        mIsRecording = true;
        mEncoder.start();
//        new Thread(new EncodeThread(), "EncodeThread").start();
    }

    public void release() {
        if (!mIsRecording) return;
        mIsRecording = false;
        if (mEncoder != null) {
            mEncoder.stop();
            mEncoder.release();
            mEncoder = null;
        }
        if (mMediaMuxer != null) {
            mMediaMuxer.stop();
            mMediaMuxer.release();
            mMediaMuxer = null;
        }
        if (mVirtualDisplay != null) {
            mVirtualDisplay.release();
            mVirtualDisplay = null;
        }
    }

    public void getThumbnail() {
        if (mMediaProjection == null) return;
        ImageReader imageReader = ImageReader.newInstance(mVideoConfig.width, mVideoConfig.height, PixelFormat.RGBA_8888, 1);
        VirtualDisplay display = mMediaProjection.createVirtualDisplay("screen_record", mVideoConfig.width, mVideoConfig.height, 1, DisplayManager.VIRTUAL_DISPLAY_FLAG_PUBLIC, imageReader.getSurface(), null, null);
        App.postDelay(() -> {
            Image image = imageReader.acquireLatestImage();
            if (image != null) {
                Image.Plane[] planes = image.getPlanes();
                if (planes != null) {
                    int width = image.getWidth();
                    int height = image.getHeight();
                    Image.Plane plane = image.getPlanes()[0];
                    ByteBuffer buffer = plane.getBuffer();
                    int rowStride = plane.getRowStride();
                    int pixelStride = plane.getPixelStride();
                    int rowPadding = rowStride - pixelStride * width;
                    Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
                    bitmap.copyPixelsFromBuffer(buffer);
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, (int) (100 * bitmap.getHeight() * 1.0f / bitmap.getWidth()), false);
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
//                    if (callback != null) {
//                        callback.onThumbnailCallback(out.toByteArray());
//                    }
                    bitmap.recycle();
                    scaledBitmap.recycle();
                    image.close();
                    display.release();
                }
            }
        }, 500);
    }

    private int mTrackIndex;

    private class EncodeThread implements Runnable {

        @Override
        public void run() {
            try {
                while (mIsRecording && mEncoder != null && mMediaMuxer != null) {
                    int index = mEncoder.dequeueOutputBuffer(mBufferInfo, TIMEOUT_US);
                    if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                        mTrackIndex = mMediaMuxer.addTrack(mEncoder.getOutputFormat());
                        mMediaMuxer.start();
                    } else if (index == MediaCodec.INFO_TRY_AGAIN_LATER) {
                        Thread.sleep(50);
                    } else if (index >= 0 && mBufferInfo.flags != MediaCodec.BUFFER_FLAG_CODEC_CONFIG) {
                        ByteBuffer encodedData = mEncoder.getOutputBuffer(index);
                        if (encodedData != null && mBufferInfo.size > 0) {
                            mMediaMuxer.writeSampleData(mTrackIndex, encodedData, mBufferInfo);
                        }
                        mEncoder.releaseOutputBuffer(index, false);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                LogUtil.e(TAG, e);
            }
        }
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
}
