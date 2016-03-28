package com.huotu.duobaoweb.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by lhx on 2016/3/28.
 */
@Getter
@Setter
public class RaiderNumbersModel {
    /**
     * 商品标题
     */
    private String  goodsTitle;
    /**
     * 期号
     */
    private Long issueId;

    /**
     * 人次
     */
    private Long amount;

    /**
     * 中奖号码
     */
    private List<Long> numbers;
}
