package com.visionvera.bmob.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.CheckResult;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.trello.rxlifecycle.LifecycleProvider;
import com.trello.rxlifecycle.LifecycleTransformer;
import com.trello.rxlifecycle.RxLifecycle;
import com.trello.rxlifecycle.android.ActivityEvent;
import com.trello.rxlifecycle.android.RxLifecycleAndroid;
import com.visionvera.bmob.R;
import com.visionvera.bmob.event.FinishEvent;
import com.visionvera.bmob.event.RxBus;
import com.visionvera.bmob.event.RxEvent;
import com.visionvera.bmob.listener.PressEffectTouchListener;
import com.visionvera.bmob.utils.IntentUtil;
import com.visionvera.bmob.utils.ResUtil;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

/**
 * Created by Qiao on 2016/12/15.
 */

public abstract class BaseActivity extends AppCompatActivity implements LifecycleProvider<ActivityEvent> {
    public static final String TAG = BaseActivity.class.getSimpleName();
    private final BehaviorSubject<ActivityEvent> lifecycleSubject = BehaviorSubject.create();
    protected ImageView common_title_button_iv;
    private View common_title_back_iv;
    private TextView common_title_text_tv;
    private View common_content_view;
    private View common_failed_view;
    private View common_loading_view;
    private View common_empty_view;
    private Subscription mRxBusSubscription;

    @Override
    @CallSuper
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerRxBus();
        lifecycleSubject.onNext(ActivityEvent.CREATE);
        setStatusBarColorId(R.color.colorWhite, true);

        setContentView();
        getIntentData();
        initData();
        initCommonView();
        initViews();
        loadData(true);
    }

    public void setStatusBarColor(int statusBarColor, boolean darkStatusContent) {
        try {
            Window window = getWindow();
            if (darkStatusContent) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    if (statusBarColor == ResUtil.getColor(android.R.color.white)) {
//                        statusBarColor = 0xffefefef;
                    }
                }
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(statusBarColor);
                //底部导航栏
                window.setNavigationBarColor(statusBarColor);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setStatusBarColorId(int statusBarColorId, boolean darkStatusContent) {
        setStatusBarColor(ResUtil.getColor(statusBarColorId), darkStatusContent);
    }

    private void initCommonView() {
        common_title_back_iv = findViewById(R.id.common_title_back_iv);
        common_title_text_tv = (TextView) findViewById(R.id.common_title_text_tv);
        common_title_button_iv = (ImageView) findViewById(R.id.common_title_button_iv);
        common_content_view = findViewById(R.id.common_content_view);
        common_failed_view = findViewById(R.id.common_failed_view);
        common_loading_view = findViewById(R.id.common_loading_view);
        common_empty_view = findViewById(R.id.common_empty_view);

        if (common_title_back_iv != null) {
            common_title_back_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
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

    protected abstract void setContentView();

    protected void getIntentData() {
    }

    protected abstract void initData();

    protected abstract void initViews();

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

    @Override
    public void finish() {
        super.finish();
        IntentUtil.exitActivityAnim(this);
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
                            if (rxEvent instanceof FinishEvent) {
                                finish();
                            } else {
                                onEventMainThread(rxEvent);
                            }
                        }
                    }
                });
    }

    @Override
    @NonNull
    @CheckResult
    public final Observable<ActivityEvent> lifecycle() {
        return lifecycleSubject.asObservable();
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindUntilEvent(@NonNull ActivityEvent event) {
        return RxLifecycle.bindUntilEvent(lifecycleSubject, event);
    }

    @Override
    @NonNull
    @CheckResult
    public final <T> LifecycleTransformer<T> bindToLifecycle() {
        return RxLifecycleAndroid.bindActivity(lifecycleSubject);
    }

    @Override
    @CallSuper
    protected void onStart() {
        super.onStart();
        lifecycleSubject.onNext(ActivityEvent.START);
    }

    @Override
    @CallSuper
    protected void onResume() {
        super.onResume();
        lifecycleSubject.onNext(ActivityEvent.RESUME);
    }

    @Override
    @CallSuper
    protected void onPause() {
        lifecycleSubject.onNext(ActivityEvent.PAUSE);
        super.onPause();
    }

    @Override
    @CallSuper
    protected void onStop() {
        lifecycleSubject.onNext(ActivityEvent.STOP);
        super.onStop();
    }

    @Override
    @CallSuper
    protected void onDestroy() {
        lifecycleSubject.onNext(ActivityEvent.DESTROY);
        if (!mRxBusSubscription.isUnsubscribed()) {
            mRxBusSubscription.unsubscribe();
        }
        super.onDestroy();
    }

    protected abstract void onEventMainThread(RxEvent rxEvent);
}
