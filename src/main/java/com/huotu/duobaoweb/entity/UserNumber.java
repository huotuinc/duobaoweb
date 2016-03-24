package com.huotu.duobaoweb.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 用户购买的号码
 * 系统会根据此表进行抽奖
 * Created by lgh on 2016/1/15.
 */
@Entity
@Getter
@Setter
public class UserNumber {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    /**
     * 用户
     */
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private User user;

    /**
     * 期号
     */
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private Issue issue;
    /**
     * 抽奖号码 (8位数字)
     * 格式 10005888
     */
    private Long number;

    /**
     * 购买时间
     */
    private Long time;

    /**
     * 所属订单
     */
    @ManyToOne(cascade = {CascadeType.MERGE, CascadeType.REFRESH})
    private Orders orders;

    public UserNumber() {
    }

    public UserNumber(User user, Issue issue, Long number, Long time, Orders orders) {
        this.user = user;
        this.issue = issue;
        this.number = number;
        this.time = time;
        this.orders = orders;
    }
}
