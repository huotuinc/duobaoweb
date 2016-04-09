package com.huotu.duobaoweb.model;

import lombok.Getter;
import lombok.Setter;

/**
 * 与商城通信，告知商城支付是否成功
 * Created by xhk on 2016/4/9.
 */
@Setter
@Getter
public class PayResult {
    /**
     * 支付结果，成功：1；失败：0
     */
    private Integer code;

    /**
     * 提示信息
     */
    private String msg;
}
