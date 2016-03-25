package com.huotu.duobaoweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 个人系统模块
 * Created by lhx on 2016/3/25.
 */
@RequestMapping(value="/web")
@Controller
public class PersonalController {

    /**
     * 查看我的参与记录
     * @param model
     * @param userId 用户ID
     * @return String
     * @throws Exception all
     */
    @RequestMapping("/getMyInvolvedRecord")
    public String getMyInvolvedRecord(Model model,Long userId) throws Exception{
        return "";
    }

    /**
     * 查看我的夺宝号码
     * @param model
     * @param userId 用户ID
     * @param issueId 期号
     * @return String
     * @throws Exception all
     */
    @RequestMapping("/getMyRaiderNumbers")
    public String getMyRaiderNumbers(Model model,Long userId, Long issueId) throws Exception {
        return "";
    }

    /**
     * 中奖详细信息确认
     * @param model
     * @param userId 用户ID
     * @param issueId 期号
     * @return String
     * @throws Exception all
     */
    @RequestMapping("/getOneLotteryInfo")
    public String getOneLotteryInfo(Model model,Long userId, Long issueId)  throws Exception {
        return "";
    }


}
