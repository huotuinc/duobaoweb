package com.huotu.mallduobao.service.impl;

import com.huotu.mallduobao.common.CommonEnum;
import com.huotu.mallduobao.entity.Orders;
import com.huotu.mallduobao.entity.OrdersItem;
import com.huotu.mallduobao.entity.UserBuyFail;
import com.huotu.mallduobao.repository.UserBuyFailRepository;
import com.huotu.mallduobao.service.UserBuyFailService;
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
