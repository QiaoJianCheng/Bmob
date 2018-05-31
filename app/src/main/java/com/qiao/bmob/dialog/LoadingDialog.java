package com.qiao.bmob.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.qiao.bmob.R;
import com.qiao.bmob.utils.DensityUtil;

/**
 * Created by Qiao on 2016/12/18.
 */

public class LoadingDialog {
    private Dialog mDialog;
    private Context mContext;


    public LoadingDialog(Context context) {
        this.mContext = context;
        builder();
    }

    private void builder() {
        mDialog = new Dialog(mContext, R.style.CommonDialogStyle);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = DensityUtil.getScreenWidth();
            params.height = DensityUtil.getScreenHeight();
            window.setGravity(Gravity.CENTER);
            params.x = 0;
            params.y = 0;
            window.setAttributes(params);
        }
        mDialog.setContentView(getContentView());
    }

    @NonNull
    private View getContentView() {
        return View.inflate(mContext, R.layout.dialog_loading, null);
    }

    public void show() {
        if (mDialog != null && !mDialog.isShowing()) {
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }

    public LoadingDialog setCancelable(boolean cancel) {
        if (mDialog != null) {
            mDialog.setCancelable(cancel);
        }
        return this;
    }

    public LoadingDialog setCanceledOnTouchOutside(boolean cancel) {
        if (mDialog != null) {
            mDialog.setCanceledOnTouchOutside(cancel);
        }
        return this;
    }
}
