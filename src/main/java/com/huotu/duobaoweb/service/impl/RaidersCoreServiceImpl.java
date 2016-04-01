package com.huotu.duobaoweb.service.impl;

import com.huotu.common.base.DateHelper;
import com.huotu.common.base.HttpHelper;
import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.common.LotteryCode;
import com.huotu.duobaoweb.common.SysRegex;
import com.huotu.duobaoweb.entity.*;
import com.huotu.duobaoweb.entity.Goods;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.exceptions.CrabLotteryCodeRepeatException;
import com.huotu.duobaoweb.exceptions.InterrelatedException;
import com.huotu.duobaoweb.exceptions.LotteryCodeError;
import com.huotu.duobaoweb.repository.*;
import com.huotu.duobaoweb.service.CacheService;
import com.huotu.duobaoweb.service.RaidersCoreService;
import com.huotu.duobaoweb.service.UserNumberService;
import com.huotu.huobanplus.common.entity.*;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * 夺宝核心服务代码
 * Created by lgh on 2016/1/25.
 */
@Service
//@SuppressWarnings({"unchecked", "SpringJavaAutowiringInspection"})
public class RaidersCoreServiceImpl implements RaidersCoreService {

    private static Log log = LogFactory.getLog(RaidersCoreServiceImpl.class);

    @Autowired
    private Environment env;


    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private CachedIssueLeaveNumberRepository cachedIssueLeaveNumberRepository;

    @Autowired
    private UserNumberRepository userNumberRepository;

    @Autowired
    private UserNumberService userNumberService;

    @Autowired
    private CountResultRepository countResultRepository;


    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;


    @Autowired
    private GoodsRestRepository goodsRestRepository;

    @Autowired
    private CacheService cacheService;


    /**
     * 产生新期号
     * 触发条件：后台上架商品或用户购买某期商品成功达到总需人次后,后台商品数量从0到有的情况且商品为上架状态。
     * 处理：创建期号数据 并更新商品的最新期号 创建该期的抽奖号码（放入缓存）
     *
     * @param goods 商品
     * @return 期号
     */
    @Transactional
    public Issue generateIssue(Goods goods) throws IOException {

        //改变上期的状态
        Issue issue = goods.getIssue();
        if (issue != null) {
            issue.setStatus(CommonEnum.IssueStatus.drawing);
            issueRepository.save(issue);
        }

        com.huotu.huobanplus.common.entity.Goods mallGoods = goodsRestRepository.getOneByPK(goods.getToMallGoodsId());


        if (goods.getStatus().equals(CommonEnum.GoodsStatus.up) && (mallGoods.getStock() == -1 || mallGoods.getStock() > 1)) {

            //处理下期的情况
            Issue nextIssue = new Issue();
            nextIssue.setGoods(goods);
            nextIssue.setStepAmount(goods.getStepAmount());
            nextIssue.setDefaultAmount(goods.getDefaultAmount());
            nextIssue.setToAmount(goods.getToAmount());
            nextIssue.setBuyAmount(0L);
            nextIssue.setPricePercentAmount(goods.getPricePercentAmount());
            nextIssue.setStatus(CommonEnum.IssueStatus.going);

            nextIssue.setAttendAmount(0L);
            nextIssue = issueRepository.saveAndFlush(nextIssue);

            goods.setIssue(nextIssue);

            //goods.setStock(goods.getStock() - 1);
            goodsRepository.save(goods);

            mallGoods.setStock(mallGoods.getStock()-1);

            //创建抽奖号码
            List<CachedIssueLeaveNumber> cachedIssueLeaveNumberList = new ArrayList<>();
            List<Long> list = new ArrayList<>();
            Long toAmount = nextIssue.getToAmount();
            Long i = 0L;
            while (i < toAmount) {
                list.add(10000001 + i);
                cachedIssueLeaveNumberList.add(new CachedIssueLeaveNumber(nextIssue.getId(), 10000001 + i));
                i++;
            }
            //存入数据库
            cachedIssueLeaveNumberRepository.save(cachedIssueLeaveNumberList);

            //存入缓存
            cacheService.setLotteryNumber(nextIssue.getId(), list);

            return nextIssue;
        }
        return null;
    }


