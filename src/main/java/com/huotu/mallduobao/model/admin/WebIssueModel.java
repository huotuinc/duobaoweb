package com.huotu.mallduobao.model.admin;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * Created by zhang on 2016/3/7.
 */
@Getter
@Setter
public class WebIssueModel {

    /**
     * 期号
     */
    private Long id;

    /**
     * 状态
     */
    private String statusName;

    /**
     * 已购买
     */
    private  Long buyAmount;


    /**
     * 总需人次
     */
    private Long toAmount;

    /**
     * 对应商品
     */
    private String goodsName;

    /**
     * 是否专区
     */
    private String prefectureName;

    /**
     * 每人次单价
     */
    private BigDecimal pricePercentAmount;


    /**
     * 中奖用户
     */
    private String awardingUser;

    /**
     * 中奖时间
     */
    private String awardingTime;

    /**
     * 幸运号码
     */
    private Long luckNumber;

    /**
     * 默认购买量
     */
    private Long defaultAmount;


    /**
     * 对应商品Id
     */
    private Long goodsId;

}
