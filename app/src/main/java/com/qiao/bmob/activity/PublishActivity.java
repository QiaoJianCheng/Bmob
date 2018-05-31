package com.qiao.bmob.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.qiao.bmob.R;
import com.qiao.bmob.base.BaseActivity;
import com.qiao.bmob.global.UserHelper;
import com.qiao.bmob.listener.OnTextChangedListener;
import com.qiao.bmob.model.MoodsBean;
import com.qiao.bmob.model.PostBean;
import com.qiao.bmob.model.UsersBean;
import com.qiao.bmob.net.NetworkRequest;
import com.qiao.bmob.net.ResponseSubscriber;
import com.qiao.bmob.utils.TextUtil;
import com.qiao.bmob.utils.ToastUtil;

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

        publish_et = findViewById(R.id.publish_et);
        publish_count_tv = findViewById(R.id.publish_count_tv);
        publish_rl = findViewById(R.id.publish_rl);
        publish_pb = findViewById(R.id.publish_pb);

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
                    ToastUtil.showToast(getString(R.string.toast_empty_mood));
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
                                ToastUtil.showToast(error);
                                publish_rl.setEnabled(true);
                                publish_pb.setVisibility(View.INVISIBLE);
                            }
                        });
            }
        });
    }
}
