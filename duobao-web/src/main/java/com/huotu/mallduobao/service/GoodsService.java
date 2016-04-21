package com.huotu.mallduobao.service;

import com.huotu.mallduobao.entity.Goods;
import com.huotu.mallduobao.model.BuyListModelAjax;
import com.huotu.mallduobao.model.SelGoodsSpecModel;
import com.huotu.mallduobao.model.admin.DuoBaoGoodsInputModel;
import com.huotu.mallduobao.model.admin.DuoBaoGoodsSearchModel;
import com.huotu.mallduobao.model.admin.MallGoodsSearchModel;

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
     * 计算详情
     * @param issueId
     * @param map
     */
    void getCountResultByIssueId(Long issueId, Map<String, Object> map) throws Exception;

    /**
     * 跳转到图文详情页面
     * @param goodsId
     * @param map
     */
    void jumpToImageTextDetail(Long goodsId, Map<String, Object> map) throws  Exception;


    SelGoodsSpecModel getSelGoodsSpecModelByIssueId(Long issueId) throws Exception;

    /**
     * 异步获取参与记录
     * @param issueId
     * @param page
     * @param pageSize
     * @return
     * @throws Exception
     */
    BuyListModelAjax getBuyListByIssueId(Long issueId, Long lastFlag, Long page, Long pageSize) throws Exception;

    /**
     * 更新商品的参与人数
     * @param goods
     * @return
     */
    Goods upateGoodsAttendAmount(Goods goods);


    /**
     * 获取夺宝活动商品列表
     * @param duoBaoGoodsSearchModel
     * @param map
     */
    void getDuoBaoGoodsList(DuoBaoGoodsSearchModel duoBaoGoodsSearchModel, Long customerId, Map<String, Object> map) throws  Exception;

    /**
     * 获取商城活动商品列表
     * @param mallGoodsSearchModel
     * @param map
     * @throws Exception
     */
    void getMallGoodsList(MallGoodsSearchModel mallGoodsSearchModel, Long customerId, Map<String, Object> map) throws  Exception;

    /**
     * 跳转到活动商品新增页面前期准备
     * @param map
     */
    void jumpToAddDuoBaoGoods(Map<String, Object> map) throws Exception;

    /**
     * 保存商品
     * @param duoBaoGoodsInputModel
     * @throws Exception
     */
    void saveDuoBaoGoods(DuoBaoGoodsInputModel duoBaoGoodsInputModel) throws Exception;

    /**
     * 跳转到商品更新页面的前期准备
     * @param goodsId
     * @param map
     * @throws Exception
     */
    void jumpToUpdateBaoGoods(Long goodsId, Map<String, Object> map) throws Exception;

    /**
     * 获取指定商品活动的详细信息
     * @param goodsId
     * @param map
     * @throws Exception
     */
    void getDuoBaoGoodsDatailInfo(Long goodsId, Map<String, Object> map) throws Exception;

    /**
     * 异步更新商品状态
     * @param goodsId
     * @param map
     * @throws Exception
     */
    void ajaxUpdateStatus(Long goodsId, Map<String, Object> map) throws Exception;

}
