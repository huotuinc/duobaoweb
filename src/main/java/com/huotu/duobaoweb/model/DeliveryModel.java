package com.huotu.duobaoweb.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by lhx on 2016/2/20.
 */
@Getter
@Setter
public class DeliveryModel {
    private Long pid;
    /**
     * 发货状态
     */
    private Integer deliveryStatus;

    private Long issueId;

    private String username;

    private Long userId;
    /**
     * 收货人
     */
    private String receiver;
    /**
     * 联系电话
     */
    private String mobile;
    /**
     * 详细地址
     */
    private String details;
    /**
     * 确认收货地址时间
     */
    private Date confirmAddressTime;
    /**
     * 发货时间
     */
    private Date deliveryTime;
    /**
     * 确认收货时间
     */
    private Date RecieveGoodsTime;
    /**
     * 中奖时间
     */
    private Date awardingDate;
}
