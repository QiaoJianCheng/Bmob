package com.qiao.bmob.net;

import com.google.gson.JsonObject;
import com.qiao.bmob.global.UserHelper;
import com.qiao.bmob.model.AppsBean;
import com.qiao.bmob.model.AuthorBean;
import com.qiao.bmob.model.BaseBean;
import com.qiao.bmob.model.CrashesBean;
import com.qiao.bmob.model.FileBean;
import com.qiao.bmob.model.MoodsBean;
import com.qiao.bmob.model.PostBean;
import com.qiao.bmob.model.UsersBean;
import com.qiao.bmob.utils.TextUtil;
import com.trello.rxlifecycle.LifecycleProvider;

import java.util.Map;

import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Qiao on 2017/3/8.
 */

public class NetworkRequest {

    private static NetworkAPI getServiceInstance() {
        return NetworkClient.createAPI(NetworkAPI.class);
    }

    private static <T extends BaseBean, E> void subscribe(LifecycleProvider<E> lifecycleProvider, Observable<T> observable, ResponseSubscriber<T> subscriber) {
        if (observable == null || subscriber == null) return;
        observable
                .map(new ResponseFunc<T>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(lifecycleProvider.bindToLifecycle())
                .subscribe(subscriber);
    }

    public static void getUsers(LifecycleProvider lifecycleProvider, ResponseSubscriber<UsersBean> subscriber) {
        subscribe(lifecycleProvider, getServiceInstance().getUsers(), subscriber);
    }

    public static void getLogin(LifecycleProvider lifecycleProvider, String username, String password, ResponseSubscriber<UsersBean.UserBean> subscriber) {
        Map<String, Object> param = RequestParam.getBaseParam();
        param.put("username", username);
        param.put("password", password);
        subscribe(lifecycleProvider, getServiceInstance().getLogin(param), subscriber);
    }

    public static void postRegister(LifecycleProvider lifecycleProvider, String username, String password, String phone, String signature, int gender, String avatar, ResponseSubscriber<UsersBean.UserBean> subscriber) {
        Map<String, Object> param = RequestParam.getBaseParam();
        param.put("username", username);
        param.put("password", password);
        param.put("mobilePhoneNumber", phone);
        param.put("signature", signature);
        param.put("gender", gender);
        param.put("avatar", avatar);
        RequestBody body = RequestParam.mapToBody(param);
        subscribe(lifecycleProvider, getServiceInstance().postRegister(body), subscriber);
    }

    public static void postFile(LifecycleProvider lifecycleProvider, String filename, byte[] bitmap, ResponseSubscriber<FileBean> subscriber) {
        subscribe(lifecycleProvider, getServiceInstance().postFile(filename, RequestParam.bytesToBody(bitmap)), subscriber);
    }

    public static void getApps(LifecycleProvider lifecycleProvider, ResponseSubscriber<AppsBean> subscriber) {
        subscribe(lifecycleProvider, getServiceInstance().getApps(), subscriber);
    }

    public static void putApp(LifecycleProvider lifecycleProvider, String id, boolean bang, ResponseSubscriber<BaseBean> subscriber) {
        Map<String, Object> param = RequestParam.getBaseParam();
        param.put("bang", bang);
        RequestBody body = RequestParam.mapToBody(param);
        subscribe(lifecycleProvider, getServiceInstance().putApp(id, body, UserHelper.getUserToken()), subscriber);
    }

    public static void getCrashes(LifecycleProvider lifecycleProvider, String appId, ResponseSubscriber<CrashesBean> subscriber) {
        String where = "";
        if (!TextUtil.isEmpty(appId)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("application_id", appId);
            where = jsonObject.toString();
        }
        subscribe(lifecycleProvider, getServiceInstance().getCrashes(where), subscriber);
    }

    public static void postMood(LifecycleProvider lifecycleProvider, MoodsBean.MoodBean moodBean, ResponseSubscriber<PostBean> subscriber) {
        subscribe(lifecycleProvider, getServiceInstance().postMood(RequestParam.beanToBody(moodBean)), subscriber);
    }

    public static void getMoods(LifecycleProvider lifecycleProvider, ResponseSubscriber<MoodsBean> subscriber) {
        subscribe(lifecycleProvider, getServiceInstance().getMoods(), subscriber);
    }

    public static void getMoodsByUser(LifecycleProvider lifecycleProvider, String userId, ResponseSubscriber<MoodsBean> subscriber) {
        subscribe(lifecycleProvider, getServiceInstance().getMoodsByUser(RequestParam.beanToString(new AuthorBean(new UsersBean.UserBean(userId)))), subscriber);
    }

}
