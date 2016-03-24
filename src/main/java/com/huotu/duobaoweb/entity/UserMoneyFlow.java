package com.huotu.duobaoweb.entity;

import com.huotu.duobaoweb.common.CommonEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户金额流水
 * Created by lgh on 2016/1/16.
 */
@Entity
@Getter
@Setter
public class UserMoneyFlow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private User user;

    /**
     * 流水类型
     */
    private CommonEnum.MoneyFlowType moneyFlowType;

    /**
     * 金额
     */
    @Column(precision = 20, scale = 2)
    private BigDecimal money;

    /**
     * 当前余额
     */
    @Column(precision = 20, scale = 2)
    private BigDecimal currentMoney;

    /**
     * 时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date time;

    /**
     * 备注说明
     */
    @Column(length = 200)
    private String remarek;



}
