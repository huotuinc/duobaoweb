package com.huotu.mallduobao.controller;

import com.huotu.huobanplus.sdk.base.BaseClientSpringConfig;
import com.huotu.mallduobao.base.BaseTest;
import com.huotu.mallduobao.boot.MVCConfig;
import com.huotu.mallduobao.boot.RootConfig;
import com.huotu.mallduobao.entity.Delivery;
import com.huotu.mallduobao.entity.Goods;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.Orders;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.entity.UserBuyFlow;
import com.huotu.mallduobao.entity.UserNumber;
import com.huotu.mallduobao.model.DeliveryModel;
import com.huotu.mallduobao.repository.CachedIssueLeaveNumberRepository;
import com.huotu.mallduobao.repository.DeliveryRepository;
import com.huotu.mallduobao.repository.GoodsRepository;
import com.huotu.mallduobao.repository.IssueRepository;
import com.huotu.mallduobao.repository.OrdersItemRepository;
import com.huotu.mallduobao.repository.OrdersRepository;
import com.huotu.mallduobao.repository.UserBuyFlowRepository;
import com.huotu.mallduobao.repository.UserNumberRepository;
import com.huotu.mallduobao.repository.UserRepository;
import com.huotu.mallduobao.service.CacheService;
import com.huotu.mallduobao.service.RaidersCoreService;
import com.huotu.mallduobao.utils.CommonEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

/**
 * Created by cosy on 2016/4/18.
 */


@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class, BaseClientSpringConfig.class})
@ActiveProfiles("development")
@Transactional
public class PersonControllerTestGetOneLotteryInfo extends BaseTest {


    private Log log = LogFactory.getLog(PersonControllerTestGetOneLotteryInfo.class);


    @Autowired
    UserRepository userRepository;

    @Autowired
    GoodsRepository goodsRepository;

    @Autowired
    IssueRepository issueRepository;

    @Autowired
    OrdersItemRepository ordersItemRepository;

    @Autowired
    CachedIssueLeaveNumberRepository cachedIssueLeaveNumberRepository;

    @Autowired
    CacheService cacheService;

    @Autowired
    RaidersCoreService raidersCoreService;

    @Autowired
    UserBuyFlowRepository userBuyFlowRepository;
    @Autowired
    OrdersRepository ordersRepository;
    @Autowired
    DeliveryRepository deliveryRepository;
    @Autowired
    UserNumberRepository userNumberRepository;


    private User currentUser;
    private Issue currentIssue2;
    private Goods goods;
    private Delivery delivery;



    //获得奖品（GetPrize）
    @Rollback(true)
    @Test
    public void statusGetPrize() throws Exception
    {

        //商品
        goods = new Goods();
        goods.setTitle("cosytest");
        goods.setCharacters("这是测试商品");
        goods.setDefaultPictureUrl("/resources/images/aa.jpg");
        goods.setPictureUrls("/resources/images/bb.jpg,/resources/images/cc.jpg");
        goods.setSharePictureUrl("/resources/images/dd.jpg");
        goods.setDefaultAmount(10L);
        goods.setToAmount(100L);
        goods.setPricePercentAmount(new BigDecimal(1));
        goods.setStatus(CommonEnum.GoodsStatus.up);
        goods = goodsRepository.saveAndFlush(goods);

        //用户
        currentUser = new User();
        currentUser.setUsername("cosylj");
        currentUser.setPassword("123456");
        currentUser.setMobile("13600541783");
        currentUser.setMobileBinded(true);
        currentUser.setWeixinOpenId("111");
        currentUser.setMerchantId(3447L);
        currentUser.setMoney(new BigDecimal(100));
        currentUser.setRegTime(new Date());
        currentUser=userRepository.saveAndFlush(currentUser);

        //期号二，用于设置中奖
        currentIssue2 = new Issue();
        currentIssue2.setGoods(goods);
        currentIssue2.setDefaultAmount(10L);
        currentIssue2.setToAmount(100L);//总需人次
        currentIssue2.setBuyAmount(100L);
        currentIssue2.setPricePercentAmount(new BigDecimal(1));
        currentIssue2.setAttendAmount(1L);
        currentIssue2.setStatus(CommonEnum.IssueStatus.drawed);
        currentIssue2.setAwardingDate(new Date());
        currentIssue2.setAwardingUser(currentUser);
        currentIssue2=issueRepository.saveAndFlush(currentIssue2);

        //收货地址
        delivery = new Delivery();
        delivery.setIssue(currentIssue2);
        delivery.setUser(currentUser);
        delivery.setDeliveryStatus(CommonEnum.DeliveryStatus.GetPrize);
        delivery.setIsCommit(false);
        delivery = deliveryRepository.saveAndFlush(delivery);


        MvcResult result=mockMvc.perform(get("/personal/getOneLotteryInfo")
                                .param("customerId","3447")
                                .param("issueId",currentIssue2.getId().toString()))
                                .andExpect(model().attribute("customerId",3447L))
                                .andExpect(model().attribute("issueId",currentIssue2.getId()))
                                .andExpect(model().attributeExists("deliveryModel"))
                                .andReturn();
        DeliveryModel deliveryModel=(DeliveryModel)result.getModelAndView().getModel().get("deliveryModel");
        Assert.assertEquals(delivery.getIssue().getId(),deliveryModel.getPid());
        Assert.assertEquals((Integer)delivery.getDeliveryStatus().getValue(),(Integer)deliveryModel.getDeliveryStatus());
        Assert.assertEquals(null,deliveryModel.getReceiver());
        Assert.assertEquals("手机号码为空",null,deliveryModel.getMobile());
      //  Assert.assertEquals(null,deliveryModel.getDetails());
        Assert.assertNull("详细地址不应该有",deliveryModel.getDetails());
        Assert.assertEquals(null,deliveryModel.getConfirmAddressTime());
        Assert.assertEquals(null,deliveryModel.getDeliveryTime());
        Assert.assertEquals(delivery.getIssue().getToAmount(),deliveryModel.getToAmount());
        Assert.assertEquals(delivery.getIssue().getLuckyNumber(),deliveryModel.getLuckyNumber());
        Assert.assertEquals(delivery.getIssue().getGoods().getTitle(),deliveryModel.getTitle());
        Assert.assertEquals(null,deliveryModel.getProductSpec());
    }


