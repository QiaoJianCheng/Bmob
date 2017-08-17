package com.visionvera.bmob.activity.plan;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.visionvera.bmob.R;
import com.visionvera.bmob.base.BaseActivity;
import com.visionvera.bmob.utils.ResUtil;

public class ColorFilterActivity extends BaseActivity {
    private ImageView color_filter_iv;
    private Spinner color_filter_spinner;
    private SeekBar color_filter_alpha_seek;
    private SeekBar color_filter_red_seek;
    private SeekBar color_filter_green_seek;
    private SeekBar color_filter_blue_seek;
    private CheckBox color_filter_cb;
    private PorterDuff.Mode mModes[];
    private PorterDuff.Mode mCurrentMode = PorterDuff.Mode.CLEAR;
    private int mCurrentColor;

    @Override
    protected void setContentView() {
        setStatusBarColor(R.color.colorThemeRed, false);
        setContentView(R.layout.activity_color_filter);
    }

    @Override
    protected void initData() {
        mModes = PorterDuff.Mode.values();
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitleBar(getString(R.string.title_color_filter));

        color_filter_iv = (ImageView) findViewById(R.id.color_filter_iv);
        color_filter_spinner = (Spinner) findViewById(R.id.color_filter_spinner);
        color_filter_alpha_seek = (SeekBar) findViewById(R.id.color_filter_alpha_seek);
        color_filter_red_seek = (SeekBar) findViewById(R.id.color_filter_red_seek);
        color_filter_green_seek = (SeekBar) findViewById(R.id.color_filter_green_seek);
        color_filter_blue_seek = (SeekBar) findViewById(R.id.color_filter_blue_seek);
        color_filter_cb = (CheckBox) findViewById(R.id.color_filter_cb);

        color_filter_spinner.setPopupBackgroundResource(R.color.colorWhite);
        color_filter_spinner.setDropDownVerticalOffset((int) ResUtil.getDimen(R.dimen.y120));
        color_filter_spinner.setAdapter(new ArrayAdapter<>(getApplicationContext(), R.layout.item_spinner, R.id.item_spinner_text, mModes));
        color_filter_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mCurrentMode = mModes[position];
                color_filter_iv.setColorFilter(mCurrentColor, mCurrentMode);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        color_filter_alpha_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mCurrentColor = Color.argb(color_filter_alpha_seek.getProgress(),
                        color_filter_red_seek.getProgress(),
                        color_filter_green_seek.getProgress(),
                        color_filter_blue_seek.getProgress());
                color_filter_iv.setColorFilter(mCurrentColor, mCurrentMode);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        color_filter_red_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (color_filter_cb.isChecked()) {
                    color_filter_green_seek.setProgress(progress);
                    color_filter_blue_seek.setProgress(progress);
                }
                mCurrentColor = Color.argb(color_filter_alpha_seek.getProgress(),
                        color_filter_red_seek.getProgress(),
                        color_filter_green_seek.getProgress(),
                        color_filter_blue_seek.getProgress());
                color_filter_iv.setColorFilter(mCurrentColor, mCurrentMode);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        color_filter_green_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (color_filter_cb.isChecked()) {
                    color_filter_red_seek.setProgress(progress);
                    color_filter_blue_seek.setProgress(progress);
                } else {
                    mCurrentColor = Color.argb(color_filter_alpha_seek.getProgress(),
                            color_filter_red_seek.getProgress(),
                            color_filter_green_seek.getProgress(),
                            color_filter_blue_seek.getProgress());
                    color_filter_iv.setColorFilter(mCurrentColor, mCurrentMode);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        color_filter_blue_seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (color_filter_cb.isChecked()) {
                    color_filter_red_seek.setProgress(progress);
                    color_filter_green_seek.setProgress(progress);
                } else {
                    mCurrentColor = Color.argb(color_filter_alpha_seek.getProgress(),
                            color_filter_red_seek.getProgress(),
                            color_filter_green_seek.getProgress(),
                            color_filter_blue_seek.getProgress());
                    color_filter_iv.setColorFilter(mCurrentColor, mCurrentMode);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
