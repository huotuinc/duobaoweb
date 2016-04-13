package com.huotu.mallduobao.repository;


import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.entity.UserNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 用户号码Repository
 * Created by lhx on 2016/1/19.
 */

@Repository
public interface UserNumberRepository extends JpaRepository<UserNumber, Long>, JpaSpecificationExecutor<UserNumber> {
    UserNumber findByIssueAndNumber(Issue issue, Long number);

    List<UserNumber> findByIssue(Issue issue);

    List<UserNumber> findByIssueAndUser(Issue issue, User user);

}
