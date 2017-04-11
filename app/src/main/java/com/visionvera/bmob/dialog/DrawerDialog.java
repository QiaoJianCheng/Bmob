package com.visionvera.bmob.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.visionvera.bmob.R;
import com.visionvera.bmob.utils.DensityUtil;
import com.visionvera.bmob.utils.ResUtil;

/**
 * Created by Qiao on 2016/12/21.
 */

public class DrawerDialog {

    private Dialog mDialog;
    private Context mContext;

    private OnItemClickedListener mOnItemClickedListener;
    private LinearLayout dialog_drawer_ll;

    public DrawerDialog(Context context) {
        this.mContext = context;
        builder();
    }

    private void builder() {
        mDialog = new Dialog(mContext, R.style.DrawerDialogStyle);
        Window window = mDialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams params = window.getAttributes();
            params.width = DensityUtil.getScreenWidth();
            params.height = DensityUtil.getScreenHeight();
            window.setGravity(Gravity.BOTTOM | Gravity.FILL_HORIZONTAL);
            params.x = 0;
            params.y = 0;
            window.setAttributes(params);
        }
        mDialog.setContentView(getContentView());
    }

    @NonNull
    private View getContentView() {
        View view = View.inflate(mContext, R.layout.dialog_drawer, null);
        dialog_drawer_ll = (LinearLayout) view.findViewById(R.id.dialog_drawer_ll);
        return view;
    }

    public DrawerDialog addDrawer(String text, boolean selected) {
        View inflate = View.inflate(mContext, R.layout.item_drawer, null);
        dialog_drawer_ll.addView(inflate);
        TextView textView = (TextView) inflate.findViewById(R.id.item_drawer_text_tv);
        final int position = dialog_drawer_ll.getChildCount();
        View image = inflate.findViewById(R.id.item_drawer_iv);
        textView.setText(text);
        image.setVisibility(selected ? View.VISIBLE : View.INVISIBLE);
        textView.setTextColor(ResUtil.getColor(selected ? R.color.colorThemeRed : R.color.colorTextDark));
        inflate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickedListener != null) {
                    mOnItemClickedListener.onItemClick(position);
                }
                dismiss();
            }
        });
        return this;
    }

    public DrawerDialog addDrawer(String text) {
        addDrawer(text, false);
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

    public DrawerDialog setCancelable(boolean cancel) {
        if (mDialog != null) {
            mDialog.setCancelable(cancel);
        }
        return this;
    }

    public DrawerDialog setCanceledOnTouchOutside(boolean cancel) {
        if (mDialog != null) {
            mDialog.setCanceledOnTouchOutside(cancel);
        }
        return this;
    }

    public DrawerDialog setOnItemClickedListener(OnItemClickedListener onItemClickedListener) {
        this.mOnItemClickedListener = onItemClickedListener;
        return this;
    }

    public interface OnItemClickedListener {
        void onItemClick(int position);
    }
}
