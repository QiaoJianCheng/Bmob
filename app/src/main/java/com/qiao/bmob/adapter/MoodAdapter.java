package com.qiao.bmob.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.qiao.bmob.R;
import com.qiao.bmob.base.BaseRecyclerAdapter;
import com.qiao.bmob.base.BaseViewHolder;
import com.qiao.bmob.model.MoodsBean;
import com.qiao.bmob.model.UsersBean;
import com.qiao.bmob.utils.DateFormatUtil;
import com.qiao.bmob.utils.FrescoUtil;
import com.qiao.bmob.utils.ResUtil;
import com.qiao.bmob.view.GridPhotoView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Qiao on 2017/5/12.
 */

public class MoodAdapter extends BaseRecyclerAdapter<MoodsBean.MoodBean> {
    public MoodAdapter(Context context, ArrayList<MoodsBean.MoodBean> list) {
        super(context, list);
    }

    @Override
    protected BaseViewHolder onCreateHolder(ViewGroup parent, int viewType) {
        return new MoodHolder(View.inflate(mContext, R.layout.item_mood, null));
    }

    private class MoodHolder extends BaseViewHolder {
        private SimpleDraweeView item_mood_avatar_sdv;
        private TextView item_mood_name_tv;
        private TextView item_mood_time_tv;
        private ImageView item_mood_gender_iv;
        private TextView item_mood_content_tv;
        private GridPhotoView item_mood_grid_photo_view;

        private MoodHolder(View itemView) {
            super(itemView);
            item_mood_avatar_sdv = itemView.findViewById(R.id.item_mood_avatar_sdv);
            item_mood_name_tv = itemView.findViewById(R.id.item_mood_name_tv);
            item_mood_time_tv = itemView.findViewById(R.id.item_mood_time_tv);
            item_mood_gender_iv = itemView.findViewById(R.id.item_mood_gender_iv);
            item_mood_content_tv = itemView.findViewById(R.id.item_mood_content_tv);
            item_mood_grid_photo_view = itemView.findViewById(R.id.item_mood_grid_photo_view);
        }

        @Override
        public void onBindViewHolder(final int position) {
            final MoodsBean.MoodBean moodBean = mList.get(position);
            UsersBean.UserBean userBean = moodBean.author;
            item_mood_name_tv.setText(String.format("%1$s (@%2$s)", userBean.nickname, userBean.username));
            item_mood_gender_iv.setImageResource(userBean.gender == 1 ? R.drawable.icon_20_male : R.drawable.icon_20_female);
            item_mood_gender_iv.setImageTintList(ColorStateList.valueOf(ResUtil.getColor(userBean.gender == 1 ? R.color.colorMale : R.color.colorFemale)));
            int size = (int) ResUtil.getDimen(R.dimen.x100);
            FrescoUtil.display(item_mood_avatar_sdv, userBean.avatar, size, size, R.drawable.sign_head);
            item_mood_content_tv.setText(moodBean.content);
            item_mood_time_tv.setText(DateFormatUtil.formatLocalDate(moodBean.updatedAt));
            Random random = new Random();
            int in = random.nextInt(9);
            for (int i = 0; i < in; i++) {
                item_mood_grid_photo_view.addUrl("http://wx1.sinaimg.cn/large/c1a9d02cly1ff25bmgprxj20zk0hsact.jpg");
            }
        }
    }
}
