package com.huotu.duobaoweb.controller;

import com.huotu.duobaoweb.common.PublicParameterHolder;
import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.model.WebPublicModel;
import com.huotu.duobaoweb.service.GoodsService;
import com.huotu.duobaoweb.service.IssueService;
import com.huotu.duobaoweb.service.ShoppingService;
import com.huotu.duobaoweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * Created by xhk on 2016/3/25.
 */
@RequestMapping(value="/user")
@Controller
public class UserController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ShoppingService shoppingService;

    @Autowired
    private UserService userService;

    @Autowired
    private IssueService issueService;
    /**
     * 获取微信的Auth2认证之后的用户openid
     * @param issueId 期号id
     * @param openid 认证后拿到的openid
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getOpid", method = RequestMethod.GET)
    public String getOpid(String issueId,String openid,Map<String, Object> map) throws Exception {

        //进行用户注册(如果用户存在则不注册，不存在才注册)
        User user =userService.registerUser(openid);

        Long goodsId=null;
        Issue issue=new Issue();
        if(issueId!=null) {
            issue= issueService.getIssueById(issueId);
            goodsId=issue.getGoods().getId();
        }
        //将当前用户写入ThreadLocal
        WebPublicModel webPublicModel=userService.getWebPublicModel(user,issue);
        PublicParameterHolder.putParameters(webPublicModel);

        String openidUrl=userService.getWeixinAuthUrl(webPublicModel);
        map.put("userId",user.getId());
        map.put("openidUrl",openidUrl);
        map.put("openId",openid);
        String sign=userService.getSign(user);
        map.put("sign",sign);
        map.put("upCookie", 1);

//        ShoppingCartsModel shoppingCartsModel=shoppingService.getShoppingCartsModel(webPublicModel.getCurrentUser().getId());
//        if(shoppingCartsModel==null){
//            map.put("notShow", "1");
//            shoppingCartsModel=new ShoppingCartsModel();
//            shoppingCartsModel.setBuyNum(0L);
//            shoppingCartsModel.setBuyMoney(0.0);
//        }
        //todo 为了测试添加的数据 以后要删除
        //map.put("shoppingCarts", shoppingCartsModel);
        goodsService.jumpToGoodsActivityIndex(goodsId, map);
        //return "/html/shopping/cartsList";
        return "/html/goods/index";
    }

}
