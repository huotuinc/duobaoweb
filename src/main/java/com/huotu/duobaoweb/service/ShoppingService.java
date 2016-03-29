package com.huotu.duobaoweb.service;

import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.Orders;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.model.PayInfoModel;
import com.huotu.duobaoweb.model.PayModel;
import com.huotu.duobaoweb.model.ShoppingCartsModel;

import java.net.URISyntaxException;

/**
 * Created by xhk on 2016/3/25.
 */
public interface ShoppingService {
    /**
     * ��ӵ����ﳵ
     * @param issue
     * @param user
     */
    void joinToShoppingCarts(Issue issue, User user,Long buyNum);

    /**
     * ��չ��ﳵ
     * @param user
     */
    void clearShoppingCarts(User user);


    /**
     * �õ��ӹ��ﳵmodel
     * @param userId
     * @return
     */
    ShoppingCartsModel getShoppingCartsModel(Long userId) throws URISyntaxException;


    /**
     * ����
     * @param cartId
     * @param buyNum
     * @return
     */
    PayModel balance(Long cartId, Integer buyNum);

    /**
     * �õ�΢��֧����url
     * @return
     */
    String getWeixinPayUrl(Orders orders);

    /**
     * ���ɶ���
     * @param payInfoModel
     * @return
     */
    Orders createOrders(PayInfoModel payInfoModel);
}
