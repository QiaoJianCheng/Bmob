package com.visionvera.bmob.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.visionvera.bmob.R;
import com.visionvera.bmob.base.BaseActivity;
import com.visionvera.bmob.global.App;
import com.visionvera.bmob.utils.IntentUtil;
import com.visionvera.bmob.utils.LogUtil;

public class SplashActivity extends BaseActivity {
    private ImageView splash_image;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        splash_image = (ImageView) findViewById(R.id.splash_image);

        App.postDelay(new Runnable() {
            @Override
            public void run() {
                IntentUtil.toLoginActivity(SplashActivity.this, splash_image);
                finish();
            }
        }, 2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtil.d(TAG, "onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.d(TAG, "onResume");
    }
}
