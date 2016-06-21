package com.huotu.mallduobao.service.impl;

import com.huotu.common.base.HttpHelper;
import com.huotu.huobanplus.common.entity.*;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.huobanplus.sdk.common.repository.ProductRestRepository;
import com.huotu.mallduobao.common.PublicParameterHolder;
import com.huotu.mallduobao.entity.*;
import com.huotu.mallduobao.entity.Goods;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.exceptions.GoodsOrIssueException;
import com.huotu.mallduobao.model.*;
import com.huotu.mallduobao.model.admin.*;
import com.huotu.mallduobao.repository.GoodsRepository;
import com.huotu.mallduobao.repository.IssueRepository;
import com.huotu.mallduobao.repository.UserBuyFlowRepository;
import com.huotu.mallduobao.service.*;
import com.huotu.mallduobao.utils.CommonEnum;
import com.huotu.mallduobao.utils.EnumHelper;
import com.jayway.jsonpath.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhang on 2016/3/25.
 */
@Transactional
@Service
public class GoodsServiceImpl implements GoodsService {

    private static final Logger logger = LoggerFactory.getLogger(GoodsServiceImpl.class);

    @Autowired
    private GoodsRepository goodsRepository;

    @Autowired
    private IssueRepository issueRepository;

    @Autowired
    private UserBuyFlowService userBuyFlowService;

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

    @Autowired
    private CommonConfigService commonConfigService;

    @Autowired
    private ProductRestRepository productRestRepository;



    /**
     * 用于判断活动状态(-1:活动未开始  0:活动正在进行 1:活动已结束 2:最后一期)
     * @param goods
     * @return
     */
    private int judgeGoodsActiveStatus(Goods goods) throws Exception{
        Date startTime = goods.getStartTime();
        Date endTime = goods.getEndTime();
        Date curTime = new Date();

       // com.huotu.huobanplus.common.entity.Goods mallGoods = goodsRestRepository.getOneByPK(goods.getToMallGoodsId());
        Long stock = goods.getStock();

        //如果当前时间小于活动开始时间
        if(curTime.compareTo(startTime) < 0){
            return -1;
        }


        Issue issue = goods.getIssue();
        //如果库存不足
        if(stock == 0 && issue.getStatus() != CommonEnum.IssueStatus.going){
            return 3;
        }


        //如果当前时间大于活动结束时间或商品下架且没有正在进行的期
        if(issue.getStatus()!= CommonEnum.IssueStatus.going){
            return 1;
        }

        //如果当前时间大于活动结束时间或商品下架且有正在进行的期
        if((curTime.compareTo(endTime) > 0 || goods.getStatus() == CommonEnum.GoodsStatus.down) &&issue.getStatus() == CommonEnum.IssueStatus.going){
            return 2;
        }


        return 0;
    }

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
        if (goods == null) {
            throw new GoodsOrIssueException("商品ID为" + goodsId + "的活动不存在");
        }


        Issue issue = goods.getIssue();
        if(issue == null){
            throw new GoodsOrIssueException("商品对应的期号不存在-----goodsId=" + goodsId);
        }

        int activeStatus = judgeGoodsActiveStatus(goods);
        goodsIndexModel.setActiveStatus(activeStatus);

        //2.获取商品中正在进行的期且封装数据
        if (goods != null) {
            goodsIndexModel.setId(goods.getId());
            goodsIndexModel.setDefaultPictureUrl(staticResourceService.getResource(goods.getDefaultPictureUrl()).toString());
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

        //6.获取该商品所有的参与次数
        Long attentAmount = 0L;
        attentAmount = attentAmount + (issue.getAttendAmount() == null ? 0L : issue.getAttendAmount());
        attentAmount = attentAmount + (goods.getAttendAmount() == null ? 0L : goods.getAttendAmount());
        goodsIndexModel.setJoinCount(attentAmount);

        map.put("issueId", goods.getIssue().getId());
        map.put("customerId", goods.getMerchantId());
        map.put("goodsIndexModel", goodsIndexModel);

        //添加分享信息
        String shareTitle = goods.getShareTitle();
        String shareDesc = goods.getShareDescription();
        String sharePic = staticResourceService.getResource(goods.getSharePictureUrl()).toString();
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
        if (goods == null) {
            throw new GoodsOrIssueException("商品ID为" + goodsId + "的活动不存在");
        }

        int activeStatus = judgeGoodsActiveStatus(goods);
        goodsDetailModel.setActiveStatus(activeStatus);

        //2.获取商品中正在进行的期且封装数据
        if (goods != null) {
            Issue issue = goods.getIssue();

            if(issue == null){
                throw new GoodsOrIssueException("商品对应的期号不存在-----goodsId=" + goodsId);
            }

            goodsDetailModel.setId(goodsId);
            goodsDetailModel.setTitle(goods.getTitle());

            List<String> picList = new ArrayList<>();
            String pictures = goods.getPictureUrls();
            if (pictures != null) {
                String[] pics = pictures.split(",");
                for (int i = 0; i < pics.length; ++i) {
                    picList.add(staticResourceService.getResource(pics[i]).toString());
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
                if (firstBuyTimeByIssueId != null) {
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
                if (user != null) {
                    //3.2获取用户参与次数
                    RaiderNumbersModel myRaiderNumbers = userNumberService.getMyRaiderNumbers(user.getId(), issue.getId());
                    if (myRaiderNumbers != null && myRaiderNumbers.getAmount() != null) {
                        int amount = myRaiderNumbers.getAmount().intValue();
                        goodsDetailModel.setJoinCount(amount);
                        if (amount == 1) {
                            goodsDetailModel.setNumber(myRaiderNumbers.getNumbers().get(0));
                        }
                    } else {
                        goodsDetailModel.setJoinCount(0);
                    }
                } else {
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

        if (issue == null) {
            throw new GoodsOrIssueException("期号ID为" + issueId + "的活动不存在");
        }

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
                        picList.add(staticResourceService.getResource(pics[i]).toString());
                    }
                    goodsDetailModel.setPictureUrls(picList);
                }

                int activeStatus = judgeGoodsActiveStatus(goods);
                goodsDetailModel.setActiveStatus(activeStatus);
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
            if (firstBuyTimeByIssueId != null) {
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
            if (user != null) {
                map.put("userId", user.getId());

                //3.2获取用户参与次数
                RaiderNumbersModel myRaiderNumbers = userNumberService.getMyRaiderNumbers(user.getId(), issueId);
                if (myRaiderNumbers.getAmount() != null) {
                    int amount = myRaiderNumbers.getAmount().intValue();
                    goodsDetailModel.setJoinCount(amount);
                    if (amount == 1) {
                        goodsDetailModel.setNumber(myRaiderNumbers.getNumbers().get(0));
                    }
                } else {
                    goodsDetailModel.setJoinCount(0);
                }
            } else {
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

        if (issue == null) {
            throw new GoodsOrIssueException("期号ID为" + issueId + "的活动不存在");
        }

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
            for (UserNumber userNumber : userNumberList) {
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
     * 把图文详情中的图片地址通过huobanpus的转换工具转换为正确的商城图片地址
     *
     * @param content 图文详情内容
     * @return
     */
    private String convertImageUrl(String content) {
        String mallImageUrl = getMallImageUrl();
        if (!StringUtils.isEmpty(mallImageUrl) && !StringUtils.isEmpty(content)) {
            content = content.replaceAll("\"", "'");
            content = content.replaceAll("src='/", "src='" + mallImageUrl);
        }
        return content;
    }


    /**
     * 获取图文详情中图片地址前缀
     * @return
     */
    private String getMallImageUrl() {
        String url = "";
        try {
            String apiid = commonConfigService.getHuobanplusOpenApiAppid();
            String appsecrect = commonConfigService.getHuobanplusOpenApiAppsecrect();
            String mallApi = commonConfigService.getHuobanplusOpenApiRoot();
            long curTime = System.currentTimeMillis();
            String sign = "appid=" + apiid + "&timestamp=" + curTime + appsecrect;
            sign = DigestUtils.md5DigestAsHex(sign.toString().getBytes("UTF-8")).toLowerCase();
            url = HttpHelper.getRequest(mallApi + "/system?appid=" + apiid + "&timestamp=" + curTime + "&sign=" + sign);
            url = JsonPath.read(url, "$.mallResourceUriRoot");
        } catch (Exception e) {
        }
        return url;
    }


    /**
     * 获取商品图文详情
     *
     * @param goodsId
     * @param map
     */
    @Override
    public void jumpToImageTextDetail(Long goodsId, Map<String, Object> map) throws Exception {
        if (goodsId == null) return;

        //1.获取商品活动
        Goods goods = goodsRepository.findOne(goodsId);

        if (goods == null) {
            throw new GoodsOrIssueException("商品ID为" + goodsId + "的活动不存在");
        }

        //2.获取商城商品
        com.huotu.huobanplus.common.entity.Goods mallGoods = goodsRestRepository.getOneByPK(goods.getToMallGoodsId());

        //3.获取商品简介
        String introduce = convertImageUrl(mallGoods.getIntro());

        map.put("introduce", introduce);
    }

    @Override
    public SelGoodsSpecModel getSelGoodsSpecModelByIssueId(Long issueId) throws Exception {
        if (issueId == null) return null;
        SelGoodsSpecModel selGoodsSpecModel = new SelGoodsSpecModel();

        //1.获取期号对用的期
        Issue issue = issueRepository.findOne(issueId);

        if (issueId == null) {
            throw new GoodsOrIssueException("期号ID为" + issueId + "的活动不存在");
        }


        //2.获取期号对应的活动商品
        if (issue != null) {
            Goods goods = issue.getGoods();

            if (goods != null) {
                selGoodsSpecModel.setId(goods.getId());
                selGoodsSpecModel.setTitle(goods.getTitle());
                selGoodsSpecModel.setMallGoodsId(goods.getToMallGoodsId());
                selGoodsSpecModel.setMerchantId(goods.getMerchantId());

                String pictureUrls = goods.getPictureUrls();
                List<String> pictureUrlList = new ArrayList<>();
                if (pictureUrls != null) {
                    String[] pics = pictureUrls.split(",");
                    for (int i = 0; i < pics.length; ++i) {
                        pictureUrlList.add(staticResourceService.getResource(pics[i]).toString());
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
     *
     * @param issueId
     * @param page
     * @param pageSize
     * @return
     * @throws Exception
     */
    @Override
    public BuyListModelAjax getBuyListByIssueId(Long issueId, Long lastFlag, Long page, Long pageSize) throws Exception {
        BuyListModelAjax buyListModelAjax = userBuyFlowService.ajaxFindBuyListByIssueId(issueId, lastFlag, page, pageSize);
        return buyListModelAjax;
    }

    @Override
    public Goods upateGoodsAttendAmount(Goods goods) {
        //1.获取期号
        Issue issue = goods.getIssue();

        //2.获取attentAmount
        if (issue != null) {
            Long attendAmount = issue.getAttendAmount();
            if (attendAmount != null) {
                Long goodsAttendAmount = goods.getAttendAmount();
                if (goodsAttendAmount == null) goodsAttendAmount = 0L;
                goodsAttendAmount = goodsAttendAmount + attendAmount;
                goods.setAttendAmount(goodsAttendAmount);
                goods = goodsRepository.save(goods);
            }
        }
        return goods;
    }


    /**
     * 获取后台商品活动列表
     *
     * @param duoBaoGoodsSearchModel
     * @param map
     * @throws Exception
     */
    @Override
    public void getDuoBaoGoodsList(DuoBaoGoodsSearchModel duoBaoGoodsSearchModel, Long customerId, Map<String, Object> map) throws Exception {
        Page<Goods> page = null;
        Sort.Direction direction = duoBaoGoodsSearchModel.getRaSortType() == 0 ? Sort.Direction.DESC : Sort.Direction.ASC;
        String s;
        if (duoBaoGoodsSearchModel.getSort().intValue() == 0) {
            s = "id";
        } else {
            s = "toAmout";
        }
        Sort sort = new Sort(direction, s);

        page = goodsRepository.findAll(new Specification<Goods>() {
            @Override
            public Predicate toPredicate(Root<Goods> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = null;

                predicate = cb.equal(root.get("merchantId").as(Long.class), customerId);

                String title = duoBaoGoodsSearchModel.getTitle();
                if (title != null && title.trim().length() > 0) {
                    predicate = cb.and(predicate, cb.like(root.get("title"), "%" + title + "%"));
                }

                if (duoBaoGoodsSearchModel.getStatus() != -1) {
                    predicate = cb.and(predicate, cb.equal(root.get("status").as(CommonEnum.GoodsStatus.class), EnumHelper.getEnumType(CommonEnum.GoodsStatus.class, duoBaoGoodsSearchModel.getStatus())));
                }
                return predicate;
            }
        }, new PageRequest(duoBaoGoodsSearchModel.getPageNoStr(), 20, sort));

        List<DuoBaoGoodsListModel> duoBaoGoodsListModelList = new ArrayList<>();
        List<Goods> duobaoGoodsList = page.getContent();
        for (Goods duobaoGoods : duobaoGoodsList) {
            DuoBaoGoodsListModel duoBaoGoodsListModel = new DuoBaoGoodsListModel();
            duoBaoGoodsListModel.setId(duobaoGoods.getId());
            duoBaoGoodsListModel.setTitle(duobaoGoods.getTitle());
            duoBaoGoodsListModel.setCharacters(duobaoGoods.getCharacters());
            duoBaoGoodsListModel.setDefaultAmount(duobaoGoods.getDefaultAmount());
            duoBaoGoodsListModel.setStepAmount(duobaoGoods.getStepAmount());
            duoBaoGoodsListModel.setToAmount(duobaoGoods.getToAmount());
            duoBaoGoodsListModel.setStartTime(duobaoGoods.getStartTime());
            duoBaoGoodsListModel.setEndTime(duobaoGoods.getEndTime());
            duoBaoGoodsListModel.setStatusName(duobaoGoods.getStatus().getName());
            duoBaoGoodsListModel.setPricePercentAmount(duobaoGoods.getPricePercentAmount());
            duoBaoGoodsListModel.setStatus(duobaoGoods.getStatus().getValue());
            duoBaoGoodsListModel.setStock(duobaoGoods.getStock());
            duoBaoGoodsListModelList.add(duoBaoGoodsListModel);
        }

        map.put("duoBaoGoodsListModelList", duoBaoGoodsListModelList);
        map.put("pageNo", page.getTotalPages() > 0 ? page.getNumber() + 1 : 0);
        map.put("totalPages", page.getTotalPages());
        map.put("totalRecords", page.getTotalElements());
        map.put("customerId", customerId);
    }

    /**
     * 获取参与活动商城商品列表
     *
     * @param mallGoodsSearchModel
     * @param map
     * @throws Exception
     */
    @Override
    public void getMallGoodsList(MallGoodsSearchModel mallGoodsSearchModel, Long customerId, Map<String, Object> map) throws Exception {
        List<MallGoodsListModel> mallGoodsListModelList = new ArrayList<>();
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        Page<com.huotu.huobanplus.common.entity.Goods> page = null;
        String title = mallGoodsSearchModel.getTitle();
        if (title != null) {
            if (title.trim().length() == 0) {
                title = null;
            } else {
                title = URLEncoder.encode("%" + title.trim() + "%", "UTF-8");
            }
        }
        page = goodsRestRepository.findByTitleAndCategoryAndScenes(title, null, customerId, 4, new PageRequest(mallGoodsSearchModel.getPageNoStr(), 20, sort));

        List<com.huotu.huobanplus.common.entity.Goods> goodsList = page.getContent();
        for (com.huotu.huobanplus.common.entity.Goods goods : goodsList) {
            MallGoodsListModel mallGoodsListModel = new MallGoodsListModel();
            mallGoodsListModel.setId(goods.getId());
            mallGoodsListModel.setTitle(goods.getTitle());
            mallGoodsListModel.setCost(goods.getCost());
            mallGoodsListModel.setMarkerPrice(goods.getMarketPrice());
            mallGoodsListModel.setPrice(goods.getPrice());
            Map<String, Object> mallGoodsStock = getMallGoodsStock(goods.getId());
            mallGoodsListModel.setStock((int)mallGoodsStock.get("stock"));
            mallGoodsListModel.setLockStock((int)mallGoodsStock.get("freeze"));

            //商家
            Merchant owner = goods.getOwner();
            if (owner != null) {
                mallGoodsListModel.setMerchantId(owner.getId());
            }

            mallGoodsListModelList.add(mallGoodsListModel);
        }

        map.put("mallGoodsListModelList", mallGoodsListModelList);
        map.put("pageNo", page.getTotalPages() > 0 ? page.getNumber() + 1 : 0);
        map.put("totalPages", page.getTotalPages());
        map.put("totalRecords", page.getTotalElements());
        map.put("customerId", customerId);
    }

    /**
     * 跳转到活动新增页面前期准备
     *
     * @param map
     */
    @Override
    public void jumpToAddDuoBaoGoods(Map<String, Object> map) {
        DuoBaoGoodsInputModel duoBaoGoodsInputModel = new DuoBaoGoodsInputModel();
        map.put("duoBaoGoodsInputModel", duoBaoGoodsInputModel);
    }

    /**
     * 保存商品活动
     *
     * @param duoBaoGoodsInputModel
     * @throws Exception
     */
    @Override
    public void saveDuoBaoGoods(DuoBaoGoodsInputModel duoBaoGoodsInputModel) throws Exception {
        //1.判断是否存在Id,如果存在id,则为更新操作，否则为新增操作
        Long duoBaoGoodsId = duoBaoGoodsInputModel.getId();
        Goods duobaoGoods;
        if (duoBaoGoodsId == null) {
            duobaoGoods = new Goods();
            duobaoGoods.setStatus(CommonEnum.GoodsStatus.uncheck);
            duobaoGoods.setAttendAmount(0L);
            duobaoGoods.setViewAmount(0L);
        } else {
            duobaoGoods = goodsRepository.getOne(duoBaoGoodsId);
        }

        //2.重新赋值
        duobaoGoods.setTitle(duoBaoGoodsInputModel.getTitle());
        duobaoGoods.setMerchantId(duoBaoGoodsInputModel.getMerchantId());
        duobaoGoods.setCharacters(duoBaoGoodsInputModel.getCharacters());
        duobaoGoods.setDefaultAmount(duoBaoGoodsInputModel.getDefaultAmount());
        String pictureUrls = duoBaoGoodsInputModel.getPictureUrls();
        duobaoGoods.setDefaultPictureUrl(pictureUrls.split(",")[0]);
        duobaoGoods.setPictureUrls(pictureUrls);
        duobaoGoods.setToMallGoodsId(duoBaoGoodsInputModel.getMallGoodsId());
        duobaoGoods.setStartTime(duoBaoGoodsInputModel.getStartTime());
        duobaoGoods.setEndTime(duoBaoGoodsInputModel.getEndTime());
        duobaoGoods.setPricePercentAmount(duoBaoGoodsInputModel.getPricePercentAmount());
        duobaoGoods.setShareTitle(duoBaoGoodsInputModel.getShareTitle());
        duobaoGoods.setSharePictureUrl(duoBaoGoodsInputModel.getSharePictureUrl());
        duobaoGoods.setShareDescription(duoBaoGoodsInputModel.getShareDescription());
        duobaoGoods.setStepAmount(duoBaoGoodsInputModel.getStepAmount());
        duobaoGoods.setToAmount(duoBaoGoodsInputModel.getToAmount());
        duobaoGoods.setStock(duoBaoGoodsInputModel.getStock());

        goodsRepository.save(duobaoGoods);
    }

    /**
     * 跳转到商品编辑页面的前期准备
     *
     * @param goodsId
     * @param map
     * @throws Exception
     */
    @Override
    public void jumpToUpdateBaoGoods(Long goodsId, Map<String, Object> map) throws Exception {
        if (goodsId == null) return;

        //1.获取活动商品
        Goods duobaoGoods = goodsRepository.findOne(goodsId);
        DuoBaoGoodsInputModel duoBaoGoodsInputModel = new DuoBaoGoodsInputModel();

        //2.获取商城商品
        if (duobaoGoods != null) {
            com.huotu.huobanplus.common.entity.Goods goods = goodsRestRepository.getOneByPK(duobaoGoods.getToMallGoodsId());
            duoBaoGoodsInputModel.setId(duobaoGoods.getId());
            duoBaoGoodsInputModel.setMerchantId(duobaoGoods.getMerchantId());
            duoBaoGoodsInputModel.setMallGoodsId(duobaoGoods.getToMallGoodsId());
            duoBaoGoodsInputModel.setMallGoodsTitle(goods.getTitle());
            duoBaoGoodsInputModel.setTitle(duobaoGoods.getTitle());
            duoBaoGoodsInputModel.setCharacters(duobaoGoods.getCharacters());
            duoBaoGoodsInputModel.setStepAmount(duobaoGoods.getStepAmount());
            duoBaoGoodsInputModel.setDefaultAmount(duobaoGoods.getDefaultAmount());
            duoBaoGoodsInputModel.setToAmount(duobaoGoods.getToAmount());
            duoBaoGoodsInputModel.setPricePercentAmount(duobaoGoods.getPricePercentAmount());
            duoBaoGoodsInputModel.setStartTime(duobaoGoods.getStartTime());
            duoBaoGoodsInputModel.setEndTime(duobaoGoods.getEndTime());
            duoBaoGoodsInputModel.setStock(duobaoGoods.getStock());
            Map<String, Object> mallGoodsStock = getMallGoodsStock(duobaoGoods.getToMallGoodsId());
            duoBaoGoodsInputModel.setAvailableStock((int)mallGoodsStock.get("availableStock"));


            String pictureUrls = duobaoGoods.getPictureUrls();
            if (pictureUrls != null) {
                List<String> list = Arrays.asList(pictureUrls.split(","));
                list = list.subList(1, list.size());

                List<String> list1 = new ArrayList<>();
                for (String url : list) {
                    list1.add(staticResourceService.getResource(url).toString());
                }
                duoBaoGoodsInputModel.setPictureUrlList(list1);
                duoBaoGoodsInputModel.setPictureRelativelyUrlList(list);

                String defaultPictureUrl = pictureUrls.split(",")[0];
                duoBaoGoodsInputModel.setDefaultPictureUrl(staticResourceService.getResource(defaultPictureUrl).toString());
                duoBaoGoodsInputModel.setDefaultPictureRelativelyUrl(defaultPictureUrl);
            }

            duoBaoGoodsInputModel.setShareTitle(duobaoGoods.getShareTitle());
            duoBaoGoodsInputModel.setSharePictureUrl(staticResourceService.getResource(duobaoGoods.getSharePictureUrl()).toString());
            duoBaoGoodsInputModel.setSharePictureRelativelyUrl(duobaoGoods.getSharePictureUrl());
            duoBaoGoodsInputModel.setShareDescription(duobaoGoods.getShareDescription());
        }

        map.put("duoBaoGoodsInputModel", duoBaoGoodsInputModel);
    }

    /**
     * 获取指定的商品活动的详细信息
     *
     * @param goodsId
     * @param map
     * @throws Exception
     */
    @Override
    public void getDuoBaoGoodsDatailInfo(Long goodsId, Map<String, Object> map) throws Exception {
        if (goodsId == null) return;

        //1.获取指定商品活动
        Goods duobaoGoods = goodsRepository.findOne(goodsId);
        DuoBaoGoodsDetailModel duoBaoGoodsDetailModel = new DuoBaoGoodsDetailModel();

        //2.获取商城商品名称
        com.huotu.huobanplus.common.entity.Goods goods = goodsRestRepository.getOneByPK(duobaoGoods.getToMallGoodsId());
        duoBaoGoodsDetailModel.setMallGoodsTitle(goods.getTitle());

        //3获取商品活动信息
        duoBaoGoodsDetailModel.setTitle(duobaoGoods.getTitle());
        duoBaoGoodsDetailModel.setCharacters(duobaoGoods.getCharacters());
        duoBaoGoodsDetailModel.setToAmount(duobaoGoods.getToAmount());
        duoBaoGoodsDetailModel.setStepAmount(duobaoGoods.getStepAmount());
        duoBaoGoodsDetailModel.setDefaultAmount(duobaoGoods.getDefaultAmount());
        duoBaoGoodsDetailModel.setPricePercentAmount(duobaoGoods.getPricePercentAmount());
        duoBaoGoodsDetailModel.setAttendAmount(duobaoGoods.getAttendAmount());
        duoBaoGoodsDetailModel.setViewAmount(duobaoGoods.getViewAmount());
        duoBaoGoodsDetailModel.setShareTitle(duobaoGoods.getShareTitle());
        duoBaoGoodsDetailModel.setShareDescription(duobaoGoods.getShareDescription());
        duoBaoGoodsDetailModel.setStatusName(duobaoGoods.getStatus().getName());

        String pictureUrls = duobaoGoods.getPictureUrls();
        if (pictureUrls != null) {
            List<String> list = Arrays.asList(pictureUrls.split(","));
            for (int i = 0; i < list.size(); ++i) {
                String url = list.get(i);
                list.set(i, staticResourceService.getResource(url).toString());
            }
            duoBaoGoodsDetailModel.setPictureUrls(list);
        }
        duoBaoGoodsDetailModel.setSharePictureUrl(staticResourceService.getResource(duobaoGoods.getSharePictureUrl()).toString());

        map.put("duoBaoGoodsDetailModel", duoBaoGoodsDetailModel);
    }

    /**
     * 异步更新商品状态
     *
     * @param goodsId
     * @param map
     * @throws Exception
     */
    @Transactional
    @Override
    public void ajaxUpdateStatus(Long goodsId, Map<String, Object> map) throws Exception {
        //1.获取商品
        Goods duobaoGoods = goodsRepository.findOne(goodsId);

        //2.获取商品状态
        if (duobaoGoods != null) {
            CommonEnum.GoodsStatus status = duobaoGoods.getStatus();

            try {
                if (status == CommonEnum.GoodsStatus.up) {
                    //下架
                    duobaoGoods.setStatus(CommonEnum.GoodsStatus.down);
                    goodsRepository.save(duobaoGoods);
                    map.put("msg", "下架成功");
                    map.put("msgCode", 1);
                } else {
                    //先检测活动是否结束
                    Date endTime = duobaoGoods.getEndTime();

                    if (endTime.compareTo(new Date()) < 0) {
                        map.put("msg", "活动已结束,不能上架");
                        map.put("msgCode", 0);
                        return;
                    }

                    //上架前先检测商品的库存
                    com.huotu.huobanplus.common.entity.Goods goods = goodsRestRepository.getOneByPK(duobaoGoods.getToMallGoodsId());
                    int stock = -1;
                    if (goods != null) {
                        stock = goodsRestRepository.getOneByPK(duobaoGoods.getToMallGoodsId()).getStock();
                    }

                    if (stock > 0 || stock == -1) {
                        if (status == CommonEnum.GoodsStatus.uncheck) {
                            map.put("msg", "审核成功且上架成功");
                        } else {
                            map.put("msg", "上架成功");
                        }
                        duobaoGoods.setStatus(CommonEnum.GoodsStatus.up);
                        duobaoGoods = goodsRepository.save(duobaoGoods);

                        //判断该商品是否已存在正在进行的期号
                        if (issueRepository.findIssueByGoodsIdAndStautsIsGoing(duobaoGoods.getId(), CommonEnum.IssueStatus.going) == null) {
                            raidersCoreService.generateIssue(duobaoGoods);
                        }
                        map.put("msgCode", 1);

                    } else {
                        if (status == CommonEnum.GoodsStatus.uncheck) {
                            map.put("msg", "审核成功但商品无库存,不能上架");
                            duobaoGoods.setStatus(CommonEnum.GoodsStatus.down);
                            goodsRepository.save(duobaoGoods);
                            map.put("msgCode", 1);
                        } else {
                            map.put("msg", "商品无库存,不能上架");
                        }
                        map.put("msgCode", 0);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (status == CommonEnum.GoodsStatus.uncheck) {
                    map.put("msg", "商品审核失败");
                } else if (status == CommonEnum.GoodsStatus.up) {
                    map.put("msg", "商品下架失败");
                } else {
                    map.put("msg", "商品上架失败");
                }
                map.put("code", 0);
                throw new Exception(e);
            }
        } else {
            map.put("msg", "该商品不存在");
            map.put("msgCode", 0);
        }
    }

    /**
     * 获取商品指定商品的库存
     *
     * @param mallGoodsId
     * @return
     * @throws Exception
     */
    @Override
    public  Map<String, Object> getMallGoodsStock(Long mallGoodsId) throws Exception {
        Map<String, Object> map = new HashMap<>();
        int stock = 0;   //库存
        int freeze = 0;  //冻结
        int availableStock = 0; //可用库存

        com.huotu.huobanplus.common.entity.Goods goods = goodsRestRepository.getOneByPK(mallGoodsId);
        List<Product> products = productRestRepository.findByGoods(goods);
        for(Product product : products){
             stock += product.getStock();
             freeze += product.getFreeze();
             availableStock += (product.getStock() - product.getFreeze());
        }

        map.put("stock", stock);
        map.put("freeze", freeze);
        map.put("availableStock", availableStock);

        return  map;
    }

}
