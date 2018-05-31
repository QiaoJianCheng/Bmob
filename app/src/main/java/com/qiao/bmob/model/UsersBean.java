package com.qiao.bmob.model;

import java.util.List;

/**
 * Created by Qiao on 2017/3/15.
 */

public class UsersBean extends BaseBean {
    public List<UserBean> results;

    public static class UserBean extends BaseBean {
        public String avatar;
        public String createdAt;
        public int gender;
        public int userLevel;
        public String mobilePhoneNumber;
        public String objectId;
        public String signature;
        public String updatedAt;
        public String username;
        public String nickname;
        public String sessionToken;
        public String __type;
        public String className;

        public UserBean(String objectId) {
            this.objectId = objectId;
            __type = "Pointer";
            className = "_User";
        }
    }
}
