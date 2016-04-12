package com.huotu.duobaoweb.model;

import com.huotu.huobanplus.common.entity.Product;
import com.huotu.huobanplus.common.entity.support.SpecDescription;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 商城商品规格模型
 * Created by lhx on 2016/4/7.
 */
@Getter
@Setter
public class MallGoodsSpecificationsModel {
    /**
     * 规格id
     */
    private String id;
    /**
     * 规格名称
     */
    private String name;
    /**
     * 规格内容
     */
    private List<SpecDescription> specDescriptionList;

}
