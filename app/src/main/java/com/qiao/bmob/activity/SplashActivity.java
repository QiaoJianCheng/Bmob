package com.qiao.bmob.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.qiao.bmob.R;
import com.qiao.bmob.base.BaseActivity;
import com.qiao.bmob.global.App;
import com.qiao.bmob.utils.IntentUtil;

public class SplashActivity extends BaseActivity {
    private ImageView splash_image;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        splash_image = findViewById(R.id.splash_image);

        App.postDelay(new Runnable() {
            @Override
            public void run() {
                IntentUtil.toLoginActivity(SplashActivity.this, splash_image);
                finish();
            }
        }, 2000);
    }
}
