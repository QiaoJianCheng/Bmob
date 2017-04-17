package com.visionvera.bmob.activity;

import android.os.Bundle;
import android.widget.ImageView;

import com.visionvera.bmob.R;
import com.visionvera.bmob.base.BaseActivity;
import com.visionvera.bmob.global.App;
import com.visionvera.bmob.utils.IntentUtil;

public class SplashActivity extends BaseActivity {
    private ImageView splash_image;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_splash);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        splash_image = (ImageView) findViewById(R.id.splash_image);

        App.getInstance().postDelay(new Runnable() {
            @Override
            public void run() {
                IntentUtil.toLoginActivity(SplashActivity.this, splash_image);
                App.getInstance().postDelay(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            }
        }, 2000);
//        splash_image.animate()
//                .translationY(ResUtil.getDimen(R.dimen.x300) - DensityUtil.getScreenHeight() / 2 + DensityUtil.getStatusBarHeight())
//                .scaleX(2.0f / 3)
//                .scaleY(2.0f / 3)
//                .setDuration(1000)
//                .setStartDelay(2000)
//                .setListener(new AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//                    }
//                })
//                .start();
    }
}
