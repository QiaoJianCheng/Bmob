package com.visionvera.bmob.activity.plan.camera.opengl;

import android.graphics.Bitmap;

//视频显示帧
public class VideoDisplayFrame {
    public Bitmap BMP;
    public long TimeTick;
    public byte[] YUV;
    public int YUVLength;
    public int YUVOffset;
    public int Width;
    public int Height;

    public VideoDisplayFrame(Bitmap bmp, long timeTick) {
        BMP = bmp;
        TimeTick = timeTick;
        YUV = null;
    }

    public VideoDisplayFrame(byte[] yuv, int offset, int length, int width, int height, long timeTick) {
        BMP = null;
        TimeTick = timeTick;
        YUV = yuv;
        Width = width;
        Height = height;
        YUVOffset = offset;
        YUVLength = length;
    }
}
