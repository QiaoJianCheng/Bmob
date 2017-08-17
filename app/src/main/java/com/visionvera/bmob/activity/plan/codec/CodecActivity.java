package com.visionvera.bmob.activity.plan.codec;

import android.os.Bundle;

import com.visionvera.bmob.R;
import com.visionvera.bmob.base.BaseActivity;

public class CodecActivity extends BaseActivity {

    @Override
    protected void setContentView() {
        setStatusBarColor(R.color.colorThemeRed, false);
        setContentView(R.layout.activity_codec);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        try {
            new CameraToMpegTest().testEncodeCameraToMp4();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }
}
