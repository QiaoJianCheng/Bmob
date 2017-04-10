package com.visionvera.bmob.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Qiao on 2016/12/16.
 */

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
    }

    public abstract void onBindViewHolder(int position);

}
