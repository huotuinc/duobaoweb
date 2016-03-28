package com.huotu.duobaoweb.service;


import java.util.List;

/**
 * Created by admin on 2016/3/10.
 */
public interface CacheService {



    /**
     * 获取中奖号码
     *
     * @param issueId
     * @return
     */
    List<Long> getLotteryNumber(Long issueId);

    /**
     * 缓存中奖号码
     *
     * @param issueId
     * @param list
     */
    void setLotteryNumber(Long issueId, List<Long> list);
}
