package com.qiao.bmob.dialog;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiao.bmob.R;
import com.qiao.bmob.listener.PressEffectTouchListener;
import com.qiao.bmob.utils.DensityUtil;
import com.qiao.bmob.utils.LogUtil;

/**
 * Created by Qiao on 2016/12/18.
 */

public class FingerprintDialog {
    private Dialog mDialog;
    private Context mContext;

    private OnAuthenticateListener mOnAuthenticateListener;
    private TextView common_dialog_title_tv;
    private TextView common_dialog_msg_tv;
    private ImageView common_dialog_msg_iv;
    private TextView common_dialog_cancel_tv;
    private FingerprintUiHelper mFingerprintUiHelper;

    @TargetApi(Build.VERSION_CODES.M)
    public FingerprintDialog(Context context) {
        this.mContext = context;
        builder();
        FingerprintManager fingerprintManager = (FingerprintManager) mContext.getSystemService(Context.FINGERPRINT_SERVICE);
        mFingerprintUiHelper = new FingerprintUiHelper(fingerprintManager, common_dialog_msg_iv, common_dialog_msg_tv, new FingerprintUiHelper.Callback() {
            @Override
            public void onAuthenticated() {
                LogUtil.d("onAuthenticated");
                if (mOnAuthenticateListener != null) {
                    dismiss();
                    mOnAuthenticateListener.onAuthenticated();
                }
            }

            @Override
            public void onError() {
                LogUtil.d("onError");
            }
        });
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
        View view = View.inflate(mContext, R.layout.dialog_fingerprint, null);
        common_dialog_title_tv = view.findViewById(R.id.common_dialog_title_tv);
        common_dialog_msg_tv = view.findViewById(R.id.common_dialog_msg_tv);
        common_dialog_cancel_tv = view.findViewById(R.id.common_dialog_cancel_tv);
        common_dialog_msg_iv = view.findViewById(R.id.common_dialog_msg_iv);

        common_dialog_cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        common_dialog_cancel_tv.setOnTouchListener(new PressEffectTouchListener());

        return view;
    }

    public FingerprintDialog setTitle(String title) {
        common_dialog_title_tv.setText(title);
        return this;
    }

    public FingerprintDialog setMsg(String msg) {
        common_dialog_msg_tv.setText(msg);
        return this;
    }

    public FingerprintDialog setCancelText(String text) {
        common_dialog_cancel_tv.setText(text);
        return this;
    }

    public void show() {
        if (mDialog != null && !mDialog.isShowing()) {
            mFingerprintUiHelper.startListening(null);
            mDialog.show();
        }
    }

    public void dismiss() {
        if (mDialog != null && mDialog.isShowing()) {
            mFingerprintUiHelper.stopListening();
            mDialog.dismiss();
        }
    }

    public FingerprintDialog setCancelable(boolean cancel) {
        if (mDialog != null) {
            mDialog.setCancelable(cancel);
        }
        return this;
    }

    public FingerprintDialog setCanceledOnTouchOutside(boolean cancel) {
        if (mDialog != null) {
            mDialog.setCanceledOnTouchOutside(cancel);
        }
        return this;
    }

    public FingerprintDialog setOnAuthenticateListener(OnAuthenticateListener onAuthenticateListener) {
        this.mOnAuthenticateListener = onAuthenticateListener;
        return this;
    }

    public interface OnAuthenticateListener {
        void onAuthenticated();
    }
}
