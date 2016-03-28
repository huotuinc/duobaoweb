package com.huotu.duobaoweb.repository;


import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by admin on 2016/1/21.
 */
@Repository
public interface IssueRepository extends JpaRepository<Issue, Long>, JpaSpecificationExecutor<Issue> {
    List<Issue> findAllByStatus(CommonEnum.IssueStatus status);


}
