package com.huotu.duobaoweb.controller;

import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.common.PublicParameterHolder;
import com.huotu.duobaoweb.common.WeixinPayUrl;
import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.Orders;
import com.huotu.duobaoweb.entity.ShoppingCart;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.model.PayModel;
import com.huotu.duobaoweb.model.ResultModel;
import com.huotu.duobaoweb.model.ShoppingCartsModel;
import com.huotu.duobaoweb.model.WebPublicModel;
import com.huotu.duobaoweb.repository.IssueRepository;
import com.huotu.duobaoweb.repository.UserRepository;
import com.huotu.duobaoweb.service.ShoppingService;
import com.huotu.duobaoweb.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

/**
 * Created by xhk on 2016/3/25.
 */
@RequestMapping(value="/shopping")
@Controller
public class ShoppingController {


    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShoppingService shoppingService;

    @Autowired
    private UserService userService;


    /**
     * 加入购物车
     *
     * @return
     */
    @RequestMapping(value ="/joinToCarts",method = RequestMethod.POST)
    @ResponseBody
    public ResultModel joinToCarts(Long buyNum){ //String issueId,Long userId,
        WebPublicModel common = PublicParameterHolder.getParameters();
        ResultModel resultModel=new ResultModel();
        if(common.getIssueId()==null){
            resultModel.setMessage("添加到购物车失败！");
            resultModel.setCode(404);
            return resultModel;
        }
        Issue issue=issueRepository.findOne(common.getIssueId());
        if(issue==null){
            resultModel.setMessage("商品不存在，请重新购买！");
            resultModel.setCode(404);
            return resultModel;
        }else if(issue.getStatus()!= CommonEnum.IssueStatus.going){
            resultModel.setMessage("商品已过期，请重新购买！");
            resultModel.setCode(404);
            return resultModel;
        }

        User user=userRepository.findOne(common.getCurrentUser().getId());
        if(user==null){
            resultModel.setMessage("用户不合法，请重新进入！");
            resultModel.setCode(404);
            return resultModel;
        }

        shoppingService.joinToShoppingCarts(issue,user,buyNum);

        resultModel.setMessage("添加成功！");
        resultModel.setCode(200);
        return resultModel;
    }

    /**
     * 全额购买购物车 直接跳转到支付界面 pay.html
     */
    @RequestMapping(value ="/allToCarts",method = RequestMethod.POST)
    @ResponseBody
    public ResultModel allToCarts() throws UnsupportedEncodingException {//String issueId,Long userId
        WebPublicModel common = PublicParameterHolder.getParameters();
        ResultModel resultModel=new ResultModel();
        if(common.getIssueId()==null){
            resultModel.setMessage("添加到购物车失败！");
            resultModel.setCode(404);
            return resultModel;
        }
        Issue issue=issueRepository.findOne(common.getIssueId());

        if(issue==null){
            resultModel.setMessage("商品不存在，请重新购买！");
            resultModel.setCode(404);
            return resultModel;
        }

        //todo 商品库存如果不够则不能全额购买 待罗国华提供接口
        if(issue.getGoods().getToMallGoodsId()!=null){
            resultModel.setMessage("全额购买商品库存不足，请联系卖家！");
            resultModel.setCode(404);
            return resultModel;
        }

        User user=userRepository.findOne(common.getCurrentUser().getId());
        if(user==null){
            resultModel.setMessage("用户不合法，请重新进入！");
            resultModel.setCode(404);
            return resultModel;
        }

        ShoppingCart shoppingCart=shoppingService.allToShoppingCarts(issue,user);

        String openidUrl=userService.getWeixinAuthUrl(common);

        resultModel.setUrl("../shopping/toAllPay?shoppingCartId="+shoppingCart.getId()+
                "&userId="+common.getCurrentUser().getId()+
                "&issueId="+common.getIssueId()+
                "&customerId"+common.getCustomerId()+
                "&openId="+common.getOpenId()+
                "&sign="+common.getSign()+
                "&openidUrl"+openidUrl);
        resultModel.setMessage("添加成功！");
        resultModel.setCode(200);
        return resultModel;
    }

