package com.visionvera.bmob.listener;

import android.text.Editable;
import android.text.TextWatcher;

import com.visionvera.bmob.model.MoodsBean;
import com.visionvera.bmob.model.UsersBean;

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
