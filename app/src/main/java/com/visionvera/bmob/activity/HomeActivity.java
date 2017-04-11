package com.visionvera.bmob.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.visionvera.bmob.R;
import com.visionvera.bmob.adapter.HomePagerAdapter;
import com.visionvera.bmob.base.BaseActivity;
import com.visionvera.bmob.utils.ResUtil;

public class HomeActivity extends BaseActivity {
    private TextView home_title_tv;
    private RadioGroup home_tab_rg;
    private RadioButton home_tab_apps_rb;
    private RadioButton home_tab_users_rb;
    private ViewPager home_view_pager;
    private HomePagerAdapter mHomePagerAdapter;

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
        home_title_tv = (TextView) findViewById(R.id.home_title_tv);
        home_tab_rg = (RadioGroup) findViewById(R.id.home_tab_rg);
        home_tab_apps_rb = (RadioButton) findViewById(R.id.home_tab_apps_rb);
        home_tab_users_rb = (RadioButton) findViewById(R.id.home_tab_users_rb);
        home_view_pager = (ViewPager) findViewById(R.id.home_view_pager);

        int paddingTop = (int) ResUtil.getDimen(R.dimen.x20);
        int x71 = (int) ResUtil.getDimen(R.dimen.x71);
        int x60 = (int) ResUtil.getDimen(R.dimen.x60);
        Drawable appDrawable = ResUtil.getDrawable(R.drawable.selector_home_tab_apps);
        appDrawable.setBounds(0, paddingTop, x71, x60 + paddingTop);
        home_tab_apps_rb.setCompoundDrawables(null, appDrawable, null, null);
        Drawable userDrawable = ResUtil.getDrawable(R.drawable.selector_home_tab_users);
        userDrawable.setBounds(0, paddingTop, x71, x60 + paddingTop);
        home_tab_users_rb.setCompoundDrawables(null, userDrawable, null, null);

        home_tab_rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                home_view_pager.setCurrentItem(checkedId == R.id.home_tab_apps_rb ? 0 : 1);
            }
        });

        home_view_pager.setAdapter(mHomePagerAdapter);
        home_view_pager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                home_tab_rg.check(position == 0 ? R.id.home_tab_apps_rb : R.id.home_tab_users_rb);
                home_title_tv.setText(position == 0 ? R.string.tab_home_apps : R.string.tab_home_users);
            }
        });
    }

}