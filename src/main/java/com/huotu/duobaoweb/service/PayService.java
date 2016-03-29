package com.huotu.duobaoweb.service;

/**
 * Created by xhk on 2016/3/29.
 */
public interface PayService {

    /**
     * 处理微信支付结果
     * @param orderNo
     * @return
     */
    boolean solveWeixinPayResult(String orderNo);
}
