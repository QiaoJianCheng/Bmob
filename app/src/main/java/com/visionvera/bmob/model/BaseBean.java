package com.visionvera.bmob.model;

import java.io.Serializable;

/**
 * Created by Qiao on 2017/1/4.
 */

public class BaseBean<T extends BaseBean> implements Serializable, Comparable<T> {

    public static final String TAG = BaseBean.class.getSimpleName();

    @Override
    public int compareTo(T o) {
        return 0;
    }
}
