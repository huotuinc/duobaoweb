package com.huotu.duobaoweb.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by lgh on 2016/1/16.
 */
@Entity
@Getter
@Setter
@Cacheable(value = false)
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

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
     * 城市（浙江省|杭州市|滨江区）
     */
    @Column(length = 50)
    private String cityName;
    /**
     * 详细地址
     */
    @Column(length = 400)
    private String details;
    /**
     * 缺省地址
     */
    private Boolean defaultAddress;
}
