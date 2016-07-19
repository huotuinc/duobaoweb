package com.huotu.mallduobao.controller;

import com.alibaba.fastjson.JSONObject;
import com.huotu.huobanplus.sdk.common.repository.MerchantRestRepository;
import com.huotu.mallduobao.common.PublicParameterHolder;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.model.*;
import com.huotu.mallduobao.repository.CityRepository;
import com.huotu.mallduobao.repository.DeliveryRepository;
import com.huotu.mallduobao.repository.UserRepository;
import com.huotu.mallduobao.service.*;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.mallduobao.utils.CodeType;
import com.huotu.mallduobao.utils.EnumHelper;
import com.huotu.mallduobao.utils.StringHelper;
import com.huotu.mallduobao.utils.SysRegex;
import com.huotu.mallduobao.utils.VerificationType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;
import java.util.Random;

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

    @Autowired
    UserService userService;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    UserRepository userRepository;


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
        UserBuyFlowModelAjax userBuyFlowModelAjax = userBuyFlowService.findByUserAjax(common.getCurrentUser().getId(), lastFlag, pageSize, page);
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
        String url = "http://"+merchantRestRepository.getOneByPK(String.valueOf(common.getCustomerId())).getSubDomain()+"."+commonConfigService.getMainDomain().trim();
        model.addAttribute("mallOrderUrl",url);
        model.addAttribute("customerId", common.getCustomerId());
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
        model.addAttribute("productList", map.get("productList"));
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
        model.addAttribute("issueId", common.getIssueId());
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
        deliveryService.addRecpeiptAddress(deliveryId, receiver, mobile, details,remark);
        model.addAttribute("customerId", common.getCustomerId());
        model.addAttribute("issueId", common.getIssueId());
        return "redirect:/personal/getOneLotteryInfo?issueId="+common.getIssueId()+"&customerId="+common.getCustomerId();
    }

    /**
     * 跳转到绑定手机页面
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/toBindMobilePage", method = RequestMethod.GET)
    public String toBindMobilePage(String redirectUrl,  Map<String, Object> map) throws Exception{
        WebPublicModel common = PublicParameterHolder.getParameters();
        map.put("returnUrl", redirectUrl.replaceAll("&", "@") + "@customerId=" + common.getCustomerId());
        map.put("customerId", common.getCustomerId());
        return "/html/personal/bindMobile";
    }


    /**
     * 发送验证码
     * @param phone 手机号
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/sendCode", method = RequestMethod.POST)
    @ResponseBody
    public ResultModel sendCode(String phone,String returnUrl) throws Exception {
        WebPublicModel parameters = PublicParameterHolder.getParameters();
        Long customerId = parameters.getCustomerId();
        ResultModel resultModel = new ResultModel();

        if (!SysRegex.IsValidMobileNo(phone)) {
            resultModel.setCode(404);
            resultModel.setMessage("手机格式不正确!");
            return resultModel;
        }

        if (userService.findByMobile(phone) != null) {
            resultModel.setCode(404);
            resultModel.setMessage("手机已经被注册！");
            return resultModel;
        }

        VerificationType verificationType = EnumHelper.getEnumType(VerificationType.class, 1);
        Random rnd = new Random();
        Date date = new Date();
        String code = StringHelper.RandomNum(rnd, 4);
        try {
            verificationService.sendCode(phone, VerificationService.VerificationProject.fanmore, code, date, verificationType, CodeType.text);
        } catch (Exception e) {
            resultModel.setCode(404);
            resultModel.setMessage("错误");
            return resultModel;
        }
        resultModel.setCode(200);
        resultModel.setUrl("submitCode?phone=" + phone + "&returnUrl=" + returnUrl + "&customerId=" + customerId);
        resultModel.setMessage("短信已发送");
        return resultModel;
    }


    /**
     * 跳转到验证码输入界面
     *
     * @param phone 手机号
     * @param returnUrl 渠道商ID，如果为null代表用户不是通过渠道访问，就从哪里来回哪里;
     *                  如果不为null就在注册流程中从头传到位
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/submitCode", method = RequestMethod.GET)
    public String submitCode(String phone,String returnUrl, Model model) throws Exception {
        WebPublicModel parameters = PublicParameterHolder.getParameters();
        Long customerId = parameters.getCustomerId();
        model.addAttribute("phone", phone);
        model.addAttribute("returnUrl", returnUrl);
        model.addAttribute("customerId", customerId);
        return "/html/personal/checkPhoneCode";
    }

    /**
     * 核对验证码
     *
     * @param phone 手机号
     * @param code  验证码
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/checkCode", method = RequestMethod.POST)
    @ResponseBody
    public ResultModel checkCode(String phone, String code,String returnUrl) throws Exception {
        ResultModel resultModel = new ResultModel();
        if (!SysRegex.IsValidMobileNo(phone)) {
            resultModel.setCode(404);
            resultModel.setMessage("手机格式不正确!");
            return resultModel;
        }

        if (userService.findByMobile(phone) != null) {
            resultModel.setCode(404);
            resultModel.setMessage("手机已经被注册！");
            return resultModel;
        }
        // 不合法的验证码
        if (!SysRegex.IsValidNum(code)) {
            resultModel.setCode(404);
            resultModel.setMessage("验证码错误");
            return resultModel;
        }
        Date date = new Date();
        if (!verificationService.verifyCode(phone, VerificationService.VerificationProject.fanmore, code, date, EnumHelper.getEnumType(VerificationType.class, 1))) {
            resultModel.setCode(404);
            resultModel.setMessage("验证码错误");
            return resultModel;
        }

        WebPublicModel parameters = PublicParameterHolder.getParameters();
        User currentUser = parameters.getCurrentUser();
        User user = userRepository.findOne(currentUser.getId());
        user.setMobile(phone);
        user.setMobileBinded(true);
        userRepository.save(user);

        resultModel.setCode(200);
        resultModel.setUrl(returnUrl.replaceAll("@", "&"));
        resultModel.setMessage("验证成功，跳转中！");
        return resultModel;
    }

}
