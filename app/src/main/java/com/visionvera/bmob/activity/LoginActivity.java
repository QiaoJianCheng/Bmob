package com.visionvera.bmob.activity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.visionvera.bmob.R;
import com.visionvera.bmob.base.BaseActivity;
import com.visionvera.bmob.event.RxBus;
import com.visionvera.bmob.global.App;
import com.visionvera.bmob.global.UserHelper;
import com.visionvera.bmob.listener.PressEffectTouchListener;
import com.visionvera.bmob.model.UserBean;
import com.visionvera.bmob.net.NetworkRequest;
import com.visionvera.bmob.net.ResponseSubscriber;
import com.visionvera.bmob.utils.IntentUtil;
import com.visionvera.bmob.utils.ResUtil;
import com.visionvera.bmob.utils.TextUtil;
import com.visionvera.bmob.utils.ToastUtil;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private EditText login_account_et;
    private EditText login_password_et;
    private View login_account_clear_iv;
    private View login_password_clear_iv;
    private CheckBox login_show_password_cb;
    private View login_confirm_rl;
    private View login_progress;
    private View login_forget_tv;
    private View login_register_tv;

    @Override
    protected void setContentView() {
        setStatusBarColor(R.color.colorWhite, true);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initData() {
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        login_account_et = (EditText) findViewById(R.id.login_account_et);
        login_password_et = (EditText) findViewById(R.id.login_password_et);
        login_account_clear_iv = findViewById(R.id.login_account_clear_iv);
        login_password_clear_iv = findViewById(R.id.login_password_clear_iv);
        login_show_password_cb = (CheckBox) findViewById(R.id.login_show_password_cb);
        login_confirm_rl = findViewById(R.id.login_confirm_rl);
        login_progress = findViewById(R.id.login_progress);
        login_forget_tv = findViewById(R.id.login_forget_tv);
        login_register_tv = findViewById(R.id.login_register_tv);

        login_account_et.setText(UserHelper.getUserName());
        login_password_et.setText(UserHelper.getPassword());
        login_account_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                login_account_clear_iv.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
            }
        });
        login_password_et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                login_password_clear_iv.setVisibility(hasFocus ? View.VISIBLE : View.GONE);
            }
        });
        login_password_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    onClick(login_confirm_rl);
                }
                return false;
            }
        });
        login_account_et.setSelection(login_account_et.getText().length());

        login_account_clear_iv.setOnClickListener(this);
        login_password_clear_iv.setOnClickListener(this);
        login_confirm_rl.setOnClickListener(this);
        login_forget_tv.setOnClickListener(this);
        login_register_tv.setOnClickListener(this);

        login_account_clear_iv.setOnTouchListener(new PressEffectTouchListener());
        login_password_clear_iv.setOnTouchListener(new PressEffectTouchListener());

        Drawable drawable = ResUtil.getDrawable(R.drawable.selector_show_password_cb);
        int x64 = (int) ResUtil.getDimen(R.dimen.x50);
        int x42 = (int) ResUtil.getDimen(R.dimen.x30);
        drawable.setBounds(0, 0, x64, x42);
        login_show_password_cb.setCompoundDrawables(null, null, drawable, null);
        login_show_password_cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                login_password_et.setInputType(InputType.TYPE_CLASS_TEXT | (isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_TEXT_VARIATION_PASSWORD));
                login_password_et.setSelection(login_password_et.getText().length());
            }
        });
    }

    @Override
    protected void loadData(boolean showLoading) {
        super.loadData(showLoading);
        if (UserHelper.isLogin()) {
            App.getInstance().postDelay(new Runnable() {
                @Override
                public void run() {
                    onClick(login_confirm_rl);
                }
            }, 1000);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_confirm_rl:
                String account = login_account_et.getText().toString();
                if (TextUtil.isEmpty(account)) {
                    ToastUtil.showToast(getString(R.string.toast_login_empty_account));
                    return;
                }
                final String password = login_password_et.getText().toString();
                if (TextUtil.isEmpty(password)) {
                    ToastUtil.showToast(getString(R.string.toast_login_empty_password));
                    return;
                }
                login_progress.setVisibility(View.VISIBLE);
                login_confirm_rl.setClickable(false);
                NetworkRequest.getLogin(LoginActivity.this, account, password, new ResponseSubscriber<UserBean>() {
                    @Override
                    public void onSuccess(UserBean userBean) {
                        login_progress.setVisibility(View.GONE);
                        login_confirm_rl.setClickable(true);
                        ToastUtil.showToast(getString(R.string.toast_login_success));
                        UserHelper.saveUser(userBean);
                        UserHelper.savePassword(password);
                        UserHelper.saveIsLogin(true);
                        IntentUtil.toMainActivity2(LoginActivity.this);
//                        IntentUtil.toMainActivity(LoginActivity.this);
                        finish();
                    }

                    @Override
                    public void onFailure(int code, String error) {
                        ToastUtil.networkFailure(error);
                        login_progress.setVisibility(View.GONE);
                        login_confirm_rl.setClickable(true);
                    }
                });
                break;
            case R.id.login_forget_tv:
                break;
            case R.id.login_register_tv:
                IntentUtil.toRegisterActivity(LoginActivity.this);
                break;
            case R.id.login_account_clear_iv:
                login_account_et.getText().clear();
                break;
            case R.id.login_password_clear_iv:
                login_password_et.getText().clear();
                break;
        }
    }

}
