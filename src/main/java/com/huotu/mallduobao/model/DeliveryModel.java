package com.huotu.mallduobao.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by lhx on 2016/2/20.
 */
@Getter
@Setter
public class DeliveryModel {

    private Long pid;

    private Long issueId;

    private String username;

    private Long userId;
    /**
     * 发货状态
     */
    private Integer deliveryStatus;

    /**
     * 收货人
     */
    private String receiver;

    /**
     * 联系电话
     */
    private String mobile;

    /**
     * 详细地址
     */
    private String details;

    /**
     * 确认收货地址时间
     */
    private Date confirmAddressTime;
    /**
     * 发货时间
     */
    private Date deliveryTime;

    /**
     * 确认收货时间
     */
    private Date recieveGoodsTime;

    /**
     * 中奖时间
     */
    private Date awardingDate;


    /**
     * 总需人次
     */
    private Long toAmount;
    /**
     * 幸运号码  (8位数字)
     * 格式 10005888
     * 触发条件：抽奖后
     */
    private Long luckyNumber;
    /**
     * 商品名称
     */
    private String title;

    /**
     * 缺省图片地址
     * 用于在商品列表中展示
     */
    private String defaultPictureUrl;

    /**
     * 商品规格
     */
    private String productSpec;

    /**
     * 商城订单id
     */
    private String mallOrderId;
}
