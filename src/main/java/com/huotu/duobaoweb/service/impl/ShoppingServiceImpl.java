package com.huotu.duobaoweb.service.impl;

import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.ShoppingCart;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.model.ShoppingCartsModel;
import com.huotu.duobaoweb.repository.ShoppingCartRepository;
import com.huotu.duobaoweb.repository.UserRepository;
import com.huotu.duobaoweb.service.ShoppingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xhk on 2016/3/25.
 */
@Service
public class ShoppingServiceImpl implements ShoppingService{

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public void joinToShoppingCarts(Issue issue, User user,Long buyNum) {

        this.clearShoppingCarts(user);
        ShoppingCart shoppingCart=new ShoppingCart();
        if(buyNum==null){
            shoppingCart.setBuyAmount(issue.getDefaultAmount());
        }else {
            shoppingCart.setBuyAmount(buyNum);
        }
        shoppingCart.setIssue(issue);
        shoppingCart.setUser(user);
        shoppingCart=shoppingCartRepository.saveAndFlush(shoppingCart);
    }

    public void clearShoppingCarts(User user) {
        shoppingCartRepository.clearShoppingCarts(user);
    }

    @Override
    public ShoppingCartsModel getShoppingCartsModel(Long userId) {

        ShoppingCart shoppingCart=shoppingCartRepository.findOneByUserId(userId);
        ShoppingCartsModel shoppingCartsModel = new ShoppingCartsModel();
        if(shoppingCart!=null&&shoppingCart.getIssue().getStatus()!= CommonEnum.IssueStatus.going){
            //如果购物车中的信息已过期则删除购物车中的信息
            shoppingCartRepository.clearShoppingCarts(userRepository.findOne(userId));
            return null;
        }
        if(shoppingCart!=null) {
            Long left=shoppingCart.getIssue().getToAmount()-shoppingCart.getIssue().getBuyAmount();
            shoppingCartsModel.setCartId(shoppingCart.getId());
            shoppingCartsModel.setNeedNumber(shoppingCart.getIssue().getToAmount());
            shoppingCartsModel.setPerMoney(shoppingCart.getIssue().getPricePercentAmount().doubleValue());
            shoppingCartsModel.setLeftNumber(left);
            shoppingCartsModel.setImgUrl(shoppingCart.getIssue().getGoods().getDefaultPictureUrl());
            //如果购买量大于库存量，默认调整为库存量
            shoppingCartsModel.setBuyNum(shoppingCart.getBuyAmount()>left?left:shoppingCart.getBuyAmount());

            shoppingCartsModel.setDetail(shoppingCart.getIssue().getGoods().getTitle());
            shoppingCartsModel.setBuyMoney(shoppingCartsModel.getPerMoney()*shoppingCartsModel.getBuyNum());
        }
        return shoppingCartsModel;
    }
}
