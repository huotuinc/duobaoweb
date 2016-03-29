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
     * ���빺�ﳵ
     *@param issueId �ں�id
     *@param userId �û�id
     * @return
     */
    @RequestMapping(value ="/joinToCarts",method = RequestMethod.POST)
    @ResponseBody
    public ResultModel joinToCarts(String issueId,Long userId,Long buyNum){
        ResultModel resultModel=new ResultModel();
        if(issueId==null){
            resultModel.setMessage("��ӵ����ﳵʧ�ܣ�");
            resultModel.setCode(404);
            return resultModel;
        }
        Issue issue=issueRepository.findOne(Long.parseLong(issueId));
        if(issue==null){
            resultModel.setMessage("��Ʒ�Ѿ����ڣ������¹���");
            resultModel.setCode(404);
            return resultModel;
        }

        User user=userRepository.findOne(userId);
        if(user==null){
            resultModel.setMessage("�û����Ϸ��������½��룡");
            resultModel.setCode(404);
            return resultModel;
        }

        shoppingService.joinToShoppingCarts(issue,user,buyNum);

        resultModel.setMessage("��ӳɹ���");
        resultModel.setCode(200);
        return resultModel;
    }

    /**
     *�õ����ﳵ�б�
     * @return
     */
    public String getCartsList(){
        return null;
    }

    /**
     * �õ����ﳵ
     * @return
     */
    @RequestMapping(value="/showShoppingCarts",method = RequestMethod.GET)
    public String showShoppingCarts(Model model,Long userId){
        ShoppingCartsModel shoppingCartsModel=shoppingService.getShoppingCartsModel(userId);
        //��������ڣ�����ǰ�˲���ʾ���ﳵ��ͬʱ֧����Ϊ0
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
     * ���㹺�ﳵ
     * @return
     */
    @RequestMapping(value="/balance",method = RequestMethod.GET)
    public String balance(Long cartId,Integer buyNum){

        return "html/shopping/pay";
    }

    /**
     * ֧��
     * @return
     */
    public String pay(){

        return null;
    }
}
