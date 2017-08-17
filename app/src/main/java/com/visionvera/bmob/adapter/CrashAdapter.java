package com.visionvera.bmob.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.visionvera.bmob.R;
import com.visionvera.bmob.base.BaseRecyclerAdapter;
import com.visionvera.bmob.base.BaseViewHolder;
import com.visionvera.bmob.model.CrashesBean;

import java.util.List;

/**
 * Created by Qiao on 2017/4/12.
 */

public class CrashAdapter extends BaseRecyclerAdapter<CrashesBean.CrashBean> {
    public CrashAdapter(Context context, List<CrashesBean.CrashBean> list) {
        super(context, list);
    }

    @Override
    protected BaseViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new CrashHolder(View.inflate(mContext, R.layout.item_crash, null));
    }

    private class CrashHolder extends BaseViewHolder {
        private TextView item_crash_model_tv;
        private TextView item_crash_api_tv;
        private TextView item_crash_version_tv;
        private TextView item_crash_time_tv;

        public CrashHolder(View itemView) {
            super(itemView);
            item_crash_model_tv = (TextView) itemView.findViewById(R.id.item_crash_model_tv);
            item_crash_api_tv = (TextView) itemView.findViewById(R.id.item_crash_api_tv);
            item_crash_version_tv = (TextView) itemView.findViewById(R.id.item_crash_version_tv);
            item_crash_time_tv = (TextView) itemView.findViewById(R.id.item_crash_time_tv);
        }

        @Override
        public void onBindViewHolder(int position) {
            CrashesBean.CrashBean bean = mList.get(position);
            item_crash_model_tv.setText(bean.model);
            item_crash_api_tv.setText(bean.api_level);
            item_crash_version_tv.setText(bean.version_name);
            item_crash_time_tv.setText(bean.updatedAt);
        }
    }
}
