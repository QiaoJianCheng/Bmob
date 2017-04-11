package com.visionvera.bmob.base;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.visionvera.bmob.event.RxBus;
import com.visionvera.bmob.event.RxEvent;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by Qiao on 2016/12/16.
 */

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(View itemView) {
        super(itemView);
        RxBus.getDefault().toObservable(RxEvent.class)
                //在io线程进行订阅，可以执行一些耗时操作
                .subscribeOn(Schedulers.io())
                //在主线程进行观察，可做UI更新操作
                .observeOn(AndroidSchedulers.mainThread())
                //观察的对象
                .subscribe(new Action1<RxEvent>() {
                    @Override
                    public void call(RxEvent rxEvent) {
                        if (rxEvent != null) {
                            onEventMainThread(rxEvent);
                        }
                    }
                });
    }

    public void onEventMainThread(RxEvent rxEvent) {
    }

    public abstract void onBindViewHolder(int position);

}
