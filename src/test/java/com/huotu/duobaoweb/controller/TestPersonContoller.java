package com.huotu.duobaoweb.controller;

import com.huotu.duobaoweb.base.BaseTest;
import com.huotu.duobaoweb.boot.RootConfig;
import com.huotu.duobaoweb.entity.*;
import com.huotu.duobaoweb.repository.CityRepository;
import com.huotu.duobaoweb.repository.UserRepository;
import com.huotu.duobaoweb.service.StaticResourceService;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.huobanplus.sdk.common.repository.impl.GoodsRestRepositoryImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.UUID;

/**
 * Created by lhx on 2016/3/25.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class})
@ActiveProfiles("development")
@Transactional
public class TestPersonContoller extends BaseTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    StaticResourceService staticResourceService;

    @Autowired
    CityRepository cityRepository;

    /**
     * 用于测试的用户名
     */
    private String mockUsername;
    /**
     * 用于测试的密码
     */
    private String mockPassword;

    private User mUser;

    @Before
    public void prepareDevice() throws UnsupportedEncodingException {
        mockPassword = UUID.randomUUID().toString();
        //用户名的要求 大于3 小于20
        User user = generateUserWithMobileWithToken(mockPassword, userRepository);
        mockUsername = user.getUsername();
        mUser=user;
    }

    @Test
    @Rollback(false)
    public void initData(){
        User user = userRepository.findOne(1L);
        Goods good2 = saveGodds();
        for (int i = 1; i < 15; i++) {
            if (i % 2 == 0) {
                Orders orders = saveOrders(user, i);
                Issue issue = saveIssue(good2,user,i);
                saveOrderItem(orders,issue);
                saveUserNumber(user,issue,i);
                saveUserBuyFlow(user,issue);
            } else {
                Orders orders = saveOrders(user, i);
                Issue issue = saveIssue(good2,user,i);
                saveOrderItem(orders, issue);
                saveUserNumber(user, issue,i);
                saveUserBuyFlow(user, issue);
            }
        }
    }


}
