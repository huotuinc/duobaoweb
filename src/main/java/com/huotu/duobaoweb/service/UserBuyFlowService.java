package com.huotu.duobaoweb.service;

import com.huotu.duobaoweb.entity.UserBuyFlow;
import com.huotu.duobaoweb.model.*;
import org.springframework.data.domain.Page;

import java.net.URISyntaxException;
import java.util.List;

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
     * 参与记录，获取某期的参与记录列表
     * @param issueId  期号id
     * @param lastId 分页最后一条id、
     * @return AppBuyListModel[]
     * @throws Exception
     */
    BuyListModel[] findByIssuIdList(Long issueId, Long lastId) throws Exception;


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


    Page<UserBuyFlow> getMyInvolvedRecordAjax(Long userId, Integer type, Integer page, Integer pageSize);

    RaiderListModelAjax toListRaiderListModel(Integer type,Long userId, Integer pageSize, Integer page) throws URISyntaxException;
}
