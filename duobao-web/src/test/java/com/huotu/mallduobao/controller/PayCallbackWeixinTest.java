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
import com.huotu.mallduobao.repository.*;
import com.huotu.mallduobao.service.UserMoneyFlowService;
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
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by daisy.zhang on 2016/4/13.
 * 订单通过微信现金支付成功，
 * 1.订单状态应该改变，2.订单详情状态应该改变，3.已购买人数增加 4.更新购买流水 5.创建金额流水 6.创建用户中奖号码.7.如果是最后一个用户购买，
 * 则立即新建下一个期号
 * 8.如果购买的时候，该期已经完成，则默认购买的是下一期
 * 支付失败
 * 1.订单状态不改变
 * 充值订单完成成功
 * 1.用户余额增加
 * 2.金额流水增加
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
    private OrdersRepository ordersRepository;
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private OrdersItemRepository ordersItemRepository;
    @Autowired
    private PayCallBackController payCallBackController;
    @Autowired
    private UserBuyFlowRepository userBuyFlowRepository;
    @Autowired
    private UserMoneyFlowService userMoneyFlowService;
    @Autowired
    private UserNumberRepository userNumberRepository;

    private User mockUser;
    private Goods mockGoods;
    private Issue mockIssue;
    private Orders mockOrder;
    private OrdersItem mockItem;
    private MockMvc mockMvcPay;

    @Before
    public void setUp() throws Exception {
        mockMvcPay = MockMvcBuilders.standaloneSetup(payCallBackController).build();
        //模拟一个商品
        mockGoods = daisyMockGoods();
        //模拟一期商品
        mockIssue = daisyMockIssue(mockGoods);
        mockGoods.setIssue(mockIssue);
        //模拟一个购买用户
        mockUser = generateUserWithOpenId("123456", "999999", userRepository);
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
        mockItem = saveOrderItem(mockOrder, mockIssue);
    }

    //正常订单，使用微信，支付成功
    @Test
    public void testPayCallBackWeiXinSuccess() throws Exception {
        Long BuyAmount = mockIssue.getBuyAmount();
        MvcResult result = mockMvcPay.perform(post("/pay/payCallbackWeixin")
                .param("orderNo", mockOrder.getId()).param("totalfee", mockOrder.getMoney().toString())
                .param("outtradeno", "7777777777"))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.msg").value("支付成功！")))
                .andExpect((jsonPath("$.code").value(1)))
                .andDo(print())
                .andReturn();
//        PayResult payResult = (PayResult) result.getModelAndView().getModel().get("payResult");
//        Assert.assertEquals("code值错误", "1", payResult.getCode().toString());
//        Assert.assertEquals("提示信息错误", "支付成功！", payResult.getMsg());
        Assert.assertEquals("订单状态没改变", CommonEnum.OrderStatus.payed,
                ordersRepository.findOne(mockOrder.getId()).getStatus());
        Assert.assertEquals("订单详情状态没改变", CommonEnum.OrderStatus.payed,
                ordersItemRepository.findOne(mockItem.getId()).getStatus());
        Assert.assertEquals("已购买数量并没有增加", String.valueOf(BuyAmount + mockItem.getAmount()),
                issueRepository.findOne(mockIssue.getId()).getBuyAmount().toString());
        //获取购买记录
        UserBuyFlow userBuyFlow = userBuyFlowRepository.findAllByIssueAndUser(mockIssue.getId(), mockUser.getId()).get(0);
        //判断购买记录中的值是否正确
        Assert.assertEquals("购买记录中数量不对", mockItem.getAmount(), userBuyFlow.getAmount());
        Assert.assertEquals("购买用户不对", mockUser, userBuyFlow.getUser());
        Assert.assertEquals("购买不是对应的期号", mockIssue, userBuyFlow.getIssue());
        Assert.assertNotNull("购买时间缺失", userBuyFlow.getTime());
        //获取用户中奖号码
        Assert.assertNotNull("没有产生用户中奖号码", userNumberRepository.findByIssueAndUser(mockIssue, mockUser));
        //获取金额流水
        UserMoneyFlow userMoneyFlow = userMoneyFlowService.getUserMoneyFlowByUserId(mockUser.getId()).get(0);
        //判断金额流水中的各属性值是否正确
        Assert.assertEquals("流水类型错误", CommonEnum.MoneyFlowType.buy, userMoneyFlow.getMoneyFlowType());
        Assert.assertEquals("金额出错", mockOrder.getMoney(), userMoneyFlow.getMoney());
        Assert.assertEquals("用户余额出错", mockUser.getMoney(), userMoneyFlow.getCurrentMoney());
        Assert.assertNotNull("购买时间缺失", userMoneyFlow.getTime());
        Assert.assertEquals("购买", userMoneyFlow.getRemarek());
    }

    //订单不存在
    @Test
    public void testOrderNotFind() throws Exception {
        MvcResult result = mockMvcPay.perform(post("/pay/payCallbackWeixin")
                .param("orderNo", "999999").param("totalfee", mockOrder.getMoney().toString())
                .param("outtradeno", "7777777777"))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.msg").value("支付失败！")))
                .andExpect((jsonPath("$.code").value(0)))
                .andReturn();
//        PayResult payResult = (PayResult) result.getModelAndView().getModel().get("payResult");
//        Assert.assertEquals("code值错误", "0", payResult.getCode().toString());
//        Assert.assertEquals("提示信息错误", "支付失败！", payResult.getMsg());

    }
    //支付时，是最后一笔订单，则立即新建一期商品

    //订单已经被支付成功,再次收到回调请求，则直接返回支付成功
    @Test
    public void testOrderPayed() throws Exception {
        //设置订单状态为已支付
        mockOrder.setStatus(CommonEnum.OrderStatus.payed);
        ordersRepository.saveAndFlush(mockOrder);
        //设置订单详情状态为已支付
        mockItem.setStatus(CommonEnum.OrderStatus.payed);
        ordersItemRepository.saveAndFlush(mockItem);
        MvcResult result = mockMvcPay.perform(post("/pay/payCallbackWeixin")
                .param("orderNo", mockOrder.getId().toString()).param("totalfee", mockOrder.getMoney().toString())
                .param("outtradeno", "7777777777"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect((jsonPath("$.msg").value("支付成功！")))
                .andExpect((jsonPath("$.code").value(1)))
                .andReturn();
//        PayResult payResult = (PayResult) result.getModelAndView().getModel().get("payResult");
//        Assert.assertEquals("code值错误", "1", payResult.getCode().toString());
//        Assert.assertEquals("提示信息错误", "支付成功！", payResult.getMsg());

    }

    //支付金额不对
    @Test
    public void testMoneyWrong() throws Exception {
        Long BuyAmount = mockIssue.getBuyAmount();
        MvcResult result = mockMvcPay.perform(post("/pay/payCallbackWeixin")
                .param("orderNo", mockOrder.getId().toString()).param("totalfee", "20")
                .param("outtradeno", "7777777777"))
                .andExpect(status().isOk())
                .andExpect((jsonPath("$.msg").value("支付失败！")))
                .andExpect((jsonPath("$.code").value(0)))
                .andReturn();
//        PayResult payResult = (PayResult) result.getModelAndView().getModel().get("payResult");
//        Assert.assertEquals("code值错误", "0", payResult.getCode().toString());
//        Assert.assertEquals("提示信息错误", "支付失败！", payResult.getMsg());
        Assert.assertEquals("订单状态改变", CommonEnum.OrderStatus.paying, ordersRepository.findOne(mockOrder.getId()).getStatus());
        Assert.assertEquals("订单详情状态改变", CommonEnum.OrderStatus.paying, ordersItemRepository.findOne(mockItem.getId()).getStatus());
        Assert.assertEquals("购买数量被增加", BuyAmount.toString(), issueRepository.findOne(mockIssue.getId()).getBuyAmount().toString());
        Assert.assertNull("产生了购买记录", userBuyFlowRepository.getFirstBuyTimeByIssueId(mockIssue.getId()));
        Assert.assertEquals("产生了中奖号码", 0, userNumberRepository.findByIssueAndUser(mockIssue, mockUser).size());
        Assert.assertEquals("产生了金额流水", 0, userMoneyFlowService.getUserMoneyFlowByUserId(mockUser.getId()).size());
    }

    //支付时，已完被抢完，但状态是待开奖
//    @Test
//    public void testIssueOver() throws Exception {
//        //设置期号为已买完
//        mockIssue.setBuyAmount(20L);
//        mockIssue.setStatus(CommonEnum.IssueStatus.drawing);
//        issueRepository.saveAndFlush(mockIssue);
//        //本期
//        Long BuyAmount = mockIssue.getBuyAmount();
//        MvcResult result = mockMvcPay.perform(post("/pay/payCallbackWeixin")
//                .param("orderNo", mockOrder.getId().toString()).param("totalfee", mockOrder.getMoney().toString())
//                .param("outtradeno", "7777777777"))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("payResult"))
//                .andReturn();
//        PayResult payResult = (PayResult) result.getModelAndView().getModel().get("payResult");
//        Assert.assertEquals("code值错误", "1", payResult.getCode().toString());
//        Assert.assertEquals("提示信息错误", "支付成功！", payResult.getMsg());
//        Assert.assertEquals("订单状态没改变", CommonEnum.OrderStatus.payed,
//                ordersRepository.findOne(mockOrder.getId()).getStatus());
//        Assert.assertEquals("订单详情状态没改变", CommonEnum.OrderStatus.payed,
//                ordersItemRepository.findOne(mockItem.getId()).getStatus());
//        //不会在本次再次增加已购数量
//        Assert.assertEquals("在本期增加了购买人数", BuyAmount, issueRepository.findOne(mockIssue.getId()).getBuyAmount());
//        //新一期的期号
//        Long newIssueId = findMaxIssueId(issueRepository.findAllIssueByGoodsId(mockGoods.getId()));
//        Assert.assertNotEquals("并没有生成下一期", mockIssue.getId(), newIssueId);
//        //订单详情中的新期号
//        Long newOrderItemIssueId = ordersItemRepository.findOne(mockItem.getId()).getIssue().getId();
//        Assert.assertNotEquals("并没有更新期次", mockIssue.getId(), newOrderItemIssueId);
//        Assert.assertEquals("已购买数量并没有增加", mockItem.getAmount(), issueRepository.findOne(newIssueId).getBuyAmount());
//        //获取购买记录
//        UserBuyFlow userBuyFlow = userBuyFlowRepository.findAllByIssueAndUser(newIssueId, mockUser.getId()).get(0);
//        //判断购买记录中的值是否正确
//        Assert.assertEquals("购买记录中数量不对", mockItem.getAmount(), userBuyFlow.getAmount());
//        Assert.assertEquals("购买用户不对", mockUser, userBuyFlow.getUser());
//        Assert.assertNotNull("没有期号", userBuyFlow.getIssue());
//        Assert.assertNotNull("购买时间缺失", userBuyFlow.getTime());
//        //获取用户中奖号码
//        Assert.assertNotNull("没有产生用户中奖号码", userNumberRepository.findByIssueAndUser(issueRepository.findOne(newIssueId), mockUser));
//        //获取金额流水
//        UserMoneyFlow userMoneyFlow = userMoneyFlowService.getUserMoneyFlowByUserId(mockUser.getId()).get(0);
//        //判断金额流水中的各属性值是否正确
//        Assert.assertEquals("流水类型错误", CommonEnum.MoneyFlowType.buy, userMoneyFlow.getMoneyFlowType());
//        Assert.assertEquals("金额出错", mockOrder.getMoney(), userMoneyFlow.getMoney());
//        Assert.assertEquals("用户余额出错", mockUser.getMoney(), userMoneyFlow.getCurrentMoney());
//        Assert.assertNotNull("购买时间缺失", userMoneyFlow.getTime());
//        Assert.assertEquals("购买", userMoneyFlow.getRemarek());
//
//    }
//    //支付时，已经是已开奖状态
//
//    @Test
//    public void testIssueDrawed() throws Exception {
//        //设置期号为已买完
//        mockIssue.setBuyAmount(20L);
//        mockIssue.setStatus(CommonEnum.IssueStatus.drawed);
//        issueRepository.saveAndFlush(mockIssue);
//        //本期
//        Long BuyAmount = mockIssue.getBuyAmount();
//        MvcResult result = mockMvcPay.perform(post("/pay/payCallbackWeixin")
//                .param("orderNo", mockOrder.getId().toString()).param("totalfee", mockOrder.getMoney().toString())
//                .param("outtradeno", "7777777777"))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("payResult"))
//                .andReturn();
//        PayResult payResult = (PayResult) result.getModelAndView().getModel().get("payResult");
//        Assert.assertEquals("code值错误", "1", payResult.getCode().toString());
//        Assert.assertEquals("提示信息错误", "支付成功！", payResult.getMsg());
//        Assert.assertEquals("订单状态没改变", CommonEnum.OrderStatus.payed,
//                ordersRepository.findOne(mockOrder.getId()).getStatus());
//        Assert.assertEquals("订单详情状态没改变", CommonEnum.OrderStatus.payed,
//                ordersItemRepository.findOne(mockItem.getId()).getStatus());
//        //不会在本次再次增加已购数量
//        Assert.assertEquals("在本期增加了购买人数", BuyAmount, issueRepository.findOne(mockIssue.getId()).getBuyAmount());
//        //新一期的期号
//        Long newIssueId = findMaxIssueId(issueRepository.findAllIssueByGoodsId(mockGoods.getId()));
//        Assert.assertNotEquals("并没有生成下一期", mockIssue.getId(), newIssueId);
//        //订单详情中的新期号
//        Long newOrderItemIssueId = ordersItemRepository.findOne(mockItem.getId()).getIssue().getId();
//        Assert.assertNotEquals("并没有更新期次", mockIssue.getId(), newOrderItemIssueId);
//        Assert.assertEquals("已购买数量并没有增加", mockItem.getAmount(), issueRepository.findOne(newIssueId).getBuyAmount());
//        //获取购买记录
//        UserBuyFlow userBuyFlow = userBuyFlowRepository.findAllByIssueAndUser(newIssueId, mockUser.getId()).get(0);
//        //判断购买记录中的值是否正确
//        Assert.assertEquals("购买记录中数量不对", mockItem.getAmount(), userBuyFlow.getAmount());
//        Assert.assertEquals("购买用户不对", mockUser, userBuyFlow.getUser());
//        Assert.assertNotNull("没有期号", userBuyFlow.getIssue());
//        Assert.assertNotNull("购买时间缺失", userBuyFlow.getTime());
//        //获取用户中奖号码
//        Assert.assertNotNull("没有产生用户中奖号码", userNumberRepository.findByIssueAndUser(issueRepository.findOne(newIssueId), mockUser));
//        //获取金额流水
//        UserMoneyFlow userMoneyFlow = userMoneyFlowService.getUserMoneyFlowByUserId(mockUser.getId()).get(0);
//        //判断金额流水中的各属性值是否正确
//        Assert.assertEquals("流水类型错误", CommonEnum.MoneyFlowType.buy, userMoneyFlow.getMoneyFlowType());
//        Assert.assertEquals("金额出错", mockOrder.getMoney(), userMoneyFlow.getMoney());
//        Assert.assertEquals("用户余额出错", mockUser.getMoney(), userMoneyFlow.getCurrentMoney());
//        Assert.assertNotNull("购买时间缺失", userMoneyFlow.getTime());
//        Assert.assertEquals("购买", userMoneyFlow.getRemarek());
//    }


//    //充值订单，正常完成
//    @Test
//    public void testAddMoney() throws Exception {
//        //设置订单类型为充值订单
//        mockOrder.setOrderType(CommonEnum.OrderType.put);
//        MvcResult result = mockMvcPay.perform(post("/pay/payCallbackWeixin")
//                .param("tradeno", mockOrder.getId().toString()).param("totalfee", mockOrder.getMoney().toString())
//                .param("outtradeno", "7777777777"))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("payResult"))
//                .andReturn();

//    }

//    /**
//     * 测试两个订单支付成功，但是剩余人次不足，写入“订单失败”表 by xhk
//     */
//    @Test
//    @Rollback(false)
//    public void testOrderFail() throws Exception {
//        MvcResult result = mockMvcPay.perform(post("/pay/payCallbackWeixin")
//                .param("orderNo", "201604181610345011")
//                .param("totalfee", "0.01")
//                .param("outtradeno", "123456"))
//                .andExpect(status().isOk())
//                .andReturn();
//        MvcResult result1 = mockMvcPay.perform(post("/pay/payCallbackWeixin")
//                .param("orderNo", "2016041816111983120")
//                .param("totalfee", "0.01")
//                .param("outtradeno", "654321"))
//                .andExpect(status().isOk())
//                .andReturn();
//    }

}
