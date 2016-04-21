package com.huotu.mallduobao.model.admin;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by admin on 2016/2/29.
 */

@Getter
@Setter
public class WebIssueListModel {

    /**
     * 期号
     */
    private Long id;
    /**
     * 中奖用户
     */
    private String awardingUser;

    /**
     * 所属的商品
     */
    private String goodsTitle;

    /**
     * 开奖日期(揭晓日期)
     */
    private Date awardingDate;
    /**
     * 幸运号码  (8位数字)
     * 格式 10005888
     * 触发条件：抽奖后
     */
    private Long luckyNumber;
}
