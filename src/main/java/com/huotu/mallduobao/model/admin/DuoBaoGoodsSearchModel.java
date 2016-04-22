package com.huotu.mallduobao.model.admin;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by zhang on 2016/4/1.
 */
@Setter
@Getter
public class DuoBaoGoodsSearchModel {

    /**
     * 标题
     */
    private String title = "";

    /**
     *  商品状态
     */
    private Integer status = -1;


    /**
     * 排序
     *
     */
    private Integer sort=0;

    /**
     * 排序
     */
    private Integer raSortType = 0;

    /**
     * ָ页码
     */
    private Integer pageNoStr = 0;


}
