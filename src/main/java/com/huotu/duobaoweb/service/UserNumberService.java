package com.huotu.duobaoweb.service;


import com.huotu.duobaoweb.model.RaiderNumbersModel;


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

}
