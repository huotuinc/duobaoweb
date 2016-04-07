package com.huotu.duobaoweb.service;

import com.huotu.duobaoweb.model.PayResultModel;

/**
 * Created by xhk on 2016/3/29.
 */
public interface PayService {

    /**
     * 处理微信支付结果
     * @param orderNo
     * @return
     */
    PayResultModel solveWeixinPayResult(String orderNo,float money,String outOrderNo);
}
