/*
 *  * 版权所有:杭州火图科技有限公司
 *  * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *  *
 *  * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 *  * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 *  * 2013-2015. All rights reserved.
 */

package com.huotu.duobaoweb.controller;

import com.huotu.duobaoweb.base.BaseTest;
import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.controller.page.JumpToGoodsActivityDetailByGoodsId;
import com.huotu.duobaoweb.entity.Goods;
import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.entity.UserBuyFlow;
import com.huotu.duobaoweb.repository.GoodsRepository;
import com.huotu.duobaoweb.repository.IssueRepository;
import com.huotu.duobaoweb.repository.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by daisy.zhang on 2016/4/9.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("development")
@Transactional
public class GoodsControllerTestJumpToGoodsActivityDetailByIssueId extends BaseTest{
    @Autowired
    private GoodsRepository mockGoodsRep;
    @Autowired
    private IssueRepository mockIssueRep;
    @Autowired
    private UserRepository mockUserRep;


    private Issue mockIssueA;
    private Issue mockIssueB;
    private Goods mockGoodsA;
    private Goods mockGoodsB;
    private User mockUserA;
    private User mockUserB;
    private UserBuyFlow mockUserBuyFlowA;
    private UserBuyFlow mockUserBuyFlowB;

    @Before
    public void setUp() throws ParseException, UnsupportedEncodingException {
        //模拟一个期号,没有人购买过
        mockIssueA = new Issue();
        mockIssueA.setGoods(mockGoodsA);//所属活动商品
        mockIssueA.setStepAmount(mockGoodsA.getStepAmount());//单次购买最低量
        mockIssueA.setDefaultAmount(mockGoodsA.getDefaultAmount()); //缺省购买人次
        mockIssueA.setToAmount(mockGoodsA.getToAmount()); //总需购买人次
        mockIssueA.setBuyAmount(0L); //已购买的人次
        mockIssueA.setPricePercentAmount(mockGoodsA.getPricePercentAmount()); //每人次单价
        mockIssueA.setAttendAmount(mockGoodsA.getAttendAmount()); //购买次数,在中奖时从每期中累计此值
        mockIssueA.setStatus(CommonEnum.IssueStatus.going);//状态
        mockIssueA.setAwardingDate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-04-28 05:00:00"));//开奖日期
        mockIssueA = mockIssueRep.saveAndFlush(mockIssueA);

        //模拟出一期，有人购买过
        mockIssueB = new Issue();
        mockIssueB.setGoods(mockGoodsB);//所属活动商品
        mockIssueB.setStepAmount(mockGoodsB.getStepAmount());//单次购买最低量
        mockIssueB.setDefaultAmount(mockGoodsB.getDefaultAmount()); //缺省购买人次
        mockIssueB.setToAmount(mockGoodsB.getToAmount()); //总需购买人次
        mockIssueB.setBuyAmount(2L); //已购买的人次
        mockIssueB.setPricePercentAmount(mockGoodsB.getPricePercentAmount()); //每人次单价
        mockIssueB.setAttendAmount(mockGoodsB.getAttendAmount()); //购买次数,在中奖时从每期中累计此值
        mockIssueB.setStatus(CommonEnum.IssueStatus.going);//状态
        mockIssueB.setAwardingDate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-04-28 05:00:00"));//开奖日期
        mockIssueB = mockIssueRep.saveAndFlush(mockIssueB);

        //模拟出一个商品A
        mockGoodsA = new Goods();
        mockGoodsA.setTitle("daisy测试商品A");
        mockGoodsA.setDefaultPictureUrl("DefaultPicture.jpg");
        mockGoodsA.setPictureUrls("DefaultPicture.jpg");
        mockGoodsA.setCharacters("商品特征是红色");
        mockGoodsA.setStepAmount(1L); //单次购买最低量
        mockGoodsA.setDefaultAmount(1L); //购买时缺省人次
        mockGoodsA.setToAmount(10L);//总需人数
        mockGoodsA.setPricePercentAmount(new BigDecimal(1L)); //购买每人次单价
        mockGoodsA.setStatus(CommonEnum.GoodsStatus.up); //商品状态
        mockGoodsA.setIssue(mockIssueA); //当期期号
        mockGoodsA.setStartTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-01-27 00:00:00")); //活动开始时间
        mockGoodsA.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-05-27 00:00:00")); //活动截止时间
        mockGoodsA.setShareTitle("丹青测试商品的分享标题"); //分享标题
        mockGoodsA.setShareDescription("丹青测试商品的分享描述"); //分享描述
        mockGoodsA.setSharePictureUrl("http://XXXXX.jpg"); //分享图片地址
        mockGoodsA.setToMallGoodsId(123456L);
        mockGoodsA.setAttendAmount(0L); //购买次数
        mockGoodsA.setViewAmount(2L); //浏览器
        mockGoodsA.setMerchantId(3347L); //设置商城ID
        mockGoodsA = mockGoodsRep.saveAndFlush(mockGoodsA);

        //模拟出一个商品B
        mockGoodsB = new Goods();
        mockGoodsB.setTitle("daisy测试商品B");
        mockGoodsB.setDefaultPictureUrl("DefaultPicture.jpg");
        mockGoodsB.setPictureUrls("DefaultPicture.jpg");
        mockGoodsB.setCharacters("商品特征是红色");
        mockGoodsB.setStepAmount(1L); //单次购买最低量
        mockGoodsB.setDefaultAmount(1L); //购买时缺省人次
        mockGoodsB.setToAmount(10L);//总需人数
        mockGoodsB.setPricePercentAmount(new BigDecimal(1L)); //购买每人次单价
        mockGoodsB.setStatus(CommonEnum.GoodsStatus.up); //商品状态
        mockGoodsB.setIssue(mockIssueB); //当期期号
        mockGoodsB.setStartTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-01-27 00:00:00")); //活动开始时间
        mockGoodsB.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-05-27 00:00:00")); //活动截止时间
        mockGoodsB.setShareTitle("丹青测试商品的分享标题"); //分享标题
        mockGoodsB.setShareDescription("丹青测试商品的分享描述"); //分享描述
        mockGoodsB.setSharePictureUrl("http://XXXXX.jpg"); //分享图片地址
        mockGoodsB.setToMallGoodsId(123456L);
        mockGoodsB.setAttendAmount(0L); //购买次数
        mockGoodsB.setViewAmount(2L); //浏览器
        mockGoodsB.setMerchantId(3447L); //设置商城ID
        mockGoodsB = mockGoodsRep.saveAndFlush(mockGoodsB);

        //模拟两个用户
        mockUserA = generateUserWithOpenId("123456", "9999999", mockUserRep);
        mockUserA.setUsername("daisyTest1");
        mockUserB = generateUserWithOpenId("456789", "1000000", mockUserRep);
        mockUserB.setUsername("蛋清测试账号B");

        //模拟两个用户的两条购买记录
        mockUserBuyFlowA = saveUserBuyFlow(mockUserA, mockIssueB);
        mockUserBuyFlowB = saveUserBuyFlow(mockUserB, mockIssueB);
    }

