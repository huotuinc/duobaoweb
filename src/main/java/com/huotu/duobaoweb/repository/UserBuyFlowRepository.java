package com.huotu.duobaoweb.repository;

import com.huotu.duobaoweb.entity.UserBuyFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by lhx on 2016/1/22.
 */
@Repository
public interface UserBuyFlowRepository extends JpaRepository<UserBuyFlow,Long>, JpaSpecificationExecutor<UserBuyFlow> {

    @Query("Select f From UserBuyFlow f Where f.issue.id = ?1 and f.user.id = ?2")
    List<UserBuyFlow> findAllByIssueAndUser(Long issueId, Long userId);

}
