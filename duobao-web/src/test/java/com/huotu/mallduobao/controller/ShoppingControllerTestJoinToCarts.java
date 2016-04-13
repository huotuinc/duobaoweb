package com.huotu.mallduobao.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huotu.mallduobao.base.BaseTest;
import com.huotu.mallduobao.boot.MVCConfig;
import com.huotu.mallduobao.boot.RootConfig;
import com.huotu.mallduobao.common.CommonEnum;
import com.huotu.mallduobao.entity.Goods;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.entity.UserBuyFlow;
import com.huotu.mallduobao.repository.GoodsRepository;
import com.huotu.mallduobao.repository.IssueRepository;
import com.huotu.mallduobao.repository.UserBuyFlowRepository;
import com.huotu.mallduobao.repository.UserRepository;
import com.huotu.huobanplus.sdk.base.BaseClientSpringConfig;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.springframework.transaction.annotation.Transactional;

import javax.ws.rs.GET;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by cosy on 2016/4/5.
 */
@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class, BaseClientSpringConfig.class})
@ActiveProfiles("development")
@Transactional

public class ShoppingControllerTestJoinToCarts extends BaseTest {
    private Log log = LogFactory.getLog(ShoppingControllerTestJoinToCarts.class);

    @Autowired
    GoodsRepository goodsRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    IssueRepository issueRepository;
    @Autowired
    UserBuyFlowRepository userBuyFlowRepository;
    @Autowired
    MockMvc mockMvc;

    private Goods goods;
    private User currentUser;
    private Issue currentIssue;
    private Issue currentIssue2;
    private UserBuyFlow userBuyFlow;


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

        //用户
        currentUser=new User();
        currentUser.setUsername("cosylj");
        currentUser.setPassword("123456");
        currentUser.setMobile("13600541783");
        currentUser.setMobileBinded(true);
        currentUser.setMoney(new BigDecimal(100));
        userRepository.saveAndFlush(currentUser);



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
        issueRepository.saveAndFlush(currentIssue);

        //期号2,商品状态为进行中
        currentIssue2=new Issue();
        currentIssue2.setGoods(goods);
        currentIssue2.setDefaultAmount(10L);
        currentIssue2.setToAmount(100L);
        currentIssue2.setBuyAmount(10L);
        currentIssue2.setPricePercentAmount(new BigDecimal(1));
        currentIssue2.setAttendAmount(10L);
        currentIssue2.setStatus(CommonEnum.IssueStatus.going);
        currentIssue2.setAwardingUser(currentUser);
        issueRepository.saveAndFlush(currentIssue2);


        //用户购买商品的记录
        userBuyFlow =new UserBuyFlow();
        userBuyFlow.setUser(currentUser);
        userBuyFlow.setIssue(currentIssue);
        userBuyFlow.setAmount(10L);
        userBuyFlowRepository.saveAndFlush(userBuyFlow);
    }


    //没有Issueid的情况下
    @Test
    public void nullIssueId () throws Exception
    {

        MvcResult result= mockMvc.perform(get("/web/joinToCarts")
                                 .param(currentUser.getId().toString())
                                 .param(userBuyFlow.getId().toString()))
                                 .andExpect(status().isOk())
                                 .andReturn();
        String code=result.getResponse().getContentAsByteArray().toString();
        JSONObject jsonObject=JSON.parseObject(code);
        Assert.assertEquals(404, jsonObject.get("code"));
        Assert.assertEquals("添加到购物车失败！",jsonObject.get("message"));

    }


    //根据传入的issueid找不到对应得issue时候
    @Test
    public void notExitIssue() throws Exception
    {
      MvcResult result= mockMvc.perform(get("/web/joinToCarts")
                               .param("1111")
                               .param(currentUser.getId().toString())
                               .param(userBuyFlow.getId().toString()))
                               .andExpect(status().isOk())
                               .andReturn();
        String code=result.getResponse().getContentAsByteArray().toString();
        JSONObject jsonObject=JSON.parseObject(code);
        Assert.assertEquals("404",jsonObject.get("code"));
        Assert.assertEquals("商品不存在，请重新购买！",jsonObject.get("message"));
    }

    //某一期的商品状态为进行中的时候
    @Test
    public void issueStatusGoing()throws Exception
    {
      MvcResult result=mockMvc.perform(get("/web/joinToCarts")
                              .param(currentIssue2.getId().toString())
                              .param(currentUser.getId().toString())
                              .param(userBuyFlow.getId().toString()))
                              .andExpect(status().isOk())
                              .andReturn();
        String code=result.getResponse().getContentAsByteArray().toString();
        JSONObject jsonObject=JSON.parseObject(code);
        Assert.assertEquals("404",jsonObject.get("code"));
        Assert.assertEquals("商品已过期，请重新购买！",jsonObject.get("message"));
    }


    //用户是空的时候
    @Test
    public void nullUser() throws Exception
    {
        MvcResult result=mockMvc.perform(get("/web/joinToCarts")
                                .param(currentIssue.getId().toString())
                                .param(userBuyFlow.getId().toString()))
                                .andExpect(status().isOk())
                                .andReturn();
        String code=result.getResponse().getContentAsByteArray().toString();
        JSONObject jsonObject=JSON.parseObject(code);
        Assert.assertEquals("404",jsonObject.get("code"));
        Assert.assertEquals("用户不合法，请重新进入！",jsonObject.get("message"));
    }


    //成功加入商品
    @Test
    public void successJoinToShoppingCarts() throws Exception
    {
          MvcResult result=mockMvc.perform(get("/web/joinToCarts")
                                  .param(currentIssue.getId().toString())
                                  .param(currentUser.getId().toString())
                                   .param(userBuyFlow.getId().toString()))
                                   .andExpect(status().isOk())
                                   .andReturn();
        String code=result.getResponse().getContentAsByteArray().toString();
        JSONObject jsonObject=JSON.parseObject(code);
        Assert.assertEquals("200",jsonObject.get("code"));
        Assert.assertEquals("添加成功！",jsonObject.get("message"));
    }
}
