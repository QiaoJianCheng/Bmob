package com.visionvera.bmob.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.visionvera.bmob.R;
import com.visionvera.bmob.base.BaseFragment;
import com.visionvera.bmob.utils.IntentUtil;

/**
 * Created by Qiao on 2017/4/13.
 */

public class PlanTabFragment extends BaseFragment {
    private TextView plan_sensor_tv;

    @NonNull
    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_plan, container, false);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        plan_sensor_tv = (TextView) view.findViewById(R.id.plan_sensor_tv);
        plan_sensor_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.toSensorActivity(getActivity());
            }
        });
    }
}