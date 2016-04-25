/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */
package com.huotu.mallduobao.controller;


import com.huotu.mallduobao.base.BaseTest;
import com.huotu.mallduobao.boot.MVCConfig;
import com.huotu.mallduobao.boot.RootConfig;
import com.huotu.mallduobao.entity.Goods;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.entity.UserBuyFlow;
import com.huotu.mallduobao.model.GoodsIndexModel;
import com.huotu.mallduobao.repository.GoodsRepository;
import com.huotu.mallduobao.repository.IssueRepository;
import com.huotu.mallduobao.repository.UserRepository;
import com.huotu.mallduobao.service.CommonConfigService;
import com.huotu.mallduobao.utils.CommonEnum;
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
    @Autowired
    CommonConfigService commonConfigService;


    private Issue mockIssue;
    private Goods mockGoods;
    private User mockUserA;
    private UserBuyFlow mockUserBuyFlow;
    //商品原价
    private BigDecimal costPrice;
    //商品现价
    private BigDecimal currentPrice;


    @Before
    public void setUp() throws Exception {


        //模拟出一个商品
        mockGoods = daisyMockGoods();

        //模拟一个期号
        mockIssue = daisyMockIssue(mockGoods);
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

        MvcResult result = mockMvc.perform(get("/goods/index").param("goodsId", mockGoods.getId().toString())
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("/html/goods/index"))
                .andExpect(model().attributeExists("goodsIndexModel"))
                .andExpect(model().attribute("issueId", mockIssue.getId()))
                .andReturn();
        GoodsIndexModel goodsIndexModel = (GoodsIndexModel) result.getModelAndView().getModel().get("goodsIndexModel");
        Assert.assertEquals("商品ID错误", mockGoods.getId(), goodsIndexModel.getId());
        Assert.assertNotNull("图片没有", goodsIndexModel.getDefaultPictureUrl());
        Assert.assertEquals("原价计算错误", costPrice, goodsIndexModel.getCostPrice());
        Assert.assertEquals("现价计算错误", currentPrice, goodsIndexModel.getCurrentPrice());
        Assert.assertNotNull("时间戳不存在", goodsIndexModel.getStartTime());
        Assert.assertNotNull("时间戳不存在", goodsIndexModel.getEndTime());
        Assert.assertNotNull("参与人数出错", goodsIndexModel.getJoinCount());
        Assert.assertTrue("用户登陆状态错误", goodsIndexModel.isLogined());
        Assert.assertFalse("用户参与状态错误", goodsIndexModel.isJoined());
    }

    //测试商品ID正确，活动进行中，用户已登陆，并且购买过(过滤处理，查找的并非当前用户，而是数据库中第一个用户，去
    // 除用户相关断言)
    @Test
    public void TestUserLoginAndBuy() throws Exception {
        //模拟一条用户购买记录
        mockUserBuyFlow = saveUserBuyFlow(mockUserA, mockIssue);
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
        Assert.assertEquals("商品ID错误", mockGoods.getId(), goodsIndexModel.getId());
        Assert.assertNotNull("图片没有", goodsIndexModel.getDefaultPictureUrl());
        Assert.assertEquals("原价计算错误", costPrice, goodsIndexModel.getCostPrice());
        Assert.assertEquals("现价计算错误", currentPrice, goodsIndexModel.getCurrentPrice());
        Assert.assertNotNull("时间戳不存在", goodsIndexModel.getStartTime());
        Assert.assertNotNull("时间戳不存在", goodsIndexModel.getEndTime());
//        Assert.assertEquals("参与人数错误", mockGoods.getAttendAmount(), goodsIndexModel.getJoinCount());
        // Assert.assertTrue("用户登陆状态错误", goodsIndexModel.isLogined());
        // Assert.assertTrue("用户参与状态错误", goodsIndexModel.isJoined());
    }

    //  GoodID不传，错误页面未定义
    @Test
    public void TestNoGoodsID() throws Exception {
        mockMvc.perform(get("/goods/index")
                .param("customerId", "3447").param("issueId", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("/html/error"))
                .andExpect(model().attribute("message", "商品ID不能为空"));
    }

    //上传GoodID格式错误
    @Test
    public void TestWrongGoodsID() throws Exception {
        mockMvc.perform(get("/goods/index").param("goodsId", "abc")
                .param("customerId", "3447"))
                .andExpect(status().is(302));
    }

    //测试商品ID在数据库中不存在，错误码和错误信息还未定义
    @Test
    public void TestNotFindGoodsID() throws Exception {
        mockMvc.perform(get("/goods/index").param("goodsId", "999999999")
                .param("customerId", "3447"))
                .andDo(print())
                .andExpect(status().is(302))
                .andExpect(view().name("redirect:/html/error.html"))
                .andExpect(model().attribute("message", "商品ID为999999999的活动不存在"));
    }

    //测试商品状态未审核，错误码和错误信息暂未定义
    @Test
    public void TestGoodUnCheck() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.uncheck);
        mockGoods.setIssue(null);
        mockGoodsRep.saveAndFlush(mockGoods);
        mockMvc.perform(get("/goods/index").param("goodsId", mockGoods.getId().toString())
                .param("customerId", "3447"))
                .andExpect(status().is(302))
                .andExpect(view().name("redirect:/html/error.html"))
                .andExpect(model().attribute("message", "商品不存在或期号不存在或商品已下架---goodsId=" + mockGoods.getId()));

    }

    //测试商品状态下架，错误码和错误信息暂未定义
    @Test
    public void TestGoodsDown() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.down);
        mockGoods.setIssue(null);
        mockGoodsRep.saveAndFlush(mockGoods);
        mockMvc.perform(get("/goods/index").param("goodsId", mockGoods.getId().toString())
                .param("customerId", "3447"))
                .andExpect(status().is(302))
                .andExpect(view().name("redirect:/html/error.html"))
                .andExpect(model().attribute("message", "商品不存在或期号不存在或商品已下架---goodsId=" + mockGoods.getId()));

    }
}
