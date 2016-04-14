package com.huotu.mallduobao.controller;

import com.alibaba.fastjson.JSON;
import com.huotu.mallduobao.common.AuthEntity;
import com.huotu.mallduobao.common.PublicParameterHolder;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.model.PaysResultShowModel;
import com.huotu.mallduobao.model.WebPublicModel;
import com.huotu.mallduobao.service.GoodsService;
import com.huotu.mallduobao.service.IssueService;
import com.huotu.mallduobao.service.ShoppingService;
import com.huotu.mallduobao.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by xhk on 2016/3/25.
 */
@RequestMapping(value="/user")
@Controller
public class UserController {

    private static final Log log = LogFactory.getLog(UserController.class);

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ShoppingService shoppingService;

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private IssueService issueService;
    /**
     * 获取微信的Auth2认证之后的用户openid
     * @param issueId 期号id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getOpid", method = RequestMethod.GET)
    public String getOpid(HttpServletRequest request,String customerId,String issueId,Map<String, Object> map) throws Exception {
        String info=request.getParameter("retuinfo");
        log.info("issueId:"+issueId+";retuinfo:"+info);
        AuthEntity retuinfo=JSON.parseObject(info, AuthEntity.class);
        //进行用户注册(如果用户存在则不注册，不存在才注册)
        User user = userService.registerUser(retuinfo, customerId);

        Issue issue=new Issue();
        if(issueId!=null&&!issueId.equals("")) {
            issue= issueService.getIssueById(issueId);
        }
        //将当前用户写入ThreadLocal
        WebPublicModel webPublicModel=userService.getWebPublicModel(user,issue);
        PublicParameterHolder.putParameters(webPublicModel);


        Cookie custId=new Cookie("customerId",customerId);
        custId.setMaxAge(60*10); //单位是秒 todo 正式上线的时候时间设置长一点
        custId.setPath("/");
        Cookie userId=new Cookie("userId",String.valueOf(webPublicModel.getCurrentUser().getId()));
        userId.setMaxAge(60*10);
        userId.setPath("/");
        Cookie openId=new Cookie("openId",retuinfo.getOpenid());
        openId.setMaxAge(60*10);
        openId.setPath("/");
        Cookie sign=new Cookie("sign",webPublicModel.getSign());
        sign.setMaxAge(60*10);
        sign.setPath("/");
        response.addCookie(custId);
        response.addCookie(userId);
        response.addCookie(openId);
        response.addCookie(sign);

        goodsService.jumpToGoodsActivityIndex(Long.parseLong(issueId), map);

        return "/html/goods/index";
    }

    /**
     * 支付结果显示
     *
     * @return
     */
    @RequestMapping(value = "/showResult", method = RequestMethod.GET)
    public String showResult(String orderNo,Model model) throws UnsupportedEncodingException {
        PaysResultShowModel paysResultShowModel=shoppingService.getPayResultShowModel(orderNo);
        model.addAttribute("paysResultShowModel", paysResultShowModel);
        return "/html/shopping/payResult";
    }

}
