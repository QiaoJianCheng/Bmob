package com.visionvera.bmob.model;

/**
 * Created by Qiao on 2017/5/15.
 */

public class AuthorBean extends BaseBean {
    public UsersBean.UserBean author;

    public AuthorBean(UsersBean.UserBean author) {
        this.author = author;
    }
}
