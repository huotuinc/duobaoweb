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

    int countByMobile(String mobile);

    /**
     * 根据用户名获取用户数据
     *
     * @param username
     * @return
     */
    User findByUsername(String username);



    /**
     * 根据微信openid获取用户数据
     *
     * @param weixinOpenId
     * @return
     */
    User findByWeixinOpenId(String weixinOpenId);

}