    /**
     * 创建用户抽奖号码
     * 触发条件：用户进行夺宝支付成功后
     * 从缓存的系统中抽取剩余抽奖号码
     * 注意：此数据需要同步处理
     *
     * @param user   用户
     * @param issue  商品期号
     * @param amount 人次数
     * @param orders 订单
     */
    @Transactional
    public synchronized boolean generateUserNumber(User user, Issue issue, Long amount, Orders orders) {
        if (amount <= 0) return false;

        //取出缓存中剩余的号码
        List<Long> list = cacheService.getLotteryNumber(issue.getId());
        if (list == null) {
            list = cachedIssueLeaveNumberRepository.findAllByIssueId(issue.getId());
        }

        if (list == null || list.size() < amount) return false;

        log.info("issue " + issue.getId() + " cached " + list.size());
        log.info("issue " + issue.getId() + " cached db " + cachedIssueLeaveNumberRepository.findAllByIssueId(issue.getId()).size());

        /**
         * 获取相应随机序号
         */
        HashSet<Integer> hashSet = new HashSet<>();
        if (list.size() == amount) {
            for (int i = 0; i < amount; i++)
                hashSet.add(i);
        } else {
            getRandomNumber(0, list.size() - 1, amount, amount, hashSet);
        }


        log.info("user " + user.getId() + " issue " + issue.getId() + " draw number size:" + hashSet.size() + " to number size:" + amount);


        /**
         * 获得相应的随机号码
         */
        List<Long> listNumbers = new ArrayList<>();
        for (Integer p : hashSet) {
            listNumbers.add(list.get(p));
        }

        /**
         * 相应的业务逻辑处理
         */
        //1.缓存中取出抽奖号码,并同步到数据库
        list.removeAll(listNumbers);
        cacheService.setLotteryNumber(issue.getId(), list);


        List<CachedIssueLeaveNumber> cachedIssueLeaveNumbers = new ArrayList<>();
        for (Long number : listNumbers) {
            cachedIssueLeaveNumbers.add(new CachedIssueLeaveNumber(issue.getId(), number));
        }
        cachedIssueLeaveNumberRepository.delete(cachedIssueLeaveNumbers);

        log.info("issue " + issue.getId() + " cached " + list.size());
        log.info("issue " + issue.getId() + " cached db " + cachedIssueLeaveNumberRepository.findAllByIssueId(issue.getId()).size());

        //2.创建用户抽奖号码
        List<UserNumber> userNumbers = new ArrayList<>();
        for (Long number : listNumbers) {
            Date date = new Date();
            userNumbers.add(new UserNumber(user, issue, number, date.getTime(), orders));
        }
        userNumberRepository.save(userNumbers);

        log.info("issue " + issue.getId() + " db " + userNumberRepository.findByIssue(issue).size());

//        /**
//         */
//        Iterator<Long> iterator = list.iterator();
//        while (iterator.hasNext()) {
//             iterator.next();
//        }
        return true;
    }

