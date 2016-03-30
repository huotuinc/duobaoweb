package com.huotu.duobaoweb.service.impl;

import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.common.thirdparty.WeixinUtils;
import com.huotu.duobaoweb.entity.*;
import com.huotu.duobaoweb.model.PayInfoModel;
import com.huotu.duobaoweb.model.PayModel;
import com.huotu.duobaoweb.model.ShoppingCartsModel;
import com.huotu.duobaoweb.repository.OrdersItemRepository;
import com.huotu.duobaoweb.repository.OrdersRepository;
import com.huotu.duobaoweb.repository.ShoppingCartRepository;
import com.huotu.duobaoweb.repository.UserRepository;
import com.huotu.duobaoweb.service.CommonConfigService;
import com.huotu.duobaoweb.service.ShoppingService;
import com.huotu.duobaoweb.service.StaticResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.Date;

/**
 * Created by xhk on 2016/3/25.
 */
@Service
public class ShoppingServiceImpl implements ShoppingService{

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

    @Override
    public void joinToShoppingCarts(Issue issue, User user,Long buyNum) {

        this.clearShoppingCarts(user);
        ShoppingCart shoppingCart=new ShoppingCart();
        if(buyNum==null){
            shoppingCart.setBuyAmount(issue.getDefaultAmount());
        }else {
            //����������������ʣ����������������Ϊʣ����
            Long left=issue.getToAmount()-issue.getBuyAmount();
            shoppingCart.setBuyAmount(left<buyNum?left:buyNum);
        }
        shoppingCart.setIssue(issue);
        shoppingCart.setUser(user);
        shoppingCart=shoppingCartRepository.saveAndFlush(shoppingCart);
    }

    public void clearShoppingCarts(User user) {
        shoppingCartRepository.clearShoppingCarts(user);
    }

    @Override
    public ShoppingCartsModel getShoppingCartsModel(Long userId) throws URISyntaxException {

        ShoppingCart shoppingCart=shoppingCartRepository.findOneByUserId(userId);
        ShoppingCartsModel shoppingCartsModel = new ShoppingCartsModel();
        if(shoppingCart!=null&&shoppingCart.getIssue().getStatus()!= CommonEnum.IssueStatus.going){
            //������ﳵ�е���Ϣ�ѹ�����ɾ�����ﳵ�е���Ϣ
            shoppingCartRepository.clearShoppingCarts(userRepository.findOne(userId));
            return null;
        }
        if(shoppingCart!=null) {
            Long left=shoppingCart.getIssue().getToAmount()-shoppingCart.getIssue().getBuyAmount();
            shoppingCartsModel.setCartId(shoppingCart.getId());
            shoppingCartsModel.setNeedNumber(shoppingCart.getIssue().getToAmount());
            shoppingCartsModel.setPerMoney(shoppingCart.getIssue().getPricePercentAmount().doubleValue());
            shoppingCartsModel.setLeftNumber(left);
            //�õ�ͼƬ���Ե�ַ
            String url=staticResourceService.getResource(shoppingCart.getIssue().getGoods().getDefaultPictureUrl()).toString();
            shoppingCartsModel.setImgUrl(url);
            //������������ڿ������Ĭ�ϵ���Ϊ�����
            shoppingCartsModel.setBuyNum(shoppingCart.getBuyAmount()>left?left:shoppingCart.getBuyAmount());

            shoppingCartsModel.setDetail(shoppingCart.getIssue().getGoods().getTitle());
            shoppingCartsModel.setBuyMoney(shoppingCartsModel.getPerMoney()*shoppingCartsModel.getBuyNum());
        }
        return shoppingCartsModel;
    }

