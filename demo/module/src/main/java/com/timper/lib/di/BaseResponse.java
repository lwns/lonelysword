package com.timper.lib.di;

/**
 * User: tangpeng.yang
 * Date: 2019-08-06
 * Description:
 * FIXME
 */
public class BaseResponse<T> {

    private T data;
    private String message;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
