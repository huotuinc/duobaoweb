package com.huotu.mallduobao.controller;

import com.huotu.huobanplus.sdk.base.BaseClientSpringConfig;
import com.huotu.mallduobao.base.BaseTest;
import com.huotu.mallduobao.boot.MVCConfig;
import com.huotu.mallduobao.boot.RootConfig;
import com.huotu.mallduobao.entity.Goods;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.ShoppingCart;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.model.ShoppingCartsModel;
import com.huotu.mallduobao.repository.GoodsRepository;
import com.huotu.mallduobao.repository.IssueRepository;
import com.huotu.mallduobao.repository.ShoppingCartRepository;
import com.huotu.mallduobao.repository.UserRepository;
import com.huotu.mallduobao.utils.CommonEnum;
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
 * Created by cosy on 2016/4/15.
 */


@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class, BaseClientSpringConfig.class})
@ActiveProfiles("development")
@Transactional
public class ShoppingControllerTestShowShoppingCarts extends BaseTest {

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


    //购物车中有商品
    @Rollback(true)
    @Test
    public void testShowShoppingCarts() throws Exception
    {
        //将商品添加到购物车
        shoppingCart=new ShoppingCart();
        shoppingCart.setUser(currentUser);
        shoppingCart.setIssue(currentIssue);
        shoppingCart.setBuyAmount(10l);
        shoppingCart=shoppingCartRepository.saveAndFlush(shoppingCart);

        MvcResult result= mockMvc.perform(get("/shopping/showShoppingCarts")
                .param("customerId","3447")
                .param("issueId",currentIssue.getId().toString()))
                .andExpect(model().attributeExists("shoppingCarts"))
                .andExpect(model().attribute("issueId",currentIssue.getId()))
                .andExpect(model().attribute("customerId",3447L))
                .andReturn();
        ShoppingCartsModel shoppingCartsModel=(ShoppingCartsModel)result.getModelAndView().getModel().get("shoppingCarts");
        Assert.assertEquals(shoppingCart.getId(),shoppingCartsModel.getCartId());
        Assert.assertEquals(currentIssue.getGoods().getToAmount(),shoppingCartsModel.getNeedNumber());
        Assert.assertEquals("90",shoppingCartsModel.getLeftNumber().toString());
        Assert.assertEquals(currentIssue.getBuyAmount(),shoppingCartsModel.getBuyNum());
        Assert.assertEquals(currentIssue.getStepAmount(),shoppingCartsModel.getStepNum());
        Assert.assertEquals("10.0",shoppingCartsModel.getBuyMoney().toString());
    }

    //购物车中无商品
    @Rollback(true)
    @Test
    public void testNullShowShoppingCarts() throws  Exception
    {

        //用户2，用于测试购物车中无商品

        currentUser2 = new User();
        currentUser2.setUsername("cosy");
        currentUser2.setPassword("123456");
        currentUser2.setMobile("13600541783");
        currentUser2.setMobileBinded(true);
        currentUser2.setWeixinOpenId("111");
        currentUser2.setMerchantId(3447L);
        currentUser2.setMoney(new BigDecimal(100));
        currentUser2.setRegTime(new Date());
        currentUser2=userRepository.saveAndFlush(currentUser2);

        //期号2，用于测试购物车中无商品
        currentIssue2=new Issue();
        currentIssue2.setGoods(goods);
        currentIssue2.setDefaultAmount(10L);
        currentIssue2.setToAmount(100L);
        currentIssue2.setBuyAmount(0L);
        currentIssue2.setPricePercentAmount(new BigDecimal(1));
        currentIssue2.setAttendAmount(0L);
        currentIssue2.setStatus(CommonEnum.IssueStatus.going);
        currentIssue2.setAwardingUser(currentUser2);
        currentIssue2= issueRepository.saveAndFlush(currentIssue2);


        MvcResult result= mockMvc.perform(get("/shopping/showShoppingCarts")
                .param("customerId","3447")
                .param("issueId",currentIssue2.getId().toString()))
                //.andExpect(model().attribute("notShow","1"))
                .andExpect(model().attributeExists("shoppingCarts"))
                .andReturn();
        ShoppingCartsModel shoppingCartsModel=(ShoppingCartsModel)result.getModelAndView().getModel().get("shoppingCarts");
        Assert.assertEquals("0",shoppingCartsModel.getBuyNum().toString());
        Assert.assertEquals("0.0",shoppingCartsModel.getBuyMoney().toString());
    }


    //购物车中商品非going状态
    @Rollback(true)
    @Test
    public void statusNotGoing() throws  Exception
    {
        //创建Status为drawed的期号
        currentIssue=new Issue();
        currentIssue.setGoods(goods);
        currentIssue.setDefaultAmount(10L);
        currentIssue.setToAmount(100L);
        currentIssue.setBuyAmount(10L);
        currentIssue.setPricePercentAmount(new BigDecimal(1));
        currentIssue.setAttendAmount(10L);
        currentIssue.setStepAmount(10L);
        currentIssue.setStatus(CommonEnum.IssueStatus.drawed);
        currentIssue.setAwardingUser(currentUser);
        currentIssue=issueRepository.saveAndFlush(currentIssue);

        //将商品添加到购物车
        shoppingCart=new ShoppingCart();
        shoppingCart.setUser(currentUser);
        shoppingCart.setIssue(currentIssue);
        shoppingCart.setBuyAmount(10l);
        shoppingCart=shoppingCartRepository.saveAndFlush(shoppingCart);

        MvcResult result=mockMvc.perform(get("/shopping/showShoppingCarts")
                .param("customerId","3447")
                .param("issueId",currentIssue.getId().toString()))
                .andExpect(model().attribute("customerId",3447L))
                .andReturn();
        ShoppingCartsModel shoppingCartsModel=(ShoppingCartsModel)result.getModelAndView().getModel().get("shoppingCarts");
        Assert.assertEquals(null,shoppingCartsModel.getCartId());
    }
}
