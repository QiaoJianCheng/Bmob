package com.qiao.bmob.jni;

/**
 * Created by Qiao on 2017/8/17.
 */

public class HelloJni {
    static {
        System.loadLibrary("HelloJni");
    }

    public native int add(int a, int b);

    public native int minus(int a, int b);

    public native int[] grayBitmap(int[] in, int w, int h);
}