package com.huotu.mallduobao.service.impl;

import com.huotu.common.base.HttpHelper;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.mallduobao.common.PublicParameterHolder;
import com.huotu.mallduobao.entity.*;
import com.huotu.mallduobao.model.*;
import com.huotu.mallduobao.repository.GoodsRepository;
import com.huotu.mallduobao.repository.IssueRepository;
import com.huotu.mallduobao.repository.UserBuyFlowRepository;
import com.huotu.mallduobao.service.*;
import com.jayway.jsonpath.JsonPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

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
    private UserBuyFlowRepository userBuyFlowRepository;

    @Autowired
    private StaticResourceService staticResourceService;

    @Autowired
    private GoodsRestRepository goodsRestRepository;


    /**
     * 跳转到商品活动首页
     *
     * @param goodsId 期号Id
     * @param map
     * @throws Exception
     */
    @Override
    public void jumpToGoodsActivityIndex(Long goodsId, Map<String, Object> map) throws Exception {
        if (goodsId == null) return;


        WebPublicModel webPublicModel = PublicParameterHolder.getParameters();

        GoodsIndexModel goodsIndexModel = new GoodsIndexModel();


        //1.通过商品Id获取对应的商品
        Goods goods = goodsRepository.findOne(goodsId);
        Issue issue = goods.getIssue();
        if(issue == null || goods == null || goods.getStatus().getValue() == 2){
            throw new Exception("商品不存在或期号不存在或商品已下架");
        }

        //2.获取商品中正在进行的期且封装数据
        if (goods != null) {
            goodsIndexModel.setId(goods.getId());
            goodsIndexModel.setDefaultPictureUrl(commonConfigService.getResourceUri() + goods.getDefaultPictureUrl());
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
            int size = userBuyFlowService.findByUserIdAndIssuId(user.getId(), issue.getId()).size();
            goodsIndexModel.setJoined(size > 0);
        } else {
            goodsIndexModel.setLogined(false);
            goodsIndexModel.setJoined(false);
        }

        //6.获取该商品所有的参与次数 todo
        Long attentAmount = 0L;
        attentAmount = attentAmount + (issue.getAttendAmount() == null ? 0L : issue.getAttendAmount());
        attentAmount = attentAmount + (goods.getAttendAmount() == null ? 0L : goods.getAttendAmount());
        goodsIndexModel.setJoinCount(attentAmount);

        map.put("issueId",goods.getIssue().getId());
        map.put("customerId",goods.getMerchantId());
        map.put("goodsIndexModel", goodsIndexModel);

        //添加分享信息
        String shareTitle = goods.getShareTitle();
        String shareDesc = goods.getShareDescription();
        String sharePic = commonConfigService.getResourceUri() + goods.getSharePictureUrl();
        map.put("shareTitle", shareTitle);
        map.put("shareDesc", shareDesc);
        map.put("sharePic", sharePic);
        map.put("andStr", "&");
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
        goodsDetailModel.setAndStr("&");

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
                    picList.add(commonConfigService.getResourceUri()  + pics[i]);
                }
                goodsDetailModel.setPictureUrls(picList);
            }

            if (issue != null) {

                map.put("issueId", issue.getId());

                //0:进行中  1:倒计时  2:已揭晓
                int status = issue.getStatus().getValue();
                goodsDetailModel.setStatus(status);
                goodsDetailModel.setIssueId(issue.getId());
                goodsDetailModel.setNextIssueId(issue.getId());

                Long toAmount = issue.getToAmount() == null ? 0L : issue.getToAmount();
                Long buyAmount = issue.getBuyAmount() == null ? 0L : issue.getBuyAmount();
                goodsDetailModel.setFullPrice(issue.getPricePercentAmount().multiply(new BigDecimal(toAmount)));
                goodsDetailModel.setDefaultAmount(issue.getDefaultAmount());
                goodsDetailModel.setStepAmount(issue.getStepAmount());

                Long firstBuyTimeByIssueId = userBuyFlowRepository.getFirstBuyTimeByIssueId(issue.getId());
                if(firstBuyTimeByIssueId != null){
                    goodsDetailModel.setFirstBuyTime(new Date(firstBuyTimeByIssueId));
                }

                //进行中
                if (status == 0) {
                    goodsDetailModel.setToAmount(toAmount);
                    goodsDetailModel.setRemainAmount(toAmount - buyAmount);
                    goodsDetailModel.setProgress(toAmount == 0 ? 0 : (int) (buyAmount * 100 / toAmount));
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
                            goodsDetailModel.setAwardUserHead(staticResourceService.getResource(head).toString());
                        }

                        goodsDetailModel.setAwardUserJoinCount(userNumberService.getMyRaiderNumbers(awardUser.getId(), issue.getId()).getAmount() + 0L);
                    }

                    goodsDetailModel.setAwardTime(issue.getAwardingDate());
                    goodsDetailModel.setLuckNumber(issue.getLuckyNumber());
                }

                //3.获取用户当前参与次数
                //3.1获取当前用户
                User user = webPublicModel.getCurrentUser();
                if(user != null){
                    //3.2获取用户参与次数
                    RaiderNumbersModel myRaiderNumbers = userNumberService.getMyRaiderNumbers(user.getId(), issue.getId());
                    if(myRaiderNumbers != null && myRaiderNumbers.getAmount() != null){
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
        goodsDetailModel.setAndStr("&");
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
                        picList.add(commonConfigService.getResourceUri()  + pics[i]);
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

            Long firstBuyTimeByIssueId = userBuyFlowRepository.getFirstBuyTimeByIssueId(issue.getId());
            if(firstBuyTimeByIssueId != null){
                goodsDetailModel.setFirstBuyTime(new Date(firstBuyTimeByIssueId));
            }

            //进行中
            if (status == 0) {
                goodsDetailModel.setToAmount(toAmount);
                goodsDetailModel.setRemainAmount(toAmount - buyAmount);
                goodsDetailModel.setProgress(toAmount == 0 ? 0 : (int) (buyAmount * 100 / toAmount));
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
                        goodsDetailModel.setAwardUserHead(staticResourceService.getResource(head).toString());
                    }
                    goodsDetailModel.setAwardUserJoinCount(userNumberService.getMyRaiderNumbers(awardUser.getId(), issue.getId()).getAmount() + 0L);
                }

                goodsDetailModel.setAwardTime(issue.getAwardingDate());
                goodsDetailModel.setLuckNumber(issue.getLuckyNumber());
            }
            goodsDetailModel.setNextIssueId(goods.getIssue().getId());

            //3.获取用户当前参与次数
            //3.1获取当前用户
            User user = webPublicModel.getCurrentUser();
            if(user != null){
                map.put("userId", user.getId());

                //3.2获取用户参与次数
                RaiderNumbersModel myRaiderNumbers = userNumberService.getMyRaiderNumbers(user.getId(), issueId);
                if(myRaiderNumbers.getAmount() != null){
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
        }

        map.put("goodsDetailModel", goodsDetailModel);
        map.put("customerId", webPublicModel.getCustomerId());
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

       if (countResult != null) {
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
        countResultModel.setLuckNumber(issue.getLuckyNumber());
        map.put("countResultModel", countResultModel);
    }

    /**
     * 工具方法
     * @return
     */
    private String convertImageUrl(String imageUrl){
        String res = "";
        try {
            String apiid = commonConfigService.getHuobanplusOpenApiAppid();
            String appsecrect = commonConfigService.getHuobanplusOpenApiAppsecrect();
            String mallApi = commonConfigService.getHuobanplusOpenApiRoot();
            long curTime = System.currentTimeMillis();
            String sign = "appid=" + apiid + "&timestamp=" + curTime + appsecrect;
            sign = DigestUtils.md5DigestAsHex(sign.toString().getBytes("UTF-8")).toLowerCase();
            res = HttpHelper.getRequest(mallApi + "/system?appid=" + apiid + "&timestamp=" + curTime + "&sign=" + sign);
            res = JsonPath.read(res, "$.mallResourceUriRoot");
        }catch (Exception e){
            e.printStackTrace();
            res= "";
        }

        if(imageUrl != null){
            imageUrl = imageUrl.replaceAll("\"", "'");
            imageUrl = imageUrl.replaceAll("src='/", "src='" + res);
        }
        return imageUrl;
    }


    /**
     * 获取商品图文详情
     * @param goodsId
     * @param map
     */
    @Override
    public void jumpToImageTextDetail(Long goodsId, Map<String, Object> map) throws Exception{
         if(goodsId == null) return;

        //1.获取商品活动
        Goods goods = goodsRepository.findOne(goodsId);

        //2.获取商城商品
        com.huotu.huobanplus.common.entity.Goods mallGoods = goodsRestRepository.getOneByPK(goods.getToMallGoodsId());

        //3.获取商品简介
        String introduce = convertImageUrl(mallGoods.getIntro());

        map.put("introduce", introduce);
    }

    @Override
    public SelGoodsSpecModel getSelGoodsSpecModelByIssueId(Long issueId) throws Exception {
        if(issueId == null) return null;
        SelGoodsSpecModel selGoodsSpecModel = new SelGoodsSpecModel();

        //1.获取期号对用的期
        Issue issue = issueRepository.findOne(issueId);

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
                        pictureUrlList.add(commonConfigService.getResourceUri() + pics[i]);
                    }
                }
                selGoodsSpecModel.setPictureUrlList(pictureUrlList);
                //3.获取商城商品
                com.huotu.huobanplus.common.entity.Goods mallGoods = goodsRestRepository.getOneByPK(goods.getToMallGoodsId());
                String introduce = convertImageUrl(mallGoods.getIntro());
                selGoodsSpecModel.setIntroduce(introduce);
            }
        }
        return selGoodsSpecModel;
    }

    /**
     * 异步获取参与记录
     * @param issueId
     * @param page
     * @param pageSize
     * @return
     * @throws Exception
     */
    @Override
    public BuyListModelAjax getBuyListByIssueId(Long issueId, Long lastFlag, Long page, Long pageSize) throws Exception{
        BuyListModelAjax buyListModelAjax = userBuyFlowService.ajaxFindBuyListByIssueId(issueId, lastFlag, page, pageSize);
        return buyListModelAjax;
    }

    @Override
    public Goods upateGoodsAttendAmount(Goods goods){
        //1.获取期号
        Issue issue = goods.getIssue();

        //2.获取attentAmount
        if(issue != null){
            Long attendAmount = issue.getAttendAmount();
            if(attendAmount != null){
                Long goodsAttendAmount = goods.getAttendAmount();
                if(goodsAttendAmount == null) goodsAttendAmount = 0L;
                goodsAttendAmount = goodsAttendAmount + attendAmount;
                goods.setAttendAmount(goodsAttendAmount);
                goods = goodsRepository.save(goods);
            }
        }
        return goods;
    }
}
