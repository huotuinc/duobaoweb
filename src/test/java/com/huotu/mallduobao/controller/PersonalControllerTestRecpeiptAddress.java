package com.huotu.mallduobao.controller;

import com.huotu.huobanplus.sdk.base.BaseClientSpringConfig;
import com.huotu.mallduobao.base.BaseTest;
import com.huotu.mallduobao.boot.MVCConfig;
import com.huotu.mallduobao.boot.RootConfig;
import com.huotu.mallduobao.entity.Delivery;
import com.huotu.mallduobao.entity.Goods;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.repository.DeliveryRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

/**
 * Created by cosy on 2016/4/19.
 */

@SuppressWarnings("SpringJavaAutowiringInspection")
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, MVCConfig.class, BaseClientSpringConfig.class})
@ActiveProfiles("development")
@Transactional

public class PersonalControllerTestRecpeiptAddress extends BaseTest {
    private Log log = LogFactory.getLog(PersonalControllerTestRecpeiptAddress.class);

@Autowired
DeliveryRepository deliveryRepository;

    private Issue currentIssue;
    private User currentUser;
    private Goods goods;
    private Delivery delivery;

    @Rollback(true)
    @Test
    public void testToRecpeiptAddress() throws Exception
    {
        goods=createGoods();
        currentUser=createUser();
        currentIssue=createIssue(goods,currentUser);

        mockMvc.perform(get("/personal/toRecpeiptAddress")
                .param("customerId","3447")
                .param("issueId",currentIssue.getId().toString())
                .param("deliveryId",currentIssue.getId().toString()))
                .andExpect(model().attribute("customerId",3447L))
                .andExpect(model().attribute("issueId",currentIssue.getId()))
                .andExpect(model().attribute("deliveryId",currentIssue.getId()))
                .andReturn();
    }


    @Rollback(true)
    @Test
    public void testAddRecpeiptAddress() throws Exception
    {
        goods=createGoods();
        currentUser=createUser();

        currentIssue=createIssue(goods,currentUser);
        delivery=createDelivery(currentIssue,currentUser);

        mockMvc.perform(get("/personal/addRecpeiptAddress")
                .param("customerId","3447")
                .param("issueId",currentIssue.getId().toString())
                .param("deliveryId",currentIssue.getId().toString())
                .param("receiver",currentIssue.getAwardingUser().getUsername())
                .param("mobile",currentIssue.getAwardingUser().getMobile())
                .param("details","浙江省,杭州市,滨江区,江南大道")
                .param("remark","bbbbb"))
                .andExpect(model().attribute("customerId",3447L))
                .andExpect(model().attribute("issueId",currentIssue.getId()))
                .andReturn();
        Delivery delivery=deliveryRepository.findByIssueId(currentIssue.getId());
        Assert.assertEquals(currentIssue.getAwardingUser().getId(),delivery.getUser().getId());
        Assert.assertEquals(currentIssue.getAwardingUser().getUsername(),delivery.getReceiver());
        Assert.assertEquals(currentIssue.getAwardingUser().getMobile(),delivery.getMobile());
        Assert.assertEquals("浙江省,杭州市,滨江区,江南大道",delivery.getDetails());
        Assert.assertEquals("bbbbb",delivery.getRemark());
    }
}
