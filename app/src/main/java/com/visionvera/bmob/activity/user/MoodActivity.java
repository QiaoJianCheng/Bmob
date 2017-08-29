package com.visionvera.bmob.activity.user;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.visionvera.bmob.R;
import com.visionvera.bmob.adapter.MoodAdapter;
import com.visionvera.bmob.base.BaseActivity;
import com.visionvera.bmob.model.MoodsBean;
import com.visionvera.bmob.net.NetworkRequest;
import com.visionvera.bmob.net.ResponseSubscriber;
import com.visionvera.bmob.utils.ToastUtil;
import com.visionvera.bmob.view.PtrRefreshLayout;

import java.util.ArrayList;

import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

public class MoodActivity extends BaseActivity {
    public static final String INTENT_NAME = "INTENT_NAME";
    public static final String INTENT_ID = "INTENT_ID";

    private PtrRefreshLayout common_content_view;
    private RecyclerView mood_rv;
    private ArrayList<MoodsBean.MoodBean> mAdapterList;
    private MoodAdapter mMoodAdapter;
    private String mName;
    private String mUserID;


    @Override
    protected void setContentView() {
        setStatusBarColor(R.color.colorThemeRed, false);
        setContentView(R.layout.activity_mood);
    }

    @Override
    protected void initData() {
        mName = getIntent().getStringExtra(INTENT_NAME);
        mUserID = getIntent().getStringExtra(INTENT_ID);
        mAdapterList = new ArrayList<>();
        mMoodAdapter = new MoodAdapter(getApplicationContext(), mAdapterList);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitleBar(String.format("%s 的 Moods", mName));

        common_content_view = (PtrRefreshLayout) findViewById(R.id.common_content_view);
        mood_rv = (RecyclerView) findViewById(R.id.mood_rv);

        common_content_view.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                loadData(false);
            }
        });

        mood_rv.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mood_rv.setAdapter(mMoodAdapter);
    }

    @Override
    protected void loadData(boolean showLoading) {
        super.loadData(showLoading);

        NetworkRequest.getMoodsByUser(this, mUserID, new ResponseSubscriber<MoodsBean>() {
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
                common_content_view.refreshComplete();
            }

            @Override
            public void onFailure(int code, String error) {
                ToastUtil.showToast(error);
                if (mAdapterList.size() > 0) {
                    showContentView();
                } else {
                    showFailedView();
                }
                common_content_view.refreshComplete();
            }
        });
    }
}
