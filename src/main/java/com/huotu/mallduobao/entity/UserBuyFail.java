package com.huotu.mallduobao.entity;

import com.huotu.mallduobao.utils.CommonEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户购买失败
 * 用户购买失败后插入此表
 * 系统会提供对失败单子的处理,购买该商品的下期进行处理
 * Created by lgh on 2016/4/1.
 */
@Entity
@Getter
@Setter
public class UserBuyFail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 购买的用户
     */
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private User user;

    /**
     * 购买的对应的商品
     */
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private Goods goods;

    /**
     * 购买的期号
     */
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private Issue issue;

    /**
     * 购买的人次
     */
    private Long amount;

    /**
     * 金钱
     */
    @Column(precision = 20, scale = 2)
    private BigDecimal money;

    /**
     * 来源订单
     */
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private Orders sourceOrders;

    /**
     * 购买的时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;


    /**
     * 状态 默认 未处理
     */
    private CommonEnum.UserBuyFailStatus status;
}
