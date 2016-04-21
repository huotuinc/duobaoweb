package com.huotu.mallduobao.controller.admin;

import com.huotu.huobanplus.sdk.mall.annotation.CustomerId;
import com.huotu.mallduobao.model.admin.WebIssueSearchModel;
import com.huotu.mallduobao.model.admin.WebLotteryInfoModel;
import com.huotu.mallduobao.model.admin.WebPersonnalIssueListModel;
import com.huotu.mallduobao.model.admin.WebUserNumberModel;
import com.huotu.mallduobao.service.LotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Created by lhx on 2016/4/1.
 */
@Controller
@RequestMapping("/admin")
public class DuoBaoLotteryController {

    @Autowired
    LotteryService duoBaoLotteryService;

    /**
     * 获取中奖纪录列表
     *
     * @param webIssueSearchModel
     * @param model
     * @return String
     * @author lhx
     */
    @RequestMapping("/getLotteryList")
    public String getLotteryList(@CustomerId Long customerId,WebIssueSearchModel webIssueSearchModel, Model model) {
        WebPersonnalIssueListModel webPersonnalIssueListModel = duoBaoLotteryService.getWebIssueListModel(webIssueSearchModel,customerId);
        if(StringUtils.isEmpty(customerId)){
            return "/admin/error";
        }
        model.addAttribute("webIssueListModels", webPersonnalIssueListModel.getList());
        model.addAttribute("pageNo", webIssueSearchModel.getPageNoStr());//当前页数
        model.addAttribute("totalPages", webPersonnalIssueListModel.getTotalPages());//总页数
        model.addAttribute("totalRecords", webPersonnalIssueListModel.getTotalRecords());//总记录数
        return "/duobao/lottery/lotteryList";
    }

    /**
     * 获取中奖纪录详细信息
     * @param issueId
     * @param model
     * @return String
     * @author lhx
     */
    @RequestMapping("/getLotteryInfo")
    public String getLotteryInfo(Model model,Long issueId) throws Exception {
        WebLotteryInfoModel webLotteryInfoModel = duoBaoLotteryService.getWebLotteryInfoModel(issueId);
        model.addAttribute("webLotteryInfoModel", webLotteryInfoModel);
        List<WebUserNumberModel> userNumbers = duoBaoLotteryService.getMyRaiderNumbers(webLotteryInfoModel.getUserId(), issueId);
        model.addAttribute("userNumbers", userNumbers);
        return "/duobao/lottery/lotteryInfo";
    }

}
