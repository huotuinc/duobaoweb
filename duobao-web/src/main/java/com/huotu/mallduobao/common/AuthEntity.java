package com.huotu.mallduobao.common;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by xhk on 2016/4/14.
 */
@Getter
@Setter
public class AuthEntity {

    /**
     * 用户昵称
     */
    private String nickname;

    /**
     * 认证获取的openid
     *
     */
    private String openid;

    /**
     * 用户头像
     */
    private String headimgurl;

    /**
     * 性别
     */
    private int  sex;

    /**
     * 省份
     */
    private String province;

    /**
     * 城市
     */
    private String city;

    /**
     * 国家
     */
    private String country;

}
