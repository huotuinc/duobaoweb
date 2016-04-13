package com.huotu.mallduobao.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;


/**
 * 中奖纪录
 * Created by lhx on 2016/2/16.
 */
@Getter
@Setter
public class UserBuyFlowModel {

    private Long pid;

    /**
     * 缺省图片地址
     * 用于在商品列表中展示
     */
    private String defaultPictureUrl;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 期号
     */
    private Long issueId;

    /**
     * 总需人次
     */
    private Long toAmount;

    /**
     * 本期购买数量
     */
    private Long amount;

    /**
     * 幸运号码  (8位数字)
     * 格式 10005888
     * 触发条件：抽奖后
     */
    private Long luckyNumber;

    /**
     * 开奖日期(揭晓日期)
     */
    private Date awardingDate;

    private String awardingDateString;

    /**
     * 发货状态
     *(0, "获得奖品"),
     *(1, "确认收货地址"),
     *(2, "等待奖品派发"),
     *(4, "确认收货"),
     *(5, "已收货"),
     *(6, "已晒单");
     */
    private Integer deliveryStatus;

    /**
     * 发货单ID
     */
    private Long deliveryId;

    private Long time;


}
