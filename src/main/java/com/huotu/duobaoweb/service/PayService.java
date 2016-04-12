package com.huotu.duobaoweb.service;

import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.Orders;
import com.huotu.duobaoweb.model.PayResultModel;

import java.io.IOException;

/**
 * Created by xhk on 2016/3/29.
 */
public interface PayService {

    /**
     * 处理微信支付结果
     * @param orderNo
     * @return
     */
    PayResultModel solveWeixinPayResult(String orderNo,float money,String outOrderNo) throws IOException;

    /**
     * 支付成功后的操作，将物品数量减少，
     * @param orders
     * @param money
     */
    PayResultModel doPay(Orders orders, float money, String outOrderNo, CommonEnum.PayType purchaseSource) throws IOException;
}
