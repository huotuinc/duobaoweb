package com.huotu.mallduobao.controller;

import com.huotu.mallduobao.base.BaseTest;
import com.huotu.mallduobao.boot.MVCConfig;
import com.huotu.mallduobao.boot.RootConfig;
import com.huotu.mallduobao.entity.Goods;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.repository.*;
import com.huotu.mallduobao.service.CacheService;
import com.huotu.mallduobao.service.CommonConfigService;
import com.huotu.mallduobao.service.RaidersCoreService;
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
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * 核心部分测试
 * Created by lgh on 2016/4/1.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class, BaseClientSpringConfig.class})//
@ActiveProfiles("test")
@Transactional
public class CoreTest extends BaseTest {

    private Log log = LogFactory.getLog(CoreTest.class);


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

    @Autowired
    private CommonConfigService commonConfigService;

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

        Issue issue = issueRepository.findOne(currentIssue.getId());
        Assert.assertEquals("期号还没有开奖", true, issue.getAwardingUser() == null);

        generateOrdersWithPayed(currentUser, goods.getIssue(), 5L, ordersRepository, ordersItemRepository, issueRepository);
        Assert.assertEquals("缓存中的数量", 0, cachedIssueLeaveNumberRepository.findAllByIssueId(currentIssue.getId()).size());
        Assert.assertEquals("缓存中的数量", 0, cacheService.getLotteryNumber(currentIssue.getId()).size());

        //进行抽奖
        raidersCoreService.drawLottery();
        issue = issueRepository.findOne(currentIssue.getId());
        log.debug("中奖号码为" + issue.getLuckyNumber().toString());
        log.debug("中奖用户为" + issue.getAwardingUser().getId());
    }

    @Test
    public void t() throws UnsupportedEncodingException {
        System.out.println(UUID.randomUUID().toString().replace("-", ""));
        System.out.println(UUID.randomUUID().toString().replace("-", ""));
        String x = "7e3f64c86f7815dba954750bbdd4e999:1175951:oSDLLwqB-nMOXlpjJ2DZf0igBRNs:oRSVjs0yiOSAtGzoDtQAO6rmA7rk";
//        System.out.println(checkAppSign(x.split(":")));
        int i = 1;
        switch (i) {
            case 1:
                if (2 == 2) return;
                break;
            case 2:
                break;
        }

        Integer b = 0;
        return;
    }

    public boolean isTrueSign(String[] data) throws UnsupportedEncodingException {
        for (String s : data) {
            if (StringUtils.isEmpty(s)) {
                return false;
            }
        }
        String s = data[1] + data[2] + data[3] + commonConfigService.getAppSecret();
        String sign = DigestUtils.md5DigestAsHex(s.getBytes("UTF-8")).toLowerCase();
        return sign.equals(data[0]);
    }

//    /**
//     * 测试spring jpa的getone与findone的区别
//     */
//    @Test
//    public void testGetAndFind(){
//        Goods goods=goodsRepository.getOne(50L);
//        Goods goods1=goodsRepository.findOne(50L);
//
//    }
}
