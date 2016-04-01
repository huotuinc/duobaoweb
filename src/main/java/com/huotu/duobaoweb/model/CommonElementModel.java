package com.huotu.duobaoweb.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 公共参数（每个页面都带有的）
 * Created by xhk on 2016/4/1.
 */
@Getter
@Setter
public class CommonElementModel {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 期号id
     */
    private Long issueId;

    /**
     * 商家id
     */
    private Long customerId;
}
