package com.visionvera.bmob.net;

import com.google.gson.JsonObject;
import com.trello.rxlifecycle.LifecycleProvider;
import com.visionvera.bmob.global.UserHelper;
import com.visionvera.bmob.model.AppsBean;
import com.visionvera.bmob.model.BaseBean;
import com.visionvera.bmob.model.CrashesBean;
import com.visionvera.bmob.model.FileBean;
import com.visionvera.bmob.model.UserBean;
import com.visionvera.bmob.model.UsersBean;
import com.visionvera.bmob.utils.TextUtil;

import java.util.Map;

import okhttp3.RequestBody;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Qiao on 2017/3/8.
 */

public class NetworkRequest {

    protected final NetworkAPI getServiceInstance() {
        return NetworkClient.getInstance().createAPI(NetworkAPI.class);
    }

    protected final <T extends BaseBean, E> void subscribe(LifecycleProvider<E> lifecycleProvider, Observable<T> observable, ResponseSubscriber<T> subscriber) {
        if (observable == null || subscriber == null) return;
        observable
                .map(new ResponseFunc<T>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .compose(lifecycleProvider.<T>bindToLifecycle())
                .subscribe(subscriber);
    }

    private static NetworkRequest sNetworkRequest;

    public static NetworkRequest getInstance() {
        if (sNetworkRequest == null) {
            sNetworkRequest = new NetworkRequest();
        }
        return sNetworkRequest;
    }

    public void getUsers(LifecycleProvider lifecycleProvider, ResponseSubscriber<UsersBean> subscriber) {
        subscribe(lifecycleProvider, getServiceInstance().getUsers(), subscriber);
    }

    public void getLogin(LifecycleProvider lifecycleProvider, String username, String password, ResponseSubscriber<UserBean> subscriber) {
        Map<String, Object> param = RequestParam.getBaseParam();
        param.put("username", username);
        param.put("password", password);
        subscribe(lifecycleProvider, getServiceInstance().getLogin(param), subscriber);
    }

    public void postRegister(LifecycleProvider lifecycleProvider, String username, String password, String phone, String signature, int gender, String avatar, ResponseSubscriber<UserBean> subscriber) {
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

    public void postFile(LifecycleProvider lifecycleProvider, String filename, byte[] bitmap, ResponseSubscriber<FileBean> subscriber) {
        subscribe(lifecycleProvider, getServiceInstance().postFile(filename, RequestParam.bytesToBody(bitmap)), subscriber);
    }

    public void getApps(LifecycleProvider lifecycleProvider, ResponseSubscriber<AppsBean> subscriber) {
        subscribe(lifecycleProvider, getServiceInstance().getApps(), subscriber);
    }

    public void putApp(LifecycleProvider lifecycleProvider, String id, boolean bang, ResponseSubscriber<BaseBean> subscriber) {
        Map<String, Object> param = RequestParam.getBaseParam();
        param.put("bang", bang);
        RequestBody body = RequestParam.mapToBody(param);
        subscribe(lifecycleProvider, getServiceInstance().putApp(id, body, UserHelper.getUserToken()), subscriber);
    }

    public void getCrashes(LifecycleProvider lifecycleProvider, String appId, ResponseSubscriber<CrashesBean> subscriber) {
        String where = "";
        if (!TextUtil.isEmpty(appId)) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("application_id", appId);
            where = jsonObject.toString();
        }
        subscribe(lifecycleProvider, getServiceInstance().getCrashes(where), subscriber);
    }

}
