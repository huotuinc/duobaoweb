package com.huotu.mallduobao.controller;

import com.alibaba.fastjson.JSONObject;
import com.huotu.huobanplus.sdk.common.repository.MerchantRestRepository;
import com.huotu.mallduobao.common.PublicParameterHolder;
import com.huotu.mallduobao.model.*;
import com.huotu.mallduobao.repository.CityRepository;
import com.huotu.mallduobao.repository.DeliveryRepository;
import com.huotu.mallduobao.service.*;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * 个人系统模块
 * Created by lhx on 2016/3/25.
 */
@RequestMapping(value="/personal")
@Controller
public class PersonalController {

    @Autowired
    UserBuyFlowService userBuyFlowService;

    @Autowired
    DeliveryService deliveryService;

    @Autowired
    UserNumberService userNumberService;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    DeliveryRepository deliveryRepository;

    @Autowired
    GoodsRestRepository goodsRestRepository;

    @Autowired
    GoodsService goodsService;

    @Autowired
    CommonConfigService commonConfigService;

    @Autowired
    MerchantRestRepository merchantRestRepository;

    /**
     * 跳转到我的参与记录页面
     * @param model
     * @return String
     * @throws Exception all
     */
    @RequestMapping(value = "/getMyInvolvedRecord" , method = RequestMethod.GET)
    public String getMyInvolvedRecord(Model model,Integer type) throws Exception{
        WebPublicModel common = PublicParameterHolder.getParameters();
        model.addAttribute("type",type);
        model.addAttribute("customerId",common.getCustomerId());
        model.addAttribute("issueId", common.getIssueId());
        return "/html/personal/raiderList";
    }

    /**
     * 查看我的参与记录列表
     * @param type       层级
     * @param pageSize   每页条数
     * @param page       页数
     * @throws IOException
     */
    @RequestMapping(value = "/getMyInvolvedRecordAjax", method = {RequestMethod.POST})
    @ResponseBody
    public RaiderListModelAjax getMyInvolvedRecordAjax(Model model,Integer type,Long lastFlag,Integer pageSize, Integer page) throws IOException, URISyntaxException {
        WebPublicModel common = PublicParameterHolder.getParameters();
        model.addAttribute("customerId",common.getCustomerId());
        model.addAttribute("issueId",common.getIssueId());
        RaiderListModelAjax raiderListAjaxModel= userBuyFlowService.toListRaiderListModel(type, common.getCurrentUser().getId(), lastFlag, pageSize, page);
        raiderListAjaxModel.setPublicParament("customerId=" + common.getCustomerId());
        return raiderListAjaxModel;
    }


    /**
     * 查看我的夺宝号码
     * @param model
     * @return String
     * @throws Exception all
     */
    @RequestMapping(value = "/getMyRaiderNumbers" , method = RequestMethod.GET)
    public String getMyRaiderNumbers(Model model) throws Exception {
        WebPublicModel common = PublicParameterHolder.getParameters();
        RaiderNumbersModel raiderNumbersModel = userNumberService.getMyRaiderNumbers(common.getCurrentUser().getId(), common.getIssueId());
        model.addAttribute("customerId",common.getCustomerId());
        model.addAttribute("issueId",common.getIssueId());
        model.addAttribute("raiderNumbersModel", raiderNumbersModel);
        return "/html/personal/duobaoNumber";
    }


    /**
     * 跳转到中奖列表界面
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getMyLotteryList" , method = RequestMethod.GET)
    public String getMyLotteryList(Model model) throws Exception {
        WebPublicModel common = PublicParameterHolder.getParameters();
        model.addAttribute("type", 3);
        model.addAttribute("customerId",common.getCustomerId());
        model.addAttribute("issueId",common.getIssueId());
        return "/html/personal/raiderList";
    }


    /**
     * 获取中奖列表
     * @param pageSize
     * @param page
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getMyLotteryListAjax" , method = RequestMethod.POST)
    @ResponseBody
    public UserBuyFlowModelAjax getMyLotteryListAjax(Model model,Long lastFlag,Integer pageSize, Integer page) throws Exception {
        WebPublicModel common = PublicParameterHolder.getParameters();
        UserBuyFlowModelAjax userBuyFlowModelAjax = userBuyFlowService.findByUserAjax(common.getCurrentUser().getId(),lastFlag, pageSize, page);
        userBuyFlowModelAjax.setPublicParament("customerId=" + common.getCustomerId());
        model.addAttribute("customerId",common.getCustomerId());
        model.addAttribute("issueId",common.getIssueId());
        return userBuyFlowModelAjax;
    }

    /**
     * 中奖详细信息确认
     * @param model
     * @return String
     * @throws Exception all
     */
    @RequestMapping(value = "/getOneLotteryInfo" , method = RequestMethod.GET)
    public String getOneLotteryInfo(Model model)  throws Exception {
        WebPublicModel common = PublicParameterHolder.getParameters();
        DeliveryModel deliveryModel = deliveryService.findByIssueId(common.getIssueId());
        //todo lhx
        String url = "http://"+merchantRestRepository.getOneByPK(String.valueOf(common.getCustomerId())).getSubDomain()+"."+commonConfigService.getMaindomain().trim();
        model.addAttribute("mallOrderUrl",url);
        model.addAttribute("customerId",common.getCustomerId());
        model.addAttribute("issueId",common.getIssueId());
        model.addAttribute("deliveryModel",deliveryModel);
        return "/html/personal/lotteryInfo";
    }

