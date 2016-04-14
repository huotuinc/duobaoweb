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
import com.huotu.mallduobao.entity.CountResult;
import com.huotu.mallduobao.entity.Goods;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.entity.UserBuyFlow;
import com.huotu.mallduobao.entity.UserNumber;
import com.huotu.mallduobao.model.CountResultModel;
import com.huotu.mallduobao.repository.CountResultRepository;
import com.huotu.mallduobao.repository.GoodsRepository;
import com.huotu.mallduobao.repository.IssueRepository;
import com.huotu.mallduobao.repository.UserNumberRepository;
import com.huotu.mallduobao.repository.UserRepository;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Created by daisy.zhang on 2016/4/12.
 * 测试获取计算结果
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class})
@ActiveProfiles("development")
@Transactional
public class GoodsControllerTestGetCountResultByIssueId extends BaseTest {
    @Autowired
    IssueRepository mockIssueRep;
    @Autowired
    GoodsRepository mockGoodsRep;
    @Autowired
    CountResultRepository mockCRR;
    @Autowired
    UserNumberRepository userNumberRepository;
    @Autowired
    UserRepository userRepository;

    private Goods mockGoods;
    private Issue mockIssue;
    private CountResult mockCountRes;
    private User mockUserA;
    private User mockUserB;
    private UserNumber mockUserNumA;
    private UserNumber mockUserNumB;

    private UserBuyFlow mockUserBuyFlowA;
    private UserBuyFlow mockUserBuyFlowB;

    @Before
    public void setUp() throws Exception {
        //模拟出一个商品
        mockGoods = daisyMockGoods();
        //模拟一个期号.
        mockIssue = daisyMockIssue(mockGoods);
        mockGoods.setIssue(mockIssue);
        mockGoods = mockGoodsRep.saveAndFlush(mockGoods);
        //模拟用户
        mockUserA = generateUserWithOpenId("123456", "77777777", userRepository);
        mockUserA.setUsername("daisyA");
        mockUserB = generateUserWithOpenId("56789", "88888888", userRepository);
        mockUserB.setUsername("daisyB");
        //设置该期中奖用户为模拟用户
        mockIssue.setAwardingUser(mockUserB);
        //两个用户模拟2条购买记录
        mockUserBuyFlowA = saveUserBuyFlow(mockUserA, mockIssue);
        mockUserBuyFlowB = saveUserBuyFlow(mockUserB, mockIssue);

        //模拟两个userNum
        mockUserNumA = saveUserNumber(mockUserA, mockIssue, 1);
        mockUserNumB = saveUserNumber(mockUserB, mockIssue, 2);
        //设置该期的中奖号码
        mockIssue.setLuckyNumber(mockUserNumB.getNumber());//设置中奖号码、

        //将模拟两个userNumber放进list
        List<UserNumber> UserNumberlist = new ArrayList<>();
        UserNumberlist.add(mockUserNumA);
        UserNumberlist.add(mockUserNumB);

        //模拟开奖结果
        mockCountRes = new CountResult();
        mockCountRes.setIssueNo("20160812");
        mockCountRes.setNumberA(1234567L);
        mockCountRes.setNumberB(81256L);
        mockCountRes.setUserNumbers(UserNumberlist);
        mockCountRes.setIssueAmount(mockIssue.getToAmount().intValue());
        mockCountRes = mockCRR.saveAndFlush(mockCountRes);
        mockIssue.setCountResult(mockCountRes);

    }

    //检查计算结果是否正确
    @Test
    public void TestCountResult() throws Exception {

        //测试本期开奖后，模型数据是否返回正确
        MvcResult result = mockMvc.perform(get("/goods/getCountResultByIssueId")
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("/html/goods/countResult"))
                .andExpect(model().attributeExists("countResultModel"))
                .andDo(print())
                .andReturn();

        CountResultModel countResultModel = (CountResultModel) result.getModelAndView().getModel().get("countResultModel");
        Assert.assertEquals("时时彩期号出错", mockCountRes.getIssueNo(), countResultModel.getIssueNo());
        Assert.assertNotNull("NumberA不存在", countResultModel.getNumberA());
        Assert.assertEquals("时时彩开奖结果出错", mockCountRes.getNumberB(), countResultModel.getNumberB());
        Assert.assertEquals("最后50条错了", mockCountRes.getUserNumbers(), countResultModel.getUserNumbers());
        Assert.assertEquals("中奖号码错了", mockIssue.getLuckyNumber(), countResultModel.getLuckNumber());

    }
}
