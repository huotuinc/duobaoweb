package com.huotu.duobaoweb.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * Created by lhx on 2016/1/16.
 */
@Getter
@Setter
public class RaiderListModel {

    private Long pid;
    /**
     * 图片地址
     */
    private String pictureUrl;

    /**
     * 标题
     */
    private String title;

    /**
     * 期号
     */
    private Long issueId;

    /**
     * 总需量
     */
    private Long toAmount;

    /**
     * 剩余量
     */
    private Long remainAmount;


    /**
     * 本期参与
     */
    private Long attendAmount;

    /**
     * 获奖者
     */
    private String winner;

    /**
     * 获奖者本期参与
     */
    private Long winnerAttendAmount;

    /**
     * 幸运号
     */
    private Long lunkyNumber;
    /**
     * 揭晓日期
     */
    private Date awardingDate;

    /**
     * 夺宝状态
     */
    private Integer status;

    /**
     * 夺宝时间
     */
    private Date time;
}
