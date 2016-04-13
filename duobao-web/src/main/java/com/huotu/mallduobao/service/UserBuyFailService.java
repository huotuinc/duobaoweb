package com.huotu.mallduobao.service;

import com.huotu.mallduobao.entity.Orders;
import com.huotu.mallduobao.entity.OrdersItem;

/**
 * Created by xhk on 2016/4/7.
 */

public interface UserBuyFailService {

    /**
     * 将这一期购买失败的转到下一期
     * @param orders
     * @param ordersItem
     */
    void recordToBuyFail(Orders orders, OrdersItem ordersItem);
}
