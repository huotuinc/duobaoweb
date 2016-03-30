package com.huotu.duobaoweb.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by xhk on 2016/3/29.
 */
@Setter
@Getter
public class PayModel {

    /**
     * 需要支付的费用
     */
    private Double payMoney;


    /**
     * 支付的物品详情
     */
    private String detail;

    /**
     * 支付类型 1：微信 2：支付宝
     */
    private Integer paytype;

}
