package com.huotu.duobaoweb.controller;

import com.huotu.duobaoweb.base.BaseTest;
import com.huotu.duobaoweb.boot.MVCConfig;
import com.huotu.duobaoweb.boot.RootConfig;
import com.huotu.duobaoweb.controller.page.GetMyRaiderNumbersPage;
import com.huotu.duobaoweb.entity.Goods;
import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.entity.UserNumber;
import com.huotu.duobaoweb.repository.*;

import com.huotu.duobaoweb.service.CacheService;
import com.huotu.duobaoweb.service.RaidersCoreService;
import com.huotu.duobaoweb.service.StaticResourceService;
import com.huotu.huobanplus.sdk.base.BaseClientSpringConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


/**
 * Created by lgh on 2016/3/30.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class, BaseClientSpringConfig.class})//
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
    OrdersRepository ordersRepository;

    @Autowired
    OrdersItemRepository ordersItemRepository;

    @Autowired
    CachedIssueLeaveNumberRepository cachedIssueLeaveNumberRepository;

    @Autowired
    CacheService cacheService;

    @Autowired
    RaidersCoreService raidersCoreService;


    private User currentUser;
    private Issue currentIssue;

    @Test
    public void testGetMyInvolvedRecord() throws Exception {

    }


    /**
     *
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
        currentUser = generateUserWithoutMobile(UUID.randomUUID().toString(), userRepository);
        //创建订单
        generateOrdersWithPayed(currentUser, goods.getIssue(), goodsAmount, ordersRepository, ordersItemRepository, issueRepository);

        // 页面测试
        GetMyRaiderNumbersPage page = new GetMyRaiderNumbersPage();
        page.to(driver, currentUser.getId(), currentIssue.getId());
    }

    @Test
    public void testGetMyLotteryList() throws Exception {

    }

    @Test
    public void testGetOneLotteryInfo() throws Exception {

    }
}