package com.visionvera.bmob.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.visionvera.bmob.R;
import com.visionvera.bmob.base.BaseActivity;
import com.visionvera.bmob.global.UserHelper;
import com.visionvera.bmob.listener.OnTextChangedListener;
import com.visionvera.bmob.model.MoodsBean;
import com.visionvera.bmob.model.PostBean;
import com.visionvera.bmob.model.UsersBean;
import com.visionvera.bmob.net.NetworkRequest;
import com.visionvera.bmob.net.ResponseSubscriber;
import com.visionvera.bmob.utils.TextUtil;
import com.visionvera.bmob.utils.ToastUtil;

import java.util.Locale;

public class PublishActivity extends BaseActivity {

    private EditText publish_et;
    private TextView publish_count_tv;
    private RelativeLayout publish_rl;
    private ProgressBar publish_pb;

    @Override
    protected void setContentView() {
        setStatusBarColor(R.color.colorThemeRed, false);
        setContentView(R.layout.activity_publish);
    }

    @Override
    protected void initViews(Bundle savedInstanceState) {
        setTitleBar(getString(R.string.title_publish));

        publish_et = (EditText) findViewById(R.id.publish_et);
        publish_count_tv = (TextView) findViewById(R.id.publish_count_tv);
        publish_rl = (RelativeLayout) findViewById(R.id.publish_rl);
        publish_pb = (ProgressBar) findViewById(R.id.publish_pb);

        publish_et.addTextChangedListener(new OnTextChangedListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                publish_count_tv.setText(String.format(Locale.CHINA, "剩余%d字", 140 - s.length()));
            }
        });

        publish_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mood = publish_et.getText().toString().trim();
                if (TextUtil.isEmpty(mood)) {
                    ToastUtil.warnToast(getString(R.string.toast_empty_mood));
                    return;
                }
                publish_rl.setEnabled(false);
                publish_pb.setVisibility(View.VISIBLE);
                NetworkRequest.postMood(PublishActivity.this,
                        new MoodsBean.MoodBean(new UsersBean.UserBean(UserHelper.getUserId()), mood),
                        new ResponseSubscriber<PostBean>() {
                            @Override
                            public void onSuccess(PostBean postBean) {
                                publish_rl.setEnabled(true);
                                publish_pb.setVisibility(View.INVISIBLE);
                                finish();
                            }

                            @Override
                            public void onFailure(int code, String error) {
                                ToastUtil.warnToast(error);
                                publish_rl.setEnabled(true);
                                publish_pb.setVisibility(View.INVISIBLE);
                            }
                        });
            }
        });
    }
}
