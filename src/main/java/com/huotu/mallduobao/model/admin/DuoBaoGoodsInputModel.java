package com.huotu.mallduobao.model.admin;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by zhang on 2016/4/1.
 */
@Getter
@Setter
public class DuoBaoGoodsInputModel {

    /**
     * 活动Id
     */
    private Long id;

    /**
     * 商城商品Id
     */
    private Long mallGoodsId;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商品特性
     */
    private String characters;

    /**
     * 单次购买最低量
     */
    private Long stepAmount;

    /**
     * 购买时缺省人次
     */
    private Long defaultAmount;

    /**
     * 总需人次
     */
    private Long toAmount;

    /**
     * 每人次单价
     */
    private BigDecimal pricePercentAmount;

    /**
     * 图片
     */
    private String pictureUrls;


    /**
     * 活动开始时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date startTime;

    /**
     * 活动结束时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endTime;


    /**
     * 分享标题
     */
    private String shareTitle;

    /**
     * 分享描述
     */
    private String shareDescription;


    /**
     * 分享图片
     */
    private String sharePictureUrl;

    /**
     * 商家Id
     */
    private Long merchantId;

    //已下为更新的冗余字段

    /**
     * 商城商品名称
     */
    private String mallGoodsTitle;

    /**
     * 默认图片
     */
    private String defaultPictureUrl;

    /**
     * 默认图片相对
     */
    private String defaultPictureRelativelyUrl;

    /**
     * 图片
     */
    private List<String> pictureUrlList;

    /**
     * 图片相对
     */
    private List<String> pictureRelativelyUrlList;

    /**
     * 分享图片相对
     */
    private String sharePictureRelativelyUrl;


}
