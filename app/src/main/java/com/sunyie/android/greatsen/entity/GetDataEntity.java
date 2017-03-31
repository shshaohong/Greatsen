package com.sunyie.android.greatsen.entity;

import java.util.List;

/**
 * Created by shaohong on 2017-2-28.
 */
/*
{"status":"1","msg":"list success",
    "data":[{"machine_code":"100204283l0299a01",
                "level":["gbl","gtl"],
                "batch":["\u53d1\u53d1\u53d1",
                "\u597d\u597d\u597d"]},
            {"machine_code":"100204283l0299a02",
                "level":["gbl","gtl"],
                "batch":["111111","222222","333333","444444","666666","888888","999999"]}]}
 */
public class GetDataEntity {
    private String status;
    private String msg;
    private List<SearchEntity> data;

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

    public List<SearchEntity> getData() {
        return data;
    }

    public void setData(List<SearchEntity> data) {
        this.data = data;
    }
}
