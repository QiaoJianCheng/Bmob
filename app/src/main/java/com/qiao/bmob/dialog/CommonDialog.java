package com.qiao.bmob.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.qiao.bmob.R;
import com.qiao.bmob.listener.PressEffectTouchListener;
import com.qiao.bmob.utils.DensityUtil;

/**
 * Created by Qiao on 2016/12/18.
 */

public class CommonDialog {
    private Dialog mDialog;
    private Context mContext;

    private OnButtonClickListener mOnButtonClickListener;
    private TextView common_dialog_title_tv;
    private TextView common_dialog_msg_tv;
    private TextView common_dialog_confirm_tv;
    private TextView common_dialog_cancel_tv;

    public CommonDialog(Context context) {
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
        View view = View.inflate(mContext, R.layout.dialog_common, null);
        common_dialog_title_tv = view.findViewById(R.id.common_dialog_title_tv);
        common_dialog_msg_tv = view.findViewById(R.id.common_dialog_msg_tv);
        common_dialog_confirm_tv = view.findViewById(R.id.common_dialog_confirm_tv);
        common_dialog_cancel_tv = view.findViewById(R.id.common_dialog_cancel_tv);

        common_dialog_confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnButtonClickListener != null) {
                    mOnButtonClickListener.onConfirm();
                }
                dismiss();
            }
        });
        common_dialog_cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnButtonClickListener != null) {
                    mOnButtonClickListener.onCancel();
                }
                dismiss();
            }
        });
        common_dialog_confirm_tv.setOnTouchListener(new PressEffectTouchListener());
        common_dialog_cancel_tv.setOnTouchListener(new PressEffectTouchListener());

        return view;
    }

    public CommonDialog setTitle(String title) {
        common_dialog_title_tv.setText(title);
        return this;
    }

    public CommonDialog setMsg(String msg) {
        common_dialog_msg_tv.setText(msg);
        return this;
    }

    public CommonDialog setConfirmText(String text) {
        common_dialog_confirm_tv.setText(text);
        return this;
    }

    public CommonDialog setCancelText(String text) {
        common_dialog_cancel_tv.setText(text);
        return this;
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

    public CommonDialog setCancelable(boolean cancel) {
        if (mDialog != null) {
            mDialog.setCancelable(cancel);
        }
        return this;
    }

    public CommonDialog setCanceledOnTouchOutside(boolean cancel) {
        if (mDialog != null) {
            mDialog.setCanceledOnTouchOutside(cancel);
        }
        return this;
    }

    public CommonDialog setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.mOnButtonClickListener = onButtonClickListener;
        return this;
    }

    public interface OnButtonClickListener {
        void onConfirm();

        void onCancel();
    }
}
