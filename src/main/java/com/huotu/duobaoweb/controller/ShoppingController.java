package com.huotu.duobaoweb.controller;

import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.Orders;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.model.PayInfoModel;
import com.huotu.duobaoweb.model.PayModel;
import com.huotu.duobaoweb.model.ResultModel;
import com.huotu.duobaoweb.model.ShoppingCartsModel;
import com.huotu.duobaoweb.repository.IssueRepository;
import com.huotu.duobaoweb.repository.UserRepository;
import com.huotu.duobaoweb.service.ShoppingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Created by xhk on 2016/3/25.
 */
@RequestMapping(value="/web")
@Controller
public class ShoppingController {


    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ShoppingService shoppingService;

    @Autowired
    private HttpServletResponse response;

    /**
     * 加入购物车
     *@param issueId 期号id
     *@param userId 用户id
     * @return
     */
    @RequestMapping(value ="/joinToCarts",method = RequestMethod.GET)
    @ResponseBody
    public ResultModel joinToCarts(String issueId,Long userId,Long buyNum){
        ResultModel resultModel=new ResultModel();
        if(issueId==null){
            resultModel.setMessage("添加到购物车失败！");
            resultModel.setCode(404);
            return resultModel;
        }
        Issue issue=issueRepository.findOne(Long.parseLong(issueId));
        if(issue==null){
            resultModel.setMessage("商品不存在，请重新购买！");
            resultModel.setCode(404);
            return resultModel;
        }else if(issue.getStatus()!= CommonEnum.IssueStatus.going){
            resultModel.setMessage("商品已过期，请重新购买！");
            resultModel.setCode(404);
            return resultModel;
        }

        User user=userRepository.findOne(userId);
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
     *得到购物车列表
     * @return
     */
    public String getCartsList(){
        return null;
    }

    /**
     * 得到购物车
     * @return
     */
    @RequestMapping(value="/showShoppingCarts",method = RequestMethod.GET)
    public String showShoppingCarts(Model model,Long userId) throws URISyntaxException {
        ShoppingCartsModel shoppingCartsModel=shoppingService.getShoppingCartsModel(userId);
        //如果不存在，则让前端不显示购物车，同时支付变为0
        if(shoppingCartsModel==null){
            model.addAttribute("notShow", "1");
            shoppingCartsModel=new ShoppingCartsModel();
            shoppingCartsModel.setBuyNum(0L);
            shoppingCartsModel.setBuyMoney(0.0);
        }
        model.addAttribute("shoppingCarts",shoppingCartsModel);
        return "html/shopping/cartsList";
    }
    /**
     * 结算购物车
     * @return
     */
    @RequestMapping(value="/balance",method = RequestMethod.GET)
    public String balance(Model model,Long cartId,Integer buyNum){
        PayModel payModel=shoppingService.balance(cartId, buyNum);
        if(payModel!=null) {
            model.addAttribute("payModel", payModel);
            return "html/shopping/pay";
        }else{
            model.addAttribute("overTime", "1");
            return "html/shopping/cartsList";
        }
    }

    /**
     * 支付
     * @return
     */
    @RequestMapping(value="/pay",method = RequestMethod.GET)
    public String pay(Model model,PayInfoModel payInfoModel) throws IOException {
        //生成订单
        Orders orders=shoppingService.createOrders(payInfoModel);
        if(orders==null){
            //如果订单生成失败则跳转到购物车提示商品已经过期
            model.addAttribute("overTime", "1");
            return "html/shopping/cartsList";
        }
        String url=shoppingService.getWeixinPayUrl(orders);
        response.sendRedirect(url);
        return null;
    }
}
