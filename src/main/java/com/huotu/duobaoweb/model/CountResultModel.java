package com.huotu.duobaoweb.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by zhang on 2016/3/30.
 */
@Setter
@Getter
public class CountResultModel {
    /**
     * 时时彩期号
     * 如20160128038
     */
    private String issueNo;

    /**
     * 截止该商品中奖时间点前最后50条全站参与记录
     */
    private String numberA;

    /***
     * 时时彩开奖结果
     */
    private String numberB;

    /**
     * 对应的最后50条参与记录
     */
    private List<CountResultUserNumberListModel> userNumbers;

    /**
     * 中奖号码
     */
    private Long luckNumber;
}
