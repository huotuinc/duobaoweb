package com.huotu.duobaoweb.controller;

import com.huotu.common.base.CookieHelper;
import com.huotu.duobaoweb.repository.GoodsRepository;
import com.huotu.duobaoweb.service.GoodsService;
import com.huotu.duobaoweb.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhang on 2016/3/25.
 */
@RequestMapping("/goods")
@Controller
public class GoodsController {

    private static Log log = LogFactory.getLog(GoodsController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private GoodsRepository goodsRepository;

    /**
     * 跳转到商品活动的首页
     *
     * @param goodsId
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping("/index")
    public String jumpToGoodsActivityIndex(Long goodsId, Map<String, Object> map) throws Exception {
        goodsService.jumpToGoodsActivityIndex(goodsId, map);
        return "/html/goods/index";
    }


    /**
     * 通过商品Id跳转到商品详情
     *
     * @param goodsId
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping("/detailByGoodsId")
    public String jumpToGoodsActivityDetailByGoodsId(Long goodsId, Map<String, Object> map) throws Exception {
        goodsService.jumpToGoodsActivityDetailByGoodsId(goodsId, map);
        return "/html/goods/detail";
    }

    /**
     * 通过期号跳转到商品详情
     *
     * @param issueId
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping("/detailByIssueId")
    public String jumpToGoodsActivityDetailByIssueId(Long issueId, Map<String, Object> map) throws Exception {
        goodsService.jumpToGoodsActivityDetailByIssueId(issueId, map);
        return "/html/goods/detail";
    }

    /**
     * 跳转到商品图文详情
     *
     * @param goodsId
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping("/imageTextDetail")
    public String jumpToImageTextDetail(Long goodsId, Map<String, Object> map) throws Exception {
        goodsService.jumpToImageTextDetail(goodsId, map);
        return "/html/goods/imageTextDetail";
    }

    /**
     * 获取某一期的参与记录
     *
     * @param issueId 期号
     * @param lastId  排序依据(购买时间)
     * @return
     * @throws Exception
     */
    @RequestMapping("/getBuyListByIssueId")
    @ResponseBody
    public Map<String, Object> getBuyListByIssueId(Long issueId, Long lastId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        goodsService.getBuyListByIssueId(issueId, lastId, map);
        return map;
    }


    /**
     * 获取计算详情
     *
     * @param issueId
     * @param map
     * @return
     * @throws Exception
     */
    @RequestMapping("/getCountResultByIssueId")
    public String getCountResultByIssueId(Long issueId, Map<String, Object> map) throws Exception {
        goodsService.getCountResultByIssueId(issueId, map);
        return "/html/goods/countResult";
    }

    @RequestMapping("/default")
    public String getDefault(HttpServletResponse response, HttpServletRequest request) throws Exception {
        Object theValue = request.getSession(true).getAttribute("totest");
        log.info("curent The Value "+theValue);
        if (theValue==null){
            request.getSession(true).setAttribute("totest",System.currentTimeMillis());
            log.info("update curent The Value ");
        }
        CookieHelper.set(response, "test", "1", request.getLocalName(), request.getContextPath(), 1000 * 60);
        return "/html/default";
    }


}