    /**
     * 定期抽奖
     * 网易彩期在每天白天10点至22点，夜场22点至凌晨2点开售；
     * 白天10分钟一期，夜场5分钟一期； 每日期数：白天72期，夜场48期，共120期
     * 0.抽奖时间确定(正常时候晚1分钟)
     * 1.获取待抽奖的期号
     * 2.根据抽奖算法计算中奖号码
     * 3.更新该期的相关数据
     */
    @Scheduled(cron = "0 4,14,24,34,44,54 10,11,12,13,14,15,16,17,18,19,20,21 * * *")
    @Scheduled(cron = "0 4,9,14,19,24,29,34,39,44,49,54,59 22,23,0,1 * * *")
    @Transactional
    public void drawLottery() throws IOException, LotteryCodeError
            , NoSuchMethodException, InterrelatedException, CrabLotteryCodeRepeatException {
        log.info("begin draw a lottery");
        Date lotteryTime = new Date();

        String lotteryNo = createLotteryNo(lotteryTime);

        LotteryCode lotteryCode = new LotteryCode();
        lotteryCode.setNo(lotteryNo);
        //最近一期中国福利彩票“老时时彩”的开奖结果
        lotteryCode.setNumber(getLotteryNumber(lotteryNo));


        CountResult countResult = countResultRepository.findOne(lotteryCode.getNo());
        if (countResult != null) {
//            String mobile = commonConfigService.getErrorPrividedMobile();
//            Random rnd = new Random();
//            String code = StringHelper.RandomNum(rnd, 4);
//            if (env.acceptsProfiles("prod")) {
//                verificationService.sendCode(mobile, VerificationService.VerificationProject.crab_error, code
//                        , new Date(), VerificationType.ERROR_REPORT, CodeType.text);
//            }
            throw new CrabLotteryCodeRepeatException(countResult.getIssueNo() + "count result repeated");
        }

//        if (!crabSuccess) {
//            String mobile = commonConfigService.getErrorPrividedMobile();
//            //发送消息 通知管理员进行后台处理
//            Random rnd = new Random();
//            String code = StringHelper.RandomNum(rnd, 4);
//            if (env.acceptsProfiles("prod")) {
//                verificationService.sendCode(mobile, VerificationService.VerificationProject.crab_error, code
//                        , new Date(), VerificationType.ERROR_REPORT, CodeType.text);
//            }
//            throw new LotteryCodeError("抓取异常");
//        }

        log.info("create a lottery no " + lotteryCode.getNo());
        log.info("create a lottery number " + lotteryCode.getNumber());

        drawLottery(lotteryCode, lotteryTime);
    }


    /**
     * 开奖期号
     *
     * @param lotteryTime
     * @return
     */
    public String createLotteryNo(Date lotteryTime) {
        DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String before = dateFormat.format(lotteryTime);
        String end = "000";

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lotteryTime);
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        int curMinute = calendar.get(Calendar.MINUTE);
        if ((curHour >= 0 && curHour < 2)) {
            //4,9,14,19,24,29,34,39,44,49,54,59
            Date fromDate = DateHelper.getThisDayBegin();
            end = Long.toString((calendar.getTime().getTime() - fromDate.getTime()) / (300 * 1000));
            //凌晨开始时前一天的最后
            if ("0".equals(end)) end = "120";
        } else if (curHour >= 10 && curHour < 22) {
            //4,14,24,34,44,54
            Calendar fromDate = Calendar.getInstance();
            fromDate.set(fromDate.HOUR_OF_DAY, 10);
            fromDate.set(fromDate.MINUTE, 0);
            fromDate.set(fromDate.SECOND, 0);
            fromDate.set(fromDate.MILLISECOND, 0);
            end = Long.toString((calendar.getTime().getTime() - fromDate.getTime().getTime()) / (600 * 1000) + 24);

        } else if ((curHour >= 22 && curHour < 24)) {
            //4,9,14,19,24,29,34,39,44,49,54,59
            Calendar fromDate = Calendar.getInstance();
            fromDate.set(fromDate.HOUR_OF_DAY, 22);
            fromDate.set(fromDate.MINUTE, 0);
            fromDate.set(fromDate.SECOND, 0);
            fromDate.set(fromDate.MILLISECOND, 0);
            end = Long.toString((calendar.getTime().getTime() - fromDate.getTime().getTime()) / (300 * 1000) + 96);
        }
        log.info("lottery no " + end);

