package com.huotu.duobaoweb.service.impl;

import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.Orders;
import com.huotu.duobaoweb.entity.OrdersItem;
import com.huotu.duobaoweb.entity.UserBuyFail;
import com.huotu.duobaoweb.repository.UserBuyFailRepository;
import com.huotu.duobaoweb.service.UserBuyFailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by xhk on 2016/4/7.
 */
@Service
public class UserBuyFailServiceImpl implements UserBuyFailService{

    @Autowired
    private UserBuyFailRepository userBuyFailRepository;

    @Override
    public void recordToBuyFail(Orders orders, OrdersItem ordersItem) {
        UserBuyFail userBuyFail=new UserBuyFail();
        Date date=new Date();
        userBuyFail.setAmount(ordersItem.getAmount());
        userBuyFail.setTime(date);
        userBuyFail.setUser(orders.getUser());
        userBuyFail.setIssue(ordersItem.getIssue());
        userBuyFail.setGoods(ordersItem.getIssue().getGoods());
        userBuyFail.setMoney(orders.getMoney());
        userBuyFail.setSourceOrders(orders);
        userBuyFail.setStatus(CommonEnum.UserBuyFailStatus.undo);
        userBuyFail=userBuyFailRepository.saveAndFlush(userBuyFail);
    }
}
