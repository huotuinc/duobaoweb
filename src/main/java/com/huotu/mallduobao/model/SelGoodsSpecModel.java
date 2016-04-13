package com.huotu.mallduobao.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by zhang on 2016/4/7.
 */
@Setter
@Getter
public class SelGoodsSpecModel {

    /**
     * 活动商品Id
     */
    private Long id;

    /**
     * 商城商品Id
     */
    private Long mallGoodsId;

    /**
     * 商品商家Id
     */
    private Long merchantId;

    /**
     * 商品标题
     */
    private String title;


    /**
     * 图片
     */
    private List<String> pictureUrlList;

    /**
     * 图文详情
     */
    private String introduce;


}
