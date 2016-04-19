package com.huotu.mallduobao.controller;

import com.huotu.huobanplus.sdk.base.BaseClientSpringConfig;
import com.huotu.mallduobao.base.BaseTest;
import com.huotu.mallduobao.boot.MVCConfig;
import com.huotu.mallduobao.boot.RootConfig;
import com.huotu.mallduobao.entity.Delivery;
import com.huotu.mallduobao.entity.Goods;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.Orders;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.entity.UserBuyFlow;
import com.huotu.mallduobao.entity.UserNumber;
import com.huotu.mallduobao.model.RaiderNumbersModel;
import com.huotu.mallduobao.repository.CachedIssueLeaveNumberRepository;
import com.huotu.mallduobao.repository.DeliveryRepository;
import com.huotu.mallduobao.repository.GoodsRepository;
import com.huotu.mallduobao.repository.IssueRepository;
import com.huotu.mallduobao.repository.OrdersItemRepository;
import com.huotu.mallduobao.repository.OrdersRepository;
import com.huotu.mallduobao.repository.UserBuyFlowRepository;
import com.huotu.mallduobao.repository.UserNumberRepository;
import com.huotu.mallduobao.repository.UserRepository;
import com.huotu.mallduobao.service.CacheService;
import com.huotu.mallduobao.service.RaidersCoreService;
import com.huotu.mallduobao.utils.CommonEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Created by cosy on 2016/4/15.
 */

@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class, BaseClientSpringConfig.class})
@ActiveProfiles("development")
@Transactional
public class PersonalControllerTestGetMyRaiderNumber extends BaseTest {
    private Log log = LogFactory.getLog(PersonalControllerTestGetMyRaiderNumber.class);

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
    @Autowired
    UserNumberRepository userNumberRepository;


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
        goods.setDefaultPictureUrl("/resources/images/aa.jpg");
        goods.setPictureUrls("/resources/images/bb.jpg,/resources/images/cc.jpg");
        goods.setSharePictureUrl("/resources/images/dd.jpg");
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
        currentUser.setWeixinOpenId("111");
        currentUser.setMerchantId(3447L);
        currentUser.setMoney(new BigDecimal(100));
        currentUser.setRegTime(new Date());
        currentUser=userRepository.saveAndFlush(currentUser);

        //期号
        currentIssue = new Issue();
        currentIssue.setGoods(goods);
        currentIssue.setDefaultAmount(10L);
        currentIssue.setToAmount(100L);
        currentIssue.setBuyAmount(10L);
        currentIssue.setAwardingDate(new Date());
        currentIssue.setPricePercentAmount(new BigDecimal(1));
        currentIssue.setAttendAmount(10L);
        currentIssue.setStatus(CommonEnum.IssueStatus.drawed);
        currentIssue.setAwardingUser(currentUser);
        currentIssue= issueRepository.saveAndFlush(currentIssue);

        delivery = new Delivery();
        delivery.setIssue(currentIssue);
        delivery.setUser(currentUser);
        delivery.setDeliveryStatus(CommonEnum.DeliveryStatus.GetPrize);
        delivery.setIsCommit(false);
        delivery = deliveryRepository.saveAndFlush(delivery);


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
        currentIssue2=issueRepository.saveAndFlush(currentIssue2);


        //用户购买商品的记录
        userBuyFlow = new UserBuyFlow();
        userBuyFlow.setUser(currentUser);
        userBuyFlow.setIssue(currentIssue);
        userBuyFlow.setAmount(10L);
        userBuyFlow= userBuyFlowRepository.saveAndFlush(userBuyFlow);
        for (int i=0 ; i<10; i++){
            UserNumber userNumber = new UserNumber();
            userNumber.setIssue(currentIssue);
            userNumber.setUser(currentUser);
            userNumber.setNumber(100000L+i);
            userNumberRepository.save(userNumber);

        }

        //创建订单
        orders = new Orders();
        orders.setId("2016011508080848599");
        orders.setUser(currentUser);
        orders.setReceiver("cosylj");
        orders.setOrderType(CommonEnum.OrderType.raiders);
        orders.setTotalMoney(new BigDecimal(10));
        orders.setMoney(new BigDecimal(10));
        orders.setPayType(CommonEnum.PayType.alipay);
        orders.setStatus(CommonEnum.OrderStatus.payed);
        orders.setDetails("aaaaa");
        orders= ordersRepository.saveAndFlush(orders);
    }



    @Rollback(true)
    @Test
    public void  getMyRaiderNumbers()throws  Exception
    {
        MvcResult result=mockMvc.perform(get("/personal/getMyRaiderNumbers")
                .param("customerId","3447")
                .param("issueId",currentIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("/html/personal/duobaoNumber"))
                .andExpect(model().attributeExists("raiderNumbersModel"))
                .andExpect(model().attribute("customerId",3447L))
                .andExpect(model().attribute("issueId",currentIssue.getId()))
                .andReturn();
        RaiderNumbersModel raider=(RaiderNumbersModel)result.getModelAndView().getModel().get("raiderNumbersModel");
        Assert.assertEquals(currentIssue.getGoods().getTitle(),raider.getGoodsTitle());
        Assert.assertEquals(currentIssue.getId(),raider.getIssueId());
        Assert.assertEquals(currentIssue.getAttendAmount(),raider.getAmount());
        List<Long> number= raider.getNumbers();
        Assert.assertEquals(10,number.size());
        Assert.assertEquals("100000",number.get(0).toString());
        Assert.assertEquals("100009",number.get(9).toString());
    }
}
