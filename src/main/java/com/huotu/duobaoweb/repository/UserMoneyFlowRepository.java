package com.huotu.duobaoweb.repository;

import com.huotu.duobaoweb.entity.UserMoneyFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by xhk on 2016/4/6.
 */
public interface UserMoneyFlowRepository extends JpaRepository<UserMoneyFlow,Long>, JpaSpecificationExecutor<UserMoneyFlow> {
}
