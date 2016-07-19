package com.huotu.mallduobao.controller;

import com.alibaba.fastjson.JSON;
import com.huotu.mallduobao.common.AuthEntity;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.model.PaysResultShowModel;
import com.huotu.mallduobao.service.GoodsService;
import com.huotu.mallduobao.service.IssueService;
import com.huotu.mallduobao.service.ShoppingService;
import com.huotu.mallduobao.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
     * @param customerId 商户id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getOpid", method = RequestMethod.GET)
    public String getOpid(HttpServletRequest request,String customerId,String redirectUrl,Map<String, Object> map) throws Exception {
        log.info("redirectUrl:"+redirectUrl);
        String info=request.getParameter("retuinfo");
        AuthEntity retuinfo=JSON.parseObject(info, AuthEntity.class);
        //进行用户注册(如果用户存在则不注册，不存在才注册)
        User user = userService.registerUser(retuinfo, customerId, getIp(request));


        //将当前用户写入ThreadLocal
        //WebPublicModel webPublicModel=userService.getWebPublicModel(user,issue);
        //PublicParameterHolder.putParameters(webPublicModel);


        Cookie custId=new Cookie("customerId",customerId);
        custId.setMaxAge(60*60*24*365*5); //单位是秒 todo 正式上线的时候时间设置长一点 5年
        custId.setPath("/");
        Cookie userId=new Cookie("userId",String.valueOf(user.getId()));
        userId.setMaxAge(60*60*24*365*5);
        userId.setPath("/");
        Cookie openId=new Cookie("openId",retuinfo.getOpenid());
        openId.setMaxAge(60 * 60 * 24 * 365 * 5);
        openId.setPath("/");
        Cookie sign=new Cookie("sign",userService.getSign(user));
        sign.setMaxAge(60 * 60 * 24 * 365 * 5);
        sign.setPath("/");
        response.addCookie(custId);
        response.addCookie(userId);
        response.addCookie(openId);
        response.addCookie(sign);

        //goodsService.jumpToGoodsActivityIndex(Long.parseLong(issueId), map);

        return "redirect:" + redirectUrl;
    }

    @RequestMapping(value = "/appAccredit", method = RequestMethod.GET)
    public String appAccredit(HttpServletRequest request, String customerId, String redirectUrl, String openId, String userid, String wxheadimg, String wxnickname) throws Exception{
        log.info("redirectUrl:" + redirectUrl);
        log.info("userid=" + userid + ",wxheadimg="+ wxheadimg + ",wxnickname=" + wxnickname);
        User user = userService.registerAppUser(customerId, openId, getIp(request), userid, wxheadimg, wxnickname);

        System.out.println("=====================================================");
        System.out.println("====================appAccredit===================");
        System.out.println("=====================================================");

        Cookie custId=new Cookie("customerId",customerId);
        custId.setMaxAge(60*60*24*365*5); //单位是秒 todo 正式上线的时候时间设置长一点 5年
        custId.setPath("/");
        Cookie userId=new Cookie("userId",String.valueOf(user.getId()));
        userId.setMaxAge(60*60*24*365*5);
        userId.setPath("/");
        Cookie cOpenId=new Cookie("openId",openId);
        cOpenId.setMaxAge(60*60*24*365*5);
        cOpenId.setPath("/");
        Cookie sign=new Cookie("sign",userService.getSign(user));
        sign.setMaxAge(60*60*24*365*5);
        sign.setPath("/");
        response.addCookie(custId);
        response.addCookie(userId);
        response.addCookie(cOpenId);
        response.addCookie(sign);
        return "redirect:" + redirectUrl;
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

    /**
     * 获取ip地址
     * @param request
     * @return
     */
    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (!StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

}
