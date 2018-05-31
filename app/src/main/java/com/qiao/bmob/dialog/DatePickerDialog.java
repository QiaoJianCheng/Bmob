package com.qiao.bmob.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.TextView;

import com.qiao.bmob.R;
import com.qiao.bmob.listener.PressEffectTouchListener;
import com.qiao.bmob.utils.DensityUtil;

import java.util.Calendar;

/**
 * Created by Qiao on 2016/11/24.
 */

public class DatePickerDialog {
    public static final String TAG = "DatePickerDialog";

    private final Activity mActivity;
    private Dialog mDialog;

    private TextView dialog_picker_cancel_tv;
    private TextView dialog_picker_confirm_tv;
    private DatePicker dialog_picker_date;
    private OnConfirmClickListener mOnConfirmClickListener;

    public DatePickerDialog(Activity activity) {
        this.mActivity = activity;
        builder();
    }

    private DatePickerDialog builder() {
        View view = getContentView();
        mDialog = new Dialog(mActivity, R.style.DrawerDialogStyle);
        mDialog.setContentView(view);
        Window dialogWindow = mDialog.getWindow();
        if (dialogWindow != null) {
            dialogWindow.setGravity(Gravity.BOTTOM);
            WindowManager.LayoutParams lp = dialogWindow.getAttributes();
            lp.width = DensityUtil.getScreenWidth();
            dialogWindow.setAttributes(lp);
        }
        return this;
    }

    @NonNull
    private View getContentView() {
        View view = View.inflate(mActivity, R.layout.dialog_date_time_picker, null);
        this.dialog_picker_date = view.findViewById(R.id.dialog_picker_date);
        this.dialog_picker_date.setDescendantFocusability(DatePicker.FOCUS_BLOCK_DESCENDANTS);
        this.dialog_picker_confirm_tv = view.findViewById(R.id.dialog_picker_confirm_tv);
        this.dialog_picker_cancel_tv = view.findViewById(R.id.dialog_picker_cancel_tv);
        dialog_picker_confirm_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnConfirmClickListener != null) {
                    mOnConfirmClickListener.onConfirm(dialog_picker_date.getYear(), dialog_picker_date.getMonth() + 1, dialog_picker_date.getDayOfMonth());
                }
                dismiss();
            }
        });
        dialog_picker_cancel_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        dialog_picker_confirm_tv.setOnTouchListener(new PressEffectTouchListener());
        dialog_picker_cancel_tv.setOnTouchListener(new PressEffectTouchListener());

        return view;
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

    public DatePickerDialog setCanceledOnTouchOutside(boolean outSideTouchCancelable) {
        if (mDialog != null) {
            mDialog.setCanceledOnTouchOutside(outSideTouchCancelable);
        }
        return this;
    }

    public DatePickerDialog setCurrentDate(int year, int month, int day) {
        dialog_picker_date.updateDate(year, month - 1, day);
        return this;
    }

    public DatePickerDialog setMinDate(long minMillis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(minMillis);
        dialog_picker_date.setMinDate(calendar.getTimeInMillis());
        return this;
    }

    public DatePickerDialog setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        this.mOnConfirmClickListener = onConfirmClickListener;
        return this;
    }

    public interface OnConfirmClickListener {
        void onConfirm(int year, int month, int day);
    }

}
