package com.huotu.duobaoweb.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huotu.duobaoweb.common.PublicParameterHolder;
import com.huotu.duobaoweb.common.StringHelper;
import com.huotu.duobaoweb.model.*;
import com.huotu.duobaoweb.repository.CityRepository;
import com.huotu.duobaoweb.repository.DeliveryRepository;
import com.huotu.duobaoweb.service.DeliveryService;
import com.huotu.duobaoweb.service.UserBuyFlowService;
import com.huotu.duobaoweb.service.UserNumberService;
import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.Product;
import com.huotu.huobanplus.common.entity.support.ProductSpecifications;
import com.huotu.huobanplus.common.entity.support.SpecDescription;
import com.huotu.huobanplus.common.entity.support.SpecDescriptions;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.huobanplus.sdk.common.repository.ProductRestRepository;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    ProductRestRepository  productRestRepository;

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
        model.addAttribute("userId",common.getCurrentUser().getId());
        model.addAttribute("customerId",common.getCustomerId());
        model.addAttribute("issueId",common.getIssueId());
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
    public RaiderListModelAjax getMyInvolvedRecordAjax(Model model,Integer type, Integer pageSize, Integer page) throws IOException, URISyntaxException {
        WebPublicModel common = PublicParameterHolder.getParameters();
        model.addAttribute("userId",common.getCurrentUser().getId());
        model.addAttribute("customerId",common.getCustomerId());
        model.addAttribute("issueId",common.getIssueId());
        RaiderListModelAjax raiderListAjaxModel= userBuyFlowService.toListRaiderListModel(type,common.getCurrentUser().getId(),pageSize,page);
        raiderListAjaxModel.setPublicParament("userId="+common.getCurrentUser().getId()+"&"+"customerId="+common.getCustomerId());
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
        model.addAttribute("userId",common.getCurrentUser().getId());
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
        model.addAttribute("type",3);
        model.addAttribute("userId",common.getCurrentUser().getId());
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
    public UserBuyFlowModelAjax getMyLotteryListAjax(Model model,Integer pageSize, Integer page) throws Exception {
        WebPublicModel common = PublicParameterHolder.getParameters();
        UserBuyFlowModelAjax userBuyFlowModelAjax = userBuyFlowService.findByUserAjax(common.getCurrentUser().getId(), pageSize, page);
        userBuyFlowModelAjax.setPublicParament("userId="+common.getCurrentUser().getId()+"&"+"customerId="+common.getCustomerId());
        model.addAttribute("userId",common.getCurrentUser().getId());
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
        model.addAttribute("userId",common.getCurrentUser().getId());
        model.addAttribute("customerId",common.getCustomerId());
        model.addAttribute("issueId",common.getIssueId());
        model.addAttribute("deliveryModel",deliveryModel);
        return "/html/personal/lotteryInfo";
    }

    /**
     * 跳转到添加地址界面
     * @param model
     * @param deliveryId
     * @return
     */
    @RequestMapping(value = "/toRecpeiptAddress" , method = RequestMethod.GET)
    public String toRecpeiptAddress(Model model, Long deliveryId) throws IOException {
//        Goods goods = goodsRestRepository.getOneByPK("1");
//        List<Product> list = productRestRepository.findByGoods(goods);
//        System.out.println("=========" + goods.getSpec());
//        SpecDescriptions specDescription = goods.getSpecDescriptions();
//        System.out.println("=========" + specDescription);
//
//        Map<Long,String> map = JSONObject.toJavaObject(JSON.parseObject(goods.getSpec()) , Map.class);
//
//        if(map==null){
//            model.addAttribute("types",null);//返回一个List
//        }else {
//            SpecDescriptions specDescriptions = goods.getSpecDescriptions();
//            List<BasicNameValuePair> types=new ArrayList<>();
//            for(Map.Entry<Long,List<SpecDescription>> entry:specDescriptions.entrySet()){
//                StringBuffer sb=new StringBuffer();
//                for(SpecDescription s:entry.getValue()){
//                    sb.append(s.getSpecValue()+"、");
//                }
//                String typeDesc=sb.toString();
//                BasicNameValuePair bnv=new BasicNameValuePair(map.get(entry.getKey()),sb.toString().substring(0,typeDesc.length()-1));
//                types.add(bnv);
//            }
//            model.addAttribute("types",types);//返回一个List
//        }

        WebPublicModel common = PublicParameterHolder.getParameters();
        model.addAttribute("userId",common.getCurrentUser().getId());
        model.addAttribute("customerId",common.getCustomerId());
        model.addAttribute("issueId",common.getIssueId());
        model.addAttribute("deliveryId",deliveryId);
        model.addAttribute("citys",cityRepository.findByParentId(0));
        return "/html/personal/receiptAddress";
    }

    /**
     * 查找数据
     * @param parentId
     * @return
     */
    @RequestMapping(value = "/ajaxFindByParentId" , method = RequestMethod.POST)
    @ResponseBody
    public String ajaxFindByParentId(Model model,Integer parentId){
        WebPublicModel common = PublicParameterHolder.getParameters();
        model.addAttribute("userId",common.getCurrentUser().getId());
        model.addAttribute("customerId",common.getCustomerId());
        model.addAttribute("issueId",common.getIssueId());
        return JSONObject.toJSONString(cityRepository.findByParentId(parentId));
    }

    /**
     * 确认收货
     * @param deliveryId
     * @return
     */
    @RequestMapping("/confirmReceipt")
    public String confirmReceipt(Model model,Long deliveryId) {
        WebPublicModel common = PublicParameterHolder.getParameters();
//        Delivery delivery = deliveryService.findById(deliveryId);
//        delivery.setDeliveryStatus(CommonEnum.DeliveryStatus.Finished);
//        delivery.setRecieveGoodsTime(new Date());
//        deliveryRepository.save(delivery);
        model.addAttribute("userId", common.getCurrentUser().getId());
        model.addAttribute("customerId", common.getCustomerId());
        model.addAttribute("issueId", common.getIssueId());
        return "redirect:/personal/getMyLotteryList?userId="+common.getCurrentUser().getId()+"&issueId="+common.getIssueId()+"&customerId="+common.getCustomerId();
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
    public String addRecpeiptAddress(Model model, Long deliveryId, String receiver, String mobile, String details) throws Exception {
        WebPublicModel common = PublicParameterHolder.getParameters();
        deliveryService.addRecpeiptAddress(deliveryId, receiver, mobile, details);
        model.addAttribute("userId", common.getCurrentUser().getId());
        model.addAttribute("customerId", common.getCustomerId());
        model.addAttribute("issueId", common.getIssueId());
        return "redirect:/personal/getOneLotteryInfo?userId="+common.getCurrentUser().getId()+"&issueId="+common.getIssueId()+"&customerId="+common.getCustomerId();
    }

}
