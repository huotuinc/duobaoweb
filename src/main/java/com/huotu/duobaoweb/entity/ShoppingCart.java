package com.huotu.duobaoweb.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 购物车
 * Created by xhk on 2016/3/25
 */
@Entity
@Getter
@Setter
public class ShoppingCart {

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
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private Issue issue;

    /**
     * 购买次数
     */
    private Long buyAmount;
}
