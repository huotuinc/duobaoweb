package com.huotu.duobaoweb.service.impl;

import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.common.WeixinPayUrl;
import com.huotu.duobaoweb.entity.*;
import com.huotu.duobaoweb.model.PayModel;
import com.huotu.duobaoweb.model.ShoppingCartsModel;
import com.huotu.duobaoweb.repository.OrdersItemRepository;
import com.huotu.duobaoweb.repository.OrdersRepository;
import com.huotu.duobaoweb.repository.ShoppingCartRepository;
import com.huotu.duobaoweb.repository.UserRepository;
import com.huotu.duobaoweb.service.CommonConfigService;
import com.huotu.duobaoweb.service.ShoppingService;
import com.huotu.duobaoweb.service.StaticResourceService;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by xhk on 2016/3/25.
 */
@Service
public class ShoppingServiceImpl implements ShoppingService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StaticResourceService staticResourceService;

    @Autowired
    private CommonConfigService commonConfigService;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrdersItemRepository ordersItemRepository;

    @Autowired
    private GoodsRestRepository goodsRestRepository;

    @Override
    public void joinToShoppingCarts(Issue issue, User user, Long buyNum) {

        this.clearShoppingCarts(user);
        ShoppingCart shoppingCart = new ShoppingCart();
        if (buyNum == null) {
            shoppingCart.setBuyAmount(issue.getDefaultAmount());
        } else {
            //如果购买的数量大于剩余量则购买数量调整为剩余量
            Long left = issue.getToAmount() - issue.getBuyAmount();
            shoppingCart.setBuyAmount(left < buyNum ? left : buyNum);
        }
        shoppingCart.setIssue(issue);
        shoppingCart.setUser(user);
        shoppingCart = shoppingCartRepository.saveAndFlush(shoppingCart);
    }

    public void clearShoppingCarts(User user) {
        shoppingCartRepository.clearShoppingCarts(user);
    }

    @Override
    public ShoppingCartsModel getShoppingCartsModel(Long userId) throws URISyntaxException {

        ShoppingCart shoppingCart = shoppingCartRepository.findOneByUserId(userId);
        ShoppingCartsModel shoppingCartsModel = new ShoppingCartsModel();
        if(shoppingCart==null){
            return null;
        } else if (shoppingCart != null && shoppingCart.getIssue().getStatus() != CommonEnum.IssueStatus.going) {
            //如果购物车中的信息已过期则删除购物车中的信息
            shoppingCartRepository.clearShoppingCarts(userRepository.findOne(userId));
            return null;
        }
        if (shoppingCart != null) {
            Long left = shoppingCart.getIssue().getToAmount() - shoppingCart.getIssue().getBuyAmount();
            shoppingCartsModel.setCartId(shoppingCart.getId());
            shoppingCartsModel.setNeedNumber(shoppingCart.getIssue().getToAmount());
            shoppingCartsModel.setPerMoney(shoppingCart.getIssue().getPricePercentAmount().doubleValue());
            shoppingCartsModel.setLeftNumber(left);
            //得到图片绝对地址
            String url = staticResourceService.getResource(shoppingCart.getIssue().getGoods().getDefaultPictureUrl()).toString();
            shoppingCartsModel.setImgUrl(url);
            //如果购买量大于库存量，默认调整为库存量
            shoppingCartsModel.setBuyNum(shoppingCart.getBuyAmount() > left ? left : shoppingCart.getBuyAmount());

            shoppingCartsModel.setStepNum(shoppingCart.getIssue().getStepAmount());
            shoppingCartsModel.setDetail(shoppingCart.getIssue().getGoods().getTitle());
            shoppingCartsModel.setBuyMoney(shoppingCartsModel.getPerMoney() * shoppingCartsModel.getBuyNum());
        }
        return shoppingCartsModel;
    }

    @Override
    public PayModel balance(Long cartId, Integer buyNum) {
        ShoppingCart shoppingCart = shoppingCartRepository.findOne(cartId);
        PayModel payModel = new PayModel();
        if (shoppingCart == null) {
            return null;
        }
        if (shoppingCart.getIssue().getStatus() != CommonEnum.IssueStatus.going) {
            //如果期号已经被买满则将购物车删除
            shoppingCartRepository.delete(shoppingCart);
            return null;
        } else {
            payModel.setDetail(shoppingCart.getIssue().getGoods().getTitle());
            Long left = shoppingCart.getIssue().getToAmount() - shoppingCart.getIssue().getBuyAmount();
            if (left < shoppingCart.getBuyAmount()) {
                //如果剩余量不足，购买量超过了剩余量，则将购物车的数量更改为剩余量
                shoppingCart.setBuyAmount(left);
                shoppingCart = shoppingCartRepository.saveAndFlush(shoppingCart);
                payModel.setPayMoney(left * shoppingCart.getIssue().getPricePercentAmount().doubleValue());
            } else {
                //正常的结算
                payModel.setPayMoney(shoppingCart.getBuyAmount() * shoppingCart.getIssue().getPricePercentAmount().doubleValue());
            }
            payModel.setCartsId(cartId);
            payModel.setType(1);
            return payModel;
        }
    }

    @Override
    public String getWeixinPayUrl(Orders orders) {
        String backUri = commonConfigService.getWebUrl() + "web/payCallbackWeixin";
        System.out.println(backUri);
        //授权后要跳转的链接所需的参数一般有会员号，金额，订单号之类，
        //最好自己带上一个加密字符串将金额加上一个自定义的key用MD5签名或者自己写的签名,
        //比如 Sign = %3D%2F%CS%
        backUri = backUri + "?orderNo=" + orders.getId();
        //URLEncoder.encode 后可以在backUri 的url里面获取传递的所有参数
        backUri = URLEncoder.encode(backUri);
        String url = WeixinPayUrl.getWeixinPayUrl();
        //scope 参数视各自需求而定，这里用scope=snsapi_base 不弹出授权页面直接授权目的只获取统一支付接口的openid
//        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
//                "appid=" + appid +
//                "&redirect_uri=" +
//                backUri +
//                "&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
        return url;
    }

    @Override
    public Orders createOrders(PayModel payModel) throws IOException {
        ShoppingCart shoppingCart = shoppingCartRepository.findOne(payModel.getCartsId());
        if (payModel.getType() == 1) {
            //如果是正常购买
            if (shoppingCart == null ||
                    shoppingCart.getIssue().getStatus() != CommonEnum.IssueStatus.going ||
                    shoppingCart.getIssue().getToAmount() < (shoppingCart.getIssue().getBuyAmount() + shoppingCart.getBuyAmount())) {
                //如果物品过期,或者数量错误，则返回null
                return null;
            } else {
                //正常生成订单
                Orders orders = new Orders();
                orders.setUser(shoppingCart.getUser());
                orders.setTime(new Date());
                orders.setMoney(shoppingCart.getIssue().getPricePercentAmount().multiply(new BigDecimal(String.valueOf(shoppingCart.getBuyAmount()))));
                orders.setOrderType(CommonEnum.OrderType.raiders);
                orders.setPayType(payModel.getPayType() == 1 ? CommonEnum.PayType.weixin : CommonEnum.PayType.alipay);
                orders.setTotalMoney(orders.getMoney());
                orders.setStatus(CommonEnum.OrderStatus.paying);
                orders = ordersRepository.saveAndFlush(orders);
                OrdersItem ordersItem = new OrdersItem();
                ordersItem.setIssue(shoppingCart.getIssue());
                ordersItem.setStatus(CommonEnum.OrderStatus.paying);
                ordersItem.setAmount(shoppingCart.getBuyAmount());
                ordersItem.setOrder(orders);
                ordersItem = ordersItemRepository.saveAndFlush(ordersItem);

                //同时删除购物车
                shoppingCartRepository.clearShoppingCarts(shoppingCart.getUser());

                return orders;
            }
        } else if (payModel.getType() == 2) {
            //全额购买暂时废弃 by Xhk
            //商品库存如果不够则不能全额购买
            com.huotu.huobanplus.common.entity.Goods goods=goodsRestRepository.getOneByPK(shoppingCart.getIssue().getGoods().getToMallGoodsId());
            if(goods.getStock()<3){
                //如果库存量小于3则不允许全额购买
                return null;
            }
            if (shoppingCart == null){
                return null;
            }
            //如果是全额购买
            Orders orders = new Orders();
            orders.setStatus(CommonEnum.OrderStatus.paying);
            orders.setTime(new Date());
            orders.setUser(shoppingCart.getUser());
            orders.setTotalMoney(shoppingCart.getIssue().getPricePercentAmount().multiply(new BigDecimal(String.valueOf(shoppingCart.getIssue().getToAmount()))));
            orders.setOrderType(CommonEnum.OrderType.allpay);
            orders.setMoney(orders.getTotalMoney());
            orders = ordersRepository.saveAndFlush(orders);
            OrdersItem ordersItem = new OrdersItem();
            ordersItem.setOrder(orders);
            ordersItem.setAmount(shoppingCart.getIssue().getToAmount());
            ordersItem.setStatus(CommonEnum.OrderStatus.paying);
            // 全额购买中的期号只是为了找到这个物品然后减库存
            ordersItem.setIssue(shoppingCart.getIssue());
            ordersItem = ordersItemRepository.saveAndFlush(ordersItem);
            return orders;
        }
        return null;
    }

    @Override
    public ShoppingCart allToShoppingCarts(Issue issue, User user) {
//        Orders orders=new Orders();
//        orders.setStatus(CommonEnum.OrderStatus.paying);
//        orders.setTime(new Date());
//        orders.setUser(user);
//        orders.setTotalMoney(issue.getPricePercentAmount().multiply(new BigDecimal(String.valueOf(issue.getToAmount()))));
//        orders.setOrderType(CommonEnum.OrderType.allpay);
//        orders.setMoney(orders.getTotalMoney());
//        orders=ordersRepository.saveAndFlush(orders);
//        OrdersItem ordersItem=new OrdersItem();
//        ordersItem.setOrder(orders);
//        ordersItem.setAmount(issue.getToAmount());
//        ordersItem.setStatus(CommonEnum.OrderStatus.paying);
//        //todo 全额购买中的期号只是为了找到这个物品然后减库存
//        ordersItem.setIssue(issue);
//        ordersItem=ordersItemRepository.saveAndFlush(ordersItem);

        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCart.setIssue(issue);
        shoppingCart.setBuyAmount(issue.getToAmount());
        shoppingCart = shoppingCartRepository.saveAndFlush(shoppingCart);
        return shoppingCart;
    }

    @Override
    public PayModel allPayAndGetModel(String shoppingCartId) {
        if (shoppingCartId == null) {
            //如果购物车为空
            return null;
        }
        ShoppingCart shoppingCart = shoppingCartRepository.findOne(Long.parseLong(shoppingCartId));
        if (shoppingCart == null) {
            //如果购物车不存在
            return null;
        }
        PayModel payModel = new PayModel();
        payModel.setCartsId(Long.parseLong(shoppingCartId));
        payModel.setType(2);
        payModel.setDetail(shoppingCart.getIssue().getGoods().getTitle());
        payModel.setPayMoney(shoppingCart.getIssue().getPricePercentAmount().multiply(new BigDecimal(String.valueOf(shoppingCart.getIssue().getBuyAmount()))).doubleValue());
        return payModel;
    }


}
