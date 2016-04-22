package com.huotu.mallduobao.service.impl;

import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.UserBuyFlow;
import com.huotu.mallduobao.entity.UserNumber;
import com.huotu.mallduobao.model.admin.*;
import com.huotu.mallduobao.repository.GoodsRepository;
import com.huotu.mallduobao.repository.IssueRepository;
import com.huotu.mallduobao.repository.UserBuyFlowRepository;
import com.huotu.mallduobao.repository.UserNumberRepository;
import com.huotu.mallduobao.service.LotteryService;
import com.huotu.mallduobao.utils.CommonEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by lhx on 2016/4/1.
 */
@Service
public class LotteryServiceImpl implements LotteryService {

    @Autowired
    IssueRepository issueRepository;

    @Autowired
    UserBuyFlowRepository duoBaoUserBuyFlowRepository;

    @Autowired
    UserNumberRepository duoBaoUserNumberRepository;

    @Autowired
    GoodsRepository duoBaoGoodsRepository;

    @Override
    public WebPersonnalIssueListModel getWebIssueListModel(WebIssueSearchModel webIssueSearchModel,Long customerId) {
        //排序
        Sort.Direction direction = webIssueSearchModel.getRaSortType() == 0 ? Sort.Direction.DESC : Sort.Direction.ASC;
        Sort sort = new Sort(direction, "id");
        //每页的条数
        int number = 20;
        Page<Issue> issuePage = issueRepository.findAll(new Specification<Issue>() {
            @Override
            public Predicate toPredicate(Root<Issue> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                Predicate predicate = cb.and(cb.equal(root.get("status").as(CommonEnum.IssueStatus.class), CommonEnum.IssueStatus.drawed));

                if (!StringUtils.isEmpty(webIssueSearchModel.getUserId())) {
                    predicate = cb.and(predicate, cb.equal(root.get("awardingUser").get("id").as(Long.class), webIssueSearchModel.getUserId()));
                }

                if (!StringUtils.isEmpty(webIssueSearchModel.getUsername())) {
                    predicate = cb.and(predicate, cb.like(root.get("awardingUser").get("username").as(String.class), "%" + webIssueSearchModel.getUsername() + "%"));
                }

                if (!StringUtils.isEmpty(webIssueSearchModel.getIssueId())) {
                    predicate = cb.and(predicate, cb.equal(root.get("id").as(Long.class), webIssueSearchModel.getIssueId()));
                }

                if (!StringUtils.isEmpty(webIssueSearchModel.getGoodsTitle())) {
                    predicate = cb.and(predicate, cb.like(root.get("goods").get("title").as(String.class), "%" + webIssueSearchModel.getGoodsTitle() + "%"));
                }

                if (!StringUtils.isEmpty(customerId)) {
                    predicate = cb.and(predicate, cb.equal(root.get("goods").get("merchantId").as(Long.class), customerId));
                }

                if (!StringUtils.isEmpty(webIssueSearchModel.getGoodsId())) {
                    predicate = cb.and(predicate, cb.equal(root.get("goods").get("id").as(Long.class), webIssueSearchModel.getGoodsId()));
                }

                if (!StringUtils.isEmpty(webIssueSearchModel.getStartTime())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = null;
                    try {
                        date = sdf.parse(webIssueSearchModel.getStartTime());
                    } catch (ParseException e) {
                        throw new RuntimeException("字符串转日期失败");
                    }
                    predicate = cb.and(predicate, cb.greaterThanOrEqualTo(root.get("awardingDate").as(Date.class), date));
                }
                if (!StringUtils.isEmpty(webIssueSearchModel.getEndTime())) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = null;
                    try {
                        date = sdf.parse(webIssueSearchModel.getEndTime());
                    } catch (ParseException e) {
                        throw new RuntimeException("字符串转日期失败");
                    }
                    predicate = cb.and(predicate, cb.lessThanOrEqualTo(root.get("awardingDate").as(Date.class), date));
                }
                return predicate;
            }
        }, new PageRequest(webIssueSearchModel.getPageNoStr(), number, sort));
        WebPersonnalIssueListModel webPersonnalIssueListModel = new WebPersonnalIssueListModel();
        List<WebIssueListModel> list = new ArrayList<>();
        for (Issue issue : issuePage) {
            WebIssueListModel webIssueListModel = new WebIssueListModel();
            webIssueListModel.setId(issue.getId());
            webIssueListModel.setAwardingDate(issue.getAwardingDate());
            webIssueListModel.setGoodsTitle(issue.getGoods() != null ? issue.getGoods().getTitle() : null);
            webIssueListModel.setLuckyNumber(issue.getLuckyNumber());
            webIssueListModel.setAwardingUser(issue.getAwardingUser() != null ? issue.getAwardingUser().getUsername() : null);
            list.add(webIssueListModel);
        }
        webPersonnalIssueListModel.setList(list);
        webPersonnalIssueListModel.setTotalPages(issuePage.getTotalPages());
        webPersonnalIssueListModel.setTotalRecords(issuePage.getTotalElements());
        return webPersonnalIssueListModel;
    }


    @Override
    public WebLotteryInfoModel getWebLotteryInfoModel(Long issueId) throws Exception {
        UserBuyFlow userBuyFlow = duoBaoUserBuyFlowRepository.findOneLotteryInfo(issueId);
        WebLotteryInfoModel webLotteryInfoModel = null;
        if (userBuyFlow != null) {
            webLotteryInfoModel = new WebLotteryInfoModel();
            webLotteryInfoModel.setId(userBuyFlow.getId());
            webLotteryInfoModel.setAwardingUser(userBuyFlow.getIssue().getAwardingUser() != null ? userBuyFlow.getIssue().getAwardingUser().getUsername() : null);
            webLotteryInfoModel.setAwardingDate(userBuyFlow.getIssue().getAwardingDate());
            webLotteryInfoModel.setGoodsTitle(userBuyFlow.getIssue().getGoods() != null ? userBuyFlow.getIssue().getGoods().getTitle() : null);
            webLotteryInfoModel.setAmount(userBuyFlow.getAmount());
            webLotteryInfoModel.setIssueId(userBuyFlow.getIssue().getId());
            webLotteryInfoModel.setLuckyNumber(userBuyFlow.getIssue().getLuckyNumber() + "");
            webLotteryInfoModel.setTime(new Date(userBuyFlow.getTime()));
            webLotteryInfoModel.setUserId(userBuyFlow.getUser().getId());
        }
        return webLotteryInfoModel;
    }

    @Override
    public List<WebUserNumberModel>  getMyRaiderNumbers(Long userId, Long issueId) throws Exception {

        List<UserNumber> userNumbers = duoBaoUserNumberRepository.findByUserAndIssueNumbers(userId,issueId);
        List<WebUserNumberModel> list = new ArrayList<>();
        for (UserNumber userNumber : userNumbers){
            WebUserNumberModel webUserNumberModel = new WebUserNumberModel();
            webUserNumberModel.setNumber(userNumber.getNumber());
            webUserNumberModel.setUser(userNumber.getUser());
            webUserNumberModel.setIssue(userNumber.getIssue());
            webUserNumberModel.setOrders(userNumber.getOrders());
            webUserNumberModel.setTime(userNumber.getTime());
            list.add(webUserNumberModel);
        }
        return list;
    }
}
