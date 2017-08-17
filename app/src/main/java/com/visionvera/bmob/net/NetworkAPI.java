package com.visionvera.bmob.net;

import com.visionvera.bmob.model.AppsBean;
import com.visionvera.bmob.model.BaseBean;
import com.visionvera.bmob.model.CrashesBean;
import com.visionvera.bmob.model.FileBean;
import com.visionvera.bmob.model.MoodsBean;
import com.visionvera.bmob.model.PostBean;
import com.visionvera.bmob.model.UsersBean;

import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Qiao on 2017/3/14.
 */

interface NetworkAPI {

    @GET("1/users")
    Observable<UsersBean> getUsers();

    @GET("1/login")
    Observable<UsersBean.UserBean> getLogin(@QueryMap Map<String, Object> map);

    @POST("1/users")
    Observable<UsersBean.UserBean> postRegister(@Body RequestBody body);

    @POST("2/files/{filename}")
    Observable<FileBean> postFile(@Path("filename") String filename, @Body RequestBody body);

    @GET("1/classes/BigBang")
    Observable<AppsBean> getApps();

    @PUT("1/classes/BigBang/{id}")
    Observable<BaseBean> putApp(@Path("id") String id, @Body RequestBody body, @Header("X-Bmob-Session-Token") String header);

    @GET("1/classes/Crash?order=-createdAt")
    Observable<CrashesBean> getCrashes(@Query("where") String where);

    @POST("1/classes/Mood")
    Observable<PostBean> postMood(@Body RequestBody body);

    @GET("1/classes/Mood?order=-createdAt&include=author[username|nickname|gender|avatar]")
    Observable<MoodsBean> getMoods();

    @GET("1/classes/Mood?order=-createdAt&include=author[username|nickname|gender|avatar]")
    Observable<MoodsBean> getMoodsByUser(@Query("where") String where);
}