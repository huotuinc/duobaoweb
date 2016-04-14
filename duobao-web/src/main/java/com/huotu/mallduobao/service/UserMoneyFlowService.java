package com.huotu.mallduobao.service;

import com.huotu.mallduobao.entity.UserMoneyFlow;

import java.util.List;

/**
 * Created by xhk on 2016/4/14.
 */
public interface UserMoneyFlowService {

    /**
     * 通过用户查找支付流水
     * @param userId
     * @return
     */
    List<UserMoneyFlow> getUserMoneyFlowByUserId(Long userId);

    /**
     * 通过用户查找对应时间前到现在的流水 second单位是毫秒
     * @param userId
     * @param seconds
     * @return
     */
    List<UserMoneyFlow> getUserMoneyFlowByUserIdAndBeforeTime(Long userId,Long seconds);
}
