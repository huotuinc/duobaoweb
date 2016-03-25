package com.huotu.duobaoweb.repository;


import com.huotu.duobaoweb.entity.User;
import org.luffy.lib.libspring.data.ClassicsRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * Created by xhk on 2016/3/25.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, ClassicsRepository<User>, JpaSpecificationExecutor<User> {



}
