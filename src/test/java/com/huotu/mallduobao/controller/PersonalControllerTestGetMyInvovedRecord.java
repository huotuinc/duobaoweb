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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

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
public class PersonalControllerTestGetMyInvovedRecord extends BaseTest {

    private Log log = LogFactory.getLog(PersonalControllerTestGetMyInvovedRecord.class);


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
    }


    //存在的type
    @Rollback(true)
    @Test
    public  void getMyInvolvedRecord() throws  Exception
    {

        mockMvc.perform(get("/personal/getMyInvolvedRecord")
                .param("customerId","3447")
                .param("issueId",currentIssue.getId().toString())
                .param("type","0"))
                .andExpect(status().isOk())
                .andExpect(view().name("/html/personal/raiderList"))
                .andExpect(model().attribute("customerId",3447L))
                .andExpect(model().attribute("type",0))
                .andExpect(model().attribute("issueId",currentIssue.getId()))
                .andReturn();
    }

}
