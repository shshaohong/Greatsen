package com.sunyie.android.greatsen.entity;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by shaohong on 2017-2-28.
 */
/*"data":[{"machine_code":"100204283l0299a01",
                "level":["gbl","gtl"],
                "batch":["\u53d1\u53d1\u53d1",
                "\u597d\u597d\u597d"]},
            {"machine_code":"100204283l0299a02",
                "level":["gbl","gtl"],
                "batch":["111111","222222","333333","444444","666666","888888","999999"]}]}
*/
//获取料号列表
public class SearchEntity {

    @SerializedName("machine_code")
    private String search;
    private List<String> level;
    private List<String> batch;

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public List<String> getLevel() {
        return level;
    }

    public void setLevel(List<String> level) {
        this.level = level;
    }

    public List<String> getBatch() {
        return batch;
    }

    public void setBatch(List<String> batch) {
        this.batch = batch;
    }
}
