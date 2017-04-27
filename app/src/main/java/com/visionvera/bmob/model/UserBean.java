package com.visionvera.bmob.model;

/**
 * Created by Qiao on 2017/3/14.
 */

public class UserBean extends BaseBean {

    public String avatar;
    public String createdAt;
    public int gender;
    public String mobilePhoneNumber;
    public String objectId;
    public String signature;
    public String updatedAt;
    public String username;
    public String nickname;
    public String sessionToken;

    @Override
    public String toString() {
        return "UserBean{" +
                "avatar='" + avatar + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", gender=" + gender +
                ", mobilePhoneNumber='" + mobilePhoneNumber + '\'' +
                ", objectId='" + objectId + '\'' +
                ", signature='" + signature + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", username='" + username + '\'' +
                ", sessionToken='" + sessionToken + '\'' +
                '}';
    }
}
