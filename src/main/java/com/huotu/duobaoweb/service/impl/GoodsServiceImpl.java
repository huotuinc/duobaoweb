package com.huotu.duobaoweb.service.impl;

import com.huotu.duobaoweb.common.PublicParameterHolder;
import com.huotu.duobaoweb.entity.*;
import com.huotu.duobaoweb.entity.Goods;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.model.*;
import com.huotu.duobaoweb.repository.GoodsRepository;
import com.huotu.duobaoweb.repository.IssueRepository;
import com.huotu.duobaoweb.service.*;
import com.huotu.huobanplus.common.entity.*;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by zhang on 2016/3/25.
 */
@Transactional
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private UserBuyFlowService userBuyFlowService;

    @Autowired
    private CommonConfigService commonConfigService;

    @Autowired
    private RaidersCoreService raidersCoreService;

    @Autowired
    private UserNumberService userNumberService;

    @Autowired
    private GoodsRestRepository goodsRestRepository;


    /**
     * 跳转到商品活动首页
     *
     * @param goodsId 商品Id
     * @param map
     * @throws Exception
     */
    @Override
    public void jumpToGoodsActivityIndex(Long goodsId, Map<String, Object> map) throws Exception {
        if (goodsId == null) return;
        Issue issue = null;

        WebPublicModel webPublicModel = PublicParameterHolder.getParameters();

        GoodsIndexModel goodsIndexModel = new GoodsIndexModel();
        //1.通过商品Id获取对应的商品
        Goods goods = goodsRepository.getOne(goodsId);

        //2.获取商品中正在进行的期且封装数据
        if (goods != null) {
            goodsIndexModel.setId(goods.getId());
            goodsIndexModel.setDefaultPictureUrl(commonConfigService.getHuoBanPlusManagerWebUrl() + goods.getDefaultPictureUrl());
            goodsIndexModel.setStartTime(goods.getStartTime().getTime());
            goodsIndexModel.setEndTime(goods.getEndTime().getTime());
            issue = goods.getIssue();
            if (issue != null) {
                goodsIndexModel.setIssueId(issue.getId());
                //计算商品价格
                BigDecimal pricePercentAmount = issue.getPricePercentAmount();
                Long toAmount = issue.getToAmount();
                Long stepAmount = issue.getStepAmount();
                if (pricePercentAmount != null && toAmount != null) {
                    goodsIndexModel.setCostPrice(pricePercentAmount.multiply(new BigDecimal(toAmount)));
                    goodsIndexModel.setCurrentPrice(pricePercentAmount.multiply(new BigDecimal(stepAmount)));
                }
            }
        }
        //3.获取当前用户
        User user = webPublicModel.getCurrentUser();

        //4.获取用户是否登录
        if (user != null) {
            goodsIndexModel.setLogined(true);
            //5.判断当前用户是否参与
            userBuyFlowService.findByUserIdAndIssuId(user.getId(), issue.getId());
            goodsIndexModel.setJoined(true);
        } else {
            goodsIndexModel.setLogined(false);
            goodsIndexModel.setJoined(false);
        }

        //6.获取该商品所有的参与次数 todo
        goodsIndexModel.setJoinCount(1000L);


        map.put("issueId",goods.getIssue().getId());
        map.put("customerId",goods.getMerchantId());


        map.put("goodsIndexModel", goodsIndexModel);
        if(user != null){
            map.put("userId", user.getId());
        }
        map.put("customerId", webPublicModel.getCustomerId());
    }



    /**
     * 通过商品Id跳转到商品详情
     *
     * @param goodsId
     * @param map
     */
    @Override
    public void jumpToGoodsActivityDetailByGoodsId(Long goodsId, Map<String, Object> map) throws Exception {
        if (goodsId == null) return;

        GoodsDetailModel goodsDetailModel = new GoodsDetailModel();

        WebPublicModel webPublicModel = PublicParameterHolder.getParameters();

        //1.通过商品Id获取商品
        Goods goods = goodsRepository.findOne(goodsId);

        //2.获取商品中正在进行的期且封装数据
        if (goods != null) {
            Issue issue = goods.getIssue();

            goodsDetailModel.setId(goodsId);
            goodsDetailModel.setTitle(goods.getTitle());

            List<String> picList = new ArrayList<>();
            String pictures = goods.getPictureUrls();
            if (pictures != null) {
                String[] pics = pictures.split(",");
                for (int i = 0; i < pics.length; ++i) {
                    picList.add(commonConfigService.getHuoBanPlusManagerWebUrl()  + pics[i]);
                }
                goodsDetailModel.setPictureUrls(picList);
            }

            if (issue != null) {

                map.put("issueId", issue.getId());

                //0:进行中  1:倒计时  2:已揭晓
                int status = issue.getStatus().getValue();
                goodsDetailModel.setStatus(status);
                goodsDetailModel.setIssueId(issue.getId());

                Long toAmount = issue.getToAmount() == null ? 0L : issue.getToAmount();
                Long buyAmount = issue.getBuyAmount() == null ? 0L : issue.getBuyAmount();
                goodsDetailModel.setFullPrice(issue.getPricePercentAmount().multiply(new BigDecimal(toAmount)));
                goodsDetailModel.setDefaultAmount(issue.getDefaultAmount());
                goodsDetailModel.setStepAmount(issue.getStepAmount());

                //进行中
                if (status == 0) {
                    goodsDetailModel.setToAmount(toAmount);
                    goodsDetailModel.setRemainAmount(toAmount - buyAmount);
                    goodsDetailModel.setProgress(toAmount == 0 ? 0 : (int) (buyAmount / toAmount));
                } else if (status == 1) {
                    //倒计时
                    goodsDetailModel.setToAwardTime(raidersCoreService.getAwardingTime() * 1000);
                } else {
                    //已揭晓
                    User awardUser = issue.getAwardingUser();

                    if (awardUser != null) {
                        goodsDetailModel.setAwardUserName(awardUser.getRealName());
                        goodsDetailModel.setAwardUserIp(awardUser.getIp());
                        goodsDetailModel.setAwardUserCityName(awardUser.getCityName());
                        String head = awardUser.getUserHead();
                        if (head != null) {
                            goodsDetailModel.setAwardUserHead(commonConfigService.getHuoBanPlusManagerWebUrl()  + head);
                        }

                        goodsDetailModel.setAwardUserJoinCount(userBuyFlowService.findByUserIdAndIssuId(awardUser.getId(), issue.getId()).size() + 0L);
                    }

                    goodsDetailModel.setAwardTime(issue.getAwardingDate());
                    goodsDetailModel.setLuckNumber(issue.getLuckyNumber());
                }

                //3.获取用户当前参与次数
                //3.1获取当前用户
                User user = webPublicModel.getCurrentUser();
                if(user != null){
                    map.put("userId", user.getId());

                    //3.2获取用户参与次数
                    RaiderNumbersModel myRaiderNumbers = userNumberService.getMyRaiderNumbers(user.getId(), issue.getId());
                    if(myRaiderNumbers != null){
                        int amount = myRaiderNumbers.getAmount().intValue();
                        goodsDetailModel.setJoinCount(amount);
                        if(amount == 1){
                            goodsDetailModel.setNumber(myRaiderNumbers.getNumbers().get(0));
                        }
                    }else{
                        goodsDetailModel.setJoinCount(0);
                    }
                }else{
                    goodsDetailModel.setJoinCount(0);
                }

                //4.获取首次购买的时间 todo
                goodsDetailModel.setFirstBuyTime(new Date());

            }
        }
        map.put("goodsDetailModel", goodsDetailModel);
        map.put("customerId", webPublicModel.getCustomerId());
    }

    /**
     * 通过期号跳转到商品详情
     *
     * @param issueId
     * @param map
     * @throws Exception
     */
    @Override
    public void jumpToGoodsActivityDetailByIssueId(Long issueId, Map<String, Object> map) throws Exception {
        if (issueId == null) return;

        GoodsDetailModel goodsDetailModel = new GoodsDetailModel();
        WebPublicModel webPublicModel = PublicParameterHolder.getParameters();

        //1.通过期号获取定义的期
        Issue issue = issueRepository.findOne(issueId);

        //2.封装数据
        if (issue != null) {

            map.put("issueId", issue.getId());

            //3.获取商品
            Goods goods = issue.getGoods();

            if (goods != null) {
                goodsDetailModel.setId(goods.getId());
                goodsDetailModel.setTitle(goods.getTitle());

                List<String> picList = new ArrayList<>();
                String pictures = goods.getPictureUrls();
                if (pictures != null) {
                    String[] pics = pictures.split(",");
                    for (int i = 0; i < pics.length; ++i) {
                        picList.add(commonConfigService.getHuoBanPlusManagerWebUrl()  + pics[i]);
                    }
                    goodsDetailModel.setPictureUrls(picList);
                }
            }

            //0:进行中  1:倒计时  2:已揭晓
            int status = issue.getStatus().getValue();
            goodsDetailModel.setStatus(status);
            goodsDetailModel.setIssueId(issue.getId());
            Long toAmount = issue.getToAmount() == null ? 0L : issue.getToAmount();
            Long buyAmount = issue.getBuyAmount() == null ? 0L : issue.getBuyAmount();
            goodsDetailModel.setFullPrice(issue.getPricePercentAmount().multiply(new BigDecimal(toAmount)));
            goodsDetailModel.setDefaultAmount(issue.getDefaultAmount());
            goodsDetailModel.setStepAmount(issue.getStepAmount());

            //进行中
            if (status == 0) {
                goodsDetailModel.setToAmount(toAmount);
                goodsDetailModel.setRemainAmount(toAmount - buyAmount);
                goodsDetailModel.setProgress(toAmount == 0 ? 0 : (int) (buyAmount / toAmount));
            } else if (status == 1) {
                //倒计时
                goodsDetailModel.setToAwardTime(raidersCoreService.getAwardingTime() * 1000);
            } else {
                //已揭晓
                User awardUser = issue.getAwardingUser();

                if (awardUser != null) {
                    goodsDetailModel.setAwardUserName(awardUser.getRealName());
                    goodsDetailModel.setAwardUserIp(awardUser.getIp());
                    goodsDetailModel.setAwardUserCityName(awardUser.getCityName());
                    String head = awardUser.getUserHead();
                    if (head != null) {
                        goodsDetailModel.setAwardUserHead(commonConfigService.getHuoBanPlusManagerWebUrl()  + head);
                    }
                    goodsDetailModel.setAwardUserJoinCount(userBuyFlowService.findByUserIdAndIssuId(awardUser.getId(), issue.getId()).size() + 0L);
                }

                goodsDetailModel.setAwardTime(issue.getAwardingDate());
                goodsDetailModel.setLuckNumber(issue.getLuckyNumber());
            }

            //3.获取用户当前参与次数
            //3.1获取当前用户
            User user = webPublicModel.getCurrentUser();
            if(user != null){
                map.put("userId", user.getId());

                //3.2获取用户参与次数
                RaiderNumbersModel myRaiderNumbers = userNumberService.getMyRaiderNumbers(user.getId(), issueId);
                if(myRaiderNumbers != null){
                    int amount = myRaiderNumbers.getAmount().intValue();
                    goodsDetailModel.setJoinCount(amount);
                    if(amount == 1){
                        goodsDetailModel.setNumber(myRaiderNumbers.getNumbers().get(0));
                    }
                }else{
                   goodsDetailModel.setJoinCount(0);
                }
            }else{
                goodsDetailModel.setJoinCount(0);
            }

            //4.获取首次购买的时间 todo
            goodsDetailModel.setFirstBuyTime(new Date());
        }

        map.put("goodsDetailModel", goodsDetailModel);
        map.put("customerId", webPublicModel.getCustomerId());
    }

    /**
     * 获取某一期的参与记录
     *
     * @param issueId
     * @param lastId
     * @param map
     */
    @Override
    public void getBuyListByIssueId(Long issueId, Long lastId, Map<String, Object> map) throws Exception {
        //BuyListModel[] buyListModelList = userBuyFlowService.findByIssuIdList(issueId, lastId);
        //开始模拟数据
        List<BuyListModel> buyListModelList = new ArrayList<>();
        for(int i = 0; i < 10; ++i){
            BuyListModel buyListModel = new BuyListModel();
            buyListModel.setNickName("紫风飘雪");
            buyListModel.setAttendAmount(10L);
            buyListModel.setCity("杭州");
            buyListModel.setDate(new Date());
            buyListModel.setIp("192.168.1.254");
            buyListModel.setUserHeadUrl(commonConfigService.getHuoBanPlusManagerWebUrl()  + "resources/goods/defaultH.jpg");
            buyListModelList.add(buyListModel);
        }
        //结束模拟数据

        map.put("buyListModelList", buyListModelList);
    }

    /**
     * 计算详情
     *
     * @param issueId
     * @param map
     * @throws Exception
     */
    @Override
    public void getCountResultByIssueId(Long issueId, Map<String, Object> map) throws Exception {
        if (issueId == null) return;
        //1.通过期号获取指定的期
        Issue issue = issueRepository.findOne(issueId);

        //2.获取期对应的计算详情
        CountResult countResult = issue.getCountResult();
        CountResultModel countResultModel = new CountResultModel();

        //开始模拟数据
        countResultModel.setIssueNo("20160330043");
        countResultModel.setLuckNumber(10000001L);
        countResultModel.setNumberA("5659130125");
        countResultModel.setNumberB("24956");
        List<CountResultUserNumberListModel> countResultUserNumberListModelList = new ArrayList<>();
        for(int i = 0; i < 50; ++i){
            CountResultUserNumberListModel countResultUserNumberListModel = new CountResultUserNumberListModel();
            countResultUserNumberListModel.setBuyTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date()));
            countResultUserNumberListModel.setNumber(new SimpleDateFormat("HHmmssSSS").format(new Date()));
            countResultUserNumberListModel.setNickName("紫风飘雪");
            countResultUserNumberListModelList.add(countResultUserNumberListModel);
        }
        countResultModel.setUserNumbers(countResultUserNumberListModelList);
        //结束模拟数据

