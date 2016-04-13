package com.huotu.duobaoweb.service;

import com.huotu.duobaoweb.entity.UserBuyFlow;
import com.huotu.duobaoweb.model.*;
import org.springframework.data.domain.Page;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * Created by lhx on 2016/2/2.
 */
public interface UserBuyFlowService {

    /**
     * 用户某期参与记录
     * 查找用户id,和某期的购买记录列表
     * @param userId 用户id
     * @param issueId 期号id
     * @return  List<UserBuyFlow>
     * @throws Exception
     */
    List<UserBuyFlow> findByUserIdAndIssuId(Long userId, Long issueId)throws  Exception;


    /**
     * 异步获取某一期的参与记录
     * @param issueId
     * @param page
     * @param pageSize
     * @return
     * @throws Exception
     */
    BuyListModelAjax ajaxFindBuyListByIssueId(Long issueId, Long page, Long pageSize) throws  Exception;


    /**
     * 中奖列表
     * @param userId
     * @return UserBuyFlowModelAjax[]
     */
    UserBuyFlowModelAjax findByUserAjax(Long userId,Integer pageSize, Integer page) throws  Exception;

    /**
     * 获取 某期夺宝中奖详细信息
     * @param issueId 期号
     * @return UserBuyFlow
     */
    UserBuyFlow findOne(Long issueId);

    /**
     * 我的参与列表
     * @param userId
     * @param type
     * @param page
     * @param pageSize
     * @return
     */
    Page<UserBuyFlow> getMyInvolvedRecordAjax(Long userId, Integer type, Integer page, Integer pageSize);


    RaiderListModelAjax toListRaiderListModel(Integer type,Long userId, Integer pageSize, Integer page) throws URISyntaxException;

    /**
     * 商城商品id
     * @param goodsId
     * @return
     */
    Map<String,Object> getGoodsSpec(Long goodsId) throws IOException;
}
