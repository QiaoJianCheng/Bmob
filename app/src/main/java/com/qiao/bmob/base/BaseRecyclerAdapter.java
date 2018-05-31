package com.qiao.bmob.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qiao.bmob.R;

import java.util.List;

/**
 * Created by Qiao on 2016/12/16.
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseViewHolder> {
    public static final String TAG = BaseRecyclerAdapter.class.getSimpleName();
    private static final int VIEW_TYPE_FOOTER = 0x1001;
    private static final int VIEW_TYPE_ITEM = 0x1002;

    private static final int FOOTER_STATE_LOAD_COMPLETED = 10;
    private static final int FOOTER_STATE_LOADING = 11;
    private static final int FOOTER_STATE_NO_MORE_DATA = 12;
    private static final int FOOTER_STATE_FAILURE = 13;

    public List<T> mList;
    protected Context mContext;
    private OnItemClickListener mOnItemClickListener;
    private int mFooterState = FOOTER_STATE_LOAD_COMPLETED;

    public BaseRecyclerAdapter(Context context, List<T> list) {
        mContext = context;
        mList = list;
    }

    @Override
    public final int getItemViewType(int position) {
        return mFooterState != FOOTER_STATE_LOAD_COMPLETED && position == getItemCount() - 1 ? VIEW_TYPE_FOOTER : getItemType(position);
    }

    protected int getItemType(int position) {
        return VIEW_TYPE_ITEM;
    }

    @Override
    public final BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_FOOTER) {
            return new FooterHolder(View.inflate(mContext, R.layout.footer_recyclerview, null));
        } else {
            return onCreateHolder(parent, viewType);
        }
    }

    protected abstract BaseViewHolder onCreateHolder(ViewGroup parent, int viewType);

    @Override
    public final void onBindViewHolder(BaseViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.onBindViewHolder(position);
        if (getItemViewType(position) != VIEW_TYPE_FOOTER) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(position);
                    }
                }
            });
        } else {
            holder.itemView.setOnClickListener(null);
        }
    }

    private class FooterHolder extends BaseViewHolder {
        private ProgressBar footer_pb;
        private TextView footer_text_tv;

        FooterHolder(View itemView) {
            super(itemView);
            footer_pb = itemView.findViewById(R.id.footer_pb);
            footer_text_tv = itemView.findViewById(R.id.footer_text_tv);
        }

        @Override
        public void onBindViewHolder(int position) {
            if (mFooterState == FOOTER_STATE_LOADING) {
                footer_pb.setVisibility(View.VISIBLE);
                footer_text_tv.setText("加载中...");
            } else if (mFooterState == FOOTER_STATE_FAILURE) {
                footer_pb.setVisibility(View.GONE);
                footer_text_tv.setText("网络异常，加载失败");
            } else if (mFooterState == FOOTER_STATE_NO_MORE_DATA) {
                footer_pb.setVisibility(View.GONE);
                footer_text_tv.setText("没有更多数据了");
            }
        }
    }

    @Override
    public final int getItemCount() {
        return mList == null ? 0 : mFooterState != FOOTER_STATE_LOAD_COMPLETED ? mList.size() + 1 : mList.size();
    }

    public final void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        mOnItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public final void footerLoading() {
        mFooterState = FOOTER_STATE_LOADING;
        notifyDataSetChanged();
    }

    public final void footerNoMoreData() {
        mFooterState = FOOTER_STATE_NO_MORE_DATA;
        notifyDataSetChanged();
    }

    public final void footerFailed() {
        mFooterState = FOOTER_STATE_FAILURE;
        notifyDataSetChanged();
    }

    public final void footerLoadCompleted() {
        mFooterState = FOOTER_STATE_LOAD_COMPLETED;
        notifyDataSetChanged();
    }

    public final boolean isLoading() {
        return mFooterState == FOOTER_STATE_LOADING;
    }

    public final int getListSize() {
        return mList == null ? 0 : mList.size();
    }
}
