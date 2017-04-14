package com.visionvera.bmob.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.visionvera.bmob.R;
import com.visionvera.bmob.base.BaseRecyclerAdapter;
import com.visionvera.bmob.base.BaseViewHolder;
import com.visionvera.bmob.model.CrashBean;

import java.util.List;

/**
 * Created by Qiao on 2017/4/12.
 */

public class AppDetailAdapter extends BaseRecyclerAdapter<CrashBean> {
    public AppDetailAdapter(Context context, List<CrashBean> list) {
        super(context, list);
    }

    @Override
    protected BaseViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new CrashHolder(View.inflate(mContext, R.layout.item_crash, null));
    }

    private class CrashHolder extends BaseViewHolder {

        private TextView item_crash_info_tv;

        public CrashHolder(View itemView) {
            super(itemView);
            item_crash_info_tv = (TextView) itemView.findViewById(R.id.item_crash_info_tv);
        }

        @Override
        public void onBindViewHolder(int position) {
            item_crash_info_tv.setText(mList.get(position).crash_info);
        }
    }
}
