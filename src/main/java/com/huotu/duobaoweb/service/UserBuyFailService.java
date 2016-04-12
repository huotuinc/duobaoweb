package com.huotu.duobaoweb.service;

import com.huotu.duobaoweb.entity.Orders;
import com.huotu.duobaoweb.entity.OrdersItem;
import org.springframework.stereotype.Service;

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
