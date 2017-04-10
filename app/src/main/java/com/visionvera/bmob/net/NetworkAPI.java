package com.visionvera.bmob.net;

import com.visionvera.bmob.model.BaseBean;
import com.visionvera.bmob.model.FileBean;
import com.visionvera.bmob.model.UserBean;
import com.visionvera.bmob.model.UserBeans;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Qiao on 2017/3/14.
 */

interface NetworkAPI {

    @GET("1/users")
    Observable<UserBeans> getUsers();

    @GET("1/login")
    Observable<UserBean> getLogin(@QueryMap Map<String, Object> map);

    @POST("1/users")
    Observable<UserBean> postRegister(@Body RequestBody body);

    @POST("1/classes/Crash")
    Observable<BaseBean> postCrash(@Body RequestBody body);

    @POST("2/files/{filename}")
    Observable<FileBean> postFile(@Path("filename") String filename, @Body RequestBody body);
}