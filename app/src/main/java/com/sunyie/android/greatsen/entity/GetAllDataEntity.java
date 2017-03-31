package com.sunyie.android.greatsen.entity;

/**
 * Created by shaohong on 2017-2-28.
 *
 * {"status":"1","msg":"count success","data":
 *      {"start":"2017-01-01","end":"2017-03-01","pass":"56%","reject":"44%",
 *          "res":{"f5":25,"f1":13,"f0":62},
 *          "column":{"f0":"\u5176\u5b83","f1":"\u5f00\u8def","f2":"\u77ed\u8def","f3":"\u7f3a\u53e3",
 *              "f4":"\u9488\u5b54","f5":"\u94dc\u6e23","f6":"\u51f8\u8d77","f7":"\u51f9\u9677",
 *              "f8":"\u7ebf\u7ec6","f9":"\u7ebf\u7c97","f10":"\u7ebf\u8ddd","f11":"\u6b8b\u7559",
 *              "f12":"\u5fae\u77ed","f13":"\u5b54\u7834","f14":"\u5b54\u504f","f15":"SMT",
 *              "f16":"\u710a\u76d8","f17":"\u5176\u5b83"}}}
 */

public class GetAllDataEntity {
    private String status;
    private String msg;
    private AllDateEntity data;

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

    public AllDateEntity getData() {
        return data;
    }

    public void setData(AllDateEntity data) {
        this.data = data;
    }
}
