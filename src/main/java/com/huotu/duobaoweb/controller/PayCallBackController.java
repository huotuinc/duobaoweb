package com.huotu.duobaoweb.controller;

import com.huotu.duobaoweb.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by xhk on 2016/3/29.
 */
@RequestMapping(value="/web")
@Controller
public class PayCallBackController {

    @Autowired
    private HttpServletResponse response;

    @Autowired
    private PayService payService;

    @RequestMapping("/payCallbackWeixin")
    public String payCallbackWeixin(HttpServletRequest request) throws Exception {
        //网页授权后获取传递的参数
        String orderNo = request.getParameter("orderNo");

        boolean success=payService.solveWeixinPayResult(orderNo);

        if(success){
            //支付成功

        }else{
            //支付失败
        }
        response.sendRedirect("支付结果页面");//"/weChatpay/pay.jsp?appid="+appid2+"&timeStamp="+timestamp+"&nonceStr="+nonceStr2+"&package="+packages+"&sign="+finalsign);
        return null;
    }
}
