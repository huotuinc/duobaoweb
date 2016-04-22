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
import com.huotu.mallduobao.entity.*;
import com.huotu.mallduobao.model.GoodsDetailModel;
import com.huotu.mallduobao.repository.GoodsRepository;
import com.huotu.mallduobao.repository.IssueRepository;
import com.huotu.mallduobao.repository.UserBuyFlowRepository;
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

import java.math.BigDecimal;
import java.text.SimpleDateFormat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by daisy.zhang on 2016/4/12.
 * 测试通过issueId跳转到商品详情页面，断言方式和通过goodsID跳转完全一致
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class})
@ActiveProfiles("development")
@Transactional
public class GoodsControllerTestJumpToGoodsActivityDetailByIssueId extends BaseTest {

    @Autowired
    private GoodsRepository mockGoodsRep;
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private UserRepository mockUserRep;
    @Autowired
    private UserBuyFlowRepository userBuyFlowRepository;
    @Autowired
    private CommonConfigService commonConfigService;

    private Issue mockIssue;
    private Goods mockGoods;
    private User mockUserA;
    private User mockUserB;

    //商品现价
    private BigDecimal costPrice;
    private UserBuyFlow mockUserBuyFlowA;
    private UserBuyFlow mockUserBuyFlowB;
    private UserBuyFlow mockUserBuyFlowC;
    private UserNumber mockUserNumberA;
    private UserNumber mockUserNumberB;
    private UserNumber mockUserNumberC;


    @Before
    public void setUp() throws Exception {
        //模拟出一个商品
        mockGoods = daisyMockGoods();

        //模拟一个期号
        mockIssue = daisyMockIssue(mockGoods);
        mockGoods.setIssue(mockIssue);
        mockGoods = mockGoodsRep.saveAndFlush(mockGoods);
        //计算商品原价，后面case断言使用
        costPrice = mockGoods.getPricePercentAmount().multiply(new BigDecimal(mockGoods.getToAmount()));
        //模拟一个用户
        mockUserA = generateUserWithOpenId("123456", "7777777", mockUserRep);
        mockUserB = generateUserWithOpenId("4561758", "7777777", mockUserRep);

    }

    //商品活动开始，但还未有任何人参与，判断返回模型数据是否正确
    @Test
    public void TestNoJoined() throws Exception {
        MvcResult result = mockMvc.perform(get("/goods/detailByIssueId")
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("/html/goods/detail"))
                .andExpect(model().attribute("issueId", mockIssue.getId()))
                .andExpect(model().attributeExists("goodsDetailModel"))
                .andExpect(model().attribute("customerId", 3447L))
                .andDo(print())
                .andReturn();
        GoodsDetailModel goodsDetailModel = (GoodsDetailModel) result.getModelAndView().getModel().get("goodsDetailModel");
        Assert.assertEquals("商品ID错误", mockGoods.getId(), goodsDetailModel.getId());
        Assert.assertEquals("商品期号错误", mockIssue.getId(), goodsDetailModel.getIssueId());
        Assert.assertNotNull("图片列表不存在", goodsDetailModel.getPictureUrls());
        Assert.assertEquals("图片列表数量不对", 2, goodsDetailModel.getPictureUrls().size());
        Assert.assertEquals("商品状态错误", "0", goodsDetailModel.getStatus().toString());
        Assert.assertEquals("商品标题错误", mockGoods.getTitle(), goodsDetailModel.getTitle());
        Assert.assertEquals("购买进度错误", String.valueOf(mockIssue.getBuyAmount() / mockGoods.getToAmount()),
                goodsDetailModel.getProgress().toString());
        Assert.assertEquals("商品总需错误", mockGoods.getToAmount(), goodsDetailModel.getToAmount());
        Assert.assertEquals("商品剩余数量错误", String.valueOf(mockIssue.getToAmount() - mockIssue.getBuyAmount()),
                goodsDetailModel.getRemainAmount().toString());
        Assert.assertEquals("默认购买量错误", mockGoods.getDefaultAmount(), goodsDetailModel.getDefaultAmount());
        Assert.assertEquals("单次购买最低量错误", mockIssue.getStepAmount(), goodsDetailModel.getStepAmount());
        Assert.assertNull("参与号码不应该有", goodsDetailModel.getNumber());
        Assert.assertEquals("全额购买金额错误", costPrice, goodsDetailModel.getFullPrice());
        Assert.assertNull("距离开奖时间不应该有", goodsDetailModel.getToAwardTime());
        Assert.assertNull("中奖用户不应该存在", goodsDetailModel.getAwardUserName());
        Assert.assertNull("中奖用户城市不应该存在", goodsDetailModel.getAwardUserCityName());
        Assert.assertNull("三奖用户ip不应该存在", goodsDetailModel.getAwardUserIp());
        Assert.assertNull("中奖用户参与次数不应该有", goodsDetailModel.getAwardUserJoinCount());
        Assert.assertNull("开奖时间不应该有", goodsDetailModel.getAwardTime());
        Assert.assertNull("中奖号码不应该有", goodsDetailModel.getLuckNumber());
        Assert.assertNull("中奖用户头像不应该有", goodsDetailModel.getAwardUserHead());
        Assert.assertNull("还未有人购买过该期活动，首次购买时间不应该有", goodsDetailModel.getFirstBuyTime());

    }

