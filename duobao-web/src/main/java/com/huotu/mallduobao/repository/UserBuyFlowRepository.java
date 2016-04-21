package com.huotu.mallduobao.repository;

import com.huotu.mallduobao.entity.UserBuyFlow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by xhk on 2016/1/22.
 */
@Repository
public interface UserBuyFlowRepository extends JpaRepository<UserBuyFlow,Long>, JpaSpecificationExecutor<UserBuyFlow> {

    @Query("Select f From UserBuyFlow f Where f.issue.id = ?1 and f.user.id = ?2")
    List<UserBuyFlow> findAllByIssueAndUser(Long issueId, Long userId);

    @Query("SELECT min(f.time) FROM UserBuyFlow f where f.issue.id = ?1")
    Long getFirstBuyTimeByIssueId(Long issueId);


    @Query("select uby from UserBuyFlow as uby where uby.issue.id=?1 and uby.user.id =uby.issue.awardingUser.id")
    UserBuyFlow findOneLotteryInfo(Long issueId);

}
