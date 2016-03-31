package com.huotu.duobaoweb.service;

import java.net.URISyntaxException;
import java.util.Map;

/**
 * Created by zhang on 2016/3/25.
 */
public interface GoodsService {

    /**
     * 跳转到商品活动首页
     * @param goodsId  商品Id
     * @param map
     * @throws Exception
     */
    void jumpToGoodsActivityIndex(Long goodsId, Map<String, Object> map) throws Exception;

    /**
     * 通过商品Id跳转到商品详情
     * @param goodsId
     * @param map
     */
    void jumpToGoodsActivityDetailByGoodsId(Long goodsId, Map<String, Object> map) throws Exception;

    /**
     * 通过期号跳转到商品详情
     * @param issueId
     * @param map
     * @throws Exception
     */
    void jumpToGoodsActivityDetailByIssueId(Long issueId, Map<String, Object> map) throws Exception;

    /**
     * 获取某一期的参与记录
     * @param issueId
     * @param lastId
     * @param map
     */
    void getBuyListByIssueId(Long issueId, Long lastId, Map<String, Object> map) throws Exception;

    /**
     * 计算详情
     * @param issueId
     * @param map
     */
    void getCountResultByIssueId(Long issueId, Map<String, Object> map) throws Exception;
}
