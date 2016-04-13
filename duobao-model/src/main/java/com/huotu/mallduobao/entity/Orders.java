package com.huotu.mallduobao.entity;


import com.huotu.mallduobao.utils.CommonEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单表
 * Created by lgh on 2016/1/15.
 */
@Entity
@Getter
@Setter
@Cacheable(value = false)
public class Orders {

    /**
     * 订单号 由 时间(到毫秒)+用户Id 组成
     * 如2016011508080848599
     */
    @Id
    @Column(length = 50)
    private String id;


    /**
     * 用户
     */
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private User user;

    /**
     * 订单类型
     */
    private CommonEnum.OrderType orderType;

    /**
     * 总金额
     */
    @Column(precision = 20, scale = 2)
    private BigDecimal totalMoney;

    /**
     * 实付金额
     */
    @Column(precision = 20, scale = 2)
    private BigDecimal money;

    /**
     * 支付类型
     */
    private CommonEnum.PayType payType;

    /**
     * 订单状态
     */
    private CommonEnum.OrderStatus status;

    /**
     * 下单时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    /**
     * 支付时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date payTime;

    /**
     * 收货人
     */
    @Column(length = 10)
    private String receiver;
    /**
     * 联系电话
     */
    @Column(length = 11)
    private String mobile;
    /**
     * 详细地址
     */
    @Column(length = 400)
    private String details;
    /**
     * 外部流水号
     */
    @Column(length = 100)
    private String outOrderNo;
}
