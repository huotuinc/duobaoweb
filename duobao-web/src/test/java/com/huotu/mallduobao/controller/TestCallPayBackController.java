package com.huotu.mallduobao.controller;

import com.huotu.mallduobao.base.BaseTest;
import com.huotu.mallduobao.boot.RootConfig;
import com.huotu.mallduobao.repository.IssueRepository;
import com.huotu.mallduobao.repository.UserRepository;
import com.huotu.mallduobao.service.PayService;
import com.huotu.mallduobao.service.ShoppingService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by xhk on 2016/4/9.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class})
@ActiveProfiles("development")
@Transactional
public class TestCallPayBackController extends BaseTest {

    @Autowired
    private PayService payService;

    @Autowired
    private ShoppingService shoppingService;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private UserRepository userRepository;

//    @Test
//    public void testPayService() throws IOException {
//
//        //创建一个购物车
//        User user=userRepository.findOne(3L);
//        Issue issue=issueRepository.findOne(2L);
//        ShoppingCart shoppingCart=shoppingService.joinToShoppingCarts(issue, user, 10L);
//        //创建一个订单
//        PayModel payModel=new PayModel();
//        payModel.setPayType(1);
//        payModel.setPayMoney(10.0);
//        payModel.setType(1);
//        payModel.setCartsId(shoppingCart.getId());
//        Orders orders = shoppingService.createOrders(payModel);
//        PayResultModel payResultModel=payService.solveWeixinPayResult(orders.getId(),0.01F,"thisis outorderno!");
//    }
}
