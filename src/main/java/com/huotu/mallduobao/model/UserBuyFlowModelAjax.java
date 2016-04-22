package com.huotu.mallduobao.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by lhx on 2016/4/1.
 */

public class UserBuyFlowModelAjax {
    private List<UserBuyFlowModel> Rows;

    /**
     * 总条数
     */
    private int Total;

    /**
     * 总页数
     */
    private int PageCount;

    /**
     * 当前页数
     */
    private int PageIndex;

    /**
     * 每页几条
     */
    private int PageSize;

    private Long LastFlag;

    private String Hong="&";

    private String PublicParament;

    @JsonProperty(value = "Rows")
    public List<UserBuyFlowModel> getRows() {
        return Rows;
    }

    public void setRows(List<UserBuyFlowModel> Rows) {
        this.Rows = Rows;
    }

    @JsonProperty(value = "Hong")
    public String getHong() {     return Hong;    }
    public void setHong(String Hong) { this.Hong = Hong;    }

    @JsonProperty(value = "PublicParament")
    public String getPublicParament() {     return PublicParament;    }
    public void setPublicParament(String PublicParament) { this.PublicParament = PublicParament;    }

    @JsonProperty(value = "Total")
    public int getTotal() {
        return Total;
    }

    public void setTotal(int Total) {
        this.Total = Total;
    }

    @JsonProperty(value = "PageCount")
    public int getPageCount() {
        return PageCount;
    }

    public void setPageCount(int PageCount) {
        this.PageCount = PageCount;
    }

    @JsonProperty(value = "PageIndex")
    public int getPageIndex() {
        return PageIndex;
    }

    public void setPageIndex(int PageIndex) {
        this.PageIndex = PageIndex;
    }

    @JsonProperty(value = "PageSize")
    public int getPageSize() {
        return PageSize;
    }

    public void setPageSize(int PageSize) {
        this.PageSize = PageSize;
    }

    @JsonProperty(value = "LastFlag")
    public Long getLastFlag() {
        return LastFlag;
    }

    public void setLastFlag(Long LastFlag) {
        this.LastFlag = LastFlag;
    }
}
