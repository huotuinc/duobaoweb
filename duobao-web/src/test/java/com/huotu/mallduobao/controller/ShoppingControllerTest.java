package com.huotu.duobaoweb.controller;

import com.huotu.mallduobao.base.BaseTest;
import com.huotu.mallduobao.boot.MVCConfig;
import com.huotu.mallduobao.boot.RootConfig;
import com.huotu.mallduobao.common.CommonEnum;
import com.huotu.duobaoweb.controller.page.GetBalancePage;
import com.huotu.duobaoweb.controller.page.ShowShoppingCartsPage;
import com.huotu.mallduobao.entity.Delivery;
import com.huotu.mallduobao.entity.Goods;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.ShoppingCart;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.entity.UserBuyFlow;
import com.huotu.mallduobao.repository.GoodsRepository;
import com.huotu.mallduobao.repository.IssueRepository;
import com.huotu.mallduobao.repository.ShoppingCartRepository;
import com.huotu.mallduobao.repository.UserRepository;
import com.huotu.huobanplus.sdk.base.BaseClientSpringConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.Assert.*;

/**
 * Created by cosy on 2016/4/6.
 */

@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class, BaseClientSpringConfig.class})
@ActiveProfiles("development")
@Transactional
public class ShoppingControllerTest extends BaseTest{
    private Log log = LogFactory.getLog(ShoppingControllerTest.class);


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
    private  WebDriver driver;


    @Before
    public void before()
    {
        //商品
        goods=new Goods();
        goods.setTitle("cosytest");
        goods.setCharacters("这是测试商品");
        goods.setDefaultAmount(10L);
        goods.setToAmount(100L);
        goods.setPricePercentAmount(new BigDecimal(1));
        goods.setStatus(CommonEnum.GoodsStatus.up);
        goods=goodsRepository.saveAndFlush(goods);

        //用户1,用于测试购物车中有商品
        currentUser=new User();
        currentUser.setUsername("cosylj");
        currentUser.setPassword("123456");
        currentUser.setMobile("13600541783");
        currentUser.setMobileBinded(true);
        currentUser.setMoney(new BigDecimal(100));
        currentUser=userRepository.saveAndFlush(currentUser);

        //用户2，用于测试购物车中无商品

        currentUser2=new User();
        currentUser2.setUsername("cosylj");
        currentUser2.setPassword("123456");
        currentUser2.setMobile("13600541783");
        currentUser2.setMobileBinded(true);
        currentUser2.setMoney(new BigDecimal(100));
        currentUser2=userRepository.saveAndFlush(currentUser2);


        //期号1
        currentIssue=new Issue();
        currentIssue.setGoods(goods);
        currentIssue.setDefaultAmount(10L);
        currentIssue.setToAmount(100L);
        currentIssue.setBuyAmount(10L);
        currentIssue.setPricePercentAmount(new BigDecimal(1));
        currentIssue.setAttendAmount(10L);
        currentIssue.setStatus(CommonEnum.IssueStatus.drawed);
        currentIssue.setAwardingUser(currentUser);
        currentIssue=issueRepository.saveAndFlush(currentIssue);

        //期号2
        currentIssue2=new Issue();
        currentIssue2.setGoods(goods);
        currentIssue2.setDefaultAmount(10L);
        currentIssue2.setToAmount(100L);
        currentIssue2.setBuyAmount(10L);
        currentIssue2.setPricePercentAmount(new BigDecimal(1));
        currentIssue2.setAttendAmount(10L);
        currentIssue2.setStatus(CommonEnum.IssueStatus.drawed);
        currentIssue2.setAwardingUser(currentUser2);
        currentIssue2= issueRepository.saveAndFlush(currentIssue2);


        //将商品添加到购物车
        shoppingCart=new ShoppingCart();
        shoppingCart.setUser(currentUser);
        shoppingCart.setIssue(currentIssue);
        shoppingCart.setBuyAmount(10l);
        shoppingCart=shoppingCartRepository.saveAndFlush(shoppingCart);
    }


   //得到购物车
    @Test
    public void testShowShoppingCarts() throws Exception {
      //购物车中有商品
        ShowShoppingCartsPage showShoppingCartsPage=new ShowShoppingCartsPage();
        showShoppingCartsPage.existGoods(driver,currentUser,currentIssue);

        //购物车中无商品
        showShoppingCartsPage.nollGoods(driver,currentUser2);
    }


    //测试跳转到页面
    @Test
    public void testBalance() throws Exception {
        GetBalancePage getBalancePage=new GetBalancePage();
        getBalancePage.to(driver,shoppingCart);
    }

    @Test
    public void testPay() throws Exception {

    }
}