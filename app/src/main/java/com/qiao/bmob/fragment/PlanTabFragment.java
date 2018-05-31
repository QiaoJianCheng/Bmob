package com.qiao.bmob.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qiao.bmob.R;
import com.qiao.bmob.base.BaseFragment;
import com.qiao.bmob.utils.IntentUtil;

/**
 * Created by Qiao on 2017/4/13.
 */

public class PlanTabFragment extends BaseFragment {
    private TextView plan_sensor_tv;
    private TextView plan_sdcard_tv;
    private TextView plan_color_filter_tv;
    private TextView plan_camera_tv;
    private TextView plan_codec_tv;
    private TextView plan_kotlin_tv;

    @NonNull
    @Override
    protected View inflateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_tab_plan, container, false);
    }

    @Override
    protected void initViews(View view, Bundle savedInstanceState) {
        plan_sensor_tv = view.findViewById(R.id.plan_sensor_tv);
        plan_sdcard_tv = view.findViewById(R.id.plan_sdcard_tv);
        plan_color_filter_tv = view.findViewById(R.id.plan_color_filter_tv);
        plan_camera_tv = view.findViewById(R.id.plan_camera_tv);
        plan_codec_tv = view.findViewById(R.id.plan_codec_tv);
        plan_kotlin_tv = view.findViewById(R.id.plan_kotlin_tv);
        plan_sensor_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.toSensorActivity(getActivity());
            }
        });
        plan_sdcard_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.toSdcardActivity(getActivity());
            }
        });
        plan_color_filter_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.toColorFilterActivity(getActivity());
            }
        });
        plan_camera_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.toCameraActivity(getActivity());
            }
        });
        plan_codec_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtil.toCodecActivity(getActivity());
            }
        });
        plan_kotlin_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void loadData(boolean showLoading) {
        super.loadData(showLoading);
        showContentView();
    }
}
