package com.huotu.mallduobao.service;

import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.Orders;
import com.huotu.mallduobao.entity.ShoppingCart;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.model.PayModel;
import com.huotu.mallduobao.model.PaysResultShowModel;
import com.huotu.mallduobao.model.ShoppingCartsModel;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by xhk on 2016/3/25.
 */
public interface ShoppingService {
    /**
     * 添加到购物车
     * @param issue
     * @param user
     */
    ShoppingCart joinToShoppingCarts(Issue issue, User user,Long buyNum);

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
    String getWeixinPayUrl(Orders orders) throws IOException;

    /**
     * 将微信支付的参数放入到map中供加密用
     * @return
     */
    Map<String, String> putParamToPayMap();

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

    /**
     * 得到购买结果的显示
     * @param orderNo
     * @return
     */
    PaysResultShowModel getPayResultShowModel(String orderNo);
}
