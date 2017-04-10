package com.visionvera.bmob.base;

import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.FragmentEvent;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import com.visionvera.bmob.R;
import com.visionvera.bmob.event.RxBus;
import com.visionvera.bmob.event.RxEvent;
import com.visionvera.bmob.listener.PressEffectTouchListener;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

/**
 * Created by Qiao on 2016/12/16.
 */

public abstract class BaseFragment extends Fragment implements LifecycleProvider<FragmentEvent> {
    public static final String TAG = BaseFragment.class.getSimpleName();
    private final BehaviorSubject<FragmentEvent> lifecycleSubject = BehaviorSubject.create();
    private View common_title_back_iv;
    private TextView common_title_text_tv;
    private ImageView common_title_button_iv;
    private View common_content_view;
    private View common_failed_view;
    private View common_loading_view;
    private View common_empty_view;
    private Subscription mRxBusSubscription;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflateView(inflater);
    }

    @CallSuper
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        lifecycleSubject.onNext(FragmentEvent.CREATE_VIEW);
        registerRxBus();
        initCommonView(view);
        initData();
        initViews(view);
        loadData(true);
    }

    private void initCommonView(View view) {
        common_title_back_iv = view.findViewById(R.id.common_title_back_iv);
        common_title_text_tv = (TextView) view.findViewById(R.id.common_title_text_tv);
        common_title_button_iv = (ImageView) view.findViewById(R.id.common_title_button_iv);
        common_content_view = view.findViewById(R.id.common_content_view);
        common_failed_view = view.findViewById(R.id.common_failed_view);
        common_loading_view = view.findViewById(R.id.common_loading_view);
        common_empty_view = view.findViewById(R.id.common_empty_view);

        if (common_title_back_iv != null) {
            common_title_back_iv.setVisibility(View.GONE);
            common_title_back_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FragmentActivity activity = getActivity();
                    if (activity != null) {
                        activity.onBackPressed();
                    }
                }
            });
            common_title_back_iv.setOnTouchListener(new PressEffectTouchListener());
        }
        if (common_title_button_iv != null) {
            common_title_button_iv.setOnTouchListener(new PressEffectTouchListener());
        }
        if (common_failed_view != null) {
            common_failed_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadData(true);
                }
            });
        }
        if (common_empty_view != null) {
            common_empty_view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    loadData(true);
                }
            });
        }
    }

    @NonNull
    protected abstract View inflateView(LayoutInflater inflater);

    protected abstract void initData();

    protected abstract void initViews(View view);

    @CallSuper
    protected void loadData(boolean showLoading) {
        if (showLoading) {
            showLoadingView();
        }
    }

    public void setTitleBar(String title) {
        if (common_title_text_tv != null) {
            common_title_text_tv.setVisibility(View.VISIBLE);
            common_title_text_tv.setText(title);
        }
        if (common_title_button_iv != null) {
            common_title_button_iv.setVisibility(View.GONE);
        }
    }

    public void setTitleBar(String title, View.OnClickListener onClickListener) {
        if (common_title_text_tv != null) {
            common_title_text_tv.setVisibility(View.VISIBLE);
            common_title_text_tv.setText(title);
        }
        if (common_title_button_iv != null) {
            common_title_button_iv.setVisibility(View.VISIBLE);
            common_title_button_iv.setOnClickListener(onClickListener);
        }
    }

    public void setTitleBar(String title, int resId, View.OnClickListener onClickListener) {
        if (common_title_text_tv != null) {
            common_title_text_tv.setVisibility(View.VISIBLE);
            common_title_text_tv.setText(title);
        }
        if (common_title_button_iv != null) {
            common_title_button_iv.setVisibility(View.VISIBLE);
            common_title_button_iv.setOnClickListener(onClickListener);
            common_title_button_iv.setImageResource(resId);
        }
    }

    public void showLoadingView() {
        showViews(View.VISIBLE, View.GONE, View.GONE, View.GONE);
    }

    public void showFailedView() {
        showViews(View.GONE, View.VISIBLE, View.GONE, View.GONE);
    }

    public void showEmptyView() {
        showViews(View.GONE, View.GONE, View.VISIBLE, View.GONE);
    }

    public void showContentView() {
        showViews(View.GONE, View.GONE, View.GONE, View.VISIBLE);
    }

    private void showViews(int loading, int failed, int empty, int content) {
        if (common_loading_view != null) {
            common_loading_view.setVisibility(loading);
        }
        if (common_failed_view != null) {
            common_failed_view.setVisibility(failed);
        }
        if (common_empty_view != null) {
            common_empty_view.setVisibility(empty);
        }
        if (common_content_view != null) {
            common_content_view.setVisibility(content);
        }
    }

    private void registerRxBus() {
        mRxBusSubscription = RxBus.getDefault().toObservable(RxEvent.class)
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

    @Override
    @NonNull
    @CheckResult
    public final Observable<FragmentEvent> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull FragmentEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindFragment(lifecycleSubject);
    }

    @Override
    @CallSuper
    public void onAttach(android.app.Activity activity) {
        super.onAttach(activity);
        lifecycleSubject.onNext(FragmentEvent.ATTACH);
    }

    @Override
    @CallSuper
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        lifecycleSubject.onNext(FragmentEvent.CREATE);
    }

    @Override
    @CallSuper
    public void onStart() {
        super.onStart();
        lifecycleSubject.onNext(FragmentEvent.START);
    }

    @Override
    @CallSuper
    public void onResume() {
        super.onResume();
        lifecycleSubject.onNext(FragmentEvent.RESUME);
    }

    @Override
    @CallSuper
    public void onPause() {
        lifecycleSubject.onNext(FragmentEvent.PAUSE);
        super.onPause();
    }

    @Override
    @CallSuper
    public void onStop() {
        lifecycleSubject.onNext(FragmentEvent.STOP);
        super.onStop();
    }

    @Override
    @CallSuper
    public void onDestroyView() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY_VIEW);
        super.onDestroyView();
    }

    @Override
    @CallSuper
    public void onDestroy() {
        lifecycleSubject.onNext(FragmentEvent.DESTROY);
        if (!mRxBusSubscription.isUnsubscribed()) {
            mRxBusSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    @Override
    @CallSuper
    public void onDetach() {
        lifecycleSubject.onNext(FragmentEvent.DETACH);
        super.onDetach();
    }

    protected abstract void onEventMainThread(RxEvent rxEvent);
}