/*        if (countResult != null) {
            countResultModel.setIssueNo(countResult.getIssueNo());
            countResultModel.setNumberA(countResult.getNumberA() == null ? "" : countResult.getNumberA().toString());
            String numberB = countResult.getNumberB() == null ? "" : countResult.getNumberB().toString();
            int len = numberB.length();
            for (int i = 0; i < 5 - len; ++i) {
                numberB = "0" + numberB;
            }
            countResultModel.setNumberB(numberB);
            List<UserNumber> userNumberList = countResult.getUserNumbers();
            List<CountResultUserNumberListModel> countResultUserNumberListModelList = new ArrayList<>();
            for(UserNumber userNumber : userNumberList){
                Date time = new Date(userNumber.getTime());
                String buyTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(time);
                String number = new SimpleDateFormat("HHmmssSSS").format(time);
                String nickName = userNumber.getUser().getRealName();
                CountResultUserNumberListModel countResultUserNumberListModel = new CountResultUserNumberListModel();
                countResultUserNumberListModel.setBuyTime(buyTime);
                countResultUserNumberListModel.setNumber(number);
                countResultUserNumberListModel.setNickName(nickName);
                countResultUserNumberListModelList.add(countResultUserNumberListModel);
            }
            countResultModel.setUserNumbers(countResultUserNumberListModelList);
        }
        countResultModel.setLuckNumber(issue.getLuckyNumber());*/
        map.put("countResultModel", countResultModel);
    }

    /**
     * 获取商品图文详情 todo
     * @param goodsId
     * @param map
     */
    @Override
    public void jumpToImageTextDetail(Long goodsId, Map<String, Object> map) {

    }

    @Override
    public SelGoodsSpecModel getSelGoodsSpecModelByIssueId(Long issueId) throws Exception {
        if(issueId == null) return null;
        SelGoodsSpecModel selGoodsSpecModel = new SelGoodsSpecModel();

        //1.获取期号对用的期
        Issue issue = issueRepository.getOne(issueId);

        //2.获取期号对应的活动商品
        if(issue != null){
            Goods goods = issue.getGoods();

            if(goods != null){
                selGoodsSpecModel.setId(goods.getId());
                selGoodsSpecModel.setTitle(goods.getTitle());
                selGoodsSpecModel.setMallGoodsId(goods.getToMallGoodsId());
                selGoodsSpecModel.setMerchantId(goods.getMerchantId());

                String pictureUrls = goods.getPictureUrls();
                List<String> pictureUrlList = new ArrayList<>();
                if(pictureUrls != null){
                    String[] pics = pictureUrls.split(",");
                    for(int i = 0; i < pics.length; ++i){
                        pictureUrlList.add(commonConfigService.getHuoBanPlusManagerWebUrl() + pics[i]);
                    }
                }
                selGoodsSpecModel.setPictureUrlList(pictureUrlList);
                //3.获取商城商品
                com.huotu.huobanplus.common.entity.Goods mallGoods = goodsRestRepository.getOneByPK(goods.getToMallGoodsId());
                selGoodsSpecModel.setIntroduce(mallGoods.getIntro());
            }
        }

        return selGoodsSpecModel;
    }
}
