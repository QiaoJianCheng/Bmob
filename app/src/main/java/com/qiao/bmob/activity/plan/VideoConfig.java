package com.qiao.bmob.activity.plan;

import android.support.annotation.IntDef;

import com.qiao.bmob.activity.plan.camera.CameraView;
import com.qiao.bmob.utils.DensityUtil;
import com.qiao.bmob.utils.SharedPrefUtil;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Locale;

/**
 * Created by Qiao on 2017/7/13.
 */

public class VideoConfig {
    public static final int ORIENTATION_0 = 0;
    public static final int ORIENTATION_90 = 90;
    public static final int ORIENTATION_180 = 180;
    public static final int ORIENTATION_270 = 270;

    public static final int SOURCE_CAMERA_BACK = CameraView.CAMERA_FACING_BACK;
    public static final int SOURCE_CAMERA_FRONT = CameraView.CAMERA_FACING_FRONT;
    public static final int SOURCE_SCREEN = 2;

    public static final int DEFINITION_SM = 0;
    public static final int DEFINITION_SD = 1;
    public static final int DEFINITION_HD = 2;

    @IntDef({ORIENTATION_0, ORIENTATION_90, ORIENTATION_180, ORIENTATION_270})
    @Retention(RetentionPolicy.SOURCE)
    @interface Orientation {
    }

    @IntDef({SOURCE_CAMERA_BACK, SOURCE_CAMERA_FRONT, SOURCE_SCREEN})
    @Retention(RetentionPolicy.SOURCE)
    @interface Source {
    }

    @IntDef({DEFINITION_SM, DEFINITION_SD, DEFINITION_HD})
    @Retention(RetentionPolicy.SOURCE)
    @interface Definition {
    }

    public static final String KEY_CONFIG_HD = "KEY_CONFIG_HD";
    public static final String KEY_CONFIG_SD = "KEY_CONFIG_SD";
    public static final String KEY_CONFIG_SM = "KEY_CONFIG_SM";

    public static final String CONFIG_HD_DEFAULT = "1920,1080,1500,30";
    public static final String CONFIG_SD_DEFAULT = "640,480,300,30";
    public static final String CONFIG_SM_DEFAULT = "320,240,50,30";

    public int width;
    public int height;
    public int fps;
    public int bitrate;
    public byte spsLen;
    public byte ppsLen;
    @Source
    public int source;
    @Orientation
    public int orientation;
    @Definition
    public int definition;

    public static VideoConfig getCameraConfigDefault() {
        String config = SharedPrefUtil.getString(KEY_CONFIG_SM, CONFIG_SM_DEFAULT);
        String[] split = config.split(",");
        VideoConfig videoConfig = new VideoConfig();
        videoConfig.width = Integer.valueOf(split[0]);
        videoConfig.height = Integer.valueOf(split[1]);
        videoConfig.bitrate = Integer.valueOf(split[2]) * 1000 * 4;
        videoConfig.fps = Integer.valueOf(split[3]);
        videoConfig.definition = DEFINITION_SM;
        videoConfig.orientation = ORIENTATION_90;
        return videoConfig;
    }

    public static VideoConfig getCameraConfigSD() {
        String config = SharedPrefUtil.getString(KEY_CONFIG_SD, CONFIG_SD_DEFAULT);
        String[] split = config.split(",");
        VideoConfig videoConfig = new VideoConfig();
        videoConfig.width = Integer.valueOf(split[0]);
        videoConfig.height = Integer.valueOf(split[1]);
        videoConfig.bitrate = Integer.valueOf(split[2]) * 1000 * 4;
        videoConfig.fps = Integer.valueOf(split[3]);
        videoConfig.definition = DEFINITION_SD;
        videoConfig.orientation = ORIENTATION_90;
        return videoConfig;
    }

    public static VideoConfig getCameraConfigHD() {
        String config = SharedPrefUtil.getString(KEY_CONFIG_HD, CONFIG_HD_DEFAULT);
        String[] split = config.split(",");
        VideoConfig videoConfig = new VideoConfig();
        videoConfig.width = Integer.valueOf(split[0]);
        videoConfig.height = Integer.valueOf(split[1]);
        videoConfig.bitrate = Integer.valueOf(split[2]) * 1000 * 4;
        videoConfig.fps = Integer.valueOf(split[3]);
        videoConfig.definition = DEFINITION_HD;
        videoConfig.orientation = ORIENTATION_90;
        return videoConfig;
    }

    public static VideoConfig getScreenConfigOriginal(int ratio) {
        VideoConfig videoConfig = new VideoConfig();
        videoConfig.width = DensityUtil.getScreenWidth();
        videoConfig.height = DensityUtil.getScreenHeight();
        videoConfig.fps = 30;
        videoConfig.bitrate = videoConfig.width * videoConfig.height * ratio;
        videoConfig.source = SOURCE_SCREEN;
        return videoConfig;
    }

    public static VideoConfig getScreenConfigHalf(int ratio) {
        VideoConfig videoConfig = new VideoConfig();
        videoConfig.width = DensityUtil.getScreenWidth() / 2;
        videoConfig.height = DensityUtil.getScreenHeight() / 2;
        videoConfig.fps = 30;
        videoConfig.bitrate = videoConfig.width * videoConfig.height * ratio;
        videoConfig.source = SOURCE_SCREEN;
        return videoConfig;
    }

    public static VideoConfig getScreenConfigQuarter(int ratio) {
        VideoConfig videoConfig = new VideoConfig();
        videoConfig.width = DensityUtil.getScreenWidth() / 4;
        videoConfig.height = DensityUtil.getScreenHeight() / 4;
        videoConfig.fps = 30;
        videoConfig.bitrate = videoConfig.width * videoConfig.height * ratio;
        videoConfig.source = SOURCE_SCREEN;
        return videoConfig;
    }

    public static void saveHDConfig(String size, int bitrate, int fps) {
        if (size == null) return;
        SharedPrefUtil.putString(KEY_CONFIG_HD, String.format(Locale.CHINA, "%s,%d,%d", size.replace('×', ','), bitrate, fps));
    }

    public static void saveSDConfig(String size, int bitrate, int fps) {
        if (size == null) return;
        SharedPrefUtil.putString(KEY_CONFIG_SD, String.format(Locale.CHINA, "%s,%d,%d", size.replace('×', ','), bitrate, fps));
    }

    public static void saveSMConfig(String size, int bitrate, int fps) {
        if (size == null) return;
        SharedPrefUtil.putString(KEY_CONFIG_SM, String.format(Locale.CHINA, "%s,%d,%d", size.replace('×', ','), bitrate, fps));
    }

    public static void resetAllConfig() {
        SharedPrefUtil.remove(KEY_CONFIG_HD);
        SharedPrefUtil.remove(KEY_CONFIG_SD);
        SharedPrefUtil.remove(KEY_CONFIG_SM);
    }

    @Override
    public boolean equals(Object newConfig) {
        if (newConfig == null) return false;
        if (newConfig instanceof VideoConfig) {
            return ((VideoConfig) newConfig).width == this.width && ((VideoConfig) newConfig).height == this.height
                    && ((VideoConfig) newConfig).fps == this.fps && ((VideoConfig) newConfig).bitrate == this.bitrate
                    && ((VideoConfig) newConfig).source == this.source && ((VideoConfig) newConfig).orientation == this.orientation;
        }
        return false;
    }
}
