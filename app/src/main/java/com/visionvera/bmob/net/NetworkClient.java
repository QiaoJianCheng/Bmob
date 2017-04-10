package com.visionvera.bmob.net;

import com.visionvera.bmob.global.App;
import com.visionvera.bmob.utils.LogUtil;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Qiao on 2016/12/22.
 */

public class NetworkClient {
    private static final String TAG = NetworkClient.class.getSimpleName();

    private static final int HTTP_CONNECT_TIMEOUT = 10;
    private static final int HTTP_WRITE_TIMEOUT = 10;
    private static final int HTTP_READ_TIMEOUT = 10;
    private static final int HTTP_CACHE_SIZE = 5242880; //5 * 1024 * 1024
    private static final String HTTP_CACHE_DIR = "HttpCache";
    private Retrofit mRetrofit;
    private static NetworkClient mNetworkClient;
    private Object mServerAPI;

    private NetworkClient() {
        File cacheFile = new File(App.getContext().getCacheDir().getAbsolutePath(), HTTP_CACHE_DIR);
        Cache cache = new Cache(cacheFile, HTTP_CACHE_SIZE);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(getRequestHeaderInterceptor())
                .addInterceptor(getBodyInterceptor())
                .connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(HTTP_WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.SECONDS)
                .cache(cache);
        OkHttpClient okHttpClient = builder.build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(NetworkConfig.getBaseUrl())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static NetworkClient getInstance() {
        if (mNetworkClient == null) {
            mNetworkClient = new NetworkClient();
        }
        return mNetworkClient;
    }

    public <T> T createAPI(Class<T> clazz) {
        if (mServerAPI == null) {
            mServerAPI = getInstance().mRetrofit.create(clazz);
        }
        return (T) mServerAPI;
    }

    static void reset() {
        mNetworkClient = null;
    }

    private HttpLoggingInterceptor getBodyInterceptor() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                LogUtil.w(TAG, message);
            }
        });
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return interceptor;
    }

    private Interceptor getRequestHeaderInterceptor() {
        return new RequestHeaderInterceptor();
    }

    private class RequestHeaderInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request()
                    .newBuilder()
                    .addHeader("Content-Type", "application/json")
                    .addHeader("X-Bmob-Application-Id", "2bc8d0b3e8a18ce2eacbe760270bd7ce")
                    .addHeader("X-Bmob-REST-API-Key", "cf751ca5742a959ca9156a880215865c")
                    .build();

            return chain.proceed(request);
        }
    }
}
