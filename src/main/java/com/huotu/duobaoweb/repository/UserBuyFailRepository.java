package com.huotu.duobaoweb.repository;

import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.UserBuyFail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by lgh on 2016/4/1.
 */
@Repository
public interface UserBuyFailRepository extends JpaRepository<UserBuyFail, Long> {
    
    List<UserBuyFail> findAllByStatus(CommonEnum.UserBuyFailStatus status);
}
