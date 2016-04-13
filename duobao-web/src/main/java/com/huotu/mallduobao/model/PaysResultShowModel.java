package com.huotu.mallduobao.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by xhk on 2016/4/7.
 */
@Setter
@Getter
public class PaysResultShowModel {

    /**
     * 提示信息，例如：您成功参与了1件商品共2人次夺宝，信息如下：
     */
    private String title;

    /**
     * 商品信息
     */
    private String detail;

    /**
     * 商品总需人次
     */
    private Long needNumber;

    /**
     * 期号id
     */
    private Long issueId;

    /**
     * 用户购买的号码
     */
    private List<Long> numbers;
}
