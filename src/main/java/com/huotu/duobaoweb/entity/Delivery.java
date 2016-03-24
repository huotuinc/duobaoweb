package com.huotu.duobaoweb.entity;


import com.huotu.duobaoweb.common.CommonEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;


/**
 * 发货（中奖后）
 * Created by lgh on 2016/2/19.
 */
@Entity
@Getter
@Setter
public class Delivery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 发货状态
     */
    private CommonEnum.DeliveryStatus deliveryStatus;

    @OneToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private Issue issue;


    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private User user;

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
     * 确认收货地址时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date confirmAddressTime;

    /**
     * 发货时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date deliveryTime;
    /**
     * 确认收货时间
     */
    @Temporal(TemporalType.TIMESTAMP)
    private Date recieveGoodsTime;
}
