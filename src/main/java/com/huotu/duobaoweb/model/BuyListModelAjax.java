package com.huotu.duobaoweb.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * Created by zhang on 2016/4/12.
 */
public class BuyListModelAjax {
    private List<BuyListModel> Rows;

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

    private Long LastTime;

    private String Hong="&";

    private String PublicParament;

    @JsonProperty(value = "Rows")
    public List<BuyListModel> getRows() {
        return Rows;
    }

    public void setRows(List<BuyListModel> Rows) {
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

    @JsonProperty(value = "LastTime")
    public Long getLastTime() {
        return LastTime;
    }

    public void setLastTime(Long LastTime) {
        this.LastTime = LastTime;
    }
}
