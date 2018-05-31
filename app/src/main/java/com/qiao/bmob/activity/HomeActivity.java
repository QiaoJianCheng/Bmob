package com.qiao.bmob.activity;

import android.animation.Animator;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.qiao.bmob.R;
import com.qiao.bmob.adapter.HomePagerAdapter;
import com.qiao.bmob.base.BaseActivity;
import com.qiao.bmob.listener.AnimatorListener;
import com.qiao.bmob.listener.OnDoubleTapListener;
import com.qiao.bmob.listener.PressEffectTouchListener;
import com.qiao.bmob.utils.IntentUtil;
import com.qiao.bmob.utils.ResUtil;
import com.qiao.bmob.utils.ToastUtil;

public class HomeActivity extends BaseActivity {
    private TextView home_title_tv;
    private RadioGroup home_tab_rg;
    private RadioButton home_tab_apps_rb;
    private RadioButton home_tab_moods_rb;
    private ImageView home_tab_publish_iv;
    private RadioButton home_tab_users_rb;
    private RadioButton home_tab_plan_rb;
    private ViewPager home_view_pager;
    private HomePagerAdapter mHomePagerAdapter;
    private boolean mIsScrolling;

    @Override
    protected void setContentView() {
        setStatusBarColor(R.color.colorThemeRed, false);
        setContentView(R.layout.activity_home);
    }

    @Override
    protected void initData() {
        mHomePagerAdapter = new HomePagerAdapter(getSupportFragmentManager());
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        home_title_tv = findViewById(R.id.home_title_tv);
        home_tab_rg = findViewById(R.id.home_tab_rg);
        home_tab_apps_rb = findViewById(R.id.home_tab_apps_rb);
        home_tab_users_rb = findViewById(R.id.home_tab_users_rb);
        home_tab_plan_rb = findViewById(R.id.home_tab_plan_rb);
        home_tab_moods_rb = findViewById(R.id.home_tab_moods_rb);
        home_tab_publish_iv = findViewById(R.id.home_tab_publish_iv);
        home_view_pager = findViewById(R.id.home_view_pager);

        home_tab_publish_iv.setOnTouchListener(new PressEffectTouchListener());

        int paddingTop = (int) ResUtil.getDimen(R.dimen.x20);
        int x71 = (int) ResUtil.getDimen(R.dimen.x71);
        int x60 = (int) ResUtil.getDimen(R.dimen.x60);

        Drawable moodDrawable = ResUtil.getDrawable(R.drawable.selector_home_tab_moods);
        moodDrawable.setBounds(0, paddingTop, x71, x60 + paddingTop);
        home_tab_moods_rb.setCompoundDrawables(null, moodDrawable, null, null);

        Drawable appDrawable = ResUtil.getDrawable(R.drawable.selector_home_tab_apps);
        appDrawable.setBounds(0, paddingTop, x71, x60 + paddingTop);
        home_tab_apps_rb.setCompoundDrawables(null, appDrawable, null, null);

        Drawable userDrawable = ResUtil.getDrawable(R.drawable.selector_home_tab_users);
        userDrawable.setBounds(0, paddingTop, x71, x60 + paddingTop);
        home_tab_users_rb.setCompoundDrawables(null, userDrawable, null, null);

        Drawable planDrawable = ResUtil.getDrawable(R.drawable.selector_home_tab_plan);
        planDrawable.setBounds(0, paddingTop, x71, x60 + paddingTop);
        home_tab_plan_rb.setCompoundDrawables(null, planDrawable, null, null);

        home_tab_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.home_tab_moods_rb) {
                    home_view_pager.setCurrentItem(0, mIsScrolling);
                } else if (checkedId == R.id.home_tab_apps_rb) {
                    home_view_pager.setCurrentItem(1, mIsScrolling);
                } else if (checkedId == R.id.home_tab_users_rb) {
                    home_view_pager.setCurrentItem(2, mIsScrolling);
                } else if (checkedId == R.id.home_tab_plan_rb) {
                    home_view_pager.setCurrentItem(3, mIsScrolling);
                }
            }
        });
        home_tab_moods_rb.setOnTouchListener(new OnDoubleTapListener() {
            @Override
            public void onDoubleTap(View v) {
                mHomePagerAdapter.refresh(home_view_pager.getCurrentItem());
            }
        });
        home_tab_apps_rb.setOnTouchListener(new OnDoubleTapListener() {
            @Override
            public void onDoubleTap(View v) {
                mHomePagerAdapter.refresh(home_view_pager.getCurrentItem());
            }
        });
        home_tab_users_rb.setOnTouchListener(new OnDoubleTapListener() {
            @Override
            public void onDoubleTap(View v) {
                mHomePagerAdapter.refresh(home_view_pager.getCurrentItem());
            }
        });
        home_view_pager.setOffscreenPageLimit(5);
        home_view_pager.setAdapter(mHomePagerAdapter);
        home_view_pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
                mIsScrolling = !(state == ViewPager.SCROLL_STATE_IDLE);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 0) {
                    home_tab_rg.check(R.id.home_tab_moods_rb);
                    home_title_tv.setText(R.string.tab_home_mood);
                } else if (position == 1) {
                    home_tab_rg.check(R.id.home_tab_apps_rb);
                    home_title_tv.setText(R.string.tab_home_apps);
                } else if (position == 2) {
                    home_tab_rg.check(R.id.home_tab_users_rb);
                    home_title_tv.setText(R.string.tab_home_users);
                } else if (position == 3) {
                    home_tab_rg.check(R.id.home_tab_plan_rb);
                    home_title_tv.setText(R.string.tab_home_plan);
                }
            }
        });

        home_tab_publish_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                home_tab_publish_iv.animate().
                        rotationBy(180).
                        setDuration(200).
                        setListener(new AnimatorListener() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                IntentUtil.toPublishActivity(HomeActivity.this);
                            }
                        }).start();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        home_tab_publish_iv.animate().rotationBy(-180).setDuration(200).setListener(null).start();
    }

    private long mLastPress;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - mLastPress <= 2000) {
            super.onBackPressed();
        } else {
            ToastUtil.showToast(getString(R.string.toast_one_more_press));
            mLastPress = System.currentTimeMillis();
        }
    }
}