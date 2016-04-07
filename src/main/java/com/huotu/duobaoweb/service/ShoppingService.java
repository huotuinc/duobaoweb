package com.huotu.duobaoweb.service;

import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.Orders;
import com.huotu.duobaoweb.entity.ShoppingCart;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.model.PayModel;
import com.huotu.duobaoweb.model.ShoppingCartsModel;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by xhk on 2016/3/25.
 */
public interface ShoppingService {
    /**
     * 添加到购物车
     * @param issue
     * @param user
     */
    void joinToShoppingCarts(Issue issue, User user,Long buyNum);

    /**
     * 清空购物车
     * @param user
     */
    void clearShoppingCarts(User user);


    /**
     * 得到加购物车model
     * @param userId
     * @return
     */
    ShoppingCartsModel getShoppingCartsModel(Long userId) throws URISyntaxException;


    /**
     * 正常结算
     * @param cartId
     * @param buyNum
     * @return
     */
    PayModel balance(Long cartId, Integer buyNum);

    /**
     * 得到微信支付的url
     * @return
     */
    String getWeixinPayUrl(Orders orders);

    /**
     * 生成订单
     * @param payInfoModel
     * @return
     */
    Orders createOrders(PayModel payInfoModel) throws IOException;

    /**
     * 全额购买
     * @param issue
     * @param user
     */
    ShoppingCart allToShoppingCarts(Issue issue, User user);

    /**
     * 全额支付生成支付所需的model
     * @param shoppingCartId
     * @return
     */
    PayModel allPayAndGetModel(String shoppingCartId);
}
