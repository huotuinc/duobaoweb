package com.huotu.mallduobao.controller;

import com.huotu.huobanplus.sdk.base.BaseClientSpringConfig;
import com.huotu.mallduobao.base.BaseTest;
import com.huotu.mallduobao.boot.MVCConfig;
import com.huotu.mallduobao.boot.RootConfig;
import com.huotu.mallduobao.entity.Goods;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.ShoppingCart;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.model.PayModel;
import com.huotu.mallduobao.repository.GoodsRepository;
import com.huotu.mallduobao.repository.IssueRepository;
import com.huotu.mallduobao.repository.ShoppingCartRepository;
import com.huotu.mallduobao.repository.UserRepository;
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
 * Created by cosy on 2016/4/13.
 */

@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class, BaseClientSpringConfig.class})
@ActiveProfiles("development")
@Transactional
public class ShoppingControllerTestToAllPay extends BaseTest {
    private Log log = LogFactory.getLog(ShoppingControllerTestToAllPay.class);
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    private User currentUser;
    private User currentUser2;
    private Issue currentIssue;
    private Issue currentIssue2;
    private Goods goods;
    private ShoppingCart shoppingCart;
    @Before
    public void before()
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

        //用户1,用于测试购物车中有商品

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



        //期号1，用于测试购物车中有商品
        currentIssue=new Issue();
        currentIssue.setGoods(goods);
        currentIssue.setDefaultAmount(10L);
        currentIssue.setToAmount(100L);
        currentIssue.setBuyAmount(10L);
        currentIssue.setPricePercentAmount(new BigDecimal(1));
        currentIssue.setAttendAmount(10L);
        currentIssue.setStepAmount(10L);
        currentIssue.setStatus(CommonEnum.IssueStatus.going);
        currentIssue.setAwardingUser(currentUser);
        currentIssue=issueRepository.saveAndFlush(currentIssue);



    }


    /********************以下是ToAllPay测试,暂时废弃***********************/
    @Rollback(true)
    @Test
    public void  testToAllPay() throws Exception
    {


        MvcResult result=mockMvc.perform(get("/shopping/toAllPay")
                                .param("customerId","3447")
                                .param("issueId",currentIssue.getId().toString())
                                .param("shoppingCartId",shoppingCart.getId().toString()))
                                .andExpect(model().attributeExists("payModel"))
                                .andExpect(model().attribute("issueId",currentIssue.getId()))
                                .andExpect(model().attribute("customerId",3447L))
                                .andReturn();
        PayModel payModel=(PayModel)result.getModelAndView().getModel().get("payModel");
        Assert.assertEquals(shoppingCart.getIssue().getGoods().getTitle(),payModel.getDetail());
        Assert.assertEquals("2",payModel.getType().toString());
        Assert.assertEquals(shoppingCart.getId(),payModel.getCartsId());
    }













}