    //商品活动还在进行中，当前用户参与过一次，查看模型返回数据是否正确(进度刷新)
    @Test
    public void TestUserJoined() throws Exception {
        //更新一些已购人数
        mockIssue.setBuyAmount(1L);
        issueRepository.saveAndFlush(mockIssue);
        //一个用户模拟1条购买记录
        mockUserBuyFlowA = saveUserBuyFlow(mockUserA, mockIssue);
        //模拟用户的两个中奖号码
        mockUserNumberA = saveUserNumber(mockUserA, mockIssue, 1);

        MvcResult result = mockMvc.perform(get("/goods/detailByIssueId")
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("/html/goods/detail"))
                .andExpect(model().attribute("issueId", mockIssue.getId()))
                .andExpect(model().attributeExists("goodsDetailModel"))
                .andExpect(model().attribute("customerId", 3447L))
                .andDo(print())
                .andReturn();
        GoodsDetailModel goodsDetailModel = (GoodsDetailModel) result.getModelAndView().getModel().get("goodsDetailModel");
        Assert.assertEquals("商品ID错误", mockGoods.getId(), goodsDetailModel.getId());
        Assert.assertEquals("商品期号错误", mockIssue.getId(), goodsDetailModel.getIssueId());
        Assert.assertNotNull("图片列表不存在", goodsDetailModel.getPictureUrls());
        Assert.assertEquals("图片列表数量不对", 2, goodsDetailModel.getPictureUrls().size());
        Assert.assertEquals("商品状态错误", "0", goodsDetailModel.getStatus().toString());
        Assert.assertEquals("商品标题错误", mockGoods.getTitle(), goodsDetailModel.getTitle());
        Assert.assertEquals("购买进度错误", String.valueOf(mockIssue.getBuyAmount() * 100 / mockGoods.getToAmount()),
                goodsDetailModel.getProgress().toString());
        Assert.assertEquals("商品总需错误", mockGoods.getToAmount(), goodsDetailModel.getToAmount());
        Assert.assertEquals("商品剩余数量错误", String.valueOf(mockIssue.getToAmount() - mockIssue.getBuyAmount()),
                goodsDetailModel.getRemainAmount().toString());
        Assert.assertEquals("默认购买量错误", mockGoods.getDefaultAmount(), goodsDetailModel.getDefaultAmount());
        Assert.assertEquals("单次购买最低量错误", mockIssue.getStepAmount(), goodsDetailModel.getStepAmount());
        Assert.assertNull("距离开奖时间不应该有", goodsDetailModel.getToAwardTime());
        Assert.assertNull("中奖用户不应该存在", goodsDetailModel.getAwardUserName());
        Assert.assertNull("中奖用户城市不应该存在", goodsDetailModel.getAwardUserCityName());
        Assert.assertNull("三奖用户ip不应该存在", goodsDetailModel.getAwardUserIp());
        Assert.assertNull("中奖用户参与次数不应该有", goodsDetailModel.getAwardUserJoinCount());
        Assert.assertNull("开奖时间不应该有", goodsDetailModel.getAwardTime());
        Assert.assertNull("中奖号码不应该有", goodsDetailModel.getLuckNumber());
        Assert.assertNull("中奖用户头像不应该有", goodsDetailModel.getAwardUserHead());
        Assert.assertNotNull("首次购买时间不存在", goodsDetailModel.getFirstBuyTime());
    }

//    //商品活动还在进行中，当前用户参与了2次，查看模型返回数据是否正确(进度刷新)(因徐和康设计，取的是user表里第一个用户，所以无法判断准确性)
//    @Test
//    public void TestUserJoinedTwice() throws Exception {
//        //更新购买人数
//        mockIssue.setBuyAmount(2L);
//        //一个用户模拟1条购买记录
//        mockUserBuyFlowA = saveUserBuyFlow(mockUserA, mockIssue);
//        mockUserBuyFlowB = saveUserBuyFlow(mockUserA, mockIssue);
//        //模拟用户的两个中奖号码
//        mockUserNumberA = saveUserNumber(mockUserA, mockIssue, 1);
//        mockUserNumberB = saveUserNumber(mockUserA, mockIssue, 2);
//        MvcResult result = mockMvc.perform(get("/goods/detailByIssueId")
//                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
//                .andExpect(status().isOk())
//                .andExpect(view().name("/html/goods/detail"))
//                .andExpect(model().attribute("issueId", mockIssue.getId()))
//                .andExpect(model().attributeExists("goodsDetailModel"))
//                .andExpect(model().attribute("customerId", 3447L))
//                .andDo(print())
//                .andReturn();
//        GoodsDetailModel goodsDetailModel = (GoodsDetailModel) result.getModelAndView().getModel().get("goodsDetailModel");
//        Assert.assertEquals("商品ID错误", mockGoods.getId(), goodsDetailModel.getId());
//        Assert.assertEquals("商品期号错误", mockIssue.getId(), goodsDetailModel.getIssueId());
//        Assert.assertNotNull("图片列表不存在", goodsDetailModel.getPictureUrls());
//        Assert.assertEquals("图片列表数量不对", 2, goodsDetailModel.getPictureUrls().size());
//        Assert.assertEquals("商品状态错误", "0", goodsDetailModel.getStatus().toString());
//        Assert.assertEquals("商品标题错误", mockGoods.getTitle(), goodsDetailModel.getTitle());
//        Assert.assertEquals("购买进度错误", String.valueOf(mockIssue.getBuyAmount() / mockGoods.getToAmount()),
//                goodsDetailModel.getProgress().toString());
//        Assert.assertEquals("商品总需错误", mockGoods.getToAmount(), goodsDetailModel.getToAmount());
//        Assert.assertEquals("商品剩余数量错误", String.valueOf(mockIssue.getToAmount() - mockIssue.getBuyAmount()),
//                goodsDetailModel.getRemainAmount().toString());
//        Assert.assertEquals("默认购买量错误", mockGoods.getDefaultAmount(), goodsDetailModel.getDefaultAmount());
//        Assert.assertEquals("单次购买最低量错误", mockIssue.getStepAmount(), goodsDetailModel.getStepAmount());
//        Assert.assertEquals("当前用户参与人次错误", String.valueOf(userBuyFlowRepository.findAllByIssueAndUser(mockIssue.getId(), mockUserA.getId()).size()),
//                goodsDetailModel.getJoinCount().toString());
//        Assert.assertNull("参与号码不应该存在", goodsDetailModel.getNumber());
//        Assert.assertNull("距离开奖时间不应该有", goodsDetailModel.getToAwardTime());
//        Assert.assertNull("中奖用户不应该存在", goodsDetailModel.getAwardUserName());
//        Assert.assertNull("中奖用户城市不应该存在", goodsDetailModel.getAwardUserCityName());
//        Assert.assertNull("三奖用户ip不应该存在", goodsDetailModel.getAwardUserIp());
//        Assert.assertNull("中奖用户参与次数不应该有", goodsDetailModel.getAwardUserJoinCount());
//        Assert.assertNull("开奖时间不应该有", goodsDetailModel.getAwardTime());
//        Assert.assertNull("中奖号码不应该有", goodsDetailModel.getLuckNumber());
//        Assert.assertNull("中奖用户头像不应该有", goodsDetailModel.getAwardUserHead());
//        Assert.assertNotNull("首次购买时间不存在", goodsDetailModel.getFirstBuyTime());
//    }

