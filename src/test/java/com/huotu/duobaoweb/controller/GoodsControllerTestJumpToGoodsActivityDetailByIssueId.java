/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */
package com.huotu.duobaoweb.controller;

import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.Goods;
import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.repository.GoodsRepository;
import com.huotu.duobaoweb.repository.IssueRepository;
import com.huotu.duobaoweb.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Created by daisy.zhang on 2016/4/6.
 * 通过期号ID跳转到商品详情页
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles("development")
@Transactional
public class GoodsControllerTestJumpToGoodsActivityDetailByIssueId {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    private GoodsRepository mockGoodsRep;
    @Autowired
    private IssueRepository mockIssueRep;
    @Autowired
    private UserRepository mockUserRep;

    private Issue mockIssue;
    private Goods mockGoods;
    private User mockUser;

    @Before
    public void init() throws ParseException {
        //模拟一个期号
        mockIssue = new Issue();
        mockIssue.setGoods(mockGoods);//所属活动商品
        mockIssue.setStepAmount(mockGoods.getStepAmount());//单次购买最低量
        mockIssue.setDefaultAmount(mockGoods.getDefaultAmount()); //缺省购买人次
        mockIssue.setToAmount(mockGoods.getToAmount()); //总需购买人次
        mockIssue.setBuyAmount(3L); //已购买的人次
        mockIssue.setPricePercentAmount(mockGoods.getPricePercentAmount()); //每人次单价
        mockIssue.setAttendAmount(mockGoods.getAttendAmount()); //购买次数,在中奖时从每期中累计此值
        mockIssue.setStatus(CommonEnum.IssueStatus.going);//状态
        mockIssue.setAwardingDate(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-04-28 05:00:00"));//开奖日期
        mockIssue = mockIssueRep.save(mockIssue);

        //模拟出一个商品
        mockGoods = new Goods();
        mockGoods.setTitle("daisy测试商品");
        mockGoods.setDefaultPictureUrl("DefaultPicture.jpg");
        mockGoods.setPictureUrls("DefaultPicture.jpg");
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
        mockGoods.setAttendAmount(0L); //购买次数
        mockGoods.setViewAmount(2L); //浏览器
        mockGoods.setMerchantId(3347L); //设置商城ID
        mockGoods = mockGoodsRep.save(mockGoods);
    }

    //期号不传,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestNOIssueID() throws Exception {
        mockMvc.perform(get("/detailByIssueId").param("id", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/XXXX"));
    }

    //期号错误,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestWrongIssueID() throws Exception {
        mockMvc.perform(get("/detailByIssueId").param("id", "ABC"))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/XXXX"));
    }

    //商品ID在数据库中不存在,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestNotFindIssueID() throws Exception {
        mockMvc.perform(get("/detailByIssueId").param("id", "999999999"))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/XXXX"));
    }

    //期号所对应的商品商品状态未审核,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestGoodUnCheck() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.uncheck);
        mockMvc.perform(get("/detailByIssueId").param("id", mockGoods.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/XXXX"));

    }

    //期号所对应的商品已经过期,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestExpired() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.up);
        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-03-27 00:00:00"));
        mockMvc.perform(get("/detailByIssueId").param("id", mockGoods.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/XXXX"));
    }

    //期号所对应的商品还未开始活动,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestNotStart() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.up);
        mockGoods.setStartTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2018-03-27 00:00:00"));
        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-03-27 00:00:00"));
        mockMvc.perform(get("/detailByIssueId").param("id", mockGoods.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/XXXX"));
    }

    //期号所对应的商品活动开始，但还未有任何人参与，判断返回模型数据是否正确
    @Test
    public void TestNoJoined() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.up);
        mockGoods.setStartTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-03-27 00:00:00"));
        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-03-27 00:00:00"));
        mockIssue.setBuyAmount(0L);
        mockMvc.perform(get("/detailByIssueId").param("id", mockGoods.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/detail"))
                .andExpect(model().attribute("id", mockGoods.getId().toString()))
                .andExpect(model().attribute("issueId", mockGoods.getIssue()))
                .andExpect(model().attribute("pictureUrls", mockGoods.getPictureUrls()))
                .andExpect(model().attribute("status", 0))
                .andExpect(model().attribute("title", mockGoods.getTitle()))
                .andExpect(model().attribute("progress", mockIssue.getBuyAmount() / mockGoods.getToAmount()))
                .andExpect(model().attribute("toAmount", mockIssue.getToAmount()))
                .andExpect(model().attribute("remainAmount", mockIssue.getToAmount() - mockIssue.getBuyAmount()))
                .andExpect(model().attribute("defaultAmount", mockIssue.getDefaultAmount()))
                .andExpect(model().attribute("stepAmount", mockIssue.getStepAmount()))
                .andExpect(model().attribute("joinCount", mockIssue.getBuyAmount()))
                .andExpect(model().attribute("numbers", null))
                .andExpect(model().attribute("fullPrice", mockIssue.getPricePercentAmount().multiply(new BigDecimal(mockIssue.getToAmount()))))
                .andExpect(model().attributeExists("toAwardTime"))
                .andExpect(model().attribute("awardUserName", null))
                .andExpect(model().attribute("awardUserCityName", null))
                .andExpect(model().attribute("awardUserIp", null))
                .andExpect(model().attribute("awardUserJoinCount", null))
                .andExpect(model().attribute("awardTime", mockIssue.getAwardingDate()))
                .andExpect(model().attribute("luckNumber", null))
                .andExpect(model().attribute("awardUserHead", null))
                .andExpect(model().attribute("firstBuyTime", null));

    }

    //商品活动还在进行中，并且有用户参与过，查看模型返回数据是否正确(进度刷新)
    @Test
    public void TestUserJoined() throws Exception {
        mockIssue.setBuyAmount(8L);
        mockMvc.perform(get("/detailByIssueId").param("id", mockGoods.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/detail"))
                .andExpect(model().attribute("id", mockGoods.getId().toString()))
                .andExpect(model().attribute("issueId", mockGoods.getIssue()))
                .andExpect(model().attribute("pictureUrls", mockGoods.getPictureUrls()))
                .andExpect(model().attribute("status", 0))
                .andExpect(model().attribute("title", mockGoods.getTitle()))
                .andExpect(model().attribute("progress", mockIssue.getBuyAmount() / mockGoods.getToAmount()))
                .andExpect(model().attribute("toAmount", mockIssue.getToAmount()))
                .andExpect(model().attribute("remainAmount", mockIssue.getToAmount() - mockIssue.getBuyAmount()))
                .andExpect(model().attribute("defaultAmount", mockIssue.getDefaultAmount()))
                .andExpect(model().attribute("stepAmount", mockIssue.getStepAmount()))
                .andExpect(model().attribute("joinCount", mockIssue.getBuyAmount()))
                .andExpect(model().attribute("numbers", null))
                .andExpect(model().attribute("fullPrice", mockIssue.getPricePercentAmount().multiply(new BigDecimal(mockIssue.getToAmount()))))
                .andExpect(model().attributeExists("toAwardTime"))
                .andExpect(model().attribute("awardUserName", null))
                .andExpect(model().attribute("awardUserCityName", null))
                .andExpect(model().attribute("awardUserIp", null))
                .andExpect(model().attribute("awardUserJoinCount", null))
                .andExpect(model().attribute("awardTime", mockIssue.getAwardingDate()))
                .andExpect(model().attribute("luckNumber", null))
                .andExpect(model().attribute("awardUserHead", null))
                .andExpect(model().attribute("firstBuyTime", null));


    }

    //商品状态为等待开奖，查看模型数据返回是否正确
    @Test
    public void TestIssueDrawing() throws Exception {
        mockIssue.setStatus(CommonEnum.IssueStatus.drawing);
        mockIssue.setBuyAmount(10L);
        mockMvc.perform(get("/detailByIssueId").param("id", mockGoods.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/detail"))
                .andExpect(model().attribute("id", mockGoods.getId().toString()))
                .andExpect(model().attribute("issueId", mockGoods.getIssue()))
                .andExpect(model().attribute("pictureUrls", mockGoods.getPictureUrls()))
                .andExpect(model().attribute("status", 1))
                .andExpect(model().attribute("title", mockGoods.getTitle()))
                .andExpect(model().attribute("progress", mockIssue.getBuyAmount() / mockGoods.getToAmount()))
                .andExpect(model().attribute("toAmount", mockIssue.getToAmount()))
                .andExpect(model().attribute("remainAmount", mockIssue.getToAmount() - mockIssue.getBuyAmount()))
                .andExpect(model().attribute("defaultAmount", mockIssue.getDefaultAmount()))
                .andExpect(model().attribute("stepAmount", mockIssue.getStepAmount()))
                .andExpect(model().attribute("joinCount", mockIssue.getBuyAmount()))
                .andExpect(model().attributeExists("numbers"))
                .andExpect(model().attribute("fullPrice", mockIssue.getPricePercentAmount().multiply(new BigDecimal(mockIssue.getToAmount()))))
                .andExpect(model().attributeExists("toAwardTime"))
                .andExpect(model().attribute("awardUserName", null))
                .andExpect(model().attribute("awardUserCityName", null))
                .andExpect(model().attribute("awardUserIp", null))
                .andExpect(model().attribute("awardUserJoinCount", null))
                .andExpect(model().attribute("awardTime", mockIssue.getAwardingDate()))
                .andExpect(model().attribute("luckNumber", null))
                .andExpect(model().attribute("awardUserHead", null))
                .andExpect(model().attribute("firstBuyTime", null));
    }

    //商品状态已开奖
    @Test
    public void TestDrawed() throws Exception {
        //模拟一个用户
        mockUser.setUsername("daisy");
        mockUser.setCityName("杭州");
        mockUser.setIp("192.168.1.13");
        mockUser.setUserHead("daisy.jpg");
        mockUserRep.save(mockUser);
        //修改本期状态
        mockIssue.setStatus(CommonEnum.IssueStatus.drawed);
        //设置中奖用户为模拟用户
        mockIssue.setAwardingUser(mockUser);
        //设置中奖号码
        mockIssue.setLuckyNumber(123456L);
        //测试本期中奖后，模型数据是否返回正确
        mockMvc.perform(get("/detailByIssueId").param("id", mockGoods.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/detail"))
                .andExpect(model().attribute("id", mockGoods.getId().toString()))
                .andExpect(model().attribute("issueId", mockGoods.getIssue()))
                .andExpect(model().attribute("pictureUrls", mockGoods.getPictureUrls()))
                .andExpect(model().attribute("status", 2))
                .andExpect(model().attribute("title", mockGoods.getTitle()))
                .andExpect(model().attribute("progress", mockIssue.getBuyAmount() / mockGoods.getToAmount()))
                .andExpect(model().attribute("toAmount", mockIssue.getToAmount()))
                .andExpect(model().attribute("remainAmount", mockIssue.getToAmount() - mockIssue.getBuyAmount()))
                .andExpect(model().attribute("defaultAmount", mockIssue.getDefaultAmount()))
                .andExpect(model().attribute("stepAmount", mockIssue.getStepAmount()))
                .andExpect(model().attribute("joinCount", mockIssue.getBuyAmount()))
                .andExpect(model().attributeExists("numbers"))
                .andExpect(model().attribute("fullPrice", mockIssue.getPricePercentAmount().multiply(new BigDecimal(mockIssue.getToAmount()))))
                .andExpect(model().attributeExists("toAwardTime"))
                .andExpect(model().attribute("awardUserName", mockUser.getUsername()))
                .andExpect(model().attribute("awardUserCityName", mockUser.getCityName()))
                .andExpect(model().attribute("awardUserIp", mockUser.getIp()))
                .andExpect(model().attributeExists("awardUserJoinCount"))
                .andExpect(model().attribute("awardTime", mockIssue.getAwardingDate()))
                .andExpect(model().attribute("luckNumber", mockIssue.getLuckyNumber()))
                .andExpect(model().attributeExists("awardUserHead"))
                .andExpect(model().attributeExists("firstBuyTime"));


    }
}