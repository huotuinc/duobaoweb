package com.huotu.mallduobao.service.impl;

import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.huobanplus.sdk.common.repository.MerchantRestRepository;
import com.huotu.mallduobao.common.WeixinPayUrl;
import com.huotu.mallduobao.entity.*;
import com.huotu.mallduobao.model.PayModel;
import com.huotu.mallduobao.model.PaysResultShowModel;
import com.huotu.mallduobao.model.ShoppingCartsModel;
import com.huotu.mallduobao.repository.OrdersItemRepository;
import com.huotu.mallduobao.repository.OrdersRepository;
import com.huotu.mallduobao.repository.ShoppingCartRepository;
import com.huotu.mallduobao.repository.UserRepository;
import com.huotu.mallduobao.service.*;
import com.huotu.mallduobao.utils.CommonEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by xhk on 2016/3/25.
 */
@Service
public class ShoppingServiceImpl implements ShoppingService {

    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    @Autowired
    private MerchantRestRepository merchantRestRepository;

    @Autowired
    private RaidersCoreService raidersCoreService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SecurityService securityService;

    @Autowired
    private CommonConfigService commonConfigService;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private OrdersItemRepository ordersItemRepository;

    @Autowired
    private GoodsRestRepository goodsRestRepository;

    @Autowired
    private StaticResourceService staticResourceService;


    @Override
    public ShoppingCart joinToShoppingCarts(Issue issue, User user, Long buyNum) {

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
        return shoppingCart;
    }

    public void clearShoppingCarts(User user) {
        shoppingCartRepository.clearShoppingCarts(user);
    }

    @Override
    public ShoppingCartsModel getShoppingCartsModel(Long userId) throws URISyntaxException {

        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findOneByUserId(userId);
        ShoppingCartsModel shoppingCartsModel = new ShoppingCartsModel();
        if(shoppingCarts.size()==0){
            return null;
        } else if (shoppingCarts.size() != 0 && shoppingCarts.get(0).getIssue().getStatus() != CommonEnum.IssueStatus.going) {
            //如果购物车中的信息已过期则删除购物车中的信息
            shoppingCartRepository.clearShoppingCarts(userRepository.findOne(userId));
            return null;
        }
        if (shoppingCarts.size() != 0) {
            Long left = shoppingCarts.get(0).getIssue().getToAmount() - shoppingCarts.get(0).getIssue().getBuyAmount();
            shoppingCartsModel.setCartId(shoppingCarts.get(0).getId());
            shoppingCartsModel.setNeedNumber(shoppingCarts.get(0).getIssue().getToAmount());
            shoppingCartsModel.setPerMoney(shoppingCarts.get(0).getIssue().getPricePercentAmount().doubleValue());
            shoppingCartsModel.setLeftNumber(left);
            //得到图片绝对地址
            shoppingCartsModel.setImgUrl(staticResourceService.getResource(shoppingCarts.get(0).getIssue().getGoods().getDefaultPictureUrl()).toString());
            //如果购买量大于库存量，默认调整为库存量
            shoppingCartsModel.setBuyNum(shoppingCarts.get(0).getBuyAmount() > left ? left : shoppingCarts.get(0).getBuyAmount());

            shoppingCartsModel.setStepNum(shoppingCarts.get(0).getIssue().getStepAmount());
            shoppingCartsModel.setDetail(shoppingCarts.get(0).getIssue().getGoods().getTitle());
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
            if(buyNum!=null&&buyNum!=0) {
                if (left <buyNum) {
                    //如果剩余量不足，购买量超过了剩余量，则将购物车的数量更改为剩余量
                    shoppingCart.setBuyAmount(left);
                    shoppingCart = shoppingCartRepository.saveAndFlush(shoppingCart);
                    payModel.setPayMoney(left * shoppingCart.getIssue().getPricePercentAmount().doubleValue());
                } else {
                    //正常的结算
                    shoppingCart.setBuyAmount(Long.parseLong(String.valueOf(buyNum)));
                    shoppingCart = shoppingCartRepository.saveAndFlush(shoppingCart);
                    payModel.setPayMoney(buyNum* shoppingCart.getIssue().getPricePercentAmount().doubleValue());
                }
            }else{
                if (left < shoppingCart.getBuyAmount()) {
                    //如果剩余量不足，购买量超过了剩余量，则将购物车的数量更改为剩余量
                    shoppingCart.setBuyAmount(left);
                    shoppingCart = shoppingCartRepository.saveAndFlush(shoppingCart);
                    payModel.setPayMoney(left * shoppingCart.getIssue().getPricePercentAmount().doubleValue());
                } else {
                    //正常的结算
                    payModel.setPayMoney(shoppingCart.getBuyAmount() * shoppingCart.getIssue().getPricePercentAmount().doubleValue());
                }
            }
            payModel.setCartsId(cartId);
            payModel.setType(1);
            return payModel;
        }
    }