    //商品状态为等待开奖，查看模型数据返回是否正确
    @Test
    public void TestIssueDrawing() throws Exception {
        //更新一些设置
        mockIssue.setStatus(CommonEnum.IssueStatus.drawing);
        mockIssue.setToAmount(2L);
        mockIssue.setBuyAmount(2L);
        //两个用户模拟2条购买记录
        mockUserBuyFlowA = saveUserBuyFlow(mockUserA, mockIssue);
        mockUserBuyFlowB = saveUserBuyFlow(mockUserB, mockIssue);
        //模拟用户的两个中奖号码
        mockUserNumberA = saveUserNumber(mockUserA, mockIssue, 1);
        mockUserNumberB = saveUserNumber(mockUserB, mockIssue, 2);
        MvcResult result = mockMvc.perform(get("/goods/detailByIssueId")
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("/html/goods/detail"))
                .andExpect(model().attribute("issueId", mockIssue.getId()))
                .andExpect(model().attributeExists("goodsDetailModel"))
                .andExpect(model().attribute("customerId", 3447L))
                .andDo(print())
                .andReturn();
        GoodsDetailModel goodsDetailModel = (GoodsDetailModel) result.getModelAndView().getModel().get("goodsDetailModel");
        Assert.assertEquals("商品ID错误", mockGoods.getId(), goodsDetailModel.getId());
        Assert.assertEquals("商品期号错误", mockIssue.getId(), goodsDetailModel.getIssueId());
        Assert.assertNotNull("图片列表不存在", goodsDetailModel.getPictureUrls());
        Assert.assertEquals("图片列表数量不对", 2, goodsDetailModel.getPictureUrls().size());
        Assert.assertEquals("商品状态错误", "1", goodsDetailModel.getStatus().toString());
        Assert.assertEquals("商品标题错误", mockGoods.getTitle(), goodsDetailModel.getTitle());
        Assert.assertNull("购买进度不应该有", goodsDetailModel.getProgress());
        Assert.assertNull("商品总需不应该有", goodsDetailModel.getToAmount());
        Assert.assertNull("商品剩余数量不应该有", goodsDetailModel.getRemainAmount());
        Assert.assertEquals("默认购买量错误", mockGoods.getDefaultAmount(), goodsDetailModel.getDefaultAmount());
        Assert.assertEquals("单次购买最低量错误", mockIssue.getStepAmount(), goodsDetailModel.getStepAmount());
//        Assert.assertEquals("当前用户参与人次错误", String.valueOf(userBuyFlowRepository.findAllByIssueAndUser(mockIssue.getId(), mockUserA.getId()).size()),
//                goodsDetailModel.getJoinCount().toString());
//        Assert.assertEquals("参与号码", mockUserNumberA.getNumber(), goodsDetailModel.getNumber());
        Assert.assertNotNull("距离开奖时间缺失", goodsDetailModel.getToAwardTime());
        Assert.assertNull("中奖用户不应该存在", goodsDetailModel.getAwardUserName());
        Assert.assertNull("中奖用户城市不应该存在", goodsDetailModel.getAwardUserCityName());
        Assert.assertNull("三奖用户ip不应该存在", goodsDetailModel.getAwardUserIp());
        Assert.assertNull("中奖用户参与次数不应该有", goodsDetailModel.getAwardUserJoinCount());
        Assert.assertNull("开奖时间不应该有", goodsDetailModel.getAwardTime());
        Assert.assertNull("中奖号码不应该有", goodsDetailModel.getLuckNumber());
        Assert.assertNull("中奖用户头像不应该有", goodsDetailModel.getAwardUserHead());
        Assert.assertNotNull("首次购买时间缺失", goodsDetailModel.getFirstBuyTime());

    }

