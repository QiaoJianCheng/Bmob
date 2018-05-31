package com.qiao.bmob.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.qiao.bmob.R;
import com.qiao.bmob.adapter.AppAdapter;
import com.qiao.bmob.base.BaseFragment;
import com.qiao.bmob.base.BaseRecyclerAdapter;
import com.qiao.bmob.event.ProgressEvent;
import com.qiao.bmob.event.RxBus;
import com.qiao.bmob.model.AppsBean;
import com.qiao.bmob.model.BaseBean;
import com.qiao.bmob.net.NetworkRequest;
import com.qiao.bmob.net.ResponseSubscriber;
import com.qiao.bmob.utils.IntentUtil;
import com.qiao.bmob.utils.ToastUtil;
import com.qiao.bmob.view.PtrRefreshLayout;

import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by Qiao on 2017/4/11.
 */

public class AppsTabFragment extends BaseFragment {
    private PtrRefreshLayout apps_ptr;
    private RecyclerView apps_rv;
    private ArrayList<AppsBean.AppBean> mApps;
    private AppAdapter mAppAdapter;

    @NonNull
    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_apps, container, false);
    }

    @Override
    protected void initData() {
        mApps = new ArrayList<>();
        mAppAdapter = new AppAdapter(getContext(), mApps);
        mAppAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                AppsBean.AppBean appBean = mApps.get(position);
                IntentUtil.toAppDetailActivity(getActivity(), appBean.application_id, appBean.app_name);
            }
        });
        mAppAdapter.setOnCheckChangedListener(mOnCheckChangedListener);
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        apps_ptr = view.findViewById(R.id.common_content_view);
        apps_rv = view.findViewById(R.id.apps_rv);

        apps_ptr.disableWhenHorizontalMove(true);
        apps_ptr.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                loadData(false);
            }
        });

        apps_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        apps_rv.setAdapter(mAppAdapter);
    }

    @Override
    protected void loadData(boolean showLoading) {
        super.loadData(showLoading);
        NetworkRequest.getApps(this, new ResponseSubscriber<AppsBean>() {
            @Override
            public void onSuccess(AppsBean appsBean) {
                mApps.clear();
                if (appsBean != null && appsBean.results != null) {
                    mApps.addAll(appsBean.results);
                }
                mAppAdapter.notifyDataSetChanged();
                networkSuccess();
            }

            @Override
            public void onFailure(int code, String error) {
                networkFailure(error);
            }
        });
    }

    private void networkFailure(String error) {
        apps_ptr.refreshComplete();
        ToastUtil.showToast(error);
        if (mApps.size() == 0) {
            showFailedView();
        } else {
            showContentView();
        }
    }

    private void networkSuccess() {
        mAppAdapter.footerLoadCompleted();
        apps_ptr.refreshComplete();
        if (mApps.size() == 0) {
            showEmptyView();
        } else {
            showContentView();
        }
    }

    private AppAdapter.OnCheckChangedListener mOnCheckChangedListener = new AppAdapter.OnCheckChangedListener() {
        @Override
        public void checkChanged(final CompoundButton buttonView, final int position, final boolean isChecked) {
            if (!buttonView.isClickable()) return;
            buttonView.setClickable(false);
            RxBus.getDefault().post(new ProgressEvent(position, true));
            final AppsBean.AppBean bean = mApps.get(position);
            NetworkRequest.putApp(AppsTabFragment.this, bean.objectId, isChecked, new ResponseSubscriber<BaseBean>() {
                @Override
                public void onSuccess(BaseBean baseBean) {
                    buttonView.setClickable(true);
                    RxBus.getDefault().post(new ProgressEvent(position, false));
                }

                @Override
                public void onFailure(int code, String error) {
                    ToastUtil.showToast(error);
                    buttonView.setChecked(!isChecked);
                    buttonView.setClickable(true);
                    RxBus.getDefault().post(new ProgressEvent(position, false));
                }
            });

        }
    };

    public void pullToRefresh() {
        apps_rv.scrollToPosition(0);
        apps_ptr.autoRefresh();
    }
}
