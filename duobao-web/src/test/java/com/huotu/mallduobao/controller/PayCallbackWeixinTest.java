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
import com.huotu.mallduobao.entity.Orders;
import com.huotu.mallduobao.entity.OrdersItem;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.entity.UserBuyFlow;
import com.huotu.mallduobao.entity.UserMoneyFlow;
import com.huotu.mallduobao.model.PayResult;
import com.huotu.mallduobao.repository.IssueRepository;
import com.huotu.mallduobao.repository.OrdersRepository;
import com.huotu.mallduobao.repository.UserBuyFlowRepository;
import com.huotu.mallduobao.repository.UserMoneyFlowRepository;
import com.huotu.mallduobao.repository.UserRepository;
import com.huotu.mallduobao.service.PayService;
import com.huotu.mallduobao.service.impl.PayServiceImpl;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by daisy.zhang on 2016/4/13.
 * 订单通过微信现金支付成功，
 * 1.订单状态应该改变，
 * 2.订单详情状态应该改变，
 * 3.已购买人数+1
 * 4.更新购买流水
 * 5.创建金额流水
 * 7.如果是最后一个用户购买，则立即新建下一个期号
 * 8.如果购买的时候，该期已经完成，则默认购买的是下一期
 * 支付失败
 * 1.订单状态不改变
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class})
@ActiveProfiles("development")
@Transactional
public class PayCallbackWeixinTest extends BaseTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private OrdersRepository ordersRepository;
    @Autowired
    private PayCallBackController payCallBackController;
    @Autowired
    private UserBuyFlowRepository userBuyFlowRepository;
    @Autowired
    private UserMoneyFlowRepository userMoneyFlowRepository;

    private User mockUser;
    private Goods mockGoods;
    private Issue mockIssue;
    private UserBuyFlow mockUserBuyFlowA;
    private Orders mockOrder;
    private OrdersItem mockItem;
    private MockMvc mockMvcPay;
    @Before
    public void setUp() throws Exception {
        mockMvcPay = MockMvcBuilders.standaloneSetup(payCallBackController).build();
        //模拟一个商品
        mockGoods = daisyMockGoods();
        //模拟一期商品
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
        mockIssue = issueRepository.saveAndFlush(mockIssue);
        mockGoods.setIssue(mockIssue);
        //模拟一个购买用户
        mockUser = generateUserWithOpenId("123456","999999",userRepository);
//        模拟一个订单
        mockOrder = new Orders();
        mockOrder.setId("145789");
        mockOrder.setUser(mockUser);
        mockOrder.setOrderType(CommonEnum.OrderType.raiders);
        mockOrder.setTotalMoney(new BigDecimal(200L));
        mockOrder.setMoney(new BigDecimal(100L));
        mockOrder.setPayType(CommonEnum.PayType.weixin);
        mockOrder.setStatus(CommonEnum.OrderStatus.paying);
        mockOrder.setTime(new Date());
        mockOrder.setReceiver("daisy");
        mockOrder.setMobile("99999999999");
        mockOrder.setDetails("杭州");
        ordersRepository.saveAndFlush(mockOrder);
        //订单详情
        mockItem = saveOrderItem(mockOrder,mockIssue);
    }

    //正常订单，并且支付成功
    @Test
    public void testPayCallBackWeiXinSuccess() throws Exception {
        Long BuyAmount = mockIssue.getBuyAmount();
        MvcResult result = mockMvcPay.perform(post("/pay/payCallbackWeixin")
                .param("orderNo", mockOrder.getId()).param("money", mockOrder.getMoney().toString())
                .param("outOrderNo", "7777777777"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("payResult"))
                .andReturn();
        PayResult payResult = (PayResult) result.getModelAndView().getModel().get("payResult");
        Assert.assertEquals("code值错误","1",payResult.getCode().toString());
        Assert.assertEquals("提示信息错误","支付成功！",payResult.getMsg());
        Assert.assertEquals("订单状态没改变",CommonEnum.OrderStatus.payed,mockOrder.getStatus());
        Assert.assertEquals("订单详情状态没改变",CommonEnum.OrderStatus.payed,mockItem.getStatus());
        Assert.assertEquals("已购买数量并没有增加",String.valueOf(BuyAmount + mockItem.getAmount()),mockIssue.getBuyAmount().toString());
        //获取购买记录
        UserBuyFlow userBuyFlow = userBuyFlowRepository.findAllByIssueAndUser(mockIssue.getId(),mockUser.getId()).get(0);
        //判断购买记录中的值是否正确
        Assert.assertEquals("购买记录中数量不对",mockItem.getAmount(),userBuyFlow.getAmount());
        Assert.assertEquals("购买用户不对",mockUser,userBuyFlow.getUser());
        Assert.assertEquals("购买不是对应的期号",mockIssue,userBuyFlow.getIssue());
        Assert.assertNotNull("购买时间缺失",userBuyFlow.getTime());
        //获取金额流水
        UserMoneyFlow userMoneyFlow = userMoneyFlowRepository.findAll().get(0);

    }
    //订单不存在
    //订单已经被支付成功
    //订单已被关闭
    //支付金额不对
    @Test
    public void testMoneyWrong() throws Exception {
        Long BuyAmount = mockIssue.getBuyAmount();
        MvcResult result = mockMvcPay.perform(post("/pay/payCallbackWeixin")
                .param("orderNo", mockOrder.getId()).param("money", "20")
                .param("outOrderNo", "7777777777"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("payResult"))
                .andReturn();
        PayResult payResult = (PayResult) result.getModelAndView().getModel().get("payResult");
        Assert.assertEquals("code值错误","0",payResult.getCode().toString());
        Assert.assertEquals("提示信息错误","支付失败！",payResult.getMsg());
        Assert.assertEquals("订单状态不能改变",CommonEnum.OrderStatus.paying,mockOrder.getStatus());
        Assert.assertEquals("订单详情状态不能改变",CommonEnum.OrderStatus.paying,mockItem.getStatus());
        Assert.assertEquals("购买数量并没有增加",BuyAmount.toString(),mockIssue.getBuyAmount().toString());

    }
}
