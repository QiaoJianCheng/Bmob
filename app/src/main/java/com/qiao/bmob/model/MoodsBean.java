package com.qiao.bmob.model;

import java.util.List;

/**
 * Created by Qiao on 2017/5/12.
 */

public class MoodsBean extends BaseBean {
    public List<MoodBean> results;

    public static class MoodBean {
        public UsersBean.UserBean author;
        public String content;
        public String createdAt;
        public String objectId;
        public String updatedAt;
        public PhotoBean photo1;
        public PhotoBean photo2;
        public PhotoBean photo3;
        public PhotoBean photo4;
        public PhotoBean photo5;
        public PhotoBean photo6;
        public PhotoBean photo7;
        public PhotoBean photo8;
        public PhotoBean photo9;

        public MoodBean(UsersBean.UserBean author, String content) {
            this.author = author;
            this.content = content;
        }

        public static class PhotoBean {
            public String __type;
            public String cdn;
            public String filename;
            public String url;
        }
    }
}
