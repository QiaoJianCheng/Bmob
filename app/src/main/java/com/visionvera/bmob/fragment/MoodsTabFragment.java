package com.visionvera.bmob.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.visionvera.bmob.R;
import com.visionvera.bmob.adapter.MoodAdapter;
import com.visionvera.bmob.base.BaseFragment;
import com.visionvera.bmob.model.MoodsBean;
import com.visionvera.bmob.net.NetworkRequest;
import com.visionvera.bmob.net.ResponseSubscriber;
import com.visionvera.bmob.utils.ToastUtil;
import com.visionvera.bmob.view.PtrRefreshLayout;

import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * Created by Qiao on 2017/5/15.
 */

public class MoodsTabFragment extends BaseFragment {
    private PtrRefreshLayout moods_ptr;
    private RecyclerView moods_rv;
    private ArrayList<MoodsBean.MoodBean> mAdapterList;
    private MoodAdapter mMoodAdapter;

    @NonNull
    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_moods, container, false);
    }

    @Override
    protected void initData() {
        if (mAdapterList == null) {
            mAdapterList = new ArrayList<>();
        }
        if (mMoodAdapter == null) {
            mMoodAdapter = new MoodAdapter(getContext(), mAdapterList);
        }
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        moods_ptr = (PtrRefreshLayout) view.findViewById(R.id.common_content_view);
        moods_rv = (RecyclerView) view.findViewById(R.id.moods_rv);

        moods_ptr.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                loadData(false);
            }
        });

        moods_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        moods_rv.setAdapter(mMoodAdapter);
    }

    @Override
    protected void loadData(boolean showLoading) {
        super.loadData(showLoading);

        NetworkRequest.getMoods(this, new ResponseSubscriber<MoodsBean>() {
            @Override
            public void onSuccess(MoodsBean moodsBean) {
                mAdapterList.clear();
                if (moodsBean != null && moodsBean.results != null) {
                    mAdapterList.addAll(moodsBean.results);
                }
                if (mAdapterList.size() > 0) {
                    showContentView();
                } else {
                    showEmptyView();
                }
                mMoodAdapter.notifyDataSetChanged();
                moods_ptr.refreshComplete();
            }

            @Override
            public void onFailure(int code, String error) {
                ToastUtil.warnToast(error);
                if (mAdapterList.size() > 0) {
                    showContentView();
                } else {
                    showFailedView();
                }
                moods_ptr.refreshComplete();
            }
        });
    }

    public void pullToRefresh() {
        moods_rv.scrollToPosition(0);
        moods_ptr.autoRefresh();
    }
}
