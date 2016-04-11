package com.huotu.duobaoweb.entity;

import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.pk.DeliveryPK;
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
@Cacheable(value = false)
@IdClass(value = DeliveryPK.class)
public class Delivery {

    @Id
    @OneToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private Issue issue;


    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE})
    private User user;

    /**
     * 发货状态
     */
    private CommonEnum.DeliveryStatus deliveryStatus;

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
     * 对应商城的货品Id
     */
    private Long productId;

    @Column(length = 20)
    private String productSpec;

    /**
     * 备注说明
     */
    @Column(length = 200)
    private String remark;

    /**
     * 是否提单成功
     * true 提单成功 false提单失败
     */
    private Boolean isCommit;

    /**
     * 商城订单id
     */
    private String mallOrderId;
}
