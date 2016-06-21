package com.huotu.mallduobao.controller.admin;

import com.huotu.huobanplus.sdk.mall.annotation.CustomerId;
import com.huotu.mallduobao.model.ResultModel;
import com.huotu.mallduobao.model.admin.DuoBaoGoodsInputModel;
import com.huotu.mallduobao.model.admin.DuoBaoGoodsSearchModel;
import com.huotu.mallduobao.model.admin.MallGoodsSearchModel;
import com.huotu.mallduobao.service.CommonConfigService;
import com.huotu.mallduobao.service.GoodsService;
import com.huotu.mallduobao.service.StaticResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by zhang on 2016/3/31.
 */
@RequestMapping("/admin")
@Controller
public class DuoBaoGoodsController {

    //常用的图片格式
    private static final String[] PIC_EXT = {"BMP", "JPG", "JPEG", "PNG", "GIF"};
    private static final String DOT = ".";

    @Autowired
    private GoodsService duoBaoGoodsService;

    @Autowired
    private StaticResourceService staticResourceService;

    @Autowired
    private CommonConfigService commonConfigService;

    @Autowired
    private Environment env;

    /**
     * 获取夺宝活动商品列表
     * @param duoBaoGoodsSearchModel
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getDuoBaoGoodsList", method = RequestMethod.GET)
    public String getDuoBaoGoodsList(@CustomerId Long customerId, DuoBaoGoodsSearchModel duoBaoGoodsSearchModel, Map<String, Object> map) throws  Exception{

        if(env.acceptsProfiles("development")){
            customerId = 3447L;
        }


        if(customerId == null){
           map.put("message", "customerId不存在");
           return "/admin/error";
        }

        Integer pageNoStr = duoBaoGoodsSearchModel.getPageNoStr();
        if (pageNoStr <= 0) {
            duoBaoGoodsSearchModel.setPageNoStr(0);
        } else {
            duoBaoGoodsSearchModel.setPageNoStr(pageNoStr - 1);
        }

        duoBaoGoodsService.getDuoBaoGoodsList(duoBaoGoodsSearchModel, customerId, map);
        map.put("customerId", customerId);
        return "/admin/goods/goodsList";
    }

    /**
     * 获取参与活动的商城商品列表
     * @param mallGoodsSearchModel
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/getMallGoodsList", method = RequestMethod.GET)
    public String getMallGoodsList(@CustomerId Long customerId, MallGoodsSearchModel mallGoodsSearchModel, Map<String, Object> map) throws  Exception{

        if(env.acceptsProfiles("development")){
            customerId = 3447L;
        }


        if(customerId == null){
            map.put("message", "customerId不存在");
            return "/admin/error";
        }

        Integer pageNoStr = mallGoodsSearchModel.getPageNoStr();
        if (pageNoStr <= 0) {
            mallGoodsSearchModel.setPageNoStr(0);
        } else {
            mallGoodsSearchModel.setPageNoStr(pageNoStr - 1);
        }
        duoBaoGoodsService.getMallGoodsList(mallGoodsSearchModel, customerId, map);
        return "/admin/goods/mallGoodsList";
    }

    /**
     * 跳转到商品活动添加页面前期准备
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/jumpToAddDuoBaoGoods", method = RequestMethod.GET)
    public String jumpToAddDuoBaoGoods(@CustomerId Long customerId, Map<String, Object> map) throws Exception{

        if(env.acceptsProfiles("development")){
            customerId = 3447L;
        }


        if(customerId == null){
            map.put("message", "customerId不存在");
            return "/admin/error";
        }

        duoBaoGoodsService.jumpToAddDuoBaoGoods(map);
        map.put("customerId", customerId);
        return "/admin/goods/goodsInput";
    }

    /**
     * 保存商品活动
     * @param duoBaoGoodsInputModel
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/saveDuoBaoGoods")
    public String saveDuoBaoGoods(@CustomerId Long customerId,DuoBaoGoodsInputModel duoBaoGoodsInputModel, Map<String, Object> map) throws Exception {


        if(env.acceptsProfiles("development")){
            customerId = 3447L;
        }


        if(customerId == null){
            map.put("message", "customerId不存在");
            return "/admin/error";
        }

        duoBaoGoodsService.saveDuoBaoGoods(duoBaoGoodsInputModel);
        return "redirect:/admin/getDuoBaoGoodsList";
    }

    /**
     * 跳转到商品活动更新界面前期准备
     * @param goodsId
     * @param map
     * @return
     * @throws Exception
     */
   @RequestMapping(value = "/jumpToUpdateBaoGoods", method = RequestMethod.GET)
   public String jumpToUpdateBaoGoods(@CustomerId Long customerId,Long goodsId, Map<String, Object> map) throws Exception{


       if(env.acceptsProfiles("development")){
           customerId = 3447L;
       }


       if(customerId == null){
           map.put("message", "customerId不存在");
           return "/admin/error";
       }

       duoBaoGoodsService.jumpToUpdateBaoGoods(goodsId, map);
       map.put("customerId", customerId);
       return "/admin/goods/goodsInput";
   }


