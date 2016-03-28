package com.huotu.duobaoweb.entity;


import com.huotu.duobaoweb.common.CommonEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 活动商品
 * Created by lgh on 2016/1/15.
 */
@Entity
@Getter
@Setter
public class Goods {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    /**
     * 缺省图片地址
     * 用于在商品列表中展示
     */
    @Column(length = 100)
    private String defaultPictureUrl;
    /**
     * 图组 由多个图片地址组成 英文逗号,隔开
     * 用户商品详情中显示商品图片
     */
    @Column(length = 500)
    private String pictureUrls;

    /**
     * 标题
     */
    @Column(length = 24)
    private String title;

    /**
     * 特征 (商品特点描述)
     */
    @Column(length = 24)
    private String characters;

    /**
     * 单次购买最低量
     * 前台条件：商品总需人次/(单次购买最低量/每人次单价) 为整数
     */
    @Column(precision = 20, scale = 2)
    private BigDecimal stepAmount;

    /**
     * 购买时缺省人次
     * (此只代表商品的设置值，实际以Issue为准)
     */
    private Long defaultAmount;


    /**
     * 总需人次（商品价格/每人次单价）
     * (此只代表商品的设置值，实际以Issue为准)
     */
    private Long toAmount;


    /**
     * 每人次单价
     * 默认为1 可以为小数如 0.5
     */
    @Column(precision = 20, scale = 2)
    private BigDecimal pricePercentAmount;
    /**
     * 介绍
     */
    @Lob
    private String introduction;

    /**
     * 状态
     */
    private CommonEnum.GoodsStatus status;

    /**
     * 当前期号
     */
    @OneToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private Issue issue;

    /**
     * 活动开始时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    /**
     * 活动结束时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date endTime;

    /**
     * 分享标题
     */
    @Column(length = 24)
    private String shareTitle;

    /**
     * 分享描述
     */
    @Column(length = 500)
    private String shareDescription;


    /**
     * 分享图片
     */
    @Column(length = 100)
    private String sharePictureUrl;

    /**
     * 对应商城的商品Id
     */
    private Long toMallGoodsId;

    /**
     * 购买次数
     * 在中奖时从每期中累计此值
     */
    private Long attendAmount;

    /**
     * 浏览量
     */
    private Long viewAmount;

    /**
     * 商家Id
     */
    private Long merchantId;

}
