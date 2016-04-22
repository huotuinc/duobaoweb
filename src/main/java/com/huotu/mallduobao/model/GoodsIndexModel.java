package com.huotu.mallduobao.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * 活动商品首页model
 * Created by zhang on 2016/3/25.
 */
@Getter
@Setter
public class GoodsIndexModel {

    /**
     * 商品Id
     */
    private Long id;

    /**
     * 图片URL
     */
    private String defaultPictureUrl;

    /**
     * 原价
     */
    private BigDecimal costPrice;

    /**
     * 现价
     */
    private BigDecimal currentPrice;

    /**
     * 活动开始时间
     */
    private Long startTime;

    /**
     * 活动结束时间
     */
    private Long endTime;

    /**
     * 参与人数
     */
    private Long joinCount;

    /**
     * 获取是否登录
     */
    private boolean logined;

    /**
     * 当前用户是否参与
     */
    private boolean joined;

    /**
     * 当前用户是否参与的期号
     */
    private Long issueId;

}
