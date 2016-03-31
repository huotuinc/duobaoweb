package com.huotu.duobaoweb.controller;

import com.huotu.duobaoweb.base.BaseTest;
import com.huotu.duobaoweb.boot.RootConfig;
import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.Goods;
import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.repository.GoodsRepository;
import com.huotu.duobaoweb.repository.IssueRepository;
import com.huotu.duobaoweb.repository.UserRepository;
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
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by zhang on 2016/3/28.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class})
@ActiveProfiles("development")
@Transactional
public class TestGoodsController extends BaseTest {

    /**
     * 用于测试的用户名
     */
    private String mockUsername;
    /**
     * 用于测试的密码
     */
    private String mockPassword;

    /**
     * 用于测试的用户
     */
    private User mUser;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private IssueRepository issueRepository;

    /**
     * 用于创建一个用户
     * @throws UnsupportedEncodingException
     */
    public void createUser() throws UnsupportedEncodingException {
        List<User> userList = userRepository.findAll();
        //如果用户列表不存在用户则生成一个用户
        if(userList.size() == 0){
            mUser = generateUserWithMobileWithToken(UUID.randomUUID().toString(), userRepository);
        }else{
            mUser = userList.get(0);
        }
    }

    /**
     * 用于创建一个商品
     */
    public Goods createGoods(){
            Goods goods = new Goods();
            goods.setDefaultPictureUrl("/resources/images/dsds.jpg");
            goods.setPictureUrls("/resources/images/dsds.jpg,/resources/images/dsds.jpg,/resources/images/dsds.jpg");
            goods.setTitle("iphone6s");
            goods.setCharacters("独一无二");
            goods.setDefaultAmount(10L);
            goods.setStepAmount(10L);
            goods.setToAmount(5890L);
            goods.setPricePercentAmount(new BigDecimal(1L));
            goods.setStatus(CommonEnum.GoodsStatus.up);
            Date curDate = new Date();
            goods.setStartTime(curDate);
            goods.setEndTime(new Date(curDate.getTime() + 3600 * 24 * 1000));
            goods.setShareTitle("分享标题");
            goods.setShareDescription("分享描述");
            goods.setSharePictureUrl("/resources/images/dsds.jpg");
            goods = goodsRepository.saveAndFlush(goods);
            return goods;
    }

    /**
     * 创建新的一期
     */
    public void createIssue(){
        Goods goods = createGoods();
        Issue issue = new Issue();
        issue.setDefaultAmount(goods.getDefaultAmount());
        issue.setPricePercentAmount(goods.getPricePercentAmount());
        issue.setStatus(CommonEnum.IssueStatus.going);
        issue.setStepAmount(goods.getStepAmount());
        issue.setToAmount(goods.getToAmount());
        issue.setGoods(goods);
        issue = issueRepository.saveAndFlush(issue);
        goods.setIssue(issue);
        goodsRepository.saveAndFlush(goods);
    }

    @Rollback(false)
    @Test
    public void createNewIssue(){
       createIssue();
    }

    @Rollback(false)
    @Test
    public void createNewUser(){
        try{
            createUser();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