        if (end.length() == 1) end = "00" + end;
        else if (end.length() == 2) end = "0" + end;
        //按照前一天来计算
        if (end.equals("120")) {
            before = dateFormat.format(DateHelper.getYesterdayBegin());
        }
        return before + end;
    }


    /**
     * 进行抽奖
     * 1.中奖后的计算结果
     * 2.对每期进行抽奖，确定最后中奖结果
     *
     * @param lotteryCode 开奖号码
     * @param lotteryTime 抽奖时间
     */
    private void drawLottery(LotteryCode lotteryCode, Date lotteryTime) {

        log.info("begin generate coountResult");

        Long numberB = Long.parseLong(lotteryCode.getNumber());

        //截止该商品中奖时间点前最后50条全站参与记录 时间拼成数值
        List<UserNumber> userNumbers = userNumberService.getLotteryBeforeTop50(lotteryTime.getTime());
//        Long numberA = userNumbers.stream().mapToLong(x -> x.getNumber()).sum();
        Long numberA = 0L;
        if (userNumbers != null) {
            for (UserNumber userNumber : userNumbers) {
                numberA += countNumberA(new Date(userNumber.getTime()));
            }
        }

        //获取要开奖的期号
        List<Issue> issues = issueRepository.findAllByStatus(CommonEnum.IssueStatus.drawing);

        log.info("want to lottery issue amount is " + issues.size());
        log.info("numberA = " + numberA);

        CountResult countResult = new CountResult();
        countResult.setIssueNo(lotteryCode.getNo());
        countResult.setNumberA(numberA);
        countResult.setNumberB(numberB);
        countResult.setUserNumbers(userNumbers);
        countResult.setIssueAmount(issues.size());
        countResultRepository.save(countResult);

        for (Issue issue : issues) {
            drawIssueLottery(issue, numberA, numberB, countResult);
        }
    }

    private Long countNumberA(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return Long.valueOf(calendar.get(Calendar.HOUR_OF_DAY) * 10000000
                + calendar.get(Calendar.MINUTE) * 100000
                + calendar.get(Calendar.SECOND) * 1000
                + calendar.get(Calendar.MILLISECOND));
    }

    private void drawIssueLottery(Issue issue, Long numberA, Long numberB, CountResult countResult) {
        issue.setCountResult(countResult);
        issueRepository.save(issue);

        //计算中奖号码
        Long luckyNumber = (numberA + numberB) % issue.getToAmount() + 10000001;
        log.info(luckyNumber);
        User user = null;

        UserNumber userNumber = userNumberRepository.findByIssueAndNumber(issue, luckyNumber);
        if (userNumber != null) user = userNumber.getUser();

        issue.setAwardingUser(user);
        issue.setLuckyNumber(luckyNumber);
        issue.setAwardingDate(new Date());

        issue.setStatus(CommonEnum.IssueStatus.drawed);
//        issue.setCountResult(countResult);
        issueRepository.save(issue);

        Delivery delivery = new Delivery();
        delivery.setDeliveryStatus(CommonEnum.DeliveryStatus.GetPrize);
        delivery.setIssue(issue);
        delivery.setUser(user);
        deliveryRepository.save(delivery);


//        if (user != null) {
//            try {
//                //发送中奖消息
//                Message message = new Message();
//                //马上发送 7天以后过期
//                message.setInvalidTime(new Date(System.currentTimeMillis() + 7 * 24 * 60 * 60 * 1000));
//                message.setSendTime(new Date());
//                message.setAddedTime(new Date());
//                message.setTitle("恭喜，中奖了");
//                message.setContent("恭喜中奖,期号：" + delivery.getIssue().getId() + "\n中奖物品:" + delivery.getIssue().getGoods().getTitle());
//                messageRepository.save(message);
//                if (user.getDevice() != null) {
//
//                    log.info("start push lottery message");
//                    List<User> users = new ArrayList<>();
//                    users.add(user);
//                    messageService.sendMessageToUser(message, users);
//
//                    PushingMessage pushingMessage = new PushingMessage();
//                    pushingMessage.setUsername(user.getUsername());
//                    pushingMessage.setData("恭喜中奖,期号：" + delivery.getIssue().getId() + "\n中奖物品:" + delivery.getIssue().getGoods().getTitle());
//                    Set<String> tokens = new HashSet<>();
//
//                    tokens.add(user.getDevice().getPushingToken());
//                    if (user.getDevice().getCpa() != null)
//                        pushingMessage.setOs(user.getDevice().getCpa().getOs());
//
//                    pushingMessage.setDeviceTokens(tokens);
//                    pushingMessage.setTitle("恭喜，中奖了");
//                    pushingMessage.setType(CommonEnum.PushMessageType.Notify);
//                    messageService.pushMessage(pushingMessage);
//                }
//            } catch (Exception ex) {
//                log.info("中奖发消息失败");
//            }
//
//        }
    }

    /**
     * 获得重庆时时彩号码
     *
     * @param lotteryNo 开奖期号
     * @return
     * @throws IOException
     */
    public String getLotteryNumber(String lotteryNo) throws IOException {
        //网易的编号
        String no = lotteryNo.substring(8);

        TreeMap<String, String> map = new TreeMap<String, String>();
        String webUrl = getCaipiaoWebUrl();
        String html = HttpHelper.getRequest(webUrl);
//        URL url = new URL(webUrl);
        Source source = new Source(html);
        log.info(source.getEncoding());
        List<Element> listTR = source.getAllElements(HTMLElementName.TR);
        for (Element element : listTR) {
            List<Element> listTD = element.getAllElements(HTMLElementName.TD);
            if (listTD.size() >= 2) {
                String td0 = listTD.get(0).getContent().toString();
                String td1 = listTD.get(1).getContent().toString().replace(" ", "");
                String td7 = listTD.get(7).getContent().toString();
                String td8 = listTD.get(8).getContent().toString().replace(" ", "");
                String td14 = listTD.get(14).getContent().toString();
                String td15 = listTD.get(15).getContent().toString().replace(" ", "");

                if (SysRegex.IsValidInt(td1)) map.put(td0, td1);
                if (SysRegex.IsValidInt(td8)) map.put(td7, td8);
                if (SysRegex.IsValidInt(td15)) map.put(td14, td15);
            }
        }

        String number = map.get(no);
        if (number != null) return number;

        //去不到直接返回00000;
        return "00000";
    }

    /**
     * 获得彩票的请求地址
     *
     * @return
     */
    private String getCaipiaoWebUrl() {
        String webUrl = "http://caipiao.163.com/award/cqssc/";
        Calendar calendar = Calendar.getInstance();
        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        int curMinute = calendar.get(Calendar.MINUTE);
        if ((curHour == 0 && curMinute == 4)) {
            Date yesterdayBegin = DateHelper.getYesterdayBegin();
            DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            webUrl = "http://caipiao.163.com/award/cqssc/" + dateFormat.format(yesterdayBegin) + ".html";
        }
        return webUrl;
    }


    /**
     * 随机指定范围内N个不重复的数
     * 利用HashSet的特征，只能存放不同的值
     *
     * @param min     指定范围最小值
     * @param max     指定范围最大值
     * @param n       随机数个数
     * @param m       还需要的个数
     * @param hashSet 随机记录集合
     */

    private void getRandomNumber(int min, int max, Long n, Long m, HashSet<Integer> hashSet) {
        if (n > (max - min + 1) || max < min) {
            return;
        }
        for (int i = 0; i < m; i++) {
            // 调用Math.random()方法
            int num = (int) (Math.random() * (max - min)) + min;
            hashSet.add(num);// 将不同的数存入HashSet中
        }
        int setSize = hashSet.size();
        if (setSize < n) {
            getRandomNumber(min, max, n, n - setSize, hashSet);// 递归
        }
    }


    /**
     * 创建订单号订单号
     *
     * @param date   当前时间
     * @param userId 用户Id
     * @return
     */
    public String createOrderNo(Date date, Long userId) {
        //订单号
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        return simpleDateFormat.format(date) + userId.toString();
    }


    /**
     * 获得距离开奖的时间（秒）
     *
     * @return
     */
    public Long getAwardingTime() {
        Calendar calendar = Calendar.getInstance();
        Calendar awardingCalendar = Calendar.getInstance();
        return getAwardingTime(calendar, awardingCalendar);
    }


    /**
     * 获得距离开奖的时间（秒）
     *
     * @param calendar         当前的时间
     * @param awardingCalendar 用于设置开奖的时间
     * @return 时间值
     */
    @Override
    public Long getAwardingTime(Calendar calendar, Calendar awardingCalendar) {
        Long result;
        Integer delayTime = 5;

        int curHour = calendar.get(Calendar.HOUR_OF_DAY);
        int curMinute = calendar.get(Calendar.MINUTE);
        if (curHour >= 10 && curHour < 22) {
            //4,14,24,34,44,54
            Calendar toCalendar = awardingCalendar;
            if (curMinute < 4) {
                toCalendar.set(Calendar.MINUTE, 4);
            } else if (curMinute < 14) {
                toCalendar.set(Calendar.MINUTE, 14);
            } else if (curMinute < 24) {
                toCalendar.set(Calendar.MINUTE, 24);
            } else if (curMinute < 34) {
                toCalendar.set(Calendar.MINUTE, 34);
            } else if (curMinute < 44) {
                toCalendar.set(Calendar.MINUTE, 44);
            } else if (curMinute < 54) {
                toCalendar.set(Calendar.MINUTE, 54);
            }
            toCalendar.set(Calendar.SECOND, delayTime);//延迟10秒
            toCalendar.set(Calendar.MILLISECOND, 0);
            result = (toCalendar.getTime().getTime() - calendar.getTime().getTime());
        } else if ((curHour >= 22 && curHour < 24) || (curHour >= 0 && curHour < 2)) {
            //4,9,14,19,24,29,34,39,44,49,54,59
            Calendar toCalendar = awardingCalendar;
            if (curMinute < 4) {
                toCalendar.set(Calendar.MINUTE, 4);
            } else if (curMinute < 9) {
                toCalendar.set(Calendar.MINUTE, 9);
            } else if (curMinute < 14) {
                toCalendar.set(Calendar.MINUTE, 14);
            } else if (curMinute < 19) {
                toCalendar.set(Calendar.MINUTE, 19);
            } else if (curMinute < 24) {
                toCalendar.set(Calendar.MINUTE, 24);
            } else if (curMinute < 29) {
                toCalendar.set(Calendar.MINUTE, 29);
            } else if (curMinute < 34) {
                toCalendar.set(Calendar.MINUTE, 34);
            } else if (curMinute < 39) {
                toCalendar.set(Calendar.MINUTE, 39);
            } else if (curMinute < 44) {
                toCalendar.set(Calendar.MINUTE, 44);
            } else if (curMinute < 49) {
                toCalendar.set(Calendar.MINUTE, 49);
            } else if (curMinute < 54) {
                toCalendar.set(Calendar.MINUTE, 54);
            } else if (curMinute < 59) {
                toCalendar.set(Calendar.MINUTE, 59);
            }

            toCalendar.set(Calendar.SECOND, delayTime);//延迟10秒
            toCalendar.set(Calendar.MILLISECOND, 0);
            result = (toCalendar.getTime().getTime() - calendar.getTime().getTime());
        } else {
            Calendar toCalendar = awardingCalendar;
            toCalendar.set(Calendar.HOUR_OF_DAY, 10);
            toCalendar.set(Calendar.MINUTE, 4);
            toCalendar.set(Calendar.SECOND, delayTime);//延迟10秒
            toCalendar.set(Calendar.MILLISECOND, 0);
            result = (toCalendar.getTime().getTime() - calendar.getTime().getTime());
        }
        return result / 1000;
    }

}
