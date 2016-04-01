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
     * 需要结算的购物车
     */
    private Long cartsId;

    /**
     * 支付类型 1：微信 2：支付宝
     */
    private Integer payType;

    /**
     * 购买类型 1：正常购买 2：全额购买
     */
    private Integer type;
}
