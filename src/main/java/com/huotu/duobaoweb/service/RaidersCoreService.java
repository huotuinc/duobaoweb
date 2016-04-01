package com.huotu.duobaoweb.service;

import com.huotu.duobaoweb.entity.Goods;
import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.Orders;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.exceptions.CrabLotteryCodeRepeatException;
import com.huotu.duobaoweb.exceptions.InterrelatedException;
import com.huotu.duobaoweb.exceptions.LotteryCodeError;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * 夺宝核心服务
 * Created by lgh on 2016/1/25.
 */
public interface RaidersCoreService {
    @Transactional
    Issue generateIssue(Goods goods) throws IOException;

    @Transactional
    boolean generateUserNumber(User user, Issue issue, Long amount, Orders orders);

    /**
     * 获得重庆时时彩号码
     *
     * @param lotteryNo 开奖期号
     * @return
     * @throws IOException
     */
    String getLotteryNumber(String lotteryNo) throws IOException;



    /**
     * 创建订单号订单号
     *
     * @param date   当前时间
     * @param userId 用户Id
     * @return
     */
    String createOrderNo(Date date, Long userId);

    /**
     * 定期抽奖
     *
     * @throws IOException
     * @throws LotteryCodeError
     * @throws NoSuchMethodException
     * @throws InterrelatedException
     * @throws CrabLotteryCodeRepeatException
     */

    @Transactional
    void drawLottery() throws IOException, LotteryCodeError
            , NoSuchMethodException, InterrelatedException, CrabLotteryCodeRepeatException;

    /**
     * 创建抽奖号码
     *
     * @param lotteryTime
     * @return
     */
    String createLotteryNo(Date lotteryTime);



    /**
     * 获得距离开奖的时间（秒）
     *
     * @return
     */
    Long getAwardingTime();

    /**
     * 获得距离开奖的时间（秒）
     *
     * @param calendar         当前的时间
     * @param awardingCalendar 开奖时间
     * @return 时间值
     */
    Long getAwardingTime(Calendar calendar, Calendar awardingCalendar);


    /**
     * 处理用户购买失败
     */
    void doUserBuyFail();

}
