package com.visionvera.bmob.activity.user;

import android.graphics.drawable.Drawable;
import android.os.Build;
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
import com.visionvera.bmob.dialog.FingerprintDialog;
import com.visionvera.bmob.global.UserHelper;
import com.visionvera.bmob.listener.PressEffectTouchListener;
import com.visionvera.bmob.model.UsersBean;
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
    private View login_fingerprint_tv;

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
        login_fingerprint_tv = findViewById(R.id.login_fingerprint_tv);

        login_fingerprint_tv.setVisibility(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? View.VISIBLE : View.GONE);

        login_account_et.setText(UserHelper.getUserName());
        login_password_et.setText(UserHelper.getPassword());
        login_account_et.setOnFocusChangeListener((v, hasFocus) -> login_account_clear_iv.setVisibility(hasFocus ? View.VISIBLE : View.GONE));
        login_password_et.setOnFocusChangeListener((v, hasFocus) -> login_password_clear_iv.setVisibility(hasFocus ? View.VISIBLE : View.GONE));
        login_password_et.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                onClick(login_confirm_rl);
            }
            return false;
        });
        login_account_et.setSelection(login_account_et.getText().length());

        login_account_clear_iv.setOnClickListener(this);
        login_password_clear_iv.setOnClickListener(this);
        login_confirm_rl.setOnClickListener(this);
        login_forget_tv.setOnClickListener(this);
        login_register_tv.setOnClickListener(this);
        login_fingerprint_tv.setOnClickListener(this);

        login_account_clear_iv.setOnTouchListener(new PressEffectTouchListener());
        login_password_clear_iv.setOnTouchListener(new PressEffectTouchListener());

        Drawable drawable = ResUtil.getDrawable(R.drawable.selector_show_password_cb);
        int x64 = (int) ResUtil.getDimen(R.dimen.x50);
        int x42 = (int) ResUtil.getDimen(R.dimen.x30);
        drawable.setBounds(0, 0, x64, x42);
        login_show_password_cb.setCompoundDrawables(null, null, drawable, null);
        login_show_password_cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
            login_password_et.setInputType(InputType.TYPE_CLASS_TEXT | (isChecked ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_TEXT_VARIATION_PASSWORD));
            login_password_et.setSelection(login_password_et.getText().length());
        });
    }

    @Override
    protected void loadData(boolean showLoading) {
        super.loadData(showLoading);
    }

    private void showFingerprintDialog() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            new FingerprintDialog(this)
                    .setTitle("指纹登录")
                    .setCanceledOnTouchOutside(false)
                    .setOnAuthenticateListener(() -> {
                        IntentUtil.toHomeActivity(LoginActivity.this);
                        finish();
                    })
                    .show();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_confirm_rl:
                String account = login_account_et.getText().toString();
                if (TextUtil.isEmpty(account)) {
                    ToastUtil.warnToast(getString(R.string.toast_login_empty_account));
                    return;
                }
                final String password = login_password_et.getText().toString();
                if (TextUtil.isEmpty(password)) {
                    ToastUtil.warnToast(getString(R.string.toast_login_empty_password));
                    return;
                }
                login_progress.setVisibility(View.VISIBLE);
                login_confirm_rl.setClickable(false);
                NetworkRequest.getLogin(LoginActivity.this, account, password, new ResponseSubscriber<UsersBean.UserBean>() {
                    @Override
                    public void onSuccess(UsersBean.UserBean userBean) {
                        login_progress.setVisibility(View.GONE);
                        login_confirm_rl.setClickable(true);
                        ToastUtil.showToast(getString(R.string.toast_login_success));
                        UserHelper.saveUser(userBean);
                        UserHelper.savePassword(password);
                        UserHelper.saveIsLogin(true);
                        IntentUtil.toHomeActivity(LoginActivity.this);
                        finish();
                    }

                    @Override
                    public void onFailure(int code, String error) {
                        ToastUtil.warnToast(error);
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
            case R.id.login_fingerprint_tv:
                showFingerprintDialog();
                break;
        }
    }

}