    /**
     *获取商品活动的详细信息
     * @param goodsId
     * @param map
     * @return
     * @throws Exception
     */
   @RequestMapping(value = "/getDuoBaoGoodsDatailInfo")
   public String getDuoBaoGoodsDatailInfo(Long goodsId, Map<String, Object> map) throws Exception{
       duoBaoGoodsService.getDuoBaoGoodsDatailInfo(goodsId, map);
      return "/admin/goods/goodsInfo";
   }


    /**
     * 异步更新商品状态
     *
     * @param goodsId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/ajaxUpdateGoodsStatus", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> ajaxUpdateGoodsStatus(Long goodsId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        duoBaoGoodsService.ajaxUpdateStatus(goodsId, map);
        return map;
    }


    /**
     * 商品图片上传
     *
     * @param shareImage
     * @param response
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/UploadGoodsImg")
    @ResponseBody
    private ResultModel uploadGoodsImg(@RequestParam(value = "shareImage") MultipartFile shareImage, HttpServletResponse response) throws Exception {
            ResultModel resultModel = new ResultModel();

            //文件格式判断
            if (ImageIO.read(shareImage.getInputStream()) == null) {
                resultModel.setCode(0);
                resultModel.setMessage("请上传图片文件！");
                return resultModel;
            }

            if (shareImage.getSize() == 0) {
                resultModel.setCode(0);
                resultModel.setMessage("请上传图片！");
                return resultModel;
            }

            //获取图片后缀名
            String fileName = shareImage.getOriginalFilename();
            String ext = fileName.substring(fileName.lastIndexOf(DOT) + 1).toUpperCase();
            Boolean flag = false;

            for (String s : PIC_EXT) {
                if (ext.equals(s)) {
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                resultModel.setCode(0);
                resultModel.setMessage("文件后缀名非图片后缀名");
                return resultModel;
            }

            //保存图片
            fileName = StaticResourceService.IMG + UUID.randomUUID().toString() + DOT + ext.toLowerCase();
            URI uri = staticResourceService.uploadResource(fileName, shareImage.getInputStream());
            resultModel.setCode(1);
            resultModel.setMessage(fileName);
            resultModel.setUrl(uri.toString());
            return resultModel;
    }


    /**
     * 获取分享地址
     * @param customerId
     * @param goodsId
     * @return
     */
    @RequestMapping("/getDuoBaoGoodsShareAddress")
    public String getDuoBaoGoodsShareAddress(@CustomerId Long customerId, Long goodsId, Model model){


        if(env.acceptsProfiles("development")){
            customerId = 3447L;
        }


        if(customerId == null){
            model.addAttribute("message", "customerId不存在");
            return "/admin/error";
        }
        String url = commonConfigService.getWebUrl() + "/goods/index?goodsId=" + goodsId + "&customerId=" + customerId;
        model.addAttribute("url", url);
        return "/admin/goods/goodsShareAddress";
    }

    @RequestMapping("/ajaxGetMallGoodsStock")
    @ResponseBody
    public Map<String, Object> ajaxGetMallGoodsStock(Long mallGoodsId) throws Exception{
        Map<String, Object> map = duoBaoGoodsService.getMallGoodsStock(mallGoodsId);
        return map;
    }



}
