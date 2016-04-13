package com.huotu.mallduobao.service.impl;


import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.entity.UserNumber;
import com.huotu.mallduobao.model.RaiderNumbersModel;
import com.huotu.mallduobao.repository.UserNumberRepository;
import com.huotu.mallduobao.service.UserNumberService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;

/**
 * 用户号码
 * Created by lhx on 2016/1/25.
 */
@Service
public class UserNumberServiceImpl implements UserNumberService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private UserNumberRepository userNumberRepository;

    private static Log log = LogFactory.getLog(UserNumberServiceImpl.class);

    @Override
    public RaiderNumbersModel getMyRaiderNumbers(Long userId, Long issuId) throws Exception {
        StringBuilder hql = new StringBuilder();
        List<UserNumber> userNumbers = new ArrayList<>();
        Query query = null;
        hql.append("select n from UserNumber as n ");
        hql.append("where n.user.id =?1 ");
        hql.append("and n.issue.id=?2 ");
        hql.append("order by n.time desc");
        query = entityManager.createQuery(hql.toString());
        query.setParameter(1, userId);
        query.setParameter(2, issuId);
        userNumbers = query.getResultList();
        RaiderNumbersModel raiderNumbersModel = new RaiderNumbersModel();
        List<Long> numbers = new ArrayList<Long>();
        for (int i = 0; i < userNumbers.size(); i++) {
            UserNumber userNumber = userNumbers.get(i);
            if (i == 0) {
                raiderNumbersModel.setGoodsTitle(userNumber.getIssue().getGoods().getTitle());
                raiderNumbersModel.setAmount(Long.parseLong(userNumbers.size() + ""));
                raiderNumbersModel.setIssueId(userNumber.getIssue().getId());
            }
            numbers.add(userNumber.getNumber());
        }
        raiderNumbersModel.setNumbers(numbers);
        return raiderNumbersModel;
    }

    @Override
    public List<UserNumber> getLotteryBeforeTop50(Long date) {
        return null;
    }

    @Override
    public List<Long> getUserNumbersByUserAndIssue(User user, Issue issue) {
        List<UserNumber> userNumbers= userNumberRepository.findByIssueAndUser(issue,user);
        List<Long> numbers=new ArrayList<Long>();
        userNumbers.stream().forEach(userNumber->{
            numbers.add(userNumber.getNumber());
        });
        return numbers;
    }


}