    @Override
    public PayModel balance(Long cartId, Integer buyNum) {
        ShoppingCart shoppingCart=shoppingCartRepository.findOne(cartId);
        PayModel payModel=new PayModel();
        if(shoppingCart==null){
            return null;
        }
        if(shoppingCart.getIssue().getStatus()!= CommonEnum.IssueStatus.going){
            //����ں��Ѿ��������򽫹��ﳵɾ��
            shoppingCartRepository.delete(shoppingCart);
            return null;
        }else{
            payModel.setDetail(shoppingCart.getIssue().getGoods().getTitle());
            Long left=shoppingCart.getIssue().getToAmount()-shoppingCart.getIssue().getBuyAmount();
            if(left<shoppingCart.getBuyAmount()) {
                //���ʣ�������㣬������������ʣ�������򽫹��ﳵ����������Ϊʣ����
                shoppingCart.setBuyAmount(left);
                shoppingCart=shoppingCartRepository.saveAndFlush(shoppingCart);
                payModel.setPayMoney(left * shoppingCart.getIssue().getPricePercentAmount().doubleValue());
            }else{
                //�����Ľ���
                payModel.setPayMoney(shoppingCart.getBuyAmount()*shoppingCart.getIssue().getPricePercentAmount().doubleValue());
            }
            return payModel;
        }
    }

    @Override
    public String getWeixinPayUrl(Orders orders) {
        String appid = WeixinUtils.getAppID();
        String backUri =commonConfigService.getWebUrl()+ "web/payCallbackWeixin";
        System.out.println(backUri);
        //��Ȩ��Ҫ��ת����������Ĳ���һ���л�Ա�ţ���������֮�࣬
        //����Լ�����һ�������ַ�����������һ���Զ����key��MD5ǩ�������Լ�д��ǩ��,
        //���� Sign = %3D%2F%CS%
        backUri = backUri+"?orderNo="+orders.getId();
        //URLEncoder.encode �������backUri ��url�����ȡ���ݵ����в���
        backUri = URLEncoder.encode(backUri);
        //scope �����Ӹ������������������scope=snsapi_base ��������Ȩҳ��ֱ����ȨĿ��ֻ��ȡͳһ֧���ӿڵ�openid
        String url = "https://open.weixin.qq.com/connect/oauth2/authorize?" +
                "appid=" + appid+
                "&redirect_uri=" +
                backUri+
                "&response_type=code&scope=snsapi_base&state=123#wechat_redirect";
        return url;
    }

    @Override
    public Orders createOrders(PayInfoModel payInfoModel) {
        ShoppingCart shoppingCart=shoppingCartRepository.findOne(payInfoModel.getCartsId());
        if(shoppingCart==null||
                shoppingCart.getIssue().getStatus()!= CommonEnum.IssueStatus.going||
                shoppingCart.getIssue().getToAmount()<(shoppingCart.getIssue().getBuyAmount()+shoppingCart.getBuyAmount())){
            //�����Ʒ����,�������������򷵻�null
            return null;
        }else{
            //�������ɶ���
            Orders orders=new Orders();
            orders.setUser(shoppingCart.getUser());
            orders.setTime(new Date());
            orders.setMoney(shoppingCart.getIssue().getPricePercentAmount().multiply(new BigDecimal(String.valueOf(shoppingCart.getBuyAmount()))));
            orders.setOrderType(CommonEnum.OrderType.raiders);
            orders.setPayType(payInfoModel.getPayType()==1?CommonEnum.PayType.weixin:CommonEnum.PayType.alipay);
            orders.setTotalMoney(orders.getMoney());
            orders.setStatus(CommonEnum.OrderStatus.paying);
            orders=ordersRepository.saveAndFlush(orders);
            OrdersItem ordersItem=new OrdersItem();
            ordersItem.setIssue(shoppingCart.getIssue());
            ordersItem.setStatus(CommonEnum.OrderStatus.paying);
            ordersItem.setAmount(shoppingCart.getBuyAmount());
            ordersItem.setOrder(orders);
            ordersItem=ordersItemRepository.saveAndFlush(ordersItem);

            //ͬʱɾ�����ﳵ
            shoppingCartRepository.clearShoppingCarts(shoppingCart.getUser());

            return orders;
        }

    }


}