    //商品活动开始，但还未有任何人参与，判断返回模型数据是否正确
    @Test
    public void TestNoJoined() throws Exception {
        JumpToGoodsActivityDetailByGoodsId page = new JumpToGoodsActivityDetailByGoodsId();
        page.NoJoined(driver, mockGoodsA, mockIssueA);
    }

    //商品活动还在进行中，并且有用户参与过，自己并没有参与,查看模型返回数据是否正确(进度刷新)
    @Test
    public void TestUserJoined() throws Exception {
        JumpToGoodsActivityDetailByGoodsId page = new JumpToGoodsActivityDetailByGoodsId();
        page.UserJoined(driver, mockGoodsB, mockIssueB);

    }

    //商品活动还在进行中，自己也参与了，自己并没有参与,查看模型返回数据是否正确(进度刷新)
    @Test
    public void TestUserJoinedByMine() throws Exception {
        //还不知道怎么带进登陆信息
        Assert.assertFalse(true);
    }

    //商品状态为等待开奖，查看模型数据返回是否正确
    @Test
    public void TestIssueDrawing() throws Exception {
        mockIssueB.setBuyAmount(10L);
        mockIssueB.setStatus(CommonEnum.IssueStatus.drawing);
        JumpToGoodsActivityDetailByGoodsId page = new JumpToGoodsActivityDetailByGoodsId();
        page.IssueDrawing(driver, mockGoodsB, mockIssueB);
    }

    //商品状态已开奖
    @Test
    public void TestDrawed() throws Exception {
        //修改本期状态
        mockIssueB.setStatus(CommonEnum.IssueStatus.drawed);
        //设置中奖用户为模拟用户
        mockIssueB.setAwardingUser(mockUserB);
        //设置中奖号码
        mockIssueB.setLuckyNumber(123457L);
        //测试本期中奖后，模型数据是否返回正确
        JumpToGoodsActivityDetailByGoodsId page = new JumpToGoodsActivityDetailByGoodsId();
        page.IssueDrawed(driver, mockGoodsB, mockIssueB);
    }

    //商品ID不传,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestNOGoodsID() throws Exception {
        JumpToGoodsActivityDetailByGoodsId page = new JumpToGoodsActivityDetailByGoodsId();
        page.NOGoodsID(driver);
    }

    //商品ID错误,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestWrongGoodsID() throws Exception {
        String issueId = "abc";
        JumpToGoodsActivityDetailByGoodsId page = new JumpToGoodsActivityDetailByGoodsId();
        page.WrongGoodsID(driver, issueId);

    }

    //商品ID在数据库中不存在,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestNotFindGoodsID() throws Exception {
        long issueId = 99999999L;
        JumpToGoodsActivityDetailByGoodsId page = new JumpToGoodsActivityDetailByGoodsId();
        page.NotFindGoodsID(driver, issueId);
    }

    //商品状态未审核,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestGoodUnCheck() throws Exception {
        mockGoodsA.setStatus(CommonEnum.GoodsStatus.uncheck);
        JumpToGoodsActivityDetailByGoodsId page = new JumpToGoodsActivityDetailByGoodsId();
        page.GoodsUnCheck(driver, mockIssueA.getId());
    }

    //商品活动已结束
    @Test
    public void TestDown() {
        mockGoodsA.setStatus(CommonEnum.GoodsStatus.down);
        JumpToGoodsActivityDetailByGoodsId page = new JumpToGoodsActivityDetailByGoodsId();
        page.GoodsDown(driver, mockIssueA.getId());
    }

    //商品已经过期,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestExpired() throws Exception {
        mockGoodsA.setStatus(CommonEnum.GoodsStatus.up);
        mockGoodsA.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-03-27 00:00:00"));
        JumpToGoodsActivityDetailByGoodsId page = new JumpToGoodsActivityDetailByGoodsId();
        page.Expired(driver, mockIssueA.getId());
    }

    //商品还未开始活动,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestNotStart() throws Exception {
        mockGoodsA.setStatus(CommonEnum.GoodsStatus.up);
        mockGoodsA.setStartTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2018-03-27 00:00:00"));
        mockGoodsA.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-02-27 00:00:00"));
        JumpToGoodsActivityDetailByGoodsId page = new JumpToGoodsActivityDetailByGoodsId();
        page.NotStart(driver, mockIssueA.getId());
    }

}
