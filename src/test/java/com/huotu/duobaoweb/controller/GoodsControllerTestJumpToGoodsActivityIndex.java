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
import com.huotu.duobaoweb.boot.MVCConfig;
import com.huotu.duobaoweb.boot.RootConfig;
import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.Goods;
import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.entity.UserBuyFlow;
import com.huotu.duobaoweb.model.GoodsIndexModel;
import com.huotu.duobaoweb.repository.GoodsRepository;
import com.huotu.duobaoweb.repository.IssueRepository;
import com.huotu.duobaoweb.repository.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by daisy.zhang on 2016/4/6.
 * 测试通过商品ID，跳转到活动首页
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class})
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
    private User mockUserA;
    private User mockUserB;
    private UserBuyFlow mockUserBuyFlow;
    //商品原价
    private BigDecimal costPrice;
    //商品现价
    private BigDecimal currentPrice;


    @Before
    public void setUp() throws ParseException, UnsupportedEncodingException {


        //模拟出一个商品
        mockGoods = new Goods();
        mockGoods.setTitle("daisy测试商品");
        mockGoods.setDefaultPictureUrl("/Default.jpg");
        mockGoods.setPictureUrls("/13.jpg");
        mockGoods.setCharacters("商品特征是红色");
        mockGoods.setStepAmount(1L); //单次购买最低量
        mockGoods.setDefaultAmount(1L); //购买时缺省人次
        mockGoods.setToAmount(10L);//总需人数
        mockGoods.setPricePercentAmount(new BigDecimal(1L)); //购买每人次单价
        mockGoods.setStatus(CommonEnum.GoodsStatus.up); //商品状态
        mockGoods.setStartTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-01-27 00:00:00")); //活动开始时间
        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-05-27 00:00:00")); //活动截止时间
        mockGoods.setShareTitle("丹青测试商品的分享标题"); //分享标题
        mockGoods.setShareDescription("丹青测试商品的分享描述"); //分享描述
        mockGoods.setSharePictureUrl("http://XXXXX.jpg"); //分享图片地址
        mockGoods.setToMallGoodsId(123456L);
        mockGoods.setAttendAmount(0L); //购买次数
        mockGoods.setViewAmount(2L); //浏览器
        mockGoods.setMerchantId(3447L); //设置商城ID
        mockGoods = mockGoodsRep.saveAndFlush(mockGoods);

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
        mockGoods.setIssue(mockIssue);
        mockGoods = mockGoodsRep.saveAndFlush(mockGoods);
        //模拟一个用户
        mockUserA = generateUserWithOpenId("123456", "7777777", mockUserRep);
        //计算商品原价，后面case断言使用
        costPrice = mockGoods.getPricePercentAmount().multiply(new BigDecimal(mockGoods.getToAmount()));
        //计算商品现价，后面case断言使用
        currentPrice = mockGoods.getPricePercentAmount().multiply(new BigDecimal(mockGoods.getStepAmount()));

    }


    //当前用户并没有参与活动，判断模型数据是否正确
    @Test
    public void TestUserNotJoined() throws Exception {

        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2018-03-27 00:00:00"));
        MvcResult result = mockMvc.perform(get("/goods/index").param("goodsId", mockGoods.getId().toString())
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("/html/goods/index"))
                .andExpect(model().attributeExists("goodsIndexModel"))
                .andExpect(model().attribute("customerId", 3447L))
                .andExpect(model().attribute("issueId", mockIssue.getId()))
                .andReturn();
        GoodsIndexModel goodsIndexModel = (GoodsIndexModel) result.getModelAndView().getModel().get("goodsIndexModel");
        Assert.assertEquals("商品ID错误",mockGoods.getId(), goodsIndexModel.getId());
        Assert.assertEquals("图片获取错误","http://localhost:8888/duobaoweb" + mockGoods.getPictureUrls(), goodsIndexModel.getDefaultPictureUrl());
        Assert.assertEquals("原价计算错误",costPrice, goodsIndexModel.getCostPrice());
        Assert.assertEquals("现价计算错误",currentPrice, goodsIndexModel.getCurrentPrice());
        Assert.assertNotNull("时间戳不存在",goodsIndexModel.getStartTime());
        Assert.assertNotNull("时间戳不存在",goodsIndexModel.getEndTime());
        Assert.assertEquals("参与人数错误",mockGoods.getAttendAmount(), goodsIndexModel.getJoinCount());
        Assert.assertTrue("用户登陆状态错误",goodsIndexModel.isLogined());
        Assert.assertFalse("用户参与状态错误",goodsIndexModel.isJoined());
    }

    //测试商品ID正确，活动进行中，用户已登陆，并且购买过
    @Test
    public void TestUserLoginAndBuy() throws Exception {
        //模拟一条用户购买记录
        mockUserBuyFlow = saveUserBuyFlow(mockUserA,mockIssue);
        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2018-03-27 00:00:00"));
        MvcResult result = mockMvc.perform(get("/goods/index").param("goodsId", mockGoods.getId().toString())
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("/html/goods/index"))
                .andExpect(model().attributeExists("goodsIndexModel"))
                .andExpect(model().attribute("customerId", 3447L))
                .andExpect(model().attribute("issueId", mockIssue.getId()))
                .andDo(print())
                .andReturn();
        GoodsIndexModel goodsIndexModel = (GoodsIndexModel) result.getModelAndView().getModel().get("goodsIndexModel");
        Assert.assertEquals("商品ID错误",mockGoods.getId(), goodsIndexModel.getId());
        Assert.assertEquals("图片获取错误","http://localhost:8888/duobaoweb" + mockGoods.getPictureUrls(), goodsIndexModel.getDefaultPictureUrl());
        Assert.assertEquals("原价计算错误",costPrice, goodsIndexModel.getCostPrice());
        Assert.assertEquals("现价计算错误",currentPrice, goodsIndexModel.getCurrentPrice());
        Assert.assertNotNull("时间戳不存在",goodsIndexModel.getStartTime());
        Assert.assertNotNull("时间戳不存在",goodsIndexModel.getEndTime());
        Assert.assertEquals("参与人数错误",mockGoods.getAttendAmount(), goodsIndexModel.getJoinCount());
        Assert.assertTrue("用户登陆状态错误",goodsIndexModel.isLogined());
        Assert.assertTrue("用户参与状态错误",goodsIndexModel.isJoined());
    }

    //  GoodID不传，错误页面未定义
    @Test
    public void TestNoGoodsID() throws Exception {
        mockMvc.perform(get("/index").param("goodsId", "")
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/XXXX"));
    }

    //上传GoodID格式错误
    @Test
    public void TestWrongGoodsID() throws Exception {
        mockMvc.perform(get("/index").param("goodsId", "abc")
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/XXXX"));
    }

    //测试商品ID在数据库中不存在，错误码和错误信息还未定义
    @Test
    public void TestNotFindGoodsID() throws Exception {
        mockMvc.perform(get("/index").param("goodsId", "999999999")
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/XXXX"));
    }

    //测试商品状态未审核，错误码和错误信息暂未定义
    @Test
    public void TestGoodUnCheck() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.uncheck);
        mockMvc.perform(get("/index").param("goodsId", mockGoods.getId().toString())
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/XXXX"));

    }

    //测试商品状态下架，错误码和错误信息暂未定义
    @Test
    public void TestGoodsDown() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.down);
        mockMvc.perform(get("/index").param("goodsId", mockGoods.getId().toString())
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/XXXX"));

    }

    //测试商品已过截止期，错误码和错误信息暂未定义
    @Test
    public void TestExpired() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.up);
        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-03-27 00:00:00"));
        mockMvc.perform(get("/index").param("goodsId", mockGoods.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/XXXX"));
    }

    //测试商品活动还未开始，错误码和错误信息暂未定义
    @Test
    public void TestNotStart() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.up);
        mockGoods.setStartTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2018-03-27 00:00:00"));
        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-03-27 00:00:00"));
        mockMvc.perform(get("/index").param("goodsId", mockGoods.getId().toString())
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/XXXX"))
                .andDo(print());
    }

}
