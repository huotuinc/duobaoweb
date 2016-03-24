package com.huotu.duobaoweb.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 用户购买流水（夺宝记录）
 * 同一个人不同订单的期号需要合并
 * 触发条件：在用户支付成功后
 * Created by lgh on 2016/1/15.
 */
@Entity
@Setter
@Getter
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user", "issue"})})
public class UserBuyFlow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 用户
     */
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private User user;

    /**
     * 期号
     */
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private Issue issue;

    /**
     * 购买数量
     */
    private Long amount;

    /**
     * 购买时间
     */
    private Long time;

//    /**
//     * 排序
//     * 规则：time的utc时间+id
//     * 夺宝记录按此排序
//     */
//    private Long sort;

}
