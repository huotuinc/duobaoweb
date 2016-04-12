package com.huotu.duobaoweb.controller;

import com.huotu.duobaoweb.base.BaseTest;
import com.huotu.duobaoweb.boot.MVCConfig;
import com.huotu.duobaoweb.boot.RootConfig;
import com.huotu.duobaoweb.entity.Goods;
import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.repository.*;
import com.huotu.duobaoweb.service.CacheService;
import com.huotu.duobaoweb.service.RaidersCoreService;
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
 * 核心部分测试
 * Created by lgh on 2016/4/1.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class, BaseClientSpringConfig.class})//
@ActiveProfiles("development")
@Transactional
public class CoreTest extends BaseTest {

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
    public void drawLottery() throws Exception {
        //模拟数据
        Long goodsAmount = 10L;
        //创建商品
        Goods goods = generateGoods(goodsAmount, goodsRepository, issueRepository);
        currentIssue = goods.getIssue();

        //创建用户
        currentUser = generateUser(UUID.randomUUID().toString(), userRepository);
        User userB = generateUser(UUID.randomUUID().toString(), userRepository);
        User userC = generateUser(UUID.randomUUID().toString(), userRepository);
        User userD = generateUser(UUID.randomUUID().toString(), userRepository);
        User userE = generateUser(UUID.randomUUID().toString(), userRepository);
        //创建订单
        generateOrdersWithPayed(currentUser, goods.getIssue(), 1L, ordersRepository, ordersItemRepository, issueRepository);
        generateOrdersWithPayed(userB, goods.getIssue(), 1L, ordersRepository, ordersItemRepository, issueRepository);
        generateOrdersWithPayed(userC, goods.getIssue(), 1L, ordersRepository, ordersItemRepository, issueRepository);
        generateOrdersWithPayed(userD, goods.getIssue(), 1L, ordersRepository, ordersItemRepository, issueRepository);
        generateOrdersWithPayed(userE, goods.getIssue(), 1L, ordersRepository, ordersItemRepository, issueRepository);

        Assert.assertEquals("缓存中的数量", 5, cachedIssueLeaveNumberRepository.findAllByIssueId(currentIssue.getId()).size());
        Assert.assertEquals("缓存中的数量", 5, cacheService.getLotteryNumber(currentIssue.getId()).size());

        Issue issue = issueRepository.getOne(currentIssue.getId());
        Assert.assertEquals("期号还没有开奖", true, issue.getAwardingUser() == null);

        generateOrdersWithPayed(currentUser, goods.getIssue(), 5L, ordersRepository, ordersItemRepository, issueRepository);
        Assert.assertEquals("缓存中的数量", 0, cachedIssueLeaveNumberRepository.findAllByIssueId(currentIssue.getId()).size());
        Assert.assertEquals("缓存中的数量", 0, cacheService.getLotteryNumber(currentIssue.getId()).size());

        //进行抽奖
        raidersCoreService.drawLottery();
        issue = issueRepository.getOne(currentIssue.getId());
        log.info("中奖号码为" + issue.getLuckyNumber().toString());
        log.info("中奖用户为" + issue.getAwardingUser().getId());
    }

    @Test
    public void t()
    {
        System.out.println(UUID.randomUUID().toString().replace("-",""));
        System.out.println(UUID.randomUUID().toString().replace("-",""));
    }
}
