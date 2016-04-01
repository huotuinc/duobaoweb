package com.huotu.duobaoweb.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by xhk on 2016/3/28.
 */
@Setter
@Getter
public class ShoppingCartsModel {


    /**
     * 购物车id
     */
    private Long cartId;
    /**
     * 商品详情
     */
    private String detail;

    /**
     * 总需数量
     */
    private Long needNumber;

    /**
     * 剩余数量
     */
    private Long leftNumber;

    /**
     * 购买量
     */
    private Long buyNum;

    /**
     * 单位增减的数量
     */
    private Long stepNum;

    /**
     *购买金额
     */
    private Double buyMoney;

    /**
     * 单价
     */
    private Double perMoney;

    /**
     * 购物车图片路径
     */
    private String imgUrl;
}
