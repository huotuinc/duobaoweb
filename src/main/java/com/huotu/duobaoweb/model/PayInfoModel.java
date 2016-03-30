package com.huotu.duobaoweb.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by xhk on 2016/3/29.
 */
@Setter
@Getter
public class PayInfoModel {

    /**
     *支付类型 1：微信 2：支付宝
     */
    private Integer payType;

    /**
     * 支付的金额
     */
    private Double payMoney;

    /**
     * 需要结算的购物车
     */
    private Long cartsId;
}
