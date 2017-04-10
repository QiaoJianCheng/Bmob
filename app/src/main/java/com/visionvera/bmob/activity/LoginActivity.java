package com.visionvera.bmob.activity;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.Button;

import com.visionvera.bmob.R;
import com.visionvera.bmob.base.BaseActivity;
import com.visionvera.bmob.event.RxEvent;
import com.visionvera.bmob.listener.OnTextChangedListener;
import com.visionvera.bmob.net.NetworkRequest;
import com.visionvera.bmob.net.ResponseSubscriber;
import com.visionvera.bmob.utils.IntentUtil;
import com.visionvera.bmob.utils.LogUtil;
import com.visionvera.bmob.utils.TextUtil;
import com.visionvera.bmob.utils.ToastUtil;
import com.visionvera.bmob.BuildConfig;
import com.visionvera.bmob.global.UserHelper;
import com.visionvera.bmob.global.CrashHandler;
import com.visionvera.bmob.model.UserBean;

public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private TextInputLayout login_account_ll;
    private TextInputEditText login_account_et;
    private TextInputLayout login_password_ll;
    private TextInputEditText login_password_et;
    private Button login_confirm_bt;
    private Button login_forget_bt;
    private Button login_register_bt;

    @Override
    protected void setContentView() {
        LogUtil.init(BuildConfig.DEBUG);
        setContentView(R.layout.activity_login);
    }

    @Override
    protected void initData() {
        CrashHandler.getInstance().init(getApplicationContext(), BuildConfig.DEBUG, getPackageName(), BuildConfig.VERSION_CODE, BuildConfig.VERSION_NAME);
    }

    @Override
    protected void initViews() {
        login_account_ll = (TextInputLayout) findViewById(R.id.login_account_ll);
        login_account_et = (TextInputEditText) findViewById(R.id.login_account_et);
        login_password_ll = (TextInputLayout) findViewById(R.id.login_password_ll);
        login_password_et = (TextInputEditText) findViewById(R.id.login_password_et);
        login_confirm_bt = (Button) findViewById(R.id.login_confirm_bt);
        login_forget_bt = (Button) findViewById(R.id.login_forget_bt);
        login_register_bt = (Button) findViewById(R.id.login_register_bt);

        login_account_ll.setCounterEnabled(true);
        login_password_ll.setCounterEnabled(true);
        login_password_ll.setPasswordVisibilityToggleEnabled(true);

        login_account_et.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                login_account_ll.setError(null);
            }
        });

        login_password_et.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                login_password_ll.setError(null);
            }
        });

        login_confirm_bt.setOnClickListener(this);
        login_forget_bt.setOnClickListener(this);
        login_register_bt.setOnClickListener(this);

        login_account_et.setText(UserHelper.getUserName());
        login_password_et.setText(UserHelper.getPassword());
    }

    @Override
    protected void loadData(boolean showLoading) {
        super.loadData(showLoading);
    }

    @Override
    protected void onEventMainThread(RxEvent rxEvent) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_confirm_bt:
                String account = login_account_et.getText().toString();
                if (TextUtil.isEmpty(account)) {
                    login_account_ll.setError("empty account.");
                    return;
                }
                final String password = login_password_et.getText().toString();
                if (TextUtil.isEmpty(password)) {
                    login_password_ll.setError("empty password.");
                    return;
                }
                NetworkRequest.getInstance().getLogin(LoginActivity.this, account, password, new ResponseSubscriber<UserBean>() {
                    @Override
                    public void onSuccess(UserBean userBean) {
                        ToastUtil.showToast("login success.");
                        UserHelper.saveUser(userBean);
                        UserHelper.savePassword(password);
                        UserHelper.saveIsLogin(true);
                        IntentUtil.toMainActivity(LoginActivity.this);
                        finish();
                    }

                    @Override
                    public void onFailure(int code, String error) {
                        login_password_ll.setError(error);
                    }
                });
                break;
            case R.id.login_forget_bt:
                break;
            case R.id.login_register_bt:
                IntentUtil.toRegisterActivity(LoginActivity.this);
                break;
        }
    }
}
