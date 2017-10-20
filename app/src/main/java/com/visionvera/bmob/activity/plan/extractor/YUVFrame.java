package com.visionvera.veraclient.codec.video;

import android.graphics.Bitmap;

public class YUVFrame {
    public Bitmap BMP;
    public long TimeTick;
    public byte[] YUV;
    public int YUVLength;
    public int YUVOffset;
    public int Width;
    public int Height;

    public YUVFrame(Bitmap bmp, long timeTick) {
        BMP = bmp;
        TimeTick = timeTick;
        YUV = null;
    }

    public YUVFrame(byte[] yuv, int offset, int length, int width, int height, long timeTick) {
        BMP = null;
        TimeTick = timeTick;
        YUV = yuv;
        Width = width;
        Height = height;
        YUVOffset = offset;
        YUVLength = length;
    }

    @Override
    public String toString() {
        return "YUVFrame{" +
                ", TimeTick=" + TimeTick +
                ", YUV=" + YUV.length +
                ", YUVLength=" + YUVLength +
                ", YUVOffset=" + YUVOffset +
                ", Width=" + Width +
                ", Height=" + Height +
                '}';
    }
}