    //选择奖品的规格（ConfirmProduct）
    @Rollback(true)
    @Test
    public void statusConfirmProduct() throws Exception
    {
        //商品
        goods = new Goods();
        goods.setTitle("cosytest");
        goods.setCharacters("这是测试商品");
        goods.setDefaultPictureUrl("/resources/images/aa.jpg");
        goods.setPictureUrls("/resources/images/bb.jpg,/resources/images/cc.jpg");
        goods.setSharePictureUrl("/resources/images/dd.jpg");
        goods.setDefaultAmount(10L);
        goods.setToAmount(100L);
        goods.setPricePercentAmount(new BigDecimal(1));
        goods.setStatus(CommonEnum.GoodsStatus.up);
        goods = goodsRepository.saveAndFlush(goods);

        //用户
        currentUser = new User();
        currentUser.setUsername("cosylj");
        currentUser.setPassword("123456");
        currentUser.setMobile("13600541783");
        currentUser.setMobileBinded(true);
        currentUser.setWeixinOpenId("111");
        currentUser.setMerchantId(3447L);
        currentUser.setMoney(new BigDecimal(100));
        currentUser.setRegTime(new Date());
        currentUser=userRepository.saveAndFlush(currentUser);

        //期号二，用于设置中奖
        currentIssue2 = new Issue();
        currentIssue2.setGoods(goods);
        currentIssue2.setDefaultAmount(10L);
        currentIssue2.setToAmount(100L);//总需人次
        currentIssue2.setBuyAmount(100L);
        currentIssue2.setPricePercentAmount(new BigDecimal(1));
        currentIssue2.setAttendAmount(1L);
        currentIssue2.setStatus(CommonEnum.IssueStatus.drawed);
        currentIssue2.setAwardingDate(new Date());
        currentIssue2.setAwardingUser(currentUser);
        currentIssue2=issueRepository.saveAndFlush(currentIssue2);

        //收货地址
        delivery = new Delivery();
        delivery.setIssue(currentIssue2);
        delivery.setUser(currentUser);
        delivery.setDeliveryStatus(CommonEnum.DeliveryStatus.ConfirmProduct);
        delivery.setProductSpec("m,红色");
        delivery.setIsCommit(false);
        delivery = deliveryRepository.saveAndFlush(delivery);


        MvcResult result=mockMvc.perform(get("/personal/getOneLotteryInfo")
                .param("customerId","3447")
                .param("issueId",currentIssue2.getId().toString()))
                .andExpect(model().attribute("customerId",3447L))
                .andExpect(model().attribute("issueId",currentIssue2.getId()))
                .andExpect(model().attributeExists("deliveryModel"))
                .andReturn();
        DeliveryModel deliveryModel=(DeliveryModel)result.getModelAndView().getModel().get("deliveryModel");
        Assert.assertEquals(delivery.getIssue().getId(),deliveryModel.getPid());
        Assert.assertEquals((Integer)delivery.getDeliveryStatus().getValue(),(Integer)deliveryModel.getDeliveryStatus());
        Assert.assertEquals(null,deliveryModel.getReceiver());
        Assert.assertEquals("手机号码为空",null,deliveryModel.getMobile());
        Assert.assertEquals(null,deliveryModel.getDetails());
        Assert.assertEquals(null,deliveryModel.getConfirmAddressTime());
        Assert.assertEquals(null,deliveryModel.getDeliveryTime());
        Assert.assertEquals(delivery.getIssue().getToAmount(),deliveryModel.getToAmount());
        Assert.assertEquals(delivery.getIssue().getLuckyNumber(),deliveryModel.getLuckyNumber());
        Assert.assertEquals(delivery.getIssue().getGoods().getTitle(),deliveryModel.getTitle());
        Assert.assertEquals(delivery.getProductSpec(),deliveryModel.getProductSpec());
    }


