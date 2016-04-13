package com.huotu.mallduobao.service.impl;

import com.huotu.mallduobao.cache.CacheHelper;
import com.huotu.mallduobao.service.CacheService;
import org.springframework.stereotype.Service;


import java.util.List;

/**
 * Created by admin on 2016/3/10.
 */
@Service
public class CacheServiceImpl implements CacheService {



    /**
     * 缓存的抽奖号码
     * 用于快速生成用户的抽奖号码
     */
    private String cacheLotteryNumberKey = "LotteryNumber_";


    @Override
    public List<Long> getLotteryNumber(Long issueId) {
        Object cacheLotteryNumbers = CacheHelper.get(cacheLotteryNumberKey + issueId);
        if (cacheLotteryNumbers != null) return (List<Long>) cacheLotteryNumbers;
        return null;
    }

    @Override
    public void setLotteryNumber(Long issueId, List<Long> list) {
        //存入缓存
        CacheHelper.set(cacheLotteryNumberKey + issueId, list);
    }


}
