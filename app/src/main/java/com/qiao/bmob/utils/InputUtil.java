package com.qiao.bmob.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.qiao.bmob.global.App;

/**
 * Created by Qiao on 2016/12/21.
 */

public class InputUtil {
    private static InputMethodManager imm = (InputMethodManager) App.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

    private static boolean isInputActive() {
        return imm.isActive(); // always returns true???
    }

    public static void showInput(View view) {
        view.requestFocus();
        if (view.isFocused()) {
            imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void showInputForce(View view) {
        view.requestFocus();
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static void hideInput(View view) {
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void toggleInput() {
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
