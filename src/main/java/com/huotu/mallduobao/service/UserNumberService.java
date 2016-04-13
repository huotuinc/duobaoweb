package com.huotu.mallduobao.service;


import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.entity.UserNumber;
import com.huotu.mallduobao.model.RaiderNumbersModel;

import java.util.List;


/**
 * 用户号码
 * Created by lhx on 2016/1/25.
 */
public interface UserNumberService {
    /**
     * 获取某个用户某期的夺宝号码
     * @param userId 用户id
     * @param issueId 期号id
     * @return RaiderNumbersModel
     * @throws Exception
     */
    RaiderNumbersModel getMyRaiderNumbers(Long userId, Long issueId) throws Exception;


    /**
     * 全站前50参与记录
     * @param date 日期
     * @return
     */
    List<UserNumber> getLotteryBeforeTop50(Long date);

    /**
     * 找到对应期号的所有号码
     * @param user
     * @param issue
     * @return
     */
    List<Long> getUserNumbersByUserAndIssue(User user, Issue issue);
}
