package com.huotu.mallduobao.model.admin;

import lombok.Getter;
import lombok.Setter;

/**
 * 商城商品搜索条件
 * Created by zhang on 2016/4/5.
 */
@Setter
@Getter
public class MallGoodsSearchModel {

    /**
     * 商品标题
     */
    private String title="";

    /**
     * ָ页码
     */
    private Integer pageNoStr = 0;
}
