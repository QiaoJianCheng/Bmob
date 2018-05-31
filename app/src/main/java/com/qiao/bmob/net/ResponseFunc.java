package com.qiao.bmob.net;

import com.qiao.bmob.model.BaseBean;

import rx.functions.Func1;

/**
 * Created by Qiao on 2017/3/3.
 */

public class ResponseFunc<T extends BaseBean> implements Func1<T, T> {
    @Override
    public T call(T resultBean) {
        return resultBean;
    }
}
