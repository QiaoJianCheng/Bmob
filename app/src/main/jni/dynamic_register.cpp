//
// Created by Qiao on 2017/8/17.
//
#include <jni.h>
#include <stdio.h>
#include <stdlib.h>

jint minus(JNIEnv *env, jclass clazz, jint a, jint b) {
    return a - b;
}

jintArray grayBitmap(JNIEnv *env, jclass clazz, jintArray in, int w, int h) {
    int *buffer = env->GetIntArrayElements(in, JNI_FALSE);
    if (buffer == NULL) {
        return 0;
    }
    int alpha = 0xFF000000;
    for (int i = 0; i < h; i++) {
        for (int j = 0; j < w; j++) {
            // 获得像素的颜色
            int color = buffer[w * i + j];
            int red = ((color & 0x00FF0000) >> 16);
            int green = ((color & 0x0000FF00) >> 8);
            int blue = color & 0x000000FF;
            color = (red + green + blue) / 3;
            color = alpha | (color << 16) | (color << 8) | color;
            buffer[w * i + j] = color;
        }
    }
    int size = w * h;
    jintArray result = env->NewIntArray(size);
    env->SetIntArrayRegion(result, 0, size, buffer);
    env->ReleaseIntArrayElements(in, buffer, 0);
    return result;
}

static JNINativeMethod gMethods[] = {{"minus",      "(II)I",    (void *) minus},
                                     {"grayBitmap", "([III)[I", (void *) grayBitmap}};

JNIEXPORT jint

JNICALL JNI_OnLoad(JavaVM *jvm, void *reserved) {
    JNIEnv *env = NULL;
    jint result = JNI_FALSE;

    if (jvm->GetEnv((void **) &env, JNI_VERSION_1_6) != JNI_OK) {
        return result;
    }
    if (env == NULL) {
        return result;
    }

    jclass clazz = env->FindClass("com/visionvera/bmob/jni/HelloJni");
    if (clazz == NULL) {
        return result;
    }

    if (env->RegisterNatives(clazz, gMethods, sizeof(gMethods) / sizeof(gMethods[0])) < 0) {
        return result;
    }

    result = JNI_VERSION_1_6;
    return result;
}