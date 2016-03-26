package com.huotu.duobaoweb.repository;


import com.huotu.duobaoweb.entity.UserNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;



/**
 * 用户号码Repository
 * Created by lhx on 2016/1/19.
 */

@Repository
public interface UserNumberRepository extends JpaRepository<UserNumber, Long>, JpaSpecificationExecutor<UserNumber> {


}
