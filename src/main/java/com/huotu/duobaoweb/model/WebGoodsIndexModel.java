package com.huotu.duobaoweb.model;

import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 活动商品首页model
 * Created by zhang on 2016/3/25.
 */
public class WebGoodsIndexModel {


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
    private Date startTime;

    /**
     * 活动结束时间
     */
    private Date endTime;

    /**
     * 参与人数
     */
    private Long joinCount;

    /**
     * 当前用户Id
     */
    private Long userId;


}
