package com.huotu.duobaoweb.entity;

import com.huotu.duobaoweb.common.CommonEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 用户表
 * Created by lgh on 2016/1/12.
 */
@Entity
@Getter
@Setter
@Table(
        uniqueConstraints = {@UniqueConstraint(columnNames = {"username"})},
        indexes = {@Index(columnList = "token")
        }
)
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 账户
     * 规则 UUID去除-
     */
    @Column(nullable = false, length = 50)
    private String username;

    @Column(length = 32)
    private String password;

    /**
     * 用户来源类型
     */
    private CommonEnum.UserFromType userFromType;

    /**
     * 手机号(绑定后，可做为登录依据)
     */
    @Column(length = 11)
    private String mobile;

    /**
     * 是否绑定手机号
     */
    private boolean mobileBinded;


    /**
     * 微信唯一标示
     */
    @Column(length = 100)
    private String weixinOpenId;

    /**
     * 是否绑定微信
     */
    private boolean weixinBinded;

    /**
     * QQ唯一标示
     */
    @Column(length = 100)
    private String qqOpenId;

    /**
     * 是否绑定qq
     */
    private boolean qqBinded;


    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date regTime;

    /**
     * 昵称 （微信是微信昵称，qq是qq昵称，手机为手机号）
     */
    @Column(length = 100)
    private String realName;

    private int sex;

    @Temporal(TemporalType.DATE)
    private Date birth;

    private boolean enabled;

    /**
     * 用户头像
     */
    @Column(length = 100)
    private String userHead;

    @Column(length = 32)
    private String token;

    /**
     * 账户余额(夺宝币)
     */
    @Column(precision = 20, scale = 2)
    private BigDecimal money;

    /**
     * 用户Ip
     */
    private String ip;

    /**
     * 所属城市
     */
    @Column(length = 50)
    private String cityName;

    /**
     * 对应商城的Id
     */
    private Long mallUserId;

    /**
     * 商城Id
     */
    private Long merchantId;
}
