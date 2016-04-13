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
import com.huotu.mallduobao.utils.CommonEnum;
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

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
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
    public void setUp() throws ParseException, UnsupportedEncodingException {
        //模拟出一个商品
        mockGoods = new Goods();
        mockGoods.setTitle("daisy测试商品");
        mockGoods.setDefaultPictureUrl("/Default.jpg");
        mockGoods.setPictureUrls("/13.jpg,456.jpg");
        mockGoods.setCharacters("商品特征是红色");
        mockGoods.setStepAmount(2L); //单次购买最低量
        mockGoods.setDefaultAmount(2L); //购买时缺省人次
        mockGoods.setToAmount(2L);//总需人数
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
        mockIssue.setBuyAmount(2L); //已购买的人次
        mockIssue.setPricePercentAmount(mockGoods.getPricePercentAmount()); //每人次单价
        mockIssue.setAttendAmount(mockGoods.getAttendAmount()); //购买次数,在中奖时从每期中累计此值
        mockIssue.setStatus(CommonEnum.IssueStatus.drawed);//状态
        mockIssue.setAwardingDate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-04-11 05:00:00"));//开奖日期
        mockIssue = mockIssueRep.saveAndFlush(mockIssue);
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
