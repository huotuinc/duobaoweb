package com.huotu.mallduobao.controller;

import com.huotu.mallduobao.entity.Goods;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.model.ApiResultModel;
import com.huotu.mallduobao.repository.GoodsRepository;
import com.huotu.mallduobao.service.CommonConfigService;
import com.huotu.mallduobao.service.RaidersCoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;

/**
 * 对外接口
 * Created by lgh on 2016/4/7.
 */
@Controller
@RequestMapping("/api")
public class ApiController {

    @Autowired
    private RaidersCoreService raidersCoreService;

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private CommonConfigService commonConfigService;

    /**
     * 产生新期号
     * 触发条件：后台上架商品或用户购买某期商品成功达到总需人次后,后台商品数量从0到有的情况且商品为上架状态。
     * 处理：创建期号数据 并更新商品的最新期号 创建该期的抽奖号码（放入缓存）
     *
     * @param goodsId 商品id
     * @param sign    签名 md5(goodsId + key) 值为f7b88579e3b948bf8658d103329dd75d
     * @return
     */
    @RequestMapping(value = "/generateIssue", method = RequestMethod.GET)
    @ResponseBody
    public ApiResultModel generateIssue(Long goodsId, String sign) throws IOException {
        ApiResultModel apiResultModel = new ApiResultModel();
        String toSign = DigestUtils.md5DigestAsHex((goodsId.toString() + commonConfigService.getDuobaoApiKey()).getBytes());
        if (toSign.equals(sign)) {
            Goods goods = goodsRepository.getOne(goodsId);
            Issue issue = raidersCoreService.generateIssue(goods);
            if (issue != null) {
                apiResultModel.setCode("1");
                apiResultModel.setMessage("成功");
                apiResultModel.setData(issue.getId());
            }
            else
            {
                apiResultModel.setCode("1002");
                apiResultModel.setMessage("生成期号失败");
            }

        } else {
            apiResultModel.setCode("1001");
            apiResultModel.setMessage("签名失败");
        }
        return apiResultModel;
    }

}
