package com.huotu.duobaoweb.base;

import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.*;
import com.huotu.duobaoweb.repository.*;
import com.huotu.duobaoweb.service.StaticResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;

import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Created by lhx on 2016/3/25.
 */

public class BaseTest {


    @Autowired
    UserRepository userRepository;

    @Autowired
    StaticResourceService staticResourceService;

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    IssueRepository issueRepository;

    @Autowired
    DeliveryRepository deliveryRepository;

    @Autowired
    UserBuyFlowRepository userBuyFlowRepository;

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    OrdersItemRepository ordersItemRepository;

    @Autowired
    UserNumberRepository userNumberRepository;



    /**
     * 生成一个绑定了手机而且拥有登录token的用户
     *
     * @param password
     * @param userRepository
     * @return
     * @throws UnsupportedEncodingException
     */
    public User generateUserWithMobileWithToken(String password, UserRepository userRepository) throws UnsupportedEncodingException {
        User user = generateUserWithMobileWithoutToken(password, userRepository);
        user.setToken(DigestUtils.md5DigestAsHex(UUID.randomUUID().toString().getBytes("UTF-8")));
        user.setUserHead(staticResourceService.USER_HEAD_PATH + "defaultH.jpg");
        user.setUserFromType(CommonEnum.UserFromType.register);
        user.setMoney(new BigDecimal("1000000"));
        return userRepository.saveAndFlush(user);
    }


    public User generateUserWithoutMobile(@NotNull String password, UserRepository userRepository) throws UnsupportedEncodingException {
        String userName = generateInexistentMobile(userRepository);
        User user = new User();
        user.setEnabled(true);
        user.setRegTime(new Date(System.currentTimeMillis() + new Random().nextInt(360 * 30 * 24 * 60 * 60)));
        user.setUsername(userName);
        user.setPassword(DigestUtils.md5DigestAsHex(password.getBytes("UTF-8")).toLowerCase());
        user.setUserHead(staticResourceService.USER_HEAD_PATH + "defaultH.jpg");
        return userRepository.saveAndFlush(user);
    }

    /**
     * 生成一个班定了手机 但是没有token的用户
     *
     * @param password
     * @param userRepository
     * @return
     * @throws UnsupportedEncodingException
     */
    public User generateUserWithMobileWithoutToken(String password, UserRepository userRepository) throws UnsupportedEncodingException {
        User user = generateUserWithoutMobile(password, userRepository);
        user.setMobileBinded(true);
        user.setRegTime(new Date(System.currentTimeMillis() + new Random().nextInt(360 * 30 * 24 * 60 * 60)));
        user.setMobile(user.getUsername());
        user.setUserHead(staticResourceService.USER_HEAD_PATH + "defaultH.jpg");
        return userRepository.saveAndFlush(user);
    }


    /**
     * 返回一个不存在的手机号码
     *
     * @param userRepository
     * @return 手机号码
     */
    public String generateInexistentMobile(UserRepository userRepository) {
        while (true) {
            String number = "" + System.currentTimeMillis();
            number = number.substring(number.length() - 11);
            char[] data = number.toCharArray();
            data[0] = '1';
            number = new String(data);
            if (userRepository.count() == 0)
                return number;
            if (userRepository.countByMobile(number) == 0) {
                return number;
            }
        }
    }


    /**
     * 添加个商品
     * @return 返回添加后的商品
     */
    public Goods saveGodds() {
        Goods good = new Goods();
        good.setTitle("裙子");
        good.setDefaultPictureUrl(staticResourceService.GOODS_PICTURE_PATH + "defaultH.jpg");
        good.setCharacters("连衣裙");
        good.setStatus(CommonEnum.GoodsStatus.up);
        good.setDefaultAmount(1L);
        return goodsRepository.saveAndFlush(good);
    }


    /**
     * 获取一期夺宝
     *
     * @param goods
     * @return
     */
    public Issue saveIssue(Goods goods, User user, int i) {
        Issue issue = new Issue();
        if (i % 2 == 0) {
            issue.setStatus(CommonEnum.IssueStatus.drawing);
        } else {
            if (i % 3 == 0) {
                issue.setStatus(CommonEnum.IssueStatus.drawed);
                issue.setAwardingUser(user);
                issue.setAwardingDate(new Date());
                issue.setLuckyNumber(20160121l + i);

            } else {
                issue.setStatus(CommonEnum.IssueStatus.going);
            }
        }
        issue.setToAmount(20l);
        issue.setBuyAmount(12l);
        issue.setDefaultAmount(1l);
        issue.setGoods(goods);
        issue = issueRepository.saveAndFlush(issue);
        if (i % 3 == 0) {
            Delivery delivery = new Delivery();
            delivery.setDeliveryStatus(CommonEnum.DeliveryStatus.GetPrize);
            delivery.setDeliveryTime(new Date());
            delivery.setUser(user);
            delivery.setIssue(issue);
            deliveryRepository.saveAndFlush(delivery);
        }
        return issue;
    }

    /**
     * 添加用户中奖号码
     * @param user
     * @param issue
     * @param i
     * @return
     */
    public UserNumber saveUserNumber(User user, Issue issue, int i) {
        UserNumber userNumber = new UserNumber();
        userNumber.setIssue(issue);
        userNumber.setUser(user);
        userNumber.setNumber(100031L + i);
        return userNumberRepository.saveAndFlush(userNumber);
    }

    /**
     * 添加订单
     *
     * @param user
     * @param i
     * @return
     */
    public Orders saveOrders(User user, int i) {
        Orders order = new Orders();
        order.setId("20160115080808485" + i);
        if (i % 2 == 0) {
            order.setPayType(CommonEnum.PayType.weixin);
        } else {
            order.setPayType(CommonEnum.PayType.alipay);
        }

        order.setStatus(CommonEnum.OrderStatus.payed);
        order.setMoney(BigDecimal.valueOf(10));
        order.setUser(user);
        order.setOrderType(CommonEnum.OrderType.raiders);
        order.setPayTime(new Date());
        return ordersRepository.saveAndFlush(order);
    }

    /**
     * 添加订单明细
     *
     * @param orders
     * @param issue
     * @return
     */
    public OrdersItem saveOrderItem(Orders orders, Issue issue) {
        OrdersItem oi = new OrdersItem();
        oi.setAmount(1l);
        oi.setOrder(orders);
        oi.setIssue(issue);
        return ordersItemRepository.saveAndFlush(oi);
    }

    /**
     * 添加用户夺宝记录
     *
     * @param user
     * @param issue
     * @return
     */
    public UserBuyFlow saveUserBuyFlow(User user, Issue issue) {
        UserBuyFlow userBuyFlow = new UserBuyFlow();
        userBuyFlow.setTime(new Date().getTime());
        userBuyFlow.setUser(user);
        userBuyFlow.setIssue(issue);
        userBuyFlow.setAmount(10L);
        return userBuyFlowRepository.saveAndFlush(userBuyFlow);
    }



}