    //确认收货地址（ConfirmAddress）
    @Rollback(true)
    @Test
    public void statusConfirmAddress()throws  Exception
    {
        //商品
        goods = new Goods();
        goods.setTitle("cosytest");
        goods.setCharacters("这是测试商品");
        goods.setDefaultPictureUrl("/resources/images/aa.jpg");
        goods.setPictureUrls("/resources/images/bb.jpg,/resources/images/cc.jpg");
        goods.setSharePictureUrl("/resources/images/dd.jpg");
        goods.setDefaultAmount(10L);
        goods.setToAmount(100L);
        goods.setPricePercentAmount(new BigDecimal(1));
        goods.setStatus(CommonEnum.GoodsStatus.up);
        goods = goodsRepository.saveAndFlush(goods);

        //用户
        currentUser = new User();
        currentUser.setUsername("cosylj");
        currentUser.setPassword("123456");
        currentUser.setMobile("13600541783");
        currentUser.setMobileBinded(true);
        currentUser.setWeixinOpenId("111");
        currentUser.setMerchantId(3447L);
        currentUser.setMoney(new BigDecimal(100));
        currentUser.setRegTime(new Date());
        currentUser=userRepository.saveAndFlush(currentUser);

        //期号二，用于设置中奖
        currentIssue2 = new Issue();
        currentIssue2.setGoods(goods);
        currentIssue2.setDefaultAmount(10L);
        currentIssue2.setToAmount(100L);//总需人次
        currentIssue2.setBuyAmount(100L);
        currentIssue2.setPricePercentAmount(new BigDecimal(1));
        currentIssue2.setAttendAmount(1L);
        currentIssue2.setStatus(CommonEnum.IssueStatus.drawed);
        currentIssue2.setAwardingDate(new Date());
        currentIssue2.setAwardingUser(currentUser);
        currentIssue2=issueRepository.saveAndFlush(currentIssue2);

        //收货地址
        delivery = new Delivery();
        delivery.setIssue(currentIssue2);
        delivery.setUser(currentUser);
        delivery.setDeliveryStatus(CommonEnum.DeliveryStatus.ConfirmAddress);
        delivery.setProductSpec("m,红色");
        delivery.setReceiver(currentUser.getUsername());
        delivery.setMobile(currentUser.getMobile());
        delivery.setDetails("浙江省杭州市");
        delivery.setConfirmAddressTime(new Date());
        delivery.setIsCommit(false);
        delivery = deliveryRepository.saveAndFlush(delivery);

        MvcResult result=mockMvc.perform(get("/personal/getOneLotteryInfo")
                .param("customerId","3447")
                .param("issueId",currentIssue2.getId().toString()))
                .andExpect(model().attribute("customerId",3447L))
                .andExpect(model().attribute("issueId",currentIssue2.getId()))
                .andExpect(model().attributeExists("deliveryModel"))
                .andReturn();
        DeliveryModel deliveryModel=(DeliveryModel)result.getModelAndView().getModel().get("deliveryModel");
        Assert.assertEquals(delivery.getIssue().getId(),deliveryModel.getPid());
        Assert.assertEquals((Integer)delivery.getDeliveryStatus().getValue(),(Integer)deliveryModel.getDeliveryStatus());
        Assert.assertEquals(delivery.getUser().getUsername(),deliveryModel.getReceiver());
        Assert.assertEquals(delivery.getUser().getMobile(),deliveryModel.getMobile());
        Assert.assertEquals(delivery.getDetails(),deliveryModel.getDetails());
        Assert.assertEquals(delivery.getConfirmAddressTime(),deliveryModel.getConfirmAddressTime());
        Assert.assertEquals(null,deliveryModel.getDeliveryTime());
        Assert.assertEquals(delivery.getIssue().getToAmount(),deliveryModel.getToAmount());
        Assert.assertEquals(delivery.getIssue().getLuckyNumber(),deliveryModel.getLuckyNumber());
        Assert.assertEquals(delivery.getIssue().getGoods().getTitle(),deliveryModel.getTitle());
        Assert.assertEquals(delivery.getProductSpec(),deliveryModel.getProductSpec());
    }
}
