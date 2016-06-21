package com.huotu.mallduobao.model.admin;

import lombok.Getter;
import lombok.Setter;

/**
 * 商城商品
 * Created by zhang on 2016/4/5.
 */
@Setter
@Getter
public class MallGoodsListModel {

    /**
     * 商品Id
     */
    private Long id;

    /**
     * 品牌名称
     */
    //private String brandName;

    /**
     * 商品标题
     */
    private String title;

    /**
     * 商家昵称
     */
   // private String nickName;

    /**
     * 商家Id
     */
    private Long merchantId;

    /**
     * 市场价
     */
    private double markerPrice;

    /**
     * 商品价格
     */
    private double price;

    /**
     * 商品成本价
     */
    private double cost;

    /**
     * 库存
     */
    private int stock;

    /**
     * 锁定库存
     */
    private int lockStock;

}
