package com.huotu.duobaoweb.entity.pk;

import java.io.Serializable;

/**
 * Created by lgh on 2016/3/25.
 */
public class UserNumberPK implements Serializable {

    private Long issue;
    /**
     * 抽奖号码 (8位数字)
     * 格式 10005888
     */
    private Long number;
}
