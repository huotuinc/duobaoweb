package com.huotu.mallduobao.service.impl;

import com.huotu.mallduobao.entity.UserMoneyFlow;
import com.huotu.mallduobao.repository.UserMoneyFlowRepository;
import com.huotu.mallduobao.service.UserMoneyFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by xhk on 2016/4/14.
 */
@Service
public class UserMoneyFlowServiceImpl implements UserMoneyFlowService{
    @Autowired
    private UserMoneyFlowRepository userMoneyFlowRepository;

    @Override
    public List<UserMoneyFlow> getUserMoneyFlowByUserId(Long userId) {
        String hql = "select umf from UserMoneyFlow as umf where umf.user.id =:userid";
        List<UserMoneyFlow> list = userMoneyFlowRepository.queryHql(hql.toString(), query -> {
            query.setParameter("userid", userId);
        });
        return list;
    }

    @Override
    public List<UserMoneyFlow> getUserMoneyFlowByUserIdAndBeforeTime(Long userId, Long seconds) {
        Date date=new Date(new Date().getTime()-seconds);
        String hql = "select umf from UserMoneyFlow as umf where umf.user.id =:userid and umf.time>:date";
        List<UserMoneyFlow> list = userMoneyFlowRepository.queryHql(hql.toString(), query -> {
            query.setParameter("userid", userId);
            query.setParameter("date", date);
        });
        return list;
    }
}
