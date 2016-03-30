package com.huotu.duobaoweb.model;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 商品详情Model
 * Created by zhang on 2016/3/28.
 */
@Getter
@Setter
public class GoodsDetailModel {

    /**
     * 商品Id
     */
    private Long id;

    /**
     * 商品期号
     */
    private Long issueId;


    /**
     * 商品图片列表
     */
    private List<String> pictureUrls;

    /**
     * 状态  0:正在进行  1:倒计时  2:已揭晓
     */
    private Integer status;

    /**
     * 商品标题
     */
    private String title;

    /**
     *当前购买进度
     */
    private Integer progress;

    /**
     * 总需
     */
    private Long toAmount;

    /**
     * 剩余
     */
    private Long remainAmount;

    /**
     *默认购买量
     */
    private Long defaultAmount;

    /**
     * 单次购买最低量
     */
    private Long stepAmount;

    /**
     * 参与人次
     */
    private Integer joinCount;

    /**
     * 参与号码
     */
    private List<Long> numbers;

    /**
     * 全额购买商品价格
     */
    private BigDecimal fullPrice;

    /**
     * 离开奖的时间(毫秒数)
     */
    private Long toAwardTime;

    /**
     * 中奖用户
     */
    private String awardUserName;

    /**
     * 中奖用户城市
     */
    private String awardUserCityName;

    /**
     * 中奖用户ip
     */
    private String awardUserIp;

    /**
     * 中奖用户参与次数
     */
    private Long awardUserJoinCount;

    /**
     * 开奖时间
     */
    private Date awardTime;

    /**
     * 开奖号码
     */
    private Long luckNumber;

    /**
     * 中奖用户头像
     */
    private String awardUserHead;

    /**
     * 首次购买的时间
     */
    private Date firstBuyTime;
}
