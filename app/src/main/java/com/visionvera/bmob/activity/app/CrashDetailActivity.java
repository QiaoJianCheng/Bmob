package com.visionvera.bmob.activity.app;

import android.os.Bundle;
import android.widget.TextView;

import com.visionvera.bmob.R;
import com.visionvera.bmob.base.BaseActivity;
import com.visionvera.bmob.model.CrashesBean;

public class CrashDetailActivity extends BaseActivity {
    public static final String INTENT_TITLE = "INTENT_TITLE";
    public static final String INTENT_DETAIL = "INTENT_DETAIL";

    private String mTitle;
    private CrashesBean.CrashBean mCrashBean;

    private TextView crash_detail_tv;

    @Override
    protected void initData() {
        mTitle = getIntent().getStringExtra(INTENT_TITLE);
        mCrashBean = (CrashesBean.CrashBean) getIntent().getSerializableExtra(INTENT_DETAIL);
    }

    @Override
    protected void setContentView() {
        setStatusBarColor(R.color.colorThemeRed, false);
        setContentView(R.layout.activity_crash_detail);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitleBar(mTitle);

        crash_detail_tv = (TextView) findViewById(R.id.crash_detail_tv);

        crash_detail_tv.setText(mCrashBean.crash_info);
    }
}
