package com.huotu.duobaoweb.controller;
import com.huotu.huobanplus.sdk.base.BaseClientSpringConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.PageFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;

import java.util.Date;
import java.util.UUID;


/**
 * Created by lgh on 2016/3/30.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class, BaseClientSpringConfig.class})
@ActiveProfiles("development")
@Transactional
public class PersonalControllerTest extends BaseTest {

    private Log log = LogFactory.getLog(PersonalControllerTest.class);


    @Autowired
    UserRepository userRepository;

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    IssueRepository issueRepository;

    @Autowired
    OrdersItemRepository ordersItemRepository;

    @Autowired
    CachedIssueLeaveNumberRepository cachedIssueLeaveNumberRepository;

    @Autowired
    CacheService cacheService;

    @Autowired
    RaidersCoreService raidersCoreService;

    @Autowired
    UserBuyFlowRepository userBuyFlowRepository;
    @Autowired
    OrdersRepository ordersRepository;
    @Autowired
    DeliveryRepository deliveryRepository;


    private User currentUser;
    private Issue currentIssue;
    private Issue currentIssue2;
    private UserBuyFlow userBuyFlow;
    private Goods goods;
    private Delivery delivery;
    private Orders orders;

    @Before
    public void before() {
        //商品
        goods = new Goods();
        goods.setTitle("cosytest");
        goods.setCharacters("这是测试商品");
        goods.setDefaultAmount(10L);
        goods.setToAmount(100L);
        goods.setPricePercentAmount(new BigDecimal(1));
        goods.setStatus(CommonEnum.GoodsStatus.up);
        goods = goodsRepository.saveAndFlush(goods);

        //用户
        currentUser = new User();
        currentUser.setUsername("cosylj");
        currentUser.setPassword("123456");
        currentUser.setMobile("13600541783");
        currentUser.setMobileBinded(true);
        currentUser.setMoney(new BigDecimal(100));
        currentUser.setRegTime(new Date());
        userRepository.saveAndFlush(currentUser);

        //期号
        currentIssue = new Issue();
        currentIssue.setGoods(goods);
        currentIssue.setDefaultAmount(10L);
        currentIssue.setToAmount(100L);
        currentIssue.setBuyAmount(10L);
        currentIssue.setPricePercentAmount(new BigDecimal(1));
        currentIssue.setAttendAmount(10L);
        currentIssue.setStatus(CommonEnum.IssueStatus.drawed);
        currentIssue.setAwardingUser(currentUser);
        issueRepository.saveAndFlush(currentIssue);

        //期号二，用于设置中奖
        currentIssue2 = new Issue();
        currentIssue2.setGoods(goods);
        currentIssue2.setDefaultAmount(10L);
        currentIssue2.setToAmount(100L);
        currentIssue2.setBuyAmount(100L);
        currentIssue2.setPricePercentAmount(new BigDecimal(1));
        currentIssue2.setAttendAmount(1L);
        currentIssue2.setStatus(CommonEnum.IssueStatus.drawed);
        currentIssue2.setAwardingUser(currentUser);
        issueRepository.saveAndFlush(currentIssue2);


        //用户购买商品的记录
        userBuyFlow = new UserBuyFlow();
        userBuyFlow.setUser(currentUser);
        userBuyFlow.setIssue(currentIssue);
        userBuyFlow.setAmount(10L);
        userBuyFlowRepository.saveAndFlush(userBuyFlow);

        //创建订单
        orders = new Orders();
        orders.setId("2016011508080848599");
        orders.setUser(currentUser);
        orders.setOrderType(CommonEnum.OrderType.raiders);
        orders.setTotalMoney(new BigDecimal(10));
        orders.setMoney(new BigDecimal(10));
        orders.setPayType(CommonEnum.PayType.alipay);
        orders.setStatus(CommonEnum.OrderStatus.payed);
        ordersRepository.saveAndFlush(orders);


    }

    @Test
    public void testGetMyInvolvedRecord() throws Exception {
        //进行抽奖
        raidersCoreService.drawLottery();
        GetMyInvolvedRecordPage getMyInvolvedRecordPage = new GetMyInvolvedRecordPage();
        getMyInvolvedRecordPage.to(driver, currentUser, currentIssue);

    }


    /**
     * @throws Exception
     */
    @Test
    public void testGetMyRaiderNumbers() throws Exception {

        //模拟数据
        Long goodsAmount = 10L;
        //创建商品
        Goods goods = generateGoods(goodsAmount, goodsRepository, issueRepository);
        currentIssue = goods.getIssue();

        //创建用户
        currentUser = generateUser(UUID.randomUUID().toString(), userRepository);
        //创建订单
        generateOrdersWithPayed(currentUser, goods.getIssue(), goodsAmount, ordersRepository, ordersItemRepository, issueRepository);

        // 页面测试
        GetMyRaiderNumbersPage page = new GetMyRaiderNumbersPage();

        page.to(driver, currentUser.getId(), currentIssue.getId());

        // page.to(driver, currentUser.getId(), currentIssue.getId());
//        driver.get("http://localhost/personal/getMyRaiderNumbers?userId=" + currentUser.getId() + "&issueId=" + currentIssue.getId());
//        PageFactory.initElements(driver, GetMyRaiderNumbersPage.class);
//        List<WebElement> webElement = driver.findElements(By.cssSelector("div.commfont"));
    }



    @Test
    public void testGetMyLotteryList() throws Exception {

    }

    @Test
    public void testGetOneLotteryInfo ()throws Exception {
        //创建收货信息
        delivery = new Delivery();
        delivery.setIssue(currentIssue);
        delivery.setUser(currentUser);
        delivery.setDeliveryStatus(CommonEnum.DeliveryStatus.RecieveGoods);
        delivery.setReceiver(currentUser.getId().toString());
        delivery.setMobile("13600541783");
        delivery.setDetails("浙江省杭州市阡陌路智慧E谷");
        deliveryRepository.saveAndFlush(delivery);

        GetOneLotteryInfoPage getOneLotteryInfoPage = new GetOneLotteryInfoPage();
        getOneLotteryInfoPage.to(driver, currentUser, currentIssue, delivery);
    }

}