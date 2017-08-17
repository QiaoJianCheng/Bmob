package com.visionvera.bmob.net;

import com.google.gson.Gson;
import com.visionvera.bmob.R;
import com.visionvera.bmob.utils.ResUtil;
import com.visionvera.bmob.utils.TextUtil;
import com.visionvera.bmob.model.ErrorBean;

import java.net.SocketTimeoutException;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscriber;

/**
 * Created by Qiao on 2016/12/22.
 */

public abstract class ResponseSubscriber<T> extends Subscriber<T> {
    @Override
    public final void onCompleted() {
    }

    @Override
    public final void onError(Throwable e) {
        if (e instanceof SocketTimeoutException) {
            onFailure(-2, ResUtil.getString(R.string.msg_network_error_time_out));
        } else if (e instanceof HttpException) {
            try {
                String body = ((HttpException) e).response().errorBody().string();
                ErrorBean errorBean = new Gson().fromJson(body, ErrorBean.class);
                onFailure(errorBean.code, errorBean.error);
            } catch (Exception e1) {
                e1.printStackTrace();
                onFailure(((HttpException) e).code(), TextUtil.isEmpty(e.getMessage()) ? ResUtil.getString(R.string.msg_network_error_server) : e.getMessage());
            }
        } else if (e instanceof ResponseException) {
            onFailure(((ResponseException) e).code, TextUtil.isEmpty(e.getMessage()) ? ResUtil.getString(R.string.msg_network_error_server) : e.getMessage());
        } else {
            onFailure(-1, ResUtil.getString(R.string.msg_network_error_unknown) + e.getMessage());
        }
        onComplete();
    }

    @Override
    public final void onNext(T t) {
        onSuccess(t);
        onComplete();
    }

    public abstract void onSuccess(T t);

    public abstract void onFailure(int code, String error);

    public void onComplete() {
    }
}
