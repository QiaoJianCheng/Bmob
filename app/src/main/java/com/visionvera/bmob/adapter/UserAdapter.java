package com.visionvera.bmob.adapter;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.visionvera.bmob.R;
import com.visionvera.bmob.base.BaseRecyclerAdapter;
import com.visionvera.bmob.base.BaseViewHolder;
import com.visionvera.bmob.global.Constants;
import com.visionvera.bmob.model.UsersBean;
import com.visionvera.bmob.utils.FrescoUtil;
import com.visionvera.bmob.utils.ResUtil;

import java.util.List;

/**
 * Created by Qiao on 2017/3/14.
 */

public class UserAdapter extends BaseRecyclerAdapter<UsersBean.UserBean> {
    public UserAdapter(Context context, List<UsersBean.UserBean> list) {
        super(context, list);
    }

    @Override
    protected BaseViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new UserHolder(View.inflate(mContext, R.layout.item_user, null));
    }

    private class UserHolder extends BaseViewHolder {
        private SimpleDraweeView item_user_avatar_sdv;
        private TextView item_user_name_tv;
        private ImageView item_user_gender_iv;
        private TextView item_user_signature_tv;
        private TextView item_user_level_tv;

        public UserHolder(View itemView) {
            super(itemView);
            item_user_avatar_sdv = (SimpleDraweeView) itemView.findViewById(R.id.item_user_avatar_sdv);
            item_user_name_tv = (TextView) itemView.findViewById(R.id.item_user_name_tv);
            item_user_gender_iv = (ImageView) itemView.findViewById(R.id.item_user_gender_iv);
            item_user_signature_tv = (TextView) itemView.findViewById(R.id.item_user_signature_tv);
            item_user_level_tv = (TextView) itemView.findViewById(R.id.item_user_level_tv);

        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onBindViewHolder(int position) {
            UsersBean.UserBean userBean = mList.get(position);
            item_user_name_tv.setText(String.format("%1$s (@%2$s)", userBean.nickname, userBean.username));
            item_user_signature_tv.setText(userBean.signature);
            item_user_gender_iv.setImageResource(userBean.gender == 1 ? R.drawable.icon_20_male : R.drawable.icon_20_female);
            item_user_gender_iv.setImageTintList(ColorStateList.valueOf(ResUtil.getColor(userBean.gender == 1 ? R.color.colorMale : R.color.colorFemale)));
            int size = (int) ResUtil.getDimen(R.dimen.x130);
            FrescoUtil.display(item_user_avatar_sdv, userBean.avatar, size, size, R.drawable.sign_head);
            item_user_level_tv.setVisibility(userBean.userLevel == Constants.USER_LEVEL_ADMIN ? View.VISIBLE : View.GONE);
        }
    }
}
