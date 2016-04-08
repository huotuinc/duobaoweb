/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */
package com.huotu.duobaoweb.controller;

import com.huotu.duobaoweb.base.BaseTest;
import com.huotu.duobaoweb.boot.RootConfig;
import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.controller.page.JumpToGoodsActivityIndexPage;
import com.huotu.duobaoweb.entity.Goods;
import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.repository.GoodsRepository;
import com.huotu.duobaoweb.repository.IssueRepository;
import com.huotu.duobaoweb.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.expression.ParseException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;

/**
 * Created by daisy.zhang on 2016/4/6.
 * 测试通过商品ID，跳转到活动首页
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class})
@ActiveProfiles("development")
@Transactional
public class GoodsControllerTestJumpToGoodsActivityIndex extends BaseTest {

    @Autowired
    GoodsRepository mockGoodsRep;
    @Autowired
    IssueRepository mockIssueRep;
    @Autowired
    UserRepository mockUserRep;

    private Issue mockIssue;
    private Goods mockGoods;

    @Before
    public void setUp() throws ParseException, IOException, java.text.ParseException {
        mockGoods = generateGoods(10L, false, mockGoodsRep, mockIssueRep);
        mockGoods.setTitle("daisy测试商品");
        mockGoods.setDefaultPictureUrl("DefaultPicture.jpg");
        mockGoods.setPictureUrls("http://pic.baike.soso.com/p/20140401/bki-20140401102552-911125613.jpg");
        mockGoods.setCharacters("商品特征是红色");
        mockGoods.setStepAmount(1L); //单次购买最低量
        mockGoods.setDefaultAmount(1L); //购买时缺省人次
        mockGoods.setToAmount(10L);//总需人数
        mockGoods.setPricePercentAmount(new BigDecimal(1L)); //购买每人次单价
        mockGoods.setStatus(CommonEnum.GoodsStatus.up); //商品状态
        mockGoods.setIssue(mockIssue); //当期期号
        mockGoods.setStartTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-01-27 00:00:00")); //活动开始时间
        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-05-27 00:00:00")); //活动截止时间
        mockGoods.setShareTitle("丹青测试商品的分享标题"); //分享标题
        mockGoods.setShareDescription("丹青测试商品的分享描述"); //分享描述
        mockGoods.setSharePictureUrl("http://XXXXX.jpg"); //分享图片地址
        mockGoods.setToMallGoodsId(123456L);
        mockGoods.setAttendAmount(30L); //购买次数
        mockGoods.setViewAmount(2L); //浏览器
        mockGoods.setMerchantId(3347L); //设置商城ID

        //模拟一个期号
        mockIssue = new Issue();
        mockIssue.setGoods(mockGoods);//所属活动商品
        mockIssue.setStepAmount(mockGoods.getStepAmount());//单次购买最低量
        mockIssue.setDefaultAmount(mockGoods.getDefaultAmount()); //缺省购买人次
        mockIssue.setToAmount(mockGoods.getToAmount()); //总需购买人次
        mockIssue.setBuyAmount(1L); //已购买的人次
        mockIssue.setPricePercentAmount(mockGoods.getPricePercentAmount()); //每人次单价
        mockIssue.setAttendAmount(mockGoods.getAttendAmount()); //购买次数,在中奖时从每期中累计此值
        mockIssue.setStatus(CommonEnum.IssueStatus.going);//状态
        mockIssue.setAwardingDate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-04-28 05:00:00"));//开奖日期
        mockIssue = mockIssueRep.saveAndFlush(mockIssue);


    }

    //测试商品ID正确，活动处于进行中，查看各元素与modle各值是否正确
    @Test
    public void TestUserNotLogin() throws Exception {
        //原价 单价*总需
        BigDecimal costPrice = mockGoods.getPricePercentAmount().multiply(new BigDecimal(mockGoods.getToAmount()));
        //现价 单价*最低购买价数
        BigDecimal currentPrice = mockGoods.getPricePercentAmount().multiply(new BigDecimal(mockGoods.getStepAmount()));
        //已购买的次数
        JumpToGoodsActivityIndexPage page = new JumpToGoodsActivityIndexPage();
        page.to(driver, mockGoods.getId(), costPrice, currentPrice, mockGoods.getAttendAmount());
    }

    // GoodID不传，错误页面未定义
    @Test
    public void TestNoGoodsID() throws Exception {
        JumpToGoodsActivityIndexPage page = new JumpToGoodsActivityIndexPage();
        page.NoGoodsID(driver);
    }

    //上传GoodID格式错误
    @Test
    public void TestWrongGoodsID() throws Exception {
        String goodsId = "abc";
        JumpToGoodsActivityIndexPage page = new JumpToGoodsActivityIndexPage();
        page.WrongGoodsID(driver, goodsId);
    }

    //
    //测试商品ID在数据库中不存在，错误码和错误信息还未定义
    @Test
    public void TestNotFindGoodsID() throws Exception {
        long goodsId = 999999L;
        JumpToGoodsActivityIndexPage page = new JumpToGoodsActivityIndexPage();
        page.NotFindGoodsID(driver, goodsId);

    }

    //测试商品状态未审核，错误码和错误信息暂未定义
    @Test
    public void TestGoodUnCheck() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.uncheck);
        JumpToGoodsActivityIndexPage page = new JumpToGoodsActivityIndexPage();
        page.GoodUnCheck(driver, mockGoods.getId());

    }

    //测试商品状态下架，错误码和错误信息暂未定义
    @Test
    public void TestGoodsDown() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.down);
        JumpToGoodsActivityIndexPage page = new JumpToGoodsActivityIndexPage();
        page.GoodsDown(driver, mockGoods.getId());

    }

    //测试商品已过截止期，错误码和错误信息暂未定义
    @Test
    public void TestExpired() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.up);
        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-03-27 00:00:00"));
        JumpToGoodsActivityIndexPage page = new JumpToGoodsActivityIndexPage();
        page.Expired(driver, mockGoods.getId());

    }

    //测试商品活动还未开始，错误码和错误信息暂未定义
    @Test
    public void TestNotStart() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.up);
        mockGoods.setStartTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2018-03-27 00:00:00"));
        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-03-27 00:00:00"));
        JumpToGoodsActivityIndexPage page = new JumpToGoodsActivityIndexPage();
        page.NotStart(driver, mockGoods.getId());

    }

}
