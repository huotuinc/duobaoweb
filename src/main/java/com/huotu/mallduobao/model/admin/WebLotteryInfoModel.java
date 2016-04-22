package com.huotu.mallduobao.model.admin;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by admin on 2016/2/29.
 */
@Getter
@Setter
public class WebLotteryInfoModel {
    /**
     * id
     */
    private Long id;

    private Long userId;

    /**
     * 期号
     */
    private Long issueId;

    /**
     * 商品标题
     */
    private String goodsTitle;

    /**
     * 夺宝时间
     */
    private Date time;
    /**
     * 中奖用户
     */
    private String luckyNumber;

    /**
     * 中奖用户
     */
    private String awardingUser;
    /**
     * 中奖日期
     */
    private Date awardingDate;
    /**
     * 中奖次数
     */
    private Long amount;


}
