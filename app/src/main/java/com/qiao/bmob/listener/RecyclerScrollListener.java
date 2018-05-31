package com.qiao.bmob.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.qiao.bmob.base.BaseRecyclerAdapter;

/**
 * Created by Qiao on 2017/2/15.
 */

public abstract class RecyclerScrollListener extends RecyclerView.OnScrollListener {

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter == null || !(adapter instanceof BaseRecyclerAdapter)) {
            return;
        }
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof LinearLayoutManager) {
            int lastComplete = ((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition();
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (lastComplete == adapter.getItemCount() - 1 && !((BaseRecyclerAdapter) adapter).isLoading()) {
                    ((BaseRecyclerAdapter) adapter).footerLoading();
                    recyclerView.smoothScrollToPosition(((BaseRecyclerAdapter) adapter).getListSize());
                    onLoadMore();
                }
            }
        }
    }

    public abstract void onLoadMore();
}
