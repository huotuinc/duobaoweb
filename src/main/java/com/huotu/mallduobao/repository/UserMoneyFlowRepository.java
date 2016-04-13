package com.huotu.mallduobao.repository;

import com.huotu.mallduobao.entity.UserMoneyFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Created by xhk on 2016/4/6.
 */
public interface UserMoneyFlowRepository extends JpaRepository<UserMoneyFlow,Long>, JpaSpecificationExecutor<UserMoneyFlow> {
}
