package com.huotu.duobaoweb.controller;

import com.huotu.duobaoweb.entity.Delivery;
import com.huotu.duobaoweb.model.DeliveryModel;
import com.huotu.duobaoweb.model.RaiderListModel;
import com.huotu.duobaoweb.model.UserBuyFlowModel;
import com.huotu.duobaoweb.repository.UserRepository;
import com.huotu.duobaoweb.service.DeliveryService;
import com.huotu.duobaoweb.service.UserBuyFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 个人系统模块
 * Created by lhx on 2016/3/25.
 */
@RequestMapping(value="/web")
@Controller
public class PersonalController {

    @Autowired
    UserBuyFlowService userBuyFlowService;

    @Autowired
    DeliveryService deliveryService;

    /**
     * 查看我的参与记录
     * 查找夺宝记录
     * @param model
     * @param userId 用户ID
     * @return String
     * @throws Exception all
     */
    @RequestMapping(value = "/getMyInvolvedRecord" , method = RequestMethod.GET)
    public String getMyInvolvedRecord(Model model,Long userId,Integer type,Long lastTime) throws Exception{
//        RaiderListModel[] raiderListModels =  userBuyFlowService.findByUserIdAndType(userId, type, lastTime);
//        model.addAttribute("list",raiderListModels);
        model.addAttribute("type",type);
        return "/html/personal/list";
    }

    /**
     * 查看我的夺宝号码
     * @param model
     * @param userId 用户ID
     * @param issueId 期号
     * @return String
     * @throws Exception all
     */
    @RequestMapping(value = "/getMyRaiderNumbers" , method = RequestMethod.GET)
    public String getMyRaiderNumbers(Model model,Long userId, Long issueId) throws Exception {
        return "/html/personal/dbhm";
    }


    @RequestMapping(value = "/getMyLotteryList" , method = RequestMethod.GET)
    public String getMyLotteryList(Model model, Long userId, Long lastTime) throws Exception {
//        UserBuyFlowModel[] lotteryList = userBuyFlowService.findByUser(userId, lastTime);
//        model.addAttribute("list",lotteryList);
        model.addAttribute("type",3);
        return "/html/personal/list";
    }
    /**
     * 中奖详细信息确认
     * @param model
     * @param userId 用户ID
     * @param issueId 期号
     * @return String
     * @throws Exception all
     */
    @RequestMapping(value = "/getOneLotteryInfo" , method = RequestMethod.GET)
    public String getOneLotteryInfo(Model model,Long userId, Long issueId)  throws Exception {
//        DeliveryModel deliveryModel = deliveryService.findByIssueId(issueId);
//        model.addAttribute("deliveryModel",deliveryModel);
        return "/html/personal/zj";
    }


}
