/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */
<<<<<<< HEAD
package com.huotu.duobaoweb.controller;

import com.huotu.duobaoweb.base.BaseTest;
import com.huotu.duobaoweb.boot.MVCConfig;
import com.huotu.duobaoweb.boot.RootConfig;
import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.Goods;
import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.entity.UserBuyFlow;
import com.huotu.duobaoweb.entity.UserNumber;
import com.huotu.duobaoweb.model.GoodsDetailModel;
import com.huotu.duobaoweb.repository.GoodsRepository;
import com.huotu.duobaoweb.repository.IssueRepository;
import com.huotu.duobaoweb.repository.UserBuyFlowRepository;
import com.huotu.duobaoweb.repository.UserRepository;
=======
package com.huotu.mallduobao.controller;

import com.huotu.mallduobao.base.BaseTest;
import com.huotu.mallduobao.boot.MVCConfig;
import com.huotu.mallduobao.boot.RootConfig;
import com.huotu.mallduobao.entity.Goods;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.entity.UserBuyFlow;
import com.huotu.mallduobao.entity.UserNumber;
import com.huotu.mallduobao.model.GoodsDetailModel;
import com.huotu.mallduobao.repository.GoodsRepository;
import com.huotu.mallduobao.repository.UserBuyFlowRepository;
import com.huotu.mallduobao.repository.UserRepository;
import com.huotu.mallduobao.service.CommonConfigService;
import com.huotu.mallduobao.utils.CommonEnum;
>>>>>>> origin/master
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

<<<<<<< HEAD
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.text.ParseException;
=======
>>>>>>> origin/master
import java.text.SimpleDateFormat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by daisy.zhang on 2016/4/6.
 * 通过商品ID获取商品详情
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class})
@ActiveProfiles("development")
@Transactional
public class GoodsControllerTestJumpToGoodsActivityDetailByGoodsId extends BaseTest {

    @Autowired
    private GoodsRepository mockGoodsRep;
    @Autowired
    private UserRepository mockUserRep;
    @Autowired
    private UserBuyFlowRepository userBuyFlowRepository;

    private Issue mockIssue;
    private Goods mockGoods;
    private User mockUserA;
    private User mockUserB;

    //商品现价
    private BigDecimal costPrice;
    private UserBuyFlow mockUserBuyFlowA;
    private UserBuyFlow mockUserBuyFlowB;
    private UserNumber mockUserNumberA;
    private UserNumber mockUserNumberB;


    @Before
    public void setUp() throws ParseException, UnsupportedEncodingException {
        //模拟出一个商品
        mockGoods = new Goods();
        mockGoods.setTitle("daisy测试商品");
        mockGoods.setDefaultPictureUrl("/Default.jpg");
        mockGoods.setPictureUrls("/13.jpg,456.jpg");
        mockGoods.setCharacters("商品特征是红色");
        mockGoods.setStepAmount(1L); //单次购买最低量
        mockGoods.setDefaultAmount(1L); //购买时缺省人次
        mockGoods.setToAmount(20L);//总需人数
        mockGoods.setPricePercentAmount(new BigDecimal(1L)); //购买每人次单价
        mockGoods.setStatus(CommonEnum.GoodsStatus.up); //商品状态
        mockGoods.setStartTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-01-27 00:00:00")); //活动开始时间
        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-05-27 00:00:00")); //活动截止时间
        mockGoods.setShareTitle("丹青测试商品的分享标题"); //分享标题
        mockGoods.setShareDescription("丹青测试商品的分享描述"); //分享描述
        mockGoods.setSharePictureUrl("http://XXXXX.jpg"); //分享图片地址
        mockGoods.setToMallGoodsId(123456L);
        mockGoods.setAttendAmount(10L); //购买次数
        mockGoods.setViewAmount(2L); //浏览量
        mockGoods.setMerchantId(3447L); //设置商城ID
        mockGoods = mockGoodsRep.saveAndFlush(mockGoods);

        //模拟一个期号
        mockIssue = daisyMockIssue(mockGoods);
        mockGoods.setIssue(mockIssue);
        mockGoods = mockGoodsRep.saveAndFlush(mockGoods);
        //模拟一个用户
        mockUserA = generateUserWithOpenId("123456", "7777777", mockUserRep);
        mockUserB = generateUserWithOpenId("4561758", "7777777", mockUserRep);

        //计算商品原价，后面case断言使用
        costPrice = mockGoods.getPricePercentAmount().multiply(new BigDecimal(mockGoods.getToAmount()));
    }

