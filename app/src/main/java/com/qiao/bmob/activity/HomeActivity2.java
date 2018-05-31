package com.qiao.bmob.activity;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiao.bmob.R;
import com.qiao.bmob.adapter.HomePagerAdapter;
import com.qiao.bmob.base.BaseActivity;
import com.qiao.bmob.utils.LogUtil;
import com.qiao.bmob.utils.ResUtil;

public class HomeActivity2 extends BaseActivity implements View.OnClickListener {

    private RelativeLayout home_tab_apps_rl;
    private ImageView home_tab_apps_0_iv;
    private ImageView home_tab_apps_1_iv;
    private TextView home_tab_apps_tv;
    private RelativeLayout home_tab_users_rl;
    private ImageView home_tab_users_0_iv;
    private ImageView home_tab_users_1_iv;
    private TextView home_tab_users_tv;
    private RelativeLayout home_tab_plan_rl;
    private ImageView home_tab_plan_0_iv;
    private ImageView home_tab_plan_1_iv;
    private TextView home_tab_plan_tv;
    private TextView home_title_tv;
    private ViewPager home_view_pager;
    private HomePagerAdapter mHomePagerAdapter;
    private int mColorRed;
    private int mColorGray;
    private int mCurrentPage;
    private int mDirection;
    private float mLastRatio;

    @Override
    protected void setContentView() {
        setStatusBarColor(R.color.colorThemeRed, false);
        setContentView(R.layout.activity_home2);
    }