    /**
     * 全额支付跳转到支付页面
     * @return
     */
    @RequestMapping(value="/toAllPay",method = RequestMethod.GET)
    public String toAllPay(Model model,String shoppingCartId) throws UnsupportedEncodingException {
        PayModel payModel=shoppingService.allPayAndGetModel(shoppingCartId);
        WebPublicModel common = PublicParameterHolder.getParameters();
        payModel.setType(2);
        if(payModel!=null) {
            String openidUrl=userService.getWeixinAuthUrl(common);

            model.addAttribute("openidUrl",openidUrl);
            model.addAttribute("payModel", payModel);
            model.addAttribute("userId",common.getCurrentUser().getId());
            model.addAttribute("issueId", common.getIssueId());
            model.addAttribute("customerId", common.getCustomerId());
            model.addAttribute("openId",common.getOpenId());
            model.addAttribute("sign",common.getSign());

            return "html/shopping/pay";
        }else{
            //返回购物车提示错误
            model.addAttribute("overTime", "1");
            model.addAttribute("notShow", "1");
            return "html/shopping/cartsList";
        }
    }

    /**
     * 得到购物车
     * @return
     */
    @RequestMapping(value="/showShoppingCarts",method = RequestMethod.GET)
    public String showShoppingCarts(Model model) throws URISyntaxException, UnsupportedEncodingException {

        WebPublicModel common = PublicParameterHolder.getParameters();
        ShoppingCartsModel shoppingCartsModel=shoppingService.getShoppingCartsModel(common.getCurrentUser().getId());

        //如果不存在，则让前端不显示购物车，同时支付变为0
        if(shoppingCartsModel==null){
            model.addAttribute("notShow", "1");
            shoppingCartsModel=new ShoppingCartsModel();
            shoppingCartsModel.setBuyNum(0L);
            shoppingCartsModel.setBuyMoney(0.0);
        }
        //微信授权回调url，跳转到index
        String openidUrl=userService.getWeixinAuthUrl(common);

        model.addAttribute("openidUrl",openidUrl);
        model.addAttribute("shoppingCarts",shoppingCartsModel);
        model.addAttribute("userId",common.getCurrentUser().getId());
        model.addAttribute("issueId", common.getIssueId());
        model.addAttribute("customerId", common.getCustomerId());
        model.addAttribute("openId",common.getOpenId());
        model.addAttribute("sign",common.getSign());
        return "html/shopping/cartsList";
    }
    /**
     * 结算购物车
     * @return
     */
    @RequestMapping(value="/balance",method = RequestMethod.GET)
    public String balance(Model model,Long cartId,Integer buyNum) throws UnsupportedEncodingException {
        WebPublicModel common = PublicParameterHolder.getParameters();
        PayModel payModel=shoppingService.balance(cartId, buyNum);
        payModel.setType(1);
        if(payModel!=null) {
            //微信授权回调url，跳转到index
            String openidUrl=userService.getWeixinAuthUrl(common);

            model.addAttribute("openidUrl",openidUrl);
            model.addAttribute("payModel", payModel);
            model.addAttribute("userId",common.getCurrentUser().getId());
            model.addAttribute("issueId", common.getIssueId());
            model.addAttribute("customerId", common.getCustomerId());
            model.addAttribute("openId",common.getOpenId());
            model.addAttribute("sign",common.getSign());
            return "html/shopping/pay";
        }else{
            //返回购物车提示错误
            model.addAttribute("overTime", "1");
            model.addAttribute("notShow", "1");
            return "html/shopping/cartsList";
        }
    }

    /**
     * 支付
     * @return
     */
    @RequestMapping(value="/pay",method = RequestMethod.POST)
    @ResponseBody
    public ResultModel pay(Model model,PayModel payModel) throws IOException {
        WebPublicModel common = PublicParameterHolder.getParameters();
        ResultModel resultModel=new ResultModel();
        //todo 其他用户可以代付
        //生成订单
        Orders orders=shoppingService.createOrders(payModel);
        if(orders==null){
            //如果订单生成失败则跳转到购物车提示商品已经过期
            model.addAttribute("overTime", "1");
            model.addAttribute("notShow", "1");
            resultModel.setCode(404);
            resultModel.setMessage("购物车信息已过期，请重新选择商品！");
            return resultModel;
        }
        //设置应该支付的用户
        WeixinPayUrl.customerid=common.getCustomerId();
        String url=shoppingService.getWeixinPayUrl(orders);
        resultModel.setCode(200);
        resultModel.setMessage("跳转支付中！");
        resultModel.setUrl(url);
        return resultModel;
    }

}
