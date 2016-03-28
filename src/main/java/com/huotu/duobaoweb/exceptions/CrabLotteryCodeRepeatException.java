package com.huotu.duobaoweb.exceptions;

/**
 * 抓取中奖号码重复了
 * Created by lgh on 2016/2/2.
 */
public class CrabLotteryCodeRepeatException extends Exception {
    public CrabLotteryCodeRepeatException(String message) {
        super(message);
    }
}
