package com.huotu.duobaoweb.service;

import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.model.ShoppingCartsModel;

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
    ShoppingCartsModel getShoppingCartsModel(Long userId);
}
