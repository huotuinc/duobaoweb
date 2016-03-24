package com.huotu.duobaoweb.entity;


import com.huotu.duobaoweb.common.CommonEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 订单细项(夺宝)
 * Created by lgh on 2016/1/15.
 */
@Entity
@Getter
@Setter
public class OrdersItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private Orders order;

    /**
     * 期号
     */
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private Issue issue;

    /**
     * 该子订单状态
     */
    private CommonEnum.OrderStatus status;

    /**
     * 购买数量
     */
    private Long amount;


}
