package com.huotu.duobaoweb.base;

import com.huotu.duobaoweb.boot.RootConfig;
import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.controller.page.AbstractPage;
import com.huotu.duobaoweb.entity.*;
import com.huotu.duobaoweb.entity.Goods;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.repository.*;
import com.huotu.duobaoweb.service.CommonConfigService;
import com.huotu.duobaoweb.service.RaidersCoreService;
import com.huotu.duobaoweb.service.StaticResourceService;
import com.huotu.huobanplus.common.entity.*;
import com.huotu.huobanplus.sdk.base.BaseClientSpringConfig;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.validation.constraints.NotNull;
import java.io.IOException;
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

    @Autowired
    RaidersCoreService raidersCoreService;

    @Autowired
    CommonConfigService commonConfigService;

    @Autowired
    GoodsRestRepository goodsRestRepository;

    protected WebDriver driver;

//    @Resource(name = "entityManagerFactory")
//    protected EntityManagerFactory entityManagerFactory;
//
//    @Resource(name = "transactionManager")
//    protected JpaTransactionManager transactionManager;


    @Autowired
    WebApplicationContext context;

    /**
     * 初始化webdriver
     */
    @Before
    public void init() {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        driver = MockMvcHtmlUnitDriverBuilder.mockMvcSetup(mockMvc).build();
    }

    /**
     * 初始化逻辑页面
     *
     * @param <T>   该页面相对应的逻辑页面
     * @param clazz 该页面相对应的逻辑页面的类
     * @return 页面实例
     */
    public <T extends AbstractPage> T initPage(Class<T> clazz) {
        T page = PageFactory.initElements(driver, clazz);
        page.validate();
        return page;
    }


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
     * 生成商品
     *
     * @param amount          人次量
     * @param goodsRepository
     * @param issueRepository
     * @return
     */
    public Goods generateGoods(Long amount, GoodsRepository goodsRepository, IssueRepository issueRepository) throws IOException {
        return generateGoods(amount, false, goodsRepository, issueRepository);
    }

    /**
     * 生成商品
     *
     * @param amount          人次量
     * @param userSimulated   是否启用模拟用户
     * @param goodsRepository
     * @param issueRepository
     * @return
     */
    public Goods generateGoods(Long amount, Boolean userSimulated, GoodsRepository goodsRepository, IssueRepository issueRepository) throws IOException {
        Goods goods = new Goods();
        goods.setStatus(CommonEnum.GoodsStatus.up);
        goods.setToAmount(amount);
        goods.setDefaultAmount(10L);
        goods.setPricePercentAmount(new BigDecimal(1));
        goods.setMerchantId(Long.parseLong(commonConfigService.getMallCustomerId()));

        com.huotu.huobanplus.common.entity.Goods mallGoods = generateMallGoods();
        goods.setToMallGoodsId(mallGoods.getId());

        goods = goodsRepository.saveAndFlush(goods);
        raidersCoreService.generateIssue(goods);


        return goods;
    }

    public com.huotu.huobanplus.common.entity.Goods generateMallGoods() throws IOException {
        com.huotu.huobanplus.common.entity.Goods goods = new com.huotu.huobanplus.common.entity.Goods();
        goods.setStock(100);
        goods.setCreateTime(new Date());
        goods.setScenes(4);
        goods.setDisabled(false);
        goods.setMarketPrice(100);
        goods.setPrice(100);
        return goodsRestRepository.insert(goods);
    }

    /***
     * 创建已购买的订单
     *
     * @param user
     * @param issue
     * @param amount
     * @param ordersRepository
     * @param ordersItemRepository
     * @param issueRepository
     * @return
     */
    public Orders generateOrdersWithPayed(User user, Issue issue, Long amount
            , OrdersRepository ordersRepository, OrdersItemRepository ordersItemRepository
            , IssueRepository issueRepository) throws IOException {
        Date date = new Date();

        BigDecimal money = issue.getPricePercentAmount().multiply(new BigDecimal(amount));

        Orders orders = new Orders();
        orders.setId(raidersCoreService.createOrderNo(date, user.getId()));
        orders.setTime(date);
        orders.setStatus(CommonEnum.OrderStatus.payed);
        orders.setUser(user);
        orders.setPayType(CommonEnum.PayType.remain);
        orders.setMoney(money);
        orders.setOrderType(CommonEnum.OrderType.raiders);
        orders.setPayTime(date);
        orders.setTotalMoney(money);
        orders = ordersRepository.saveAndFlush(orders);

        OrdersItem ordersItem = new OrdersItem();
        ordersItem.setStatus(CommonEnum.OrderStatus.payed);
        ordersItem.setAmount(amount);
        ordersItem.setIssue(issue);
        ordersItem.setOrder(orders);
        ordersItemRepository.saveAndFlush(ordersItem);

        issue.setBuyAmount(issue.getBuyAmount() + amount);
        issue = issueRepository.saveAndFlush(issue);
        //创建抽奖号码
        raidersCoreService.generateUserNumber(user, issue, amount, orders);

        if (issue.getBuyAmount() >= issue.getToAmount()) {
            raidersCoreService.generateIssue(issue.getGoods());
        }

        return orders;
    }


    /**
     * 添加个商品
     *
     * @return 返回添加后的商品
     */
    public Goods saveGodds() {
        Goods goods = new Goods();
        goods.setDefaultPictureUrl("/resources/images/dsds.jpg");
        goods.setPictureUrls("/resources/images/dsds.jpg,/resources/images/dsds.jpg,/resources/images/dsds.jpg");
        goods.setTitle("iphone6s");
        goods.setCharacters("独一无二");
        goods.setDefaultAmount(10L);
        goods.setStepAmount(10L);
        goods.setToAmount(5890L);
        goods.setPricePercentAmount(new BigDecimal(1L));
        goods.setStatus(CommonEnum.GoodsStatus.up);
        Date curDate = new Date();
        goods.setStartTime(curDate);
        goods.setEndTime(new Date(curDate.getTime() + 3600 * 24 * 1000));
        goods.setShareTitle("分享标题");
        goods.setShareDescription("分享描述");
        goods.setSharePictureUrl("/resources/images/dsds.jpg");
        return goodsRepository.saveAndFlush(goods);
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
//            delivery.setDeliveryTime(new Date());
            delivery.setUser(user);
            delivery.setIssue(issue);
            deliveryRepository.saveAndFlush(delivery);
        }
        return issue;
    }

    /**
     * 添加用户中奖号码
     *
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
        oi.setStatus(CommonEnum.OrderStatus.payed);
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
