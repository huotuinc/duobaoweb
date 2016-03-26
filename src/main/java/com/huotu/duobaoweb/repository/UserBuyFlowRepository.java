package com.huotu.duobaoweb.repository;

import com.huotu.duobaoweb.entity.UserBuyFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;



/**
 * Created by xhk on 2016/1/22.
 */
@Repository
public interface UserBuyFlowRepository extends JpaRepository<UserBuyFlow,Long>, JpaSpecificationExecutor<UserBuyFlow> {


}
