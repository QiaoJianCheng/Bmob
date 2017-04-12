package com.visionvera.bmob.event;

/**
 * Created by Qiao on 2017/4/11.
 */

public class ProgressEvent extends RxEvent {
    public int position;
    public boolean showLoading;

    public ProgressEvent(int position, boolean showLoading) {
        this.position = position;
        this.showLoading = showLoading;
    }
}
