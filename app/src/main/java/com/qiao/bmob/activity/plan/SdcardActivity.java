package com.qiao.bmob.activity.plan;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.view.View;
import android.widget.TextView;

import com.qiao.bmob.R;
import com.qiao.bmob.base.BaseActivity;
import com.qiao.bmob.listener.PressEffectTouchListener;
import com.qiao.bmob.utils.DensityUtil;
import com.qiao.bmob.utils.ResUtil;

import java.lang.reflect.Method;

public class SdcardActivity extends BaseActivity {
    private TextView sdcard_screen_tv;
    private TextView sdcard_storage_tv;

    @Override
    protected void setContentView() {
        setStatusBarColor(R.color.colorThemeRed, false);
        setContentView(R.layout.activity_sdcard);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitleBar(ResUtil.getString(R.string.title_sdcard));

        sdcard_screen_tv = findViewById(R.id.sdcard_screen_tv);
        sdcard_storage_tv = findViewById(R.id.sdcard_storage_tv);

        sdcard_screen_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setScreen();
            }
        });
        sdcard_storage_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setStorage();
            }
        });

        sdcard_screen_tv.setOnTouchListener(new PressEffectTouchListener());
        sdcard_storage_tv.setOnTouchListener(new PressEffectTouchListener());

        setScreen();
        setStorage();
    }

    private void setStorage() {
        String[] storages = getStorages();
        String text = "";
        if (storages != null && storages.length > 0) {
            text += "getVolumePaths:\n\t\t";
            for (String storage : storages) {
                text += storage + ": " + getStorageState(storage) + "\n\t\t";
            }
        }
        text += "\nEnvironment:\n\t\t" + Environment.getExternalStorageDirectory().getAbsolutePath() + ": " + Environment.getExternalStorageState() + "\n\t\t";
        text += Environment.getRootDirectory().getAbsolutePath() + ": " + Environment.getExternalStorageState(Environment.getRootDirectory());
        sdcard_storage_tv.setText(text);
    }

    private void setScreen() {
        int width = getWindowManager().getDefaultDisplay().getWidth();
        int height = getWindowManager().getDefaultDisplay().getHeight();
        sdcard_screen_tv.setText("window size: " + width + "×" + height +
                "\ndisplay metric: " + DensityUtil.getScreenWidth() + "×" + DensityUtil.getScreenHeight() +
                "\nstatus bar: " + DensityUtil.getStatusBarHeight() +
                "\nnavigation bar: " + DensityUtil.getNavigationBarHeight());
    }

    public String[] getStorages() {
        try {
            StorageManager sm = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
            Method getVolumePathsMethod = StorageManager.class.getMethod(
                    "getVolumePaths");
            return (String[]) getVolumePathsMethod.invoke(sm);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public String getStorageState(String path) {
        try {
            StorageManager sm = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
            Method getVolumeStateMethod = StorageManager.class.getMethod(
                    "getVolumeState", String.class);
            return (String) getVolumeStateMethod.invoke(sm, path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
