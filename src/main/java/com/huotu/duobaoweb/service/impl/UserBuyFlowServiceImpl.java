package com.huotu.duobaoweb.service.impl;

import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.Delivery;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.entity.UserBuyFlow;
import com.huotu.duobaoweb.model.BuyListModel;
import com.huotu.duobaoweb.model.RaiderListModel;
import com.huotu.duobaoweb.model.UserBuyFlowModel;
import com.huotu.duobaoweb.repository.DeliveryRepository;
import com.huotu.duobaoweb.repository.UserBuyFlowRepository;
import com.huotu.duobaoweb.service.StaticResourceService;
import com.huotu.duobaoweb.service.UserBuyFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

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

    @Override
    public RaiderListModel[] findByUserIdAndType(Long userId, Integer type, Long lastTime) throws Exception {
        StringBuilder hql = new StringBuilder();
        List<UserBuyFlow> userBuyFlows = null;
        Query query = null;

        if (type == 0) {
            String hq = "select ubf from UserBuyFlow as ubf where ubf.user.id =?1";
            hql.append(hq);
            if (lastTime > 0) {
                hql.append(" and ubf.time<?2 order by ubf.time desc");
            } else {
                hql.append("  order by ubf.time desc");
            }
        } else if (type == 1) {
            String hq = "select ubf from UserBuyFlow as ubf where ubf.user.id =?1 and ubf.issue.status!=com.huotu.duobao.model.common.CommonEnum.IssueStatus.drawed";
            hql.append(hq);
            if (lastTime > 0) {
                hql.append(" and ubf.time<?2 order by ubf.time desc");
            } else {
                hql.append(" order by ubf.time desc");
            }
        } else if (type == 2) {
            String hq = "select ubf from UserBuyFlow as ubf where ubf.user.id =?1  and ubf.issue.status=com.huotu.duobao.model.common.CommonEnum.IssueStatus.drawed";
            hql.append(hq);
            if (lastTime > 0) {
                hql.append(" and ubf.time<?2 order by ubf.time desc");
            } else {
                hql.append("  order by ubf.time desc");
            }
        }
        query = entityManager.createQuery(hql.toString());

        query.setParameter(1, userId);
        if (lastTime > 0) {
            query.setParameter(2, lastTime);
        }
        query.setMaxResults(10);
        userBuyFlows = query.getResultList();
        RaiderListModel[] raiderListModels = null;
        if (userBuyFlows != null) {
            raiderListModels = new RaiderListModel[userBuyFlows.size()];
            for (int i = 0; i < userBuyFlows.size(); i++) {
                UserBuyFlow userBuyFlow = userBuyFlows.get(i);
                //============对基本数据进行封装==============
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
                raiderListModels[i] = appMyRaiderListModel;
            }
        }
        return raiderListModels;
    }

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
    public UserBuyFlowModel[] findByUser(User user, Long lastTime) throws Exception {
        StringBuilder hql = new StringBuilder();
        Query query = null;
        String hq = "select uby from UserBuyFlow uby,Issue i where i.id=uby.issue.id and i.awardingUser.id=?1 and uby.user.id=?2 ";
        hql.append(hq);
        if (lastTime > 0) {
            hql.append("  and uby.time<?3 order by uby.time desc");
        } else {
            hql.append("  order by uby.time desc");
        }
        query = entityManager.createQuery(hql.toString());
        query.setParameter(1, user.getId());
        query.setParameter(2, user.getId());
        if (lastTime>0) {
            query.setParameter(3, lastTime);
        }
        query.setMaxResults(10);
        List<UserBuyFlow> list = query.getResultList();
        UserBuyFlowModel[] appUserBuyFlowModels = null;
        if (list != null && list.size() > 0) {
            appUserBuyFlowModels = new UserBuyFlowModel[list.size()];
            for (int i = 0; i < list.size(); i++) {
                UserBuyFlowModel userBuyFlowModel = new UserBuyFlowModel();
                UserBuyFlow userBuyFlow = list.get(i);
                userBuyFlowModel.setDefaultPictureUrl(staticResourceService.getResource(userBuyFlow.getIssue().getGoods().getDefaultPictureUrl()).toString());
                Delivery delivery = deliveryRepository.findByIssueId(userBuyFlow.getIssue().getId());
                if (delivery==null)
                    continue;
                userBuyFlowModel.setAmount(userBuyFlow.getAmount());
                userBuyFlowModel.setTitle(userBuyFlow.getIssue().getGoods().getTitle());
                userBuyFlowModel.setIssueId(userBuyFlow.getIssue().getId());
                userBuyFlowModel.setAwardingDate(userBuyFlow.getIssue().getAwardingDate());
                userBuyFlowModel.setToAmount(userBuyFlow.getIssue().getToAmount());
                userBuyFlowModel.setLuckyNumber(userBuyFlow.getIssue().getLuckyNumber());
                userBuyFlowModel.setPid(userBuyFlow.getId());
                userBuyFlowModel.setDeliveryStatus(delivery.getDeliveryStatus().getValue());
                userBuyFlowModel.setDeliveryId(delivery.getId());
                userBuyFlowModel.setTime(userBuyFlow.getTime());
                appUserBuyFlowModels[i] = userBuyFlowModel;
            }
        }
        return appUserBuyFlowModels;
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
    public BuyListModel[] findByIssuIdList(Long issueId, Long lastId) throws URISyntaxException {
        StringBuilder hql = new StringBuilder();
        List<UserBuyFlow> ordersItemList = null;
        Query query = null;
        String hq = "select uby from UserBuyFlow as uby where  uby.issue.id = ?1 ";
        if (lastId != null && lastId.intValue() != 0) {
            hql.append(hq + "  and uby.time <?2 order by uby.time desc");
        } else {
            hql.append(hq + "  order by uby.time desc");
        }
        query = entityManager.createQuery(hql.toString());
        query.setParameter(1, issueId);
        if (lastId != null && lastId.intValue() != 0) {
            query.setParameter(2, lastId);
        }
        query.setMaxResults(10);
        List<UserBuyFlow> list = query.getResultList();
        BuyListModel[] appBuyListModels = null;
        if (list != null && list.size() > 0) {
            appBuyListModels = new BuyListModel[list.size()];
            for (int i = 0; i < list.size(); i++) {
                BuyListModel buyListModel = new BuyListModel();
                UserBuyFlow userBuyFlow = list.get(i);
                buyListModel.setUserHeadUrl(staticResourceService.getResource(userBuyFlow.getUser().getUserHead()).toString());
                buyListModel.setAttendAmount(userBuyFlow.getAmount());
                buyListModel.setCity(userBuyFlow.getUser().getCityName());
                buyListModel.setIp(userBuyFlow.getUser().getIp());
                buyListModel.setDate(new Date(userBuyFlow.getTime()));
                buyListModel.setNickName(userBuyFlow.getUser().getRealName());
                buyListModel.setPid(userBuyFlow.getId());
                appBuyListModels[i] = buyListModel;
            }
        }
        return appBuyListModels;
    }


}
