package com.huotu.duobaoweb.controller;

import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.User;
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

    /**
     * 加入购物车
     *@param issueId 期号id
     *@param userId 用户id
     * @return
     */
    @RequestMapping(value ="/joinToCarts",method = RequestMethod.POST)
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
            resultModel.setMessage("商品已经过期，请重新购买！");
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
    public String showShoppingCarts(Model model,Long userId){
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
    public String balance(Long cartId,Integer buyNum){

        return "html/shopping/pay";
    }

    /**
     * 支付
     * @return
     */
    public String pay(){

        return null;
    }
}
