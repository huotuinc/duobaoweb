package com.huotu.mallduobao.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lhx on 2016/1/15.
 */
@Getter
@Setter
public class BuyListModel {

    private Long pid;

    /**
     * 用户头像
     */
    private String userHeadUrl;

    /**
     * 用户昵称
     */
    private String nickName;

    /**
     * 城市
     */
    private String city;

    /**
     * Ip
     */
    private String ip;
    /**
     * 时间
     */
    private String date;

    /**
     * 参与次数
     */
    private Long attendAmount;

}
