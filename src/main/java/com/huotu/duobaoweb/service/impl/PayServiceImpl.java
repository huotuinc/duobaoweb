package com.huotu.duobaoweb.service.impl;

import com.huotu.duobaoweb.entity.Orders;
import com.huotu.duobaoweb.repository.OrdersItemRepository;
import com.huotu.duobaoweb.repository.OrdersRepository;
import com.huotu.duobaoweb.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xhk on 2016/3/29.
 */
@Service
public class PayServiceImpl implements PayService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrdersItemRepository ordersItemRepository;

    @Override
    public boolean solveWeixinPayResult(String orderNo) {
        Orders orders=ordersRepository.findOne(orderNo);

        return false;
    }
}
