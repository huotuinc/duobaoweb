package com.huotu.duobaoweb.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.Delivery;
import com.huotu.duobaoweb.entity.UserBuyFlow;
import com.huotu.duobaoweb.model.*;
import com.huotu.duobaoweb.repository.DeliveryRepository;
import com.huotu.duobaoweb.repository.UserBuyFlowRepository;
import com.huotu.duobaoweb.service.StaticResourceService;
import com.huotu.duobaoweb.service.UserBuyFlowService;
import com.huotu.huobanplus.common.entity.Goods;
import com.huotu.huobanplus.common.entity.Product;
import com.huotu.huobanplus.common.entity.support.SpecDescription;
import com.huotu.huobanplus.common.entity.support.SpecDescriptions;
import com.huotu.huobanplus.sdk.common.repository.GoodsRestRepository;
import com.huotu.huobanplus.sdk.common.repository.ProductRestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by admin on 2016/2/2.
 */
@Service
public class UserBuyFlowServiceImpl implements UserBuyFlowService {
    @Autowired
    EntityManager entityManager;
    @Autowired
    UserBuyFlowRepository userBuyFlowRepository;
    @Autowired
    StaticResourceService staticResourceService;

    @Autowired
    DeliveryRepository deliveryRepository;

    @Autowired
    GoodsRestRepository goodsRestRepository;

    public List<UserBuyFlow> findByUserIdAndIssuId(Long userId, Long issueId) {
        List<UserBuyFlow> userBuyFlows = null;
        Query query = null;
        String hql = "select ubf from UserBuyFlow as ubf where ubf.user.id =?1 and ubf.issue.id=?2";
        query = entityManager.createQuery(hql.toString());
        query.setParameter(1, userId);
        query.setParameter(2, issueId);
        userBuyFlows = query.getResultList();
        return userBuyFlows;
    }

    @Override
    public BuyListModelAjax ajaxFindBuyListByIssueId(Long issueId, Long page, Long pageSize) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = new Sort(direction, "time");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Page<UserBuyFlow> userBuyFlowPage = userBuyFlowRepository.findAll(new Specification<UserBuyFlow>() {
            @Override
            public Predicate toPredicate(Root<UserBuyFlow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate =  cb.equal(root.get("issue").get("id").as(Long.class), issueId);
                return predicate;
            }
        }, new PageRequest(page.intValue() -1, pageSize.intValue(), sort));
        BuyListModelAjax buyListModelAjax = new BuyListModelAjax();
        if(userBuyFlowPage != null){
            List<BuyListModel> rows = new ArrayList<>();
            for(UserBuyFlow userBuyFlow : userBuyFlowPage){
                BuyListModel buyListModel = new BuyListModel();
                buyListModel.setUserHeadUrl(staticResourceService.getResource(userBuyFlow.getUser().getUserHead()).toString());
                buyListModel.setCity(userBuyFlow.getUser().getCityName());
                buyListModel.setIp(userBuyFlow.getUser().getIp());
                buyListModel.setAttendAmount(userBuyFlow.getAmount());
                buyListModel.setDate(simpleDateFormat.format(userBuyFlow.getTime()));
                buyListModel.setNickName(userBuyFlow.getUser().getRealName());
                buyListModel.setPid(userBuyFlow.getId());
                rows.add(buyListModel);
            }
            buyListModelAjax.setRows(rows);
            buyListModelAjax.setPageCount(userBuyFlowPage.getTotalPages());
            buyListModelAjax.setPageIndex(page.intValue());
            buyListModelAjax.setTotal(Integer.parseInt(userBuyFlowPage.getTotalElements() + ""));
            buyListModelAjax.setPageSize(pageSize.intValue());
        }
        return buyListModelAjax;
    }


    @Override
    public UserBuyFlowModelAjax findByUserAjax(Long userId, Integer pageSize, Integer page) throws Exception {
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = new Sort(direction, "time");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Page<UserBuyFlow> userBuyFlowPage = userBuyFlowRepository.findAll(new Specification<UserBuyFlow>() {
            @Override
            public Predicate toPredicate(Root<UserBuyFlow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.and(cb.equal(root.get("user").get("id").as(Long.class), userId));
                predicate = cb.and(predicate, cb.equal(root.get("issue").get("awardingUser").get("id").as(Long.class), userId));
                return predicate;
            }
        }, new PageRequest(page-1, pageSize, sort));

        UserBuyFlowModelAjax userBuyFlowModelAjax = new UserBuyFlowModelAjax();
        if (userBuyFlowPage != null ) {
            List<UserBuyFlowModel> rows = new ArrayList<>();
            for (UserBuyFlow userBuyFlow : userBuyFlowPage) {
                UserBuyFlowModel userBuyFlowModel = new UserBuyFlowModel();
                userBuyFlowModel.setDefaultPictureUrl(staticResourceService.getResource(userBuyFlow.getIssue().getGoods().getDefaultPictureUrl()).toString());
                Delivery delivery = deliveryRepository.findByIssueId(userBuyFlow.getIssue().getId());
                if (delivery==null)
                    continue;
                userBuyFlowModel.setPid(userBuyFlow.getId());
                userBuyFlowModel.setAmount(userBuyFlow.getAmount());
                userBuyFlowModel.setTitle(userBuyFlow.getIssue().getGoods().getTitle());
                userBuyFlowModel.setIssueId(userBuyFlow.getIssue().getId());
                userBuyFlowModel.setAwardingDate(userBuyFlow.getIssue().getAwardingDate());
                userBuyFlowModel.setToAmount(userBuyFlow.getIssue().getToAmount());
                userBuyFlowModel.setLuckyNumber(userBuyFlow.getIssue().getLuckyNumber());
                userBuyFlowModel.setDeliveryStatus(delivery.getDeliveryStatus().getValue());
                //todo 由于程序运行不起来。注释了下面语句 by xhk
                //userBuyFlowModel.setDeliveryId(delivery.getId());
                userBuyFlowModel.setTime(userBuyFlow.getTime());
                userBuyFlowModel.setAwardingDateString(sdf.format(userBuyFlow.getIssue().getAwardingDate()));
                rows.add(userBuyFlowModel);
            }
            userBuyFlowModelAjax.setPageCount(userBuyFlowPage.getTotalPages());
            userBuyFlowModelAjax.setPageIndex(page);
            userBuyFlowModelAjax.setTotal(Integer.parseInt(userBuyFlowPage.getTotalElements() + ""));
            userBuyFlowModelAjax.setPageSize(pageSize);
            userBuyFlowModelAjax.setRows(rows);
        }
        return userBuyFlowModelAjax;
    }

    @Override
    public UserBuyFlow findOne(Long issueId) {
        Query query = null;
        String hq = "select uby from UserBuyFlow as uby where uby.issue.id=?1 and uby.user.id =uby.issue.awardingUser.id";
        query = entityManager.createQuery(hq);
        query.setParameter(1, issueId);
        List<UserBuyFlow> list = query.getResultList();
        if (list != null && list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    @Override
    public Page<UserBuyFlow> getMyInvolvedRecordAjax(Long userId, Integer type, Integer page, Integer pageSize) {
        Sort.Direction direction = Sort.Direction.DESC;
        Sort sort = new Sort(direction, "time");
        return userBuyFlowRepository.findAll(new Specification<UserBuyFlow>() {
            @Override
            public Predicate toPredicate(Root<UserBuyFlow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate predicate = cb.and(cb.equal(root.get("user").get("id").as(Long.class), userId));
                if (type == 1) {
                    predicate = cb.and(predicate, cb.notEqual(root.get("issue").get("status").as(CommonEnum.IssueStatus.class), CommonEnum.IssueStatus.drawed));
                } else if (type == 2) {
                    predicate = cb.and(predicate, cb.equal(root.get("issue").get("status").as(CommonEnum.IssueStatus.class), CommonEnum.IssueStatus.drawed));
                }
                return predicate;
            }
        }, new PageRequest(page-1, pageSize, sort));
    }

    @Override
    public RaiderListModelAjax toListRaiderListModel(Integer type, Long userId, Integer pageSize, Integer page) throws URISyntaxException {
        Page<UserBuyFlow> userBuyFlows = getMyInvolvedRecordAjax(userId,type,page,pageSize);
        if (userBuyFlows==null){
            return null;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<RaiderListModel> listModels = new ArrayList<>();
        RaiderListModelAjax raiderListModelAjax = new RaiderListModelAjax();
        for (UserBuyFlow userBuyFlow : userBuyFlows){
            RaiderListModel appMyRaiderListModel = new RaiderListModel();
            appMyRaiderListModel.setPid(userBuyFlow.getId());
            appMyRaiderListModel.setIssueId(userBuyFlow.getIssue().getId());
            appMyRaiderListModel.setPictureUrl(userBuyFlow.getIssue().getGoods() != null ? staticResourceService.getResource(userBuyFlow.getIssue().getGoods().getDefaultPictureUrl()).toString() : "");
            appMyRaiderListModel.setTitle(userBuyFlow.getIssue().getGoods() != null ? userBuyFlow.getIssue().getGoods().getTitle() : "");
            appMyRaiderListModel.setToAmount(userBuyFlow.getIssue().getToAmount());
            appMyRaiderListModel.setAttendAmount(userBuyFlow.getAmount());
            appMyRaiderListModel.setStatus(userBuyFlow.getIssue().getStatus().getValue());
            appMyRaiderListModel.setTime(new Date(userBuyFlow.getTime()));
            if (type == 2) {
                //============封装以开奖记录的额外数据============
                appMyRaiderListModel.setWinner(userBuyFlow.getIssue().getAwardingUser() == null ? "" : userBuyFlow.getIssue().getAwardingUser().getRealName());
                appMyRaiderListModel.setLunkyNumber(userBuyFlow.getIssue().getLuckyNumber());
                appMyRaiderListModel.setAwardingDate(userBuyFlow.getIssue().getAwardingDate());
                appMyRaiderListModel.setAwardingDateString(sdf.format(userBuyFlow.getIssue().getAwardingDate()));
                List<UserBuyFlow> userBuyFlowList = findByUserIdAndIssuId(userBuyFlow.getUser().getId(), userBuyFlow.getIssue().getId());
                if (userBuyFlowList != null && userBuyFlowList.size() > 0) {
                    appMyRaiderListModel.setWinnerAttendAmount(userBuyFlowList.get(0).getAmount());
                }
            } else if (type == 1) {
                //============封装进行中记录的额外数据============
                if (userBuyFlow.getIssue().getToAmount() - userBuyFlow.getIssue().getBuyAmount() > 0) {
                    appMyRaiderListModel.setRemainAmount(userBuyFlow.getIssue().getToAmount() - userBuyFlow.getIssue().getBuyAmount());
                } else {
                    appMyRaiderListModel.setRemainAmount(0l);
                }
            } else if (type == 0) {
                //============对全部数据进行查询并封装============
                if (userBuyFlow.getIssue().getStatus() == CommonEnum.IssueStatus.drawed) {
                    List<UserBuyFlow> userBuyFlowList = findByUserIdAndIssuId(userBuyFlow.getUser().getId(), userBuyFlow.getIssue().getId());
                    if (userBuyFlowList != null && userBuyFlowList.size() > 0) {
                        //============封装 进行中记录的额外数据============
                        if (userBuyFlowList.get(0).getIssue().getAwardingUser() == null) {
                            appMyRaiderListModel.setWinner("");
                        } else {
                            appMyRaiderListModel.setWinner(userBuyFlowList.get(0).getIssue().getAwardingUser().getRealName());
                        }
                        appMyRaiderListModel.setLunkyNumber(userBuyFlowList.get(0).getIssue().getLuckyNumber());
                        appMyRaiderListModel.setAwardingDate(userBuyFlowList.get(0).getIssue().getAwardingDate());
                        appMyRaiderListModel.setAwardingDateString(sdf.format(userBuyFlow.getIssue().getAwardingDate()));
                        appMyRaiderListModel.setWinnerAttendAmount(0L);
                        appMyRaiderListModel.setWinnerAttendAmount(userBuyFlowList.get(0).getAmount());
                    }
                }
                if (userBuyFlow.getIssue().getToAmount() - userBuyFlow.getIssue().getBuyAmount() > 0) {
                    appMyRaiderListModel.setRemainAmount(userBuyFlow.getIssue().getToAmount() - userBuyFlow.getIssue().getBuyAmount());
                } else {
                    appMyRaiderListModel.setRemainAmount(0l);
                }
            }
            listModels.add(appMyRaiderListModel);
        }
        raiderListModelAjax.setRows(listModels);
        raiderListModelAjax.setPageCount(userBuyFlows.getTotalPages());
        raiderListModelAjax.setPageIndex(page);
        raiderListModelAjax.setTotal(Integer.parseInt(userBuyFlows.getTotalElements() + ""));
        raiderListModelAjax.setPageSize(pageSize);
        return raiderListModelAjax;
    }

    @Autowired
    ProductRestRepository productRestRepository;

    @Override
    public Map<String,Object> getGoodsSpec(Long goodsId) throws IOException {
        Goods goods = goodsRestRepository.getOneByPK(goodsId + "");
        Map<String,Object> returnMap = new HashMap<>();
        Map<Long,String> map = JSONObject.toJavaObject(JSON.parseObject(goods.getSpec()), Map.class);
        List<MallGoodsSpecificationsModel> mgsList = null;
        if(map!=null) {
            returnMap = new HashMap<>();
            mgsList = new ArrayList<>();
            List<Product> productList = productRestRepository.findByGoods(goods);

            for (Map.Entry<Long,String> entry: map.entrySet()){
                MallGoodsSpecificationsModel mgs = new MallGoodsSpecificationsModel();
                mgs.setId(entry.getKey() + "");
                mgs.setName(entry.getValue());
                mgsList.add(mgs);
            }
            SpecDescriptions specDescriptions = goods.getSpecDescriptions();
            for(Map.Entry<Long,List<SpecDescription>> entry:specDescriptions.entrySet()){
                for (int i=0; i< mgsList.size(); i++){
                    if (mgsList.get(i).getId().equals(entry.getKey()+"")){
                        mgsList.get(i).setSpecDescriptionList(entry.getValue());
                        break;
                    }
                }
            }
            returnMap.put("mgsList",mgsList);
            List<MallProductSpecModel> list = new ArrayList<>();
            for(Product product :productList){
                MallProductSpecModel mallSpecModel = new MallProductSpecModel();
                mallSpecModel.setFreeze(product.getFreeze());
                mallSpecModel.setId(product.getId());
                mallSpecModel.setSpec(product.getSpec());
                mallSpecModel.setStock(product.getStock());
                list.add(mallSpecModel);
            }
            returnMap.put("productList",list);
        }
        return returnMap;
    }

}
