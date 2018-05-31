package com.qiao.bmob.net;

/**
 * Created by Qiao on 2016/12/22.
 */

public class ResponseException extends RuntimeException {
    public int code;

    ResponseException(int code, String message) {
        super(message);
        this.code = code;
    }
}
