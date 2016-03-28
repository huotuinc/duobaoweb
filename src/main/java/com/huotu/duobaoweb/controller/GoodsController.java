package com.huotu.duobaoweb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;


import java.util.Map;

/**
 * Created by zhang on 2016/3/25.
 */
@RequestMapping("/goods")
@Controller
public class GoodsController {

    /**
     * 跳转到商品活动的首页
     * @param goodsId
     * @param map
     * @return
     * @throws Exception
     */
   @RequestMapping("/index")
   public String jumpToGoodsActivityIndex(@RequestParam("id")Long goodsId, Map<String, Object> map) throws Exception{
        return "/html/goods/index";
    }


    /**
     *跳转到商品详情
     * @param goodsId
     * @param map
     * @return
     * @throws Exception
     */
   @RequestMapping
   public String jumpToGoodsActivityDetail(@RequestParam("id")Long goodsId, Map<String, Object> map) throws Exception{
       return "/html/goods.detail";
   }



}
