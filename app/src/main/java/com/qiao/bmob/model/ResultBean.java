package com.qiao.bmob.model;

/**
 * Created by Qiao on 2016/12/22.
 */

public class ResultBean<T> extends BaseBean {
    public int ret;
    public String msg;
    public T data;
}
