package com.huotu.mallduobao.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by zhang on 2016/3/30.
 */
@Setter
@Getter
public class CountResultUserNumberListModel {

    /**
     *购买时间(2016-03-01 18:49:04.920)
     */
    private String buyTime;

    /**
     * 数据(184904920)
     */
    private String number;

    /**
     * 用户账号
     */
    private String nickName;
}
