package com.sunyie.android.greatsen.entity;

/**
 * Created by shaohong on 2017-2-28.
 */
/*
返回：{"status":"0","msg":"login failed","data":[]}

 */
//登陆返回体
public class LoginEntity {
    private String status;
    private String msg;
    private Object[] data;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object[] getData() {
        return data;
    }

    public void setData(Object[] data) {
        this.data = data;
    }
}
