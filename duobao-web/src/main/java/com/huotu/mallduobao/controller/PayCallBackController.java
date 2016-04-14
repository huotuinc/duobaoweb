package com.huotu.mallduobao.controller;

import com.huotu.mallduobao.model.PayResult;
import com.huotu.mallduobao.model.PayResultModel;
import com.huotu.mallduobao.repository.OrdersItemRepository;
import com.huotu.mallduobao.repository.OrdersRepository;
import com.huotu.mallduobao.service.PayService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by xhk on 2016/3/29.
 */
@RequestMapping(value = "/pay")
@Controller
public class PayCallBackController {

    private static Log log = LogFactory.getLog(PayCallBackController.class);


    @Autowired
    private HttpServletResponse response;

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private PayService payService;

    @Autowired
    private OrdersItemRepository ordersItemRepository;

    /**
     * 微信回调接口
     */
    @RequestMapping("/payCallbackWeixin")
    public PayResult payCallbackWeixin(HttpServletRequest request) throws Exception {
        //网页授权后获取传递的参数
        String tradeno=request.getParameter("tradeno");
        String orderNo = request.getParameter("orderNo");
        float money = Float.parseFloat(request.getParameter("totalfee"));
        String outOrderNo = request.getParameter("outtradeno");
        log.info("进入支付提示接口orderNo："+orderNo+";totalfee:"+money+";outOrderNo"+outOrderNo+";tradeno:"+tradeno);
        PayResult payResultModel=new PayResult();
        PayResultModel payResult = payService.solveWeixinPayResult(tradeno, money, outOrderNo);

        //PaysResultShowModel paysResultShowModel = new PaysResultShowModel();
        if (payResult.isSuccess()) {
            //支付成功
//            if (orderNo != null&&payResult.getResultType().equals(CommonEnum.PayResult.normalPay)) {
                //正常支付成功
                //OrdersItem ordersItem = ordersItemRepository.findByOrderId(orderNo);
                //Orders orders=ordersRepository.findOne(orderNo);
//                if (payResult.getResultNumber() != null) {
//                    //构建前端显示内容
//                    paysResultShowModel.setIssueId(ordersItem.getIssue().getId());
//                    paysResultShowModel.setDetail(ordersItem.getIssue().getGoods().getTitle());
//                    paysResultShowModel.setNeedNumber(ordersItem.getIssue().getToAmount());
//                    paysResultShowModel.setTitle("您成功参与了1件商品共" + ordersItem.getAmount() + "人次夺宝，信息如下：");
//                    paysResultShowModel.setNumbers(payResult.getResultNumber());
//                    model.addAttribute("paysResultShowModel", paysResultShowModel);
//                }
//            }
//            else if(orderNo != null&&payResult.getResultType().equals(CommonEnum.PayResult.allPay)){
//                //全额支付成功 则直接跳转到填收货地址界面
//            }
            payResultModel.setCode(1);
            payResultModel.setMsg("支付成功！");
            return payResultModel;
        } else {
            //支付失败
            payResultModel.setCode(0);
            payResultModel.setMsg("支付失败！");
            return payResultModel;
        }

//        //以下为测试信息
//        List<Long> number=new ArrayList<Long>();
//        number.add(1L);
//        number.add(11L);
//        number.add(111L);
//        number.add(1111L);
//        paysResultShowModel.setIssueId(1111111L);
//        paysResultShowModel.setDetail("这是测试信息：哈哈哈哈哈哈哈哈哈哈！");
//        paysResultShowModel.setNeedNumber(10000L);
//        paysResultShowModel.setTitle("您成功参与了1件商品共" + 1000 + "人次夺宝，信息如下：");
//        paysResultShowModel.setNumbers(number);
//        model.addAttribute("paysResultShowModel", paysResultShowModel);

        //return "/html/shopping/payResult";
    }

    /**
     * 支付宝回调接口
     *
     * @param request
     * @return
     * @throws IOException
     */
    @RequestMapping("/payCallbackAliPay")
    public String payCallbackAliPay(HttpServletRequest request) throws IOException {

        response.sendRedirect("支付结果页面");
        return null;
    }
}
