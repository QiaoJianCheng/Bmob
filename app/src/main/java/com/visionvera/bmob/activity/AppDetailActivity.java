package com.visionvera.bmob.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.visionvera.bmob.R;
import com.visionvera.bmob.adapter.AppDetailAdapter;
import com.visionvera.bmob.base.BaseActivity;
import com.visionvera.bmob.base.BaseRecyclerAdapter;
import com.visionvera.bmob.global.App;
import com.visionvera.bmob.model.CrashBean;
import com.visionvera.bmob.model.CrashesBean;
import com.visionvera.bmob.net.NetworkRequest;
import com.visionvera.bmob.net.ResponseSubscriber;
import com.visionvera.bmob.utils.ToastUtil;
import com.visionvera.bmob.view.PtrRefreshLayout;

import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class AppDetailActivity extends BaseActivity {
    public static final String INTENT_APP_ID = "INTENT_APP_ID";
    public static final String INTENT_APP_NAME = "INTENT_APP_NAME";

    private PtrRefreshLayout app_detail_ptr;
    private RecyclerView app_detail_rv;
    private ArrayList<CrashBean> mAppDetails;
    private AppDetailAdapter mAppDetailAdapter;
    private String mAppId;
    private String mAppName;

    @Override
    protected void setContentView() {
        setStatusBarColor(R.color.colorThemeRed, false);
        setContentView(R.layout.activity_app_detail);
    }

    @Override
    protected void initData() {
        mAppId = getIntent().getStringExtra(INTENT_APP_ID);
        mAppName = getIntent().getStringExtra(INTENT_APP_NAME);
        mAppDetails = new ArrayList<>();
        mAppDetailAdapter = new AppDetailAdapter(getApplicationContext(), mAppDetails);
        mAppDetailAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                IntentUtil.toRegisterActivity(getActivity());
            }
        });
        App.getInstance().isAppOnForeground();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitleBar(mAppName);
        app_detail_ptr = (PtrRefreshLayout) findViewById(R.id.common_content_view);
        app_detail_rv = (RecyclerView) findViewById(R.id.app_detail_rv);

        app_detail_ptr.disableWhenHorizontalMove(true);
        app_detail_ptr.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                loadData(false);
            }
        });

        app_detail_rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        app_detail_rv.setAdapter(mAppDetailAdapter);
    }

    @Override
    protected void loadData(boolean showLoading) {
        super.loadData(showLoading);
        NetworkRequest.getInstance().getCrashes(this, mAppId, new ResponseSubscriber<CrashesBean>() {
            @Override
            public void onSuccess(CrashesBean crashesBean) {
                if (crashesBean != null && crashesBean.results != null) {
                    mAppDetails.clear();
                    mAppDetails.addAll(crashesBean.results);
                    mAppDetailAdapter.notifyDataSetChanged();
                }
                networkSuccess();
            }

            @Override
            public void onFailure(int code, String error) {
                networkFailure(error);
            }
        });
    }

    private void networkFailure(String error) {
        app_detail_ptr.refreshComplete();
        ToastUtil.networkFailure(error);
        if (mAppDetails.size() == 0) {
            showFailedView();
        } else {
            showContentView();
        }
    }

    private void networkSuccess() {
        mAppDetailAdapter.footerLoadCompleted();
        app_detail_ptr.refreshComplete();
        if (mAppDetails.size() == 0) {
            showEmptyView();
        } else {
            showContentView();
        }
    }
}
