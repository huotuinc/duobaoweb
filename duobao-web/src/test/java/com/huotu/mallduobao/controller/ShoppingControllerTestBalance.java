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
 * Created by cosy on 2016/4/15.
 */


@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class, BaseClientSpringConfig.class})
@ActiveProfiles("development")
@Transactional
public class ShoppingControllerTestBalance extends BaseTest {
    private Log log = LogFactory.getLog(ShoppingControllerTestBalance.class);


    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private IssueRepository issueRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;

    private User currentUser;

    private Issue currentIssue;

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


    //正常结算购物车
    @Rollback(true)
    @Test
    public void testBalance() throws Exception
    {
        shoppingCart=new ShoppingCart();
        shoppingCart.setIssue(currentIssue);
        shoppingCart.setUser(currentUser);
        shoppingCart.setBuyAmount(10L);
        shoppingCart=shoppingCartRepository.saveAndFlush(shoppingCart);

        MvcResult result=mockMvc.perform(get("/shopping/balance")
                .param("customerId","3447")
                .param("issueId",currentIssue.getId().toString())
                .param("cartId",shoppingCart.getId().toString())
                .param("buyNum",shoppingCart.getBuyAmount().toString()))
                .andExpect(model().attributeExists("payModel"))
                .andExpect(model().attribute("issueId",currentIssue.getId()))
                .andExpect(model().attribute("customerId",3447L))
                .andReturn();
        PayModel payModel=(PayModel)result.getModelAndView().getModel().get("payModel");
        Assert.assertEquals("10.0",payModel.getPayMoney().toString());
        Assert.assertEquals(shoppingCart.getIssue().getGoods().getTitle(),payModel.getDetail());
        Assert.assertEquals(shoppingCart.getId(),payModel.getCartsId());
        Assert.assertEquals("1",payModel.getType().toString());

    }


    //购物车为空
    @Rollback(true)
    @Test
    public void nullShoppingCart()throws  Exception {
        shoppingCart = new ShoppingCart();
        shoppingCart.setIssue(currentIssue);
        shoppingCart.setUser(currentUser);
        shoppingCart.setBuyAmount(10L);
        shoppingCart = shoppingCartRepository.saveAndFlush(shoppingCart);

        MvcResult result = mockMvc.perform(get("/shopping/balance")
                .param("customerId", "3447")
                .param("issueId", currentIssue.getId().toString())
                .param("cartId", "0")
                .param("buyNum", shoppingCart.getBuyAmount().toString()))
                .andExpect(model().attribute("overTime", "1"))
                .andExpect(model().attribute("notShow", "1"))
                .andReturn();
    }

    //商品不为going的状态
    @Rollback(true)
    @Test
    public void notGoing() throws  Exception
    {
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

        shoppingCart = new ShoppingCart();
        shoppingCart.setIssue(currentIssue);
        shoppingCart.setUser(currentUser);
        shoppingCart.setBuyAmount(10L);
        shoppingCart = shoppingCartRepository.saveAndFlush(shoppingCart);


        MvcResult result = mockMvc.perform(get("/shopping/balance")
                .param("customerId", "3447")
                .param("issueId", currentIssue.getId().toString())
                .param("cartId", shoppingCart.getId().toString())
                .param("buyNum", shoppingCart.getBuyAmount().toString()))
                .andExpect(model().attribute("overTime", "1"))
                .andExpect(model().attribute("notShow", "1"))
                .andReturn();

    }


    // 如果剩余量不足，购买量超过了剩余量，则将购物车的数量更改为剩余量(Amount<BuyAmount)
    @Rollback(true)
    @Test
    public void noEnoughAmount() throws Exception
    {

        currentIssue=new Issue();
        currentIssue.setGoods(goods);
        currentIssue.setDefaultAmount(10L);
        currentIssue.setToAmount(10L);
        currentIssue.setBuyAmount(9L);
        currentIssue.setPricePercentAmount(new BigDecimal(1));
        currentIssue.setAttendAmount(10L);
        currentIssue.setStepAmount(10L);
        currentIssue.setStatus(CommonEnum.IssueStatus.going);
        currentIssue.setAwardingUser(currentUser);
        currentIssue=issueRepository.saveAndFlush(currentIssue);

        shoppingCart = new ShoppingCart();
        shoppingCart.setIssue(currentIssue);
        shoppingCart.setUser(currentUser);
        shoppingCart.setBuyAmount(10L);
        shoppingCart = shoppingCartRepository.saveAndFlush(shoppingCart);

        MvcResult result=mockMvc.perform(get("/shopping/balance")
                .param("customerId","3447")
                .param("issueId",currentIssue.getId().toString())
                .param("cartId",shoppingCart.getId().toString())
                .param("buyNum",shoppingCart.getBuyAmount().toString()))
                .andExpect(model().attributeExists("payModel"))
                .andExpect(model().attribute("issueId",currentIssue.getId()))
                .andExpect(model().attribute("customerId",3447L))
                .andReturn();
        PayModel payModel=(PayModel)result.getModelAndView().getModel().get("payModel");
        Assert.assertEquals("1",shoppingCart.getBuyAmount().toString());
        Assert.assertEquals("1.0",payModel.getPayMoney().toString());

    }
}
