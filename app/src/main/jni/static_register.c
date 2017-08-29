//
// Created by Qiao on 2017/8/17.
//
#include <stdio.h>
#include <stdlib.h>
#include "static_register.h"

JNIEXPORT jint

JNICALL Java_com_visionvera_bmob_jni_HelloJni_add
        (JNIEnv *env, jobject obj, jint a, jint b) {
    return a + b;
}