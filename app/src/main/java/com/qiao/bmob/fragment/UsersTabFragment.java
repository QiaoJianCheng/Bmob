package com.qiao.bmob.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.qiao.bmob.R;
import com.qiao.bmob.adapter.UserAdapter;
import com.qiao.bmob.base.BaseFragment;
import com.qiao.bmob.base.BaseRecyclerAdapter;
import com.qiao.bmob.model.UsersBean;
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

public class UsersTabFragment extends BaseFragment {
    private PtrRefreshLayout users_ptr;
    private RecyclerView users_rv;
    private ArrayList<UsersBean.UserBean> mUsers;
    private UserAdapter mUserAdapter;

    @NonNull
    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_users, container, false);
    }

    @Override
    protected void initData() {
        mUsers = new ArrayList<>();
        mUserAdapter = new UserAdapter(getContext(), mUsers);
        mUserAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                UsersBean.UserBean userBean = mUsers.get(position);
                IntentUtil.toMoodActivity(getActivity(), userBean.nickname, userBean.objectId);
            }
        });
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        users_ptr = view.findViewById(R.id.common_content_view);
        users_rv = view.findViewById(R.id.users_rv);

        users_ptr.disableWhenHorizontalMove(true);
        users_ptr.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                loadData(false);
            }
        });

        users_rv.setLayoutManager(new LinearLayoutManager(getContext()));
        users_rv.setAdapter(mUserAdapter);
    }

    @Override
    protected void loadData(boolean showLoading) {
        super.loadData(showLoading);
        NetworkRequest.getUsers(this, new ResponseSubscriber<UsersBean>() {
            @Override
            public void onSuccess(UsersBean userBean) {
                mUsers.clear();
                if (userBean != null && userBean.results != null) {
                    mUsers.addAll(userBean.results);
                }
                mUserAdapter.notifyDataSetChanged();
                networkSuccess();
            }

            @Override
            public void onFailure(int code, String error) {
                networkFailure(error);
            }
        });
    }

    private void networkFailure(String error) {
        users_ptr.refreshComplete();
        ToastUtil.showToast(error);
        if (mUsers.size() == 0) {
            showFailedView();
            mUserAdapter.footerLoadCompleted();
        } else {
            showContentView();
            mUserAdapter.footerFailed();
        }
    }

    private void networkSuccess() {
        mUserAdapter.footerLoadCompleted();
        users_ptr.refreshComplete();
        if (mUsers.size() == 0) {
            showEmptyView();
        } else {
            showContentView();
        }
    }

    public void pullToRefresh() {
        users_rv.scrollToPosition(0);
        users_ptr.autoRefresh();
    }
}