    //商品状态已开奖,检查模型返回数据
    @Test
    public void TestDrawed() throws Exception {
        //更新一些设置
        mockIssue.setStatus(CommonEnum.IssueStatus.drawed);
        mockIssue.setToAmount(2L);
        mockIssue.setBuyAmount(2L);
        //中奖用户信息设置
        mockUserB.setUsername("daisy");
        mockUserB.setCityName("杭州");
        mockUserB.setIp("192.168.1.30");
        //两个用户模拟2条购买记录
        mockUserBuyFlowA = saveUserBuyFlow(mockUserA, mockIssue);
        mockUserBuyFlowB = saveUserBuyFlow(mockUserB, mockIssue);
        mockUserBuyFlowC = saveUserBuyFlow(mockUserB, mockIssue);
        //模拟用户的两个中奖号码
        mockUserNumberA = saveUserNumber(mockUserA, mockIssue, 1);
        mockUserNumberB = saveUserNumber(mockUserB, mockIssue, 2);
        mockUserNumberC = saveUserNumber(mockUserB, mockIssue, 3);

        //设置中奖用户为模拟用户
        mockIssue.setAwardingUser(mockUserB);

        //设置中奖号码
        mockIssue.setLuckyNumber(mockUserNumberB.getNumber());

        //测试本期中奖后，模型数据是否返回正确
        MvcResult result = mockMvc.perform(get("/goods/detailByIssueId")
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("/html/goods/detail"))
                .andExpect(model().attribute("issueId", mockIssue.getId()))
                .andExpect(model().attributeExists("goodsDetailModel"))
                .andExpect(model().attribute("customerId", 3447L))
                .andDo(print())
                .andReturn();
        GoodsDetailModel goodsDetailModel = (GoodsDetailModel) result.getModelAndView().getModel().get("goodsDetailModel");
        Assert.assertEquals("商品ID错误", mockGoods.getId(), goodsDetailModel.getId());
        Assert.assertEquals("商品期号错误", mockIssue.getId(), goodsDetailModel.getIssueId());
        Assert.assertNotNull("图片列表不存在", goodsDetailModel.getPictureUrls());
        Assert.assertEquals("图片列表数量不对", 2, goodsDetailModel.getPictureUrls().size());
        Assert.assertEquals("商品状态错误", "2", goodsDetailModel.getStatus().toString());
        Assert.assertEquals("商品标题错误", mockGoods.getTitle(), goodsDetailModel.getTitle());
        Assert.assertEquals("默认购买量错误", mockGoods.getDefaultAmount(), goodsDetailModel.getDefaultAmount());
        Assert.assertEquals("单次购买最低量错误", mockIssue.getStepAmount(), goodsDetailModel.getStepAmount());
//        Assert.assertEquals("当前用户参与人次错误", String.valueOf(userBuyFlowRepository.findAllByIssueAndUser(mockIssue.getId(), mockUserA.getId()).size()),
//                goodsDetailModel.getJoinCount().toString());
//        Assert.assertNotNull("参与号码", goodsDetailModel.getNumber());
        Assert.assertEquals("中奖用户名错误", mockIssue.getAwardingUser().getRealName(), goodsDetailModel.getAwardUserName());
        Assert.assertEquals("中奖用户城市错误", mockIssue.getAwardingUser().getCityName(), goodsDetailModel.getAwardUserCityName());
        Assert.assertEquals("中奖用户IP错误", mockIssue.getAwardingUser().getIp(), goodsDetailModel.getAwardUserIp());
        Assert.assertEquals("中奖用户参与次数错误", String.valueOf(userBuyFlowRepository.findAllByIssueAndUser(mockIssue.getId(), mockUserB.getId()).size()),
                goodsDetailModel.getAwardUserJoinCount().toString());
        Assert.assertNotNull("开奖时间缺失", goodsDetailModel.getAwardTime());
        Assert.assertEquals("幸运号码错误", mockIssue.getLuckyNumber(), goodsDetailModel.getLuckNumber());
//        Assert.assertEquals("用户头像不对", commonConfigService.getResourceUri() + mockUserB.getUserHead(),
//                goodsDetailModel.getAwardUserHead());
        Assert.assertNotNull("首次购买时间缺失", goodsDetailModel.getFirstBuyTime());


    }

    //期号ID不传,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestNoIssueID() throws Exception {
        mockMvc.perform(get("/goods/detailByIssueId")
                .param("customerId", "3447").param("issueId", ""))
                .andExpect(view().name("/html/error"))
                .andExpect(model().attribute("message", "期号ID不能为空"));

    }

    //期号ID错误,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestWrongIssueID() throws Exception {
        mockMvc.perform(get("/goods/detailByIssueId")
                .param("customerId", "3447").param("issueId", "bac"))
                .andExpect(view().name("/html/error"))
                .andExpect(model().attribute("message", "参数格式错误"));

    }

    //期号ID在数据库中不存在,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestNotFindIssue() throws Exception {
        mockMvc.perform(get("/goods/detailByIssueId")
                .param("customerId", "3447").param("issueId", "999999999"))
                .andExpect(view().name("/html/error"))
                .andExpect(model().attribute("message", "期号ID为999999999的活动不存在"));

    }


    //商品状态未审核,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestGoodUnCheck() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.uncheck);
        mockGoods.setIssue(null);
        mockGoodsRep.saveAndFlush(mockGoods);
        mockMvc.perform(get("/goods/detailByIssueId")
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().is(302))
                .andExpect(view().name("redirect:/html/error.html"));

    }




}
