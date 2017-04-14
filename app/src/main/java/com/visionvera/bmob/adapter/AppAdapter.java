package com.visionvera.bmob.adapter;

import android.content.Context;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.visionvera.bmob.R;
import com.visionvera.bmob.base.BaseRecyclerAdapter;
import com.visionvera.bmob.base.BaseViewHolder;
import com.visionvera.bmob.event.ProgressEvent;
import com.visionvera.bmob.event.RxEvent;
import com.visionvera.bmob.model.AppBean;
import com.visionvera.bmob.utils.LogUtil;

import java.util.List;

/**
 * Created by Qiao on 2017/3/14.
 */

public class AppAdapter extends BaseRecyclerAdapter<AppBean> {
    public AppAdapter(Context context, List<AppBean> list) {
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

        public AppHolder(View itemView) {
            super(itemView);
            item_app_name_tv = (TextView) itemView.findViewById(R.id.item_app_name_tv);
            item_app_switch = (SwitchCompat) itemView.findViewById(R.id.item_app_switch);
            item_app_progress = itemView.findViewById(R.id.item_app_progress);
        }

        @Override
        public void onBindViewHolder(final int position) {
            AppBean bean = mList.get(position);
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
