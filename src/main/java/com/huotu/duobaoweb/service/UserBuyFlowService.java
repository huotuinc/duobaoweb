package com.huotu.duobaoweb.service;

import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.entity.UserBuyFlow;
import com.huotu.duobaoweb.model.BuyListModel;
import com.huotu.duobaoweb.model.RaiderListModel;
import com.huotu.duobaoweb.model.UserBuyFlowModel;
import java.util.List;

/**
 * Created by lhx on 2016/2/2.
 */
public interface UserBuyFlowService {

    /**
     *  查找用户夺宝列表
     * @param userId 用户id
     * @param type 某期揭晓类型
     * @param lastTime 分页时间
     * @return RaiderListModel[]
     * @throws Exception
     */
    RaiderListModel[] findByUserIdAndType(Long userId, Integer type, Long lastTime)throws  Exception;

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
     * @param lastTime
     * @return AppUserBuyFlowModel[]
     */
    UserBuyFlowModel[] findByUser(Long userId, Long lastTime) throws  Exception;


    /**
     * 获取 某期夺宝中奖详细信息
     * @param issueId 期号
     * @return UserBuyFlow
     */
    UserBuyFlow findOne(Long issueId);


}
