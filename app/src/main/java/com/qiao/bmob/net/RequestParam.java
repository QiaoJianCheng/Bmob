package com.qiao.bmob.net;

import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Qiao on 2016/12/23.
 */

class RequestParam {
    private static final String PAGE_NO = "pageno";
    private static final String GET_COUNT = "getcount";
    static final String ACCESS_TOKEN = "access_token";

    static Map<String, Object> getBaseParam() {
        return new HashMap<>();
    }

    static Map<String, Object> getPageParam() {
        HashMap<String, Object> map = new HashMap<>();
        map.put(PAGE_NO, 1); // 第几页，大于0，默认1
        map.put(GET_COUNT, -1); // 每页几条数据，默认5，-1不分页
        return map;
    }

    static RequestBody mapToBody(Map<String, Object> map) {
        if (map == null) {
            return null;
        }
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), new Gson().toJson(map));
    }

    static <T> RequestBody beanToBody(T bean) {
        if (bean == null) {
            return null;
        }
        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), new Gson().toJson(bean));
    }

    static <T> String beanToString(T bean) {
        if (bean == null) {
            return null;
        }
        return new Gson().toJson(bean);
    }

    static RequestBody bytesToBody(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        return RequestBody.create(MediaType.parse("image/*"), bytes);
    }

    static RequestBody fileToBody(File crash) {
        if (crash == null) {
            return null;
        }
        return RequestBody.create(MediaType.parse("text/plain"), crash);
    }

    static MultipartBody.Part fileToPart(String key, File file) {
        if (file == null) {
            return null;
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        return MultipartBody.Part.createFormData(key, file.getName(), requestBody);
    }

    static MultipartBody.Part bytesToPart(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), bytes);
        return MultipartBody.Part.createFormData("file", "photo", requestBody);
    }
}
