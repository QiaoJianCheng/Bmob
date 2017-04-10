package com.visionvera.bmob.model;

import java.util.List;

/**
 * Created by Qiao on 2017/3/15.
 */

public class UserBeans extends BaseBean {
    public List<UserBean> results;

    @Override
    public String toString() {
        return "UserBeans{" +
                "results=" + results +
                '}';
    }
}
