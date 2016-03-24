package com.huotu.duobaoweb.entity;

import com.huotu.duobaoweb.common.CommonEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 期号
 * Created by lgh on 2016/1/15.
 */
@Entity
@Getter
@Setter
public class Issue {

    /**
     * 期号
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 所属的活动商品
     */
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private Goods goods;

    /**
     * 单次购买最低量
     * 前台条件：商品总需人次/(单次购买最低量/每人次单价) 为整数
     */
    @Column(precision = 20, scale = 2)
    private BigDecimal stepAmount;

    /**
     * 购买时缺省人次
     */
    private Long defaultAmount;


    /**
     * 总需人次
     */
    private Long toAmount;

    /**
     * 已购买的人次
     */
    private Long buyAmount;

    /**
     * 每人次单价
     * 默认为1 可以为小数如 0.5
     */
    @Column(precision = 20, scale = 2)
    private BigDecimal pricePercentAmount;

    /**
     * 购买次数
     * 在中奖时从每期中累计此值
     */
    private Long attendAmount;

    /**
     * 本期状态
     */
    private CommonEnum.IssueStatus status;

    /**
     * 开奖日期(揭晓日期)
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date awardingDate;

    /**
     * 中奖用户
     */
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private User awardingUser;

    /**
     * 幸运号码  (8位数字)
     * 格式 10005888
     * 触发条件：抽奖后
     */
    private Long luckyNumber;

    /**
     * 中奖后的计算结果
     */
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private CountResult countResult;
}