    /**
     * 选择商品规格
     *  中奖后选择商品规格
     *  @param model
     * @return String
     * @throws Exception all
     */
    @RequestMapping(value = "/selGoodsSpec" , method = RequestMethod.GET)
    public String selGoodsSpec(Model model)  throws Exception {
        WebPublicModel common = PublicParameterHolder.getParameters();
        SelGoodsSpecModel selGoodsSpecModel = goodsService.getSelGoodsSpecModelByIssueId(common.getIssueId());
        Map<String,Object> map = userBuyFlowService.getGoodsSpec(selGoodsSpecModel.getMallGoodsId());
        model.addAttribute("goodsAndSpecModel", selGoodsSpecModel);
        model.addAttribute("list",map.get("mgsList"));
        model.addAttribute("productList",map.get("productList"));
        model.addAttribute("customerId",common.getCustomerId());
        model.addAttribute("issueId",common.getIssueId());
        return "/html/personal/selectGoodsSpecifications";
    }

    /**
     * 添加收货货品ID,和名称
     */
    @RequestMapping(value = "/addDeliveryProductInfo" , method = RequestMethod.GET)
    public String addDeliveryProductInfo(Model model, Long productId,String productName) throws IOException {
        WebPublicModel common = PublicParameterHolder.getParameters();
        deliveryService.addDeliveryProductInfo(common.getIssueId(), productId, productName);
        model.addAttribute("customerId",common.getCustomerId());
        model.addAttribute("issueId",common.getIssueId());
        return "redirect:/personal/getOneLotteryInfo?issueId="+common.getIssueId()+"&customerId="+common.getCustomerId();
    }

    /**
     * 跳转到添加地址界面
     * @param model
     * @param deliveryId
     * @return
     */
    @RequestMapping(value = "/toRecpeiptAddress" , method = RequestMethod.GET)
    public String toRecpeiptAddress(Model model, Long deliveryId) throws IOException {
        WebPublicModel common = PublicParameterHolder.getParameters();
        model.addAttribute("customerId",common.getCustomerId());
        model.addAttribute("issueId",common.getIssueId());
        model.addAttribute("deliveryId",deliveryId);
        model.addAttribute("citys",cityRepository.findByParentId(0));
        return "/html/personal/receiptAddress";
    }

    /**
     * 查找城市city数据
     * @param parentId
     * @return
     */
    @RequestMapping(value = "/ajaxFindByParentId" , method = RequestMethod.POST)
    @ResponseBody
    public String ajaxFindByParentId(Model model,Integer parentId){
        WebPublicModel common = PublicParameterHolder.getParameters();
        model.addAttribute("customerId",common.getCustomerId());
        model.addAttribute("issueId",common.getIssueId());
        return JSONObject.toJSONString(cityRepository.findByParentId(parentId));
    }

    /**
     * 添加收货地址
     * @param model
     * @param deliveryId
     * @param receiver
     * @param mobile
     * @param details
     * @return
     * @throws Exception
     */
    @RequestMapping("/addRecpeiptAddress")
    public String addRecpeiptAddress(Model model, Long deliveryId, String receiver, String mobile, String details,String remark) throws Exception {
        WebPublicModel common = PublicParameterHolder.getParameters();
        deliveryService.addRecpeiptAddress(common.getCustomerId(),deliveryId, receiver, mobile, details,remark);
        model.addAttribute("customerId", common.getCustomerId());
        model.addAttribute("issueId", common.getIssueId());
        return "redirect:/personal/getOneLotteryInfo?issueId="+common.getIssueId()+"&customerId="+common.getCustomerId();
    }

}
