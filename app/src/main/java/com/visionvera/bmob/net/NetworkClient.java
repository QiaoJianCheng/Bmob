package com.visionvera.bmob.net;

import com.visionvera.bmob.global.App;
import com.visionvera.bmob.global.Constants;
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

    private static final int HTTP_CONNECT_TIMEOUT = 5000;
    private static final int HTTP_WRITE_TIMEOUT = 5000;
    private static final int HTTP_READ_TIMEOUT = 5000;
    private static final int HTTP_CACHE_SIZE = 5242880; //5 * 1024 * 1024
    private static final String HTTP_CACHE_DIR = "HttpCache";
    private static NetworkAPI mServerAPI;
    private Retrofit mRetrofit;

    private NetworkClient() {
        File cacheFile = new File(App.getContext().getCacheDir().getAbsolutePath(), HTTP_CACHE_DIR);
        Cache cache = new Cache(cacheFile, HTTP_CACHE_SIZE);
        OkHttpClient.Builder builder = (new OkHttpClient.Builder())
                .addInterceptor(getRequestHeaderInterceptor())
                .addInterceptor(getLogInterceptor())
                .connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
                .writeTimeout(HTTP_WRITE_TIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(HTTP_READ_TIMEOUT, TimeUnit.MILLISECONDS)
                .cache(cache);
        OkHttpClient okHttpClient = builder.build();
        mRetrofit = new Retrofit.Builder()
                .baseUrl(NetworkConfig.getBaseUrl())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    static NetworkAPI createAPI(Class<NetworkAPI> clazz) {
        if (mServerAPI == null) {
            mServerAPI = new NetworkClient().mRetrofit.create(clazz);
        }
        return mServerAPI;
    }

    static void reset() {
        mServerAPI = null;
    }

    private HttpLoggingInterceptor getLogInterceptor() {
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
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request request = chain.request()
                        .newBuilder()
                        .addHeader("Content-Type", "application/json")
                        .addHeader("X-Bmob-Application-Id", Constants.BMOB_APP_ID)
                        .addHeader("X-Bmob-REST-API-Key", Constants.BMOB_REST_API_KEY)
                        .build();

                return chain.proceed(request);
            }
        };
    }
}
