package com.huotu.mallduobao.repository;


import com.huotu.mallduobao.entity.CachedIssueLeaveNumber;
import com.huotu.mallduobao.entity.pk.CachedIssueLeaveNumberPK;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by lgh on 2016/1/27.
 */
@Repository
public interface CachedIssueLeaveNumberRepository extends JpaRepository<CachedIssueLeaveNumber, CachedIssueLeaveNumberPK> {

    @Query("select d.number from CachedIssueLeaveNumber d where d.issueId=?1")
    List<Long> findAllByIssueId(Long issueId);
}
