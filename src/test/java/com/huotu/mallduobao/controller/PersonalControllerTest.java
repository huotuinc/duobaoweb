package com.huotu.mallduobao.controller;

import com.huotu.mallduobao.base.BaseTest;
import com.huotu.mallduobao.boot.MVCConfig;
import com.huotu.mallduobao.boot.RootConfig;
import com.huotu.mallduobao.controller.page.GetMyRaiderNumbersPage;
import com.huotu.mallduobao.entity.Goods;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.repository.*;

import com.huotu.mallduobao.service.CacheService;
import com.huotu.mallduobao.service.RaidersCoreService;
import com.huotu.huobanplus.sdk.base.BaseClientSpringConfig;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
        currentUser = generateUser(UUID.randomUUID().toString(), userRepository);
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