    @Override
    protected void initData() {
        mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
        mColorRed = ResUtil.getColor(R.color.colorThemeRed);
        mColorGray = ResUtil.getColor(R.color.colorThemeGray);
        changeColor(0.5f);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        home_title_tv = findViewById(R.id.home_title_tv);
        home_view_pager = findViewById(R.id.home_view_pager);
        home_tab_apps_rl = findViewById(R.id.home_tab_apps_rl);
        home_tab_apps_0_iv = findViewById(R.id.home_tab_apps_0_iv);
        home_tab_apps_1_iv = findViewById(R.id.home_tab_apps_1_iv);
        home_tab_apps_tv = findViewById(R.id.home_tab_apps_tv);
        home_tab_users_rl = findViewById(R.id.home_tab_users_rl);
        home_tab_users_0_iv = findViewById(R.id.home_tab_users_0_iv);
        home_tab_users_1_iv = findViewById(R.id.home_tab_users_1_iv);
        home_tab_users_tv = findViewById(R.id.home_tab_users_tv);
        home_tab_plan_rl = findViewById(R.id.home_tab_plan_rl);
        home_tab_plan_0_iv = findViewById(R.id.home_tab_plan_0_iv);
        home_tab_plan_1_iv = findViewById(R.id.home_tab_plan_1_iv);
        home_tab_plan_tv = findViewById(R.id.home_tab_plan_tv);

        home_tab_apps_rl.setOnClickListener(this);
        home_tab_users_rl.setOnClickListener(this);
        home_tab_plan_rl.setOnClickListener(this);

        home_view_pager.setOffscreenPageLimit(5);
        home_view_pager.setAdapter(mHomePagerAdapter);
        home_view_pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                LogUtil.d(TAG, "onPageScrollStateChanged: " + "state=" + state);
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    mCurrentPage = home_view_pager.getCurrentItem();
                    mDirection = 0;
                    checkTab(mCurrentPage);
                }
            }

            @Override
            public void onPageScrolled(int position, float offsetRatio, int offsetPixels) {
                LogUtil.d(TAG, "onPageScrolled: " + "position=" + position + " offsetRatio=" + offsetRatio + " offsetPixels=" + offsetPixels);
                if (offsetRatio == 0) return;
                float alphaLeft = 1 - offsetRatio / 0.5f;
                float alphaRight = (offsetRatio - 0.5f) / 0.5f;
                alphaLeft = alphaLeft > 0.9f ? 1.0f : (alphaLeft < 0.2f ? 0.0f : alphaLeft);
                alphaRight = alphaRight > 0.9f ? 1.0f : (alphaRight < 0.2f ? 0.0f : alphaRight);
                if (mCurrentPage == 0) {
                    if (offsetRatio < 0.5f) {
                        home_tab_apps_1_iv.setAlpha(alphaLeft);
                    } else {
                        home_tab_users_1_iv.setAlpha(alphaRight);
                    }
                    int nextColor = changeColor(1 - offsetRatio);
                    home_tab_users_0_iv.setImageTintList(ColorStateList.valueOf(nextColor));
                    home_tab_users_tv.setTextColor(nextColor);
                    int currentColor = changeColor(offsetRatio);
                    home_tab_apps_tv.setTextColor(currentColor);
                    home_tab_apps_0_iv.setImageTintList(ColorStateList.valueOf(currentColor));
                } else if (mCurrentPage == 1) {
                    if (Math.abs(offsetRatio - mLastRatio) >= 0.9f) {
                        mDirection = 0;
                    }
                    mLastRatio = offsetRatio;
                    if (mDirection == 0) {
                        if (offsetRatio > 0.5f) {
                            mDirection = -1;
                        } else {
                            mDirection = 1;
                        }
                    }
                    if (mDirection == -1) {
                        if (offsetRatio < 0.5f) {
                            home_tab_apps_1_iv.setAlpha(alphaLeft);
                        } else {
                            home_tab_users_1_iv.setAlpha(alphaRight);
                        }
                        int nextColor = changeColor(1 - offsetRatio);
                        home_tab_users_0_iv.setImageTintList(ColorStateList.valueOf(nextColor));
                        home_tab_users_tv.setTextColor(nextColor);
                        int currentColor = changeColor(offsetRatio);
                        home_tab_apps_tv.setTextColor(currentColor);
                        home_tab_apps_0_iv.setImageTintList(ColorStateList.valueOf(currentColor));
                    } else if (mDirection == 1) {
                        if (offsetRatio > 0.5f) {
                            home_tab_plan_1_iv.setAlpha(alphaRight);
                        } else {
                            home_tab_users_1_iv.setAlpha(alphaLeft);
                        }
                        int nextColor = changeColor(offsetRatio);
                        home_tab_users_0_iv.setImageTintList(ColorStateList.valueOf(nextColor));
                        home_tab_users_tv.setTextColor(nextColor);
                        int currentColor = changeColor(1 - offsetRatio);
                        home_tab_plan_tv.setTextColor(currentColor);
                        home_tab_plan_0_iv.setImageTintList(ColorStateList.valueOf(currentColor));
                    }
                } else if (mCurrentPage == 2) {
                    if (offsetRatio > 0.5f) {
                        home_tab_plan_1_iv.setAlpha(alphaRight);
                    } else {
                        home_tab_users_1_iv.setAlpha(alphaLeft);
                    }
                    int nextColor = changeColor(offsetRatio);
                    home_tab_users_0_iv.setImageTintList(ColorStateList.valueOf(nextColor));
                    home_tab_users_tv.setTextColor(nextColor);
                    int currentColor = changeColor(1 - offsetRatio);
                    home_tab_plan_tv.setTextColor(currentColor);
                    home_tab_plan_0_iv.setImageTintList(ColorStateList.valueOf(currentColor));
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_tab_apps_rl:
                checkTab(0);
                break;
            case R.id.home_tab_users_rl:
                checkTab(1);
                break;
            case R.id.home_tab_plan_rl:
                checkTab(2);
                break;
        }
    }

    private void checkTab(int index) {
        mCurrentPage = index;
        home_view_pager.setCurrentItem(index, false);
        home_tab_apps_1_iv.setAlpha(0.0f);
        home_tab_users_1_iv.setAlpha(0.0f);
        home_tab_plan_1_iv.setAlpha(0.0f);
        home_tab_apps_tv.setTextColor(mColorGray);
        home_tab_users_tv.setTextColor(mColorGray);
        home_tab_plan_tv.setTextColor(mColorGray);
        home_tab_apps_0_iv.setImageTintList(ColorStateList.valueOf(mColorGray));
        home_tab_users_0_iv.setImageTintList(ColorStateList.valueOf(mColorGray));
        home_tab_plan_0_iv.setImageTintList(ColorStateList.valueOf(mColorGray));
        if (index == 0) {
            home_tab_apps_1_iv.setAlpha(1.0f);
            home_tab_apps_tv.setTextColor(mColorRed);
            home_tab_apps_0_iv.setImageTintList(ColorStateList.valueOf(mColorRed));
            home_title_tv.setText(R.string.tab_home_apps);
        } else if (index == 1) {
            home_tab_users_1_iv.setAlpha(1.0f);
            home_tab_users_tv.setTextColor(mColorRed);
            home_tab_users_0_iv.setImageTintList(ColorStateList.valueOf(mColorRed));
            home_title_tv.setText(R.string.tab_home_users);
        } else if (index == 2) {
            home_tab_plan_1_iv.setAlpha(1.0f);
            home_tab_plan_tv.setTextColor(mColorRed);
            home_tab_plan_0_iv.setImageTintList(ColorStateList.valueOf(mColorRed));
            home_title_tv.setText(R.string.tab_home_plan);
        }
    }

    private int changeColor(float ratio) {
        int b0 = mColorRed & 0xff;
        int g0 = (mColorRed >> 8) & 0xff;
        int r0 = (mColorRed >> 16) & 0xff;

        int b1 = mColorGray & 0xff;
        int g1 = (mColorGray >> 8) & 0xff;
        int r1 = (mColorGray >> 16) & 0xff;

        int newB0 = (int) (b0 + (b1 - b0) * ratio);
        int newG0 = (int) (g0 + (g1 - g0) * ratio);
        int newR0 = (int) (r0 + (r1 - r0) * ratio);
        return Color.argb(0xff, newR0, newG0, newB0);
    }

}