    //商品活动开始，但还未有任何人参与，判断返回模型数据是否正确
    @Test
    public void TestNoJoined() throws Exception {
        MvcResult result = mockMvc.perform(get("/goods/detailByGoodsId").param("goodsId", mockGoods.getId().toString())
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("/html/goods/detail"))
                .andExpect(model().attribute("issueId", mockIssue.getId()))
                .andExpect(model().attributeExists("goodsDetailModel"))
                .andExpect(model().attribute("customerId", 3447L))
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
        Assert.assertEquals("参与人数错误", mockIssue.getBuyAmount().toString(), goodsDetailModel.getJoinCount().toString());
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

    //商品活动还在进行中，并且有用户参与过，查看模型返回数据是否正确(进度刷新)
    @Test
    public void TestUserJoined() throws Exception {
        //更新购买人数
        mockIssue.setBuyAmount(2L);
        //一个用户模拟2条购买记录
        mockUserBuyFlowA = saveUserBuyFlow(mockUserA, mockIssue);
        mockUserBuyFlowB = saveUserBuyFlow(mockUserA, mockIssue);
        //模拟用户的两个中奖号码
        mockUserNumberA = saveUserNumber(mockUserA, mockIssue, 1);
        mockUserNumberB = saveUserNumber(mockUserA, mockIssue, 2);

        MvcResult result = mockMvc.perform(get("/goods/detailByGoodsId").param("goodsId", mockGoods.getId().toString())
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
        Assert.assertEquals("参与人数错误", mockIssue.getBuyAmount().toString(), goodsDetailModel.getJoinCount().toString());
       // Assert.assertNotNull("参与号码不存在", goodsDetailModel.getNumber());
        Assert.assertEquals("全额购买金额错误", costPrice, goodsDetailModel.getFullPrice());
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
        mockUserNumberA = saveUserNumber(mockUserA, mockIssue, 3);
        mockUserNumberB = saveUserNumber(mockUserB, mockIssue, 4);
        MvcResult result = mockMvc.perform(get("/goods/detailByGoodsId").param("goodsId", mockGoods.getId().toString())
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
        //Assert.assertEquals("参与人数错误", mockIssue.getBuyAmount().toString(), goodsDetailModel.getJoinCount().toString());
       // Assert.assertEquals("参与号码", mockUserNumberA.getNumber(), goodsDetailModel.getNumber());
        Assert.assertNotNull("距离开奖时间缺失", goodsDetailModel.getToAwardTime());
        Assert.assertNull("中奖用户不应该存在", goodsDetailModel.getAwardUserName());
        Assert.assertNull("中奖用户城市不应该存在", goodsDetailModel.getAwardUserCityName());
        Assert.assertNull("三奖用户ip不应该存在", goodsDetailModel.getAwardUserIp());
        Assert.assertNull("中奖用户参与次数不应该有", goodsDetailModel.getAwardUserJoinCount());
        //Assert.assertNotNull("开奖时间没有", goodsDetailModel.getAwardTime());
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
        mockUserB.setRealName("daisy");
        mockUserB.setCityName("杭州");
        mockUserB.setIp("192.168.1.30");
        //两个用户模拟2条购买记录
        mockUserBuyFlowA = saveUserBuyFlow(mockUserA, mockIssue);
        mockUserBuyFlowB = saveUserBuyFlow(mockUserB, mockIssue);
        //模拟用户的两个中奖号码
        mockUserNumberA = saveUserNumber(mockUserA, mockIssue, 2);
        mockUserNumberB = saveUserNumber(mockUserB, mockIssue, 3);

        //设置中奖用户为模拟用户
        mockIssue.setAwardingUser(mockUserB);
        //设置中奖号码
        mockIssue.setLuckyNumber(mockUserNumberB.getNumber());

        //测试本期中奖后，模型数据是否返回正确
        MvcResult result = mockMvc.perform(get("/goods/detailByGoodsId").param("goodsId", mockGoods.getId().toString())
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
      //  Assert.assertEquals("参与人数错误", mockIssue.getBuyAmount(), goodsDetailModel.getJoinCount());
      //  Assert.assertNotNull("参与号码", goodsDetailModel.getNumber());
        Assert.assertEquals("中奖用户名错误", mockIssue.getAwardingUser().getRealName(), goodsDetailModel.getAwardUserName());
        Assert.assertEquals("中奖用户城市错误", mockIssue.getAwardingUser().getCityName(), goodsDetailModel.getAwardUserCityName());
        Assert.assertEquals("中奖用户IP错误", mockIssue.getAwardingUser().getIp(), goodsDetailModel.getAwardUserIp());
        Assert.assertEquals("中奖用户参与次数错误", String.valueOf(userBuyFlowRepository.findAllByIssueAndUser(mockIssue.getId(), mockUserB.getId()).size()),
                goodsDetailModel.getAwardUserJoinCount().toString());
        Assert.assertNotNull("开奖时间缺失", goodsDetailModel.getAwardTime());
        Assert.assertEquals("幸运号码错误", mockIssue.getLuckyNumber(), goodsDetailModel.getLuckNumber());
        Assert.assertNotNull("用户头像不存在", goodsDetailModel.getAwardUserHead());
        Assert.assertNotNull("首次购买时间缺失", goodsDetailModel.getFirstBuyTime());

    }

    //期号ID不传,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestNoGoodsID() throws Exception {
        mockMvc.perform(get("/goods/detailByGoodsId").param("goodsId", "")
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/XXXX"));
    }

    //期号ID错误,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestWrongGoodsID() throws Exception {
        mockMvc.perform(get("/goods/detailByGoodsId").param("goodsId", "abc")
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/XXXX"));
    }

    //期号ID在数据库中不存在,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestNotFindGoods() throws Exception {
        mockMvc.perform(get("/goods/detailByGoodsId").param("goodsId", "999999999")
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/XXXX"));
    }

    //商品状态未审核,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestGoodUnCheck() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.uncheck);
        mockMvc.perform(get("/goods/detailByGoodsId").param("goodsId", mockGoods.getId().toString())
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/XXXX"));

    }

    //商品已经过期,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestExpired() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.up);
        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2016-03-27 00:00:00"));
        mockMvc.perform(get("/goods/detailByGoodsId").param("goodsId", mockGoods.getId().toString())
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/XXXX"));
    }

    //商品还未开始活动,判断容错（错误码还未定义，定义后统一修改）
    @Test
    public void TestNotStart() throws Exception {
        mockGoods.setStatus(CommonEnum.GoodsStatus.up);
        mockGoods.setStartTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2018-03-27 00:00:00"));
        mockGoods.setEndTime(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-03-27 00:00:00"));
        mockMvc.perform(get("/goods/detailByGoodsId").param("goodsId", mockGoods.getId().toString())
                .param("customerId", "3447").param("issueId", mockIssue.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(view().name("redirect:/html/goods/XXXX"));
    }


}
