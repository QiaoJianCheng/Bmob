package com.qiao.bmob.activity.app;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.qiao.bmob.R;
import com.qiao.bmob.adapter.CrashAdapter;
import com.qiao.bmob.base.BaseActivity;
import com.qiao.bmob.base.BaseRecyclerAdapter;
import com.qiao.bmob.model.CrashesBean;
import com.qiao.bmob.net.NetworkRequest;
import com.qiao.bmob.net.ResponseSubscriber;
import com.qiao.bmob.utils.IntentUtil;
import com.qiao.bmob.utils.ToastUtil;
import com.qiao.bmob.view.PtrRefreshLayout;

import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class CrashListlActivity extends BaseActivity {
    public static final String INTENT_APP_ID = "INTENT_APP_ID";
    public static final String INTENT_APP_NAME = "INTENT_APP_NAME";

    private PtrRefreshLayout crash_list_ptr;
    private RecyclerView crash_list_rv;
    private ArrayList<CrashesBean.CrashBean> mAppCrashes;
    private CrashAdapter mCrashAdapter;
    private String mAppId;
    private String mAppName;

    @Override
    protected void setContentView() {
        setStatusBarColor(R.color.colorThemeRed, false);
        setContentView(R.layout.activity_crash_list);
    }

    @Override
    protected void initData() {
        mAppId = getIntent().getStringExtra(INTENT_APP_ID);
        mAppName = getIntent().getStringExtra(INTENT_APP_NAME);
        mAppCrashes = new ArrayList<>();
        mCrashAdapter = new CrashAdapter(getApplicationContext(), mAppCrashes);
        mCrashAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                CrashesBean.CrashBean bean = mAppCrashes.get(position);
                IntentUtil.toCrashDetailActivity(CrashListlActivity.this, mAppName, bean);
            }
        });
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitleBar(mAppName);

        crash_list_ptr = findViewById(R.id.crash_list_ptr);
        crash_list_rv = findViewById(R.id.crash_list_rv);

        crash_list_ptr.disableWhenHorizontalMove(true);
        crash_list_ptr.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                loadData(false);
            }
        });

        crash_list_rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        crash_list_rv.setAdapter(mCrashAdapter);
    }

    @Override
    protected void loadData(boolean showLoading) {
        super.loadData(showLoading);
        NetworkRequest.getCrashes(this, mAppId, new ResponseSubscriber<CrashesBean>() {
            @Override
            public void onSuccess(CrashesBean crashesBean) {
                mAppCrashes.clear();
                if (crashesBean != null && crashesBean.results != null) {
                    mAppCrashes.addAll(crashesBean.results);
                }
                mCrashAdapter.notifyDataSetChanged();
                networkSuccess();
            }

            @Override
            public void onFailure(int code, String error) {
                networkFailure(error);
            }
        });
    }

    private void networkFailure(String error) {
        crash_list_ptr.refreshComplete();
        ToastUtil.showToast(error);
        if (mAppCrashes.size() == 0) {
            showFailedView();
        } else {
            showContentView();
        }
    }

    private void networkSuccess() {
        mCrashAdapter.footerLoadCompleted();
        crash_list_ptr.refreshComplete();
        if (mAppCrashes.size() == 0) {
            showEmptyView();
        } else {
            showContentView();
        }
    }
}
