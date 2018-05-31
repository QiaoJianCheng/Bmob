package com.qiao.bmob.listener;

import android.text.Editable;
import android.text.TextWatcher;

/**
 * Created by Qiao on 2017/3/15.
 */

public abstract class OnTextChangedListener implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public abstract void onTextChanged(CharSequence s, int start, int before, int count);

    @Override
    public void afterTextChanged(Editable s) {

    }
}