    @Override
    public String getWeixinPayUrl(Orders orders) throws IOException {
        //http://{subdomain}.{maindomain}/weixin/pay/payment_delegate.aspx
        Date date=new Date();
        String notifyurl = commonConfigService.getWebUrl() + "/pay/payCallbackWeixin";
        String returnurl=commonConfigService.getWebUrl()+"/user/showResult";
        notifyurl = notifyurl + "?orderNo=" + orders.getId();
        returnurl= returnurl + "?orderNo=" + orders.getId();
//        notifyurl = URLEncoder.encode(notifyurl,"UTF-8");
//        returnurl = URLEncoder.encode(returnurl,"UTF-8");
        WeixinPayUrl.subdomain=merchantRestRepository.getOneByPK(String.valueOf(WeixinPayUrl.customerid)).getSubDomain();
        WeixinPayUrl.maindomain=commonConfigService.getMainDomain();
        WeixinPayUrl.notifyurl=notifyurl;
        WeixinPayUrl.returnurl=returnurl;
        WeixinPayUrl.outtradeno=orders.getId();
        WeixinPayUrl.timestamp=date.getTime();
        WeixinPayUrl.totalfee=orders.getMoney().doubleValue();
        WeixinPayUrl.title="夺宝活动";//URLEncoder.encode("夺宝活动","UTF-8");
        Map<String,String> paramMap=this.putParamToPayMap();
        WeixinPayUrl.sign=securityService.getPaySign(paramMap);
        WeixinPayUrl.notifyurl = URLEncoder.encode(notifyurl, "UTF-8");
        WeixinPayUrl.returnurl = URLEncoder.encode(returnurl,"UTF-8");
        WeixinPayUrl.title=URLEncoder.encode("夺宝活动","UTF-8");
        String url = WeixinPayUrl.getWeixinPayUrl();
        return url;
    }

    @Override
    public Map<String, String> putParamToPayMap() {
        Map<String,String> result=new HashMap<String,String>();
        result.put("customerid",String.valueOf(WeixinPayUrl.customerid));
        result.put("returnurl",WeixinPayUrl.returnurl);
        result.put("outtradeno",WeixinPayUrl.outtradeno);
        result.put("openid",WeixinPayUrl.openid);
        result.put("title",WeixinPayUrl.title);
        result.put("timestamp",String.valueOf(WeixinPayUrl.timestamp));
        result.put("totalfee",String.valueOf(WeixinPayUrl.totalfee));
        result.put("notifyurl",WeixinPayUrl.notifyurl);
        return result;
    }

    @Override
    public Orders createOrders(PayModel payModel) throws IOException {
        ShoppingCart shoppingCart = shoppingCartRepository.findOne(payModel.getCartsId());
        if (payModel.getType()!=null&&payModel.getType() == 1) {
            //如果是正常购买
            if (shoppingCart == null ||
                    shoppingCart.getIssue().getStatus() != CommonEnum.IssueStatus.going ||
                    shoppingCart.getIssue().getToAmount() < (shoppingCart.getIssue().getBuyAmount() + shoppingCart.getBuyAmount())) {
                //如果物品过期,或者数量错误，则返回null
                return null;
            } else {
                //正常生成订单
                Orders orders = new Orders();
                orders.setId(raidersCoreService.createOrderNo(new Date(), shoppingCart.getUser().getId()));
                orders.setUser(shoppingCart.getUser());
                orders.setTime(new Date());
                //todo 测试都是付一分钱
                orders.setMoney(shoppingCart.getIssue().getPricePercentAmount().multiply(new BigDecimal(String.valueOf(shoppingCart.getBuyAmount()))));
                //orders.setMoney(new BigDecimal("0.01"));
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
        } else if (payModel.getType()!=null&&payModel.getType() == 2) {
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
            orders.setId(raidersCoreService.createOrderNo(new Date(),shoppingCart.getUser().getId()));
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

    @Override
    public PaysResultShowModel getPayResultShowModel(String orderNo) {
        OrdersItem ordersItem=ordersItemRepository.findByOrderId(orderNo);
        PaysResultShowModel paysResultShowModel=new PaysResultShowModel();
        paysResultShowModel.setIssueId(ordersItem.getIssue().getId());
        paysResultShowModel.setDetail(ordersItem.getIssue().getGoods().getTitle());
        paysResultShowModel.setNeedNumber(ordersItem.getIssue().getToAmount());
        paysResultShowModel.setCustomerId(ordersItem.getIssue().getGoods().getMerchantId());
        paysResultShowModel.setTitle("您成功参与了1件商品的夺宝，信息如下：");
        return paysResultShowModel;
    }


}
