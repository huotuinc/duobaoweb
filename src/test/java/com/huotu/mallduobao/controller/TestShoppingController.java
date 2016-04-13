package com.huotu.mallduobao.controller;

import com.huotu.mallduobao.base.BaseTest;
import com.huotu.mallduobao.boot.RootConfig;
import com.huotu.mallduobao.entity.Goods;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.repository.UserRepository;
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
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by xhk on 2016/3/30.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class})
@ActiveProfiles("development")
@Transactional
public class TestShoppingController extends BaseTest {
    @Autowired
    UserRepository userRepository;
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
        User user = generateUser(mockPassword, userRepository);
        mockUsername = user.getUsername();
        mUser=user;
    }

    @Test
    @Rollback(false)
    public void testSaveIssue(){

        String name=UUID.randomUUID().toString().replace("-","");
        User user=new User();
        user.setMobile("13852108596");
        user.setMobileBinded(true);
        user.setEnabled(true);
        user.setRegTime(new Date());
        user.setRealName("xhkTest");
        user.setUsername(name);
        user=userRepository.saveAndFlush(user);
        Goods goods=this.saveGodds();
        Issue issue=this.saveIssue(goods,user,1);
    }

    @Test
    public void testRepository(){
        List<User> user=userRepository.findAll();
        System.out.print(user.get(0).getId()+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

}
