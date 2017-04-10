package com.visionvera.bmob.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.visionvera.bmob.R;
import com.visionvera.bmob.adapter.UserAdapter;
import com.visionvera.bmob.base.BaseActivity;
import com.visionvera.bmob.base.BaseRecyclerAdapter;
import com.visionvera.bmob.event.RxEvent;
import com.visionvera.bmob.listener.RecyclerScrollListener;
import com.visionvera.bmob.model.UserBean;
import com.visionvera.bmob.model.UserBeans;
import com.visionvera.bmob.net.NetworkRequest;
import com.visionvera.bmob.net.ResponseSubscriber;
import com.visionvera.bmob.utils.IntentUtil;
import com.visionvera.bmob.utils.LogUtil;
import com.visionvera.bmob.utils.ToastUtil;
import com.visionvera.bmob.view.PtrRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class MainActivity extends BaseActivity {
    private PtrRefreshLayout ptr_view;
    private RecyclerView recycler_view;
    private View add_bt;
    private ArrayList<UserBean> mUsers;
    private UserAdapter mUserAdapter;

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void initData() {
        mUsers = new ArrayList<>();
        mUserAdapter = new UserAdapter(getApplicationContext(), mUsers);
        mUserAdapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                IntentUtil.toRegisterActivity(MainActivity.this);
            }
        });
    }

    @Override
    protected void initViews() {
        ptr_view = (PtrRefreshLayout) findViewById(R.id.ptr_view);
        recycler_view = (RecyclerView) findViewById(R.id.recycler_view);
        add_bt = findViewById(R.id.add_bt);

        add_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        ptr_view.disableWhenHorizontalMove(true);
        ptr_view.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                loadData(false);
            }
        });

        recycler_view.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recycler_view.setAdapter(mUserAdapter);
        recycler_view.addOnScrollListener(new RecyclerScrollListener() {
            @Override
            public void onLoadMore() {
                loadData(false);
            }
        });
    }

    @Override
    protected void loadData(boolean showLoading) {
        super.loadData(showLoading);
        NetworkRequest.getInstance().getUsers(this, new ResponseSubscriber<UserBeans>() {
            @Override
            public void onSuccess(UserBeans userBean) {
                if (userBean != null && userBean.results != null) {
                    mUsers.clear();
                    mUsers.addAll(userBean.results);
                    mUserAdapter.notifyDataSetChanged();
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
        ptr_view.refreshComplete();
        ToastUtil.networkFailure(error);
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
        ptr_view.refreshComplete();
        if (mUsers.size() == 0) {
            showEmptyView();
        } else {
            showContentView();
        }
    }

    @Override
    protected void onEventMainThread(RxEvent rxEvent) {

    }

    private ActivityManager.RunningAppProcessInfo getRunningAppProcessInfo(String packageName) {
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processList = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo p : processList) {
            if (p.processName.equals(packageName)) {
                return p;
            }
        }
        return null;
    }

    private boolean isForeground(String packageName) {
        ActivityManager.RunningAppProcessInfo processInfo = getRunningAppProcessInfo(packageName);
        if (processInfo != null) {
            return ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND == processInfo.importance;
        }
        return false;
    }

    private void moveTaskToFront() {
//        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
//        while (!isForeground(getPackageName())) {
//            LogUtil.d(TAG, "before is front: " + isForeground(getPackageName()));
//            am.moveTaskToFront(getTaskId(), ActivityManager.MOVE_TASK_WITH_HOME);
//            LogUtil.d(TAG, "after  is front: " + isForeground(getPackageName()));
//        }
        LogUtil.d(TAG, "before is front: " + isForeground(getPackageName()));
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
        LogUtil.d(TAG, "after  is front: " + isForeground(getPackageName()));
    }
}