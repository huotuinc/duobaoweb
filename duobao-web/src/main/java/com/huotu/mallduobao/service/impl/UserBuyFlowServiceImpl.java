package com.huotu.mallduobao.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huotu.huobanplus.common.entity.support.ProductSpecification;
import com.huotu.huobanplus.common.entity.support.ProductSpecifications;
import com.huotu.mallduobao.utils.CommonEnum;
import com.huotu.mallduobao.entity.Delivery;
import com.huotu.mallduobao.entity.UserBuyFlow;
import com.huotu.mallduobao.model.*;
import com.huotu.mallduobao.repository.DeliveryRepository;
import com.huotu.mallduobao.repository.UserBuyFlowRepository;
import com.huotu.mallduobao.service.StaticResourceService;
import com.huotu.mallduobao.service.UserBuyFlowService;
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
    public BuyListModelAjax ajaxFindBuyListByIssueId(Long issueId, Long lastFlag, Long page, Long pageSize) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        StringBuilder sb = new StringBuilder("SELECT ubf FROM UserBuyFlow ubf WHERE ubf.issue.id = ?1 ");
        if(lastFlag != null && lastFlag.intValue() > 0){
            sb.append(" and ubf.time < ?2 ");
        }
        sb.append(" order by ubf.time desc");

        Query query = entityManager.createQuery(sb.toString());
        query.setParameter(1, issueId);
        if(lastFlag != null && lastFlag.intValue() > 0){
            query.setParameter(2, lastFlag);
        }

        query.setMaxResults(10);
        List<UserBuyFlow> userBuyFlows = query.getResultList();

        BuyListModelAjax buyListModelAjax = new BuyListModelAjax();
        if(userBuyFlows != null && userBuyFlows.size() > 0){
            List<BuyListModel> rows = new ArrayList<>();
            for(UserBuyFlow userBuyFlow : userBuyFlows){
                BuyListModel buyListModel = new BuyListModel();
                String head = userBuyFlow.getUser().getUserHead();
                if(head != null){
                    buyListModel.setUserHeadUrl(staticResourceService.getResource(userBuyFlow.getUser().getUserHead()).toString());
                }
                buyListModel.setCity(userBuyFlow.getUser().getCityName());
                buyListModel.setIp(userBuyFlow.getUser().getIp());
                buyListModel.setAttendAmount(userBuyFlow.getAmount());
                buyListModel.setDate(simpleDateFormat.format(userBuyFlow.getTime()));
                buyListModel.setNickName(userBuyFlow.getUser().getRealName());
                buyListModel.setPid(userBuyFlow.getId());
                rows.add(buyListModel);
            }
            lastFlag = userBuyFlows.get(userBuyFlows.size() - 1).getTime();
            buyListModelAjax.setRows(rows);
            buyListModelAjax.setPageIndex(page.intValue());
            buyListModelAjax.setPageSize(10);
            buyListModelAjax.setLastFlag(lastFlag);
        }
        return buyListModelAjax;
    }


//    @Override
//    public UserBuyFlowModelAjax findByUserAjax(Long userId, Integer pageSize, Integer page) throws Exception {
//        Sort.Direction direction = Sort.Direction.DESC;
//        Sort sort = new Sort(direction, "time");
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        Page<UserBuyFlow> userBuyFlowPage = userBuyFlowRepository.findAll(new Specification<UserBuyFlow>() {
//            @Override
//            public Predicate toPredicate(Root<UserBuyFlow> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//                Predicate predicate = cb.and(cb.equal(root.get("user").get("id").as(Long.class), userId));
//                predicate = cb.and(predicate, cb.equal(root.get("issue").get("awardingUser").get("id").as(Long.class), userId));
//                return predicate;
//            }
//        }, new PageRequest(page-1, pageSize, sort));
//
//        UserBuyFlowModelAjax userBuyFlowModelAjax = new UserBuyFlowModelAjax();
//        if (userBuyFlowPage != null ) {
//            List<UserBuyFlowModel> rows = new ArrayList<>();
//            for (UserBuyFlow userBuyFlow : userBuyFlowPage) {
//                UserBuyFlowModel userBuyFlowModel = new UserBuyFlowModel();
//                userBuyFlowModel.setDefaultPictureUrl(staticResourceService.getResource(userBuyFlow.getIssue().getGoods().getDefaultPictureUrl()).toString());
//                Delivery delivery = deliveryRepository.findByIssueId(userBuyFlow.getIssue().getId());
//                if (delivery==null)
//                    continue;
//                userBuyFlowModel.setPid(userBuyFlow.getId());
//                userBuyFlowModel.setAmount(userBuyFlow.getAmount());
//                userBuyFlowModel.setTitle(userBuyFlow.getIssue().getGoods().getTitle());
//                userBuyFlowModel.setIssueId(userBuyFlow.getIssue().getId());
//                userBuyFlowModel.setAwardingDate(userBuyFlow.getIssue().getAwardingDate());
//                userBuyFlowModel.setToAmount(userBuyFlow.getIssue().getToAmount());
//                userBuyFlowModel.setLuckyNumber(userBuyFlow.getIssue().getLuckyNumber());
//                userBuyFlowModel.setDeliveryStatus(delivery.getDeliveryStatus().getValue());
//                //todo 由于程序运行不起来。注释了下面语句 by xhk
//                //userBuyFlowModel.setDeliveryId(delivery.getId());
//                userBuyFlowModel.setTime(userBuyFlow.getTime());
//                userBuyFlowModel.setAwardingDateString(sdf.format(userBuyFlow.getIssue().getAwardingDate()));
//                rows.add(userBuyFlowModel);
//            }
//            userBuyFlowModelAjax.setPageCount(userBuyFlowPage.getTotalPages());
//            userBuyFlowModelAjax.setPageIndex(page);
//            userBuyFlowModelAjax.setTotal(Integer.parseInt(userBuyFlowPage.getTotalElements() + ""));
//            userBuyFlowModelAjax.setPageSize(pageSize);
//            userBuyFlowModelAjax.setRows(rows);
//        }
//        return userBuyFlowModelAjax;
//    }

    @Override
    public UserBuyFlowModelAjax findByUserAjax(Long userId,Long lastFlag, Integer pageSize, Integer page) throws Exception {
        StringBuilder hql = new StringBuilder();
        List<UserBuyFlow> userBuyFlows = null;
        Query query = null;
        String hq = "select ubf from UserBuyFlow as ubf where ubf.user.id =?1 and ubf.issue.awardingUser.id=?2";
        hql.append(hq);
        if (lastFlag > 0) {
            hql.append(" and ubf.time<?3 order by ubf.time desc");
        } else {
            hql.append(" order by ubf.time desc");
        }
        query = entityManager.createQuery(hql.toString());
        query.setParameter(1, userId);
        query.setParameter(2, userId);
        if (lastFlag > 0) {
            query.setParameter(3, lastFlag);
        }
        query.setMaxResults(10);
        userBuyFlows = query.getResultList();
        UserBuyFlowModelAjax userBuyFlowModelAjax = new UserBuyFlowModelAjax();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        if (userBuyFlows != null) {
            List<UserBuyFlowModel> rows = new ArrayList<>();
            for (UserBuyFlow userBuyFlow : userBuyFlows) {
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
                userBuyFlowModel.setTime(userBuyFlow.getTime());
                userBuyFlowModel.setAwardingDateString(sdf.format(userBuyFlow.getIssue().getAwardingDate()));
                userBuyFlowModel.setRemainAmount(userBuyFlow.getIssue().getToAmount() - userBuyFlow.getIssue().getBuyAmount());
                lastFlag = userBuyFlowModel.getTime();
                rows.add(userBuyFlowModel);
            }
            if(rows.size()>0){
                lastFlag = rows.get(rows.size()-1).getTime();
            }
            userBuyFlowModelAjax.setLastFlag(lastFlag);
            userBuyFlowModelAjax.setRows(rows);
            userBuyFlowModelAjax.setPageIndex(page);
            userBuyFlowModelAjax.setPageSize(pageSize);
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

//    @Override
//    public RaiderListModelAjax toListRaiderListModel(Integer type, Long userId, Integer pageSize, Integer page) throws URISyntaxException {
//        Page<UserBuyFlow> userBuyFlows = getMyInvolvedRecordAjax(userId,type,page,pageSize);
//        if (userBuyFlows==null){
//            return null;
//        }
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//        List<RaiderListModel> listModels = new ArrayList<>();
//        RaiderListModelAjax raiderListModelAjax = new RaiderListModelAjax();
//        for (UserBuyFlow userBuyFlow : userBuyFlows){
//            RaiderListModel appMyRaiderListModel = new RaiderListModel();
//            appMyRaiderListModel.setPid(userBuyFlow.getId());
//            appMyRaiderListModel.setIssueId(userBuyFlow.getIssue().getId());
//            appMyRaiderListModel.setPictureUrl(userBuyFlow.getIssue().getGoods() != null ? staticResourceService.getResource(userBuyFlow.getIssue().getGoods().getDefaultPictureUrl()).toString() : "");
//            appMyRaiderListModel.setTitle(userBuyFlow.getIssue().getGoods() != null ? userBuyFlow.getIssue().getGoods().getTitle() : "");
//            appMyRaiderListModel.setToAmount(userBuyFlow.getIssue().getToAmount());
//            appMyRaiderListModel.setAttendAmount(userBuyFlow.getAmount());
//            appMyRaiderListModel.setStatus(userBuyFlow.getIssue().getStatus().getValue());
//            appMyRaiderListModel.setTime(new Date(userBuyFlow.getTime()));
//            if (type == 2) {
//                //============封装以开奖记录的额外数据============
//                appMyRaiderListModel.setWinner(userBuyFlow.getIssue().getAwardingUser() == null ? "" : userBuyFlow.getIssue().getAwardingUser().getRealName());
//                appMyRaiderListModel.setLunkyNumber(userBuyFlow.getIssue().getLuckyNumber());
//                appMyRaiderListModel.setAwardingDate(userBuyFlow.getIssue().getAwardingDate());
//                appMyRaiderListModel.setAwardingDateString(sdf.format(userBuyFlow.getIssue().getAwardingDate()));
//                List<UserBuyFlow> userBuyFlowList = findByUserIdAndIssuId(userBuyFlow.getUser().getId(), userBuyFlow.getIssue().getId());
//                if (userBuyFlowList != null && userBuyFlowList.size() > 0) {
//                    appMyRaiderListModel.setWinnerAttendAmount(userBuyFlowList.get(0).getAmount());
//                }
//            } else if (type == 1) {
//                //============封装进行中记录的额外数据============
//                if (userBuyFlow.getIssue().getToAmount() - userBuyFlow.getIssue().getBuyAmount() > 0) {
//                    appMyRaiderListModel.setRemainAmount(userBuyFlow.getIssue().getToAmount() - userBuyFlow.getIssue().getBuyAmount());
//                } else {
//                    appMyRaiderListModel.setRemainAmount(0l);
//                }
//            } else if (type == 0) {
//                //============对全部数据进行查询并封装============
//                if (userBuyFlow.getIssue().getStatus() == CommonEnum.IssueStatus.drawed) {
//                    List<UserBuyFlow> userBuyFlowList = findByUserIdAndIssuId(userBuyFlow.getUser().getId(), userBuyFlow.getIssue().getId());
//                    if (userBuyFlowList != null && userBuyFlowList.size() > 0) {
//                        //============封装 进行中记录的额外数据============
//                        if (userBuyFlowList.get(0).getIssue().getAwardingUser() == null) {
//                            appMyRaiderListModel.setWinner("");
//                        } else {
//                            appMyRaiderListModel.setWinner(userBuyFlowList.get(0).getIssue().getAwardingUser().getRealName());
//                        }
//                        appMyRaiderListModel.setLunkyNumber(userBuyFlowList.get(0).getIssue().getLuckyNumber());
//                        appMyRaiderListModel.setAwardingDate(userBuyFlowList.get(0).getIssue().getAwardingDate());
//                        appMyRaiderListModel.setAwardingDateString(sdf.format(userBuyFlow.getIssue().getAwardingDate()));
//                        appMyRaiderListModel.setWinnerAttendAmount(0L);
//                        appMyRaiderListModel.setWinnerAttendAmount(userBuyFlowList.get(0).getAmount());
//                    }
//                }
//                if (userBuyFlow.getIssue().getToAmount() - userBuyFlow.getIssue().getBuyAmount() > 0) {
//                    appMyRaiderListModel.setRemainAmount(userBuyFlow.getIssue().getToAmount() - userBuyFlow.getIssue().getBuyAmount());
//                } else {
//                    appMyRaiderListModel.setRemainAmount(0l);
//                }
//            }
//            listModels.add(appMyRaiderListModel);
//        }
//        raiderListModelAjax.setRows(listModels);
//        raiderListModelAjax.setPageCount(userBuyFlows.getTotalPages());
//        raiderListModelAjax.setPageIndex(page);
//        raiderListModelAjax.setTotal(Integer.parseInt(userBuyFlows.getTotalElements() + ""));
//        raiderListModelAjax.setPageSize(pageSize);
//        return raiderListModelAjax;
//    }

    @Override
    public RaiderListModelAjax toListRaiderListModel(Integer type, Long userId, Long lastFlag, Integer pageSize, Integer page) throws URISyntaxException {
        StringBuilder hql = new StringBuilder();
        List<UserBuyFlow> userBuyFlows = null;
        Query query = null;

        if (type == 0) {
            String hq = "select ubf from UserBuyFlow as ubf where ubf.user.id =?1";
            hql.append(hq);
            if (lastFlag > 0) {
                hql.append(" and ubf.time<?2 order by ubf.time desc");
            } else {
                hql.append("  order by ubf.time desc");
            }
        } else if (type == 1) {
            String hq = "select ubf from UserBuyFlow as ubf where ubf.user.id =?1 and ubf.issue.status!=com.huotu.mallduobao.utils.CommonEnum.IssueStatus.drawed";
            hql.append(hq);
            if (lastFlag > 0) {
                hql.append(" and ubf.time<?2 order by ubf.time desc");
            } else {
                hql.append(" order by ubf.time desc");
            }
        } else if (type == 2) {
            String hq = "select ubf from UserBuyFlow as ubf where ubf.user.id =?1  and ubf.issue.status=com.huotu.mallduobao.utils.CommonEnum.IssueStatus.drawed";
            hql.append(hq);
            if (lastFlag > 0) {
                hql.append(" and ubf.time<?2 order by ubf.time desc");
            } else {
                hql.append("  order by ubf.time desc");
            }
        }
        query = entityManager.createQuery(hql.toString());

        query.setParameter(1, userId);
        if (lastFlag > 0) {
            query.setParameter(2, lastFlag);
        }
        query.setMaxResults(10);
        userBuyFlows = query.getResultList();
        RaiderListModelAjax raiderListModelAjax = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        List<RaiderListModel> listModels = new ArrayList<>();
        if (userBuyFlows != null) {
            raiderListModelAjax = new RaiderListModelAjax();
            for (UserBuyFlow userBuyFlow : userBuyFlows) {
                RaiderListModel appMyRaiderListModel = new RaiderListModel();
                appMyRaiderListModel.setPid(userBuyFlow.getId());
                appMyRaiderListModel.setIssueId(userBuyFlow.getIssue().getId());
                appMyRaiderListModel.setPictureUrl(userBuyFlow.getIssue().getGoods() != null ? staticResourceService.getResource(userBuyFlow.getIssue().getGoods().getDefaultPictureUrl()).toString() : "");
                appMyRaiderListModel.setTitle(userBuyFlow.getIssue().getGoods() != null ? userBuyFlow.getIssue().getGoods().getTitle() : "");
                appMyRaiderListModel.setToAmount(userBuyFlow.getIssue().getToAmount());
                appMyRaiderListModel.setAttendAmount(userBuyFlow.getAmount());
                appMyRaiderListModel.setStatus(userBuyFlow.getIssue().getStatus().getValue());
                appMyRaiderListModel.setTime(new Date(userBuyFlow.getTime()));
                appMyRaiderListModel.setRemainAmount(userBuyFlow.getIssue().getToAmount() - userBuyFlow.getIssue().getBuyAmount());
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
                lastFlag=appMyRaiderListModel.getTime().getTime();

            }
            raiderListModelAjax.setLastFlag(lastFlag);
            raiderListModelAjax.setRows(listModels);
            raiderListModelAjax.setPageIndex(page);
            raiderListModelAjax.setPageSize(pageSize);
        }
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
            List<Product> productList = productRestRepository.findByGoods(goods);
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
