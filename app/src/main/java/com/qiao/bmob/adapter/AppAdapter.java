package com.qiao.bmob.adapter;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.qiao.bmob.R;
import com.qiao.bmob.base.BaseRecyclerAdapter;
import com.qiao.bmob.base.BaseViewHolder;
import com.qiao.bmob.event.ProgressEvent;
import com.qiao.bmob.event.RxEvent;
import com.qiao.bmob.model.AppsBean;
import com.qiao.bmob.utils.LogUtil;

import java.util.List;

/**
 * Created by Qiao on 2017/3/14.
 */

public class AppAdapter extends BaseRecyclerAdapter<AppsBean.AppBean> {
    public AppAdapter(Context context, List<AppsBean.AppBean> list) {
        super(context, list);
    }

    @Override
    protected BaseViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new AppHolder(View.inflate(mContext, R.layout.item_app, null));
    }

    private class AppHolder extends BaseViewHolder {
        private TextView item_app_name_tv;
        private SwitchCompat item_app_switch;
        private View item_app_progress;

        private AppHolder(View itemView) {
            super(itemView);
            item_app_name_tv = itemView.findViewById(R.id.item_app_name_tv);
            item_app_switch = itemView.findViewById(R.id.item_app_switch);
            item_app_progress = itemView.findViewById(R.id.item_app_progress);
        }

        @Override
        public void onBindViewHolder(final int position) {
            AppsBean.AppBean bean = mList.get(position);
            item_app_name_tv.setText(bean.app_name);
            item_app_switch.setChecked(bean.bang);
            item_app_switch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    LogUtil.d(TAG, "switch: " + isChecked);
                    if (mOnCheckChangedListener != null) {
                        mOnCheckChangedListener.checkChanged(buttonView, position, isChecked);
                    }
                }
            });
        }

        @Override
        public void onEventMainThread(RxEvent rxEvent) {
            if (rxEvent instanceof ProgressEvent) {
                if (((ProgressEvent) rxEvent).position == getAdapterPosition()) {
                    item_app_progress.setVisibility(((ProgressEvent) rxEvent).showLoading ? View.VISIBLE : View.INVISIBLE);
                }
            }
        }
    }

    public interface OnCheckChangedListener {
        void checkChanged(CompoundButton buttonView, int position, boolean isChecked);
    }

    private OnCheckChangedListener mOnCheckChangedListener;

    public void setOnCheckChangedListener(OnCheckChangedListener onCheckChangedListener) {
        mOnCheckChangedListener = onCheckChangedListener;
    }
}
