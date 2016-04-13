package com.huotu.mallduobao.repository;


import com.huotu.mallduobao.utils.CommonEnum;
import com.huotu.mallduobao.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by admin on 2016/1/21.
 */
@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {
    List<Issue> findAllByStatus(CommonEnum.IssueStatus status);

    @Query("FROM Issue i WHERE i.goods.id = ?1")
    List<Issue> findAllIssueByGoodsId(Long goodsId);
}
