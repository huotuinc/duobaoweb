package com.huotu.duobaoweb.repository;


import com.huotu.duobaoweb.common.CommonEnum;
import com.huotu.duobaoweb.entity.User;
import org.luffy.lib.libspring.data.ClassicsRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by xhk on 2016/3/25.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long>, ClassicsRepository<User>, JpaSpecificationExecutor<User> {



    /**
     * 根据用户名获取用户数据
     *
     * @param token
     * @return
     */
    User findByToken(String token);

    /**
     * 根据用户名获取用户数据
     *
     * @param username
     * @return
     */
    User findByUsername(String username);

    /**
     * 根据用户绑定手机号获取用户数据
     *
     * @param mobile
     * @return
     */
    User findByMobile(String mobile);

    /**
     * 根据用户账号和用户登录类型（微信 ，qq）
     *
     * @param rUserName
     * @param userFromType
     * @return
     */
    User findByUsernameAndUserFromType(String rUserName, CommonEnum.UserFromType userFromType);

    /**
     * 根据用户来源查找所有用户
     * @param userFromType
     * @return
     */
    List<User> findAllByUserFromType(CommonEnum.UserFromType userFromType);

    /**
     *
     * @param unionId
     * @param userFromType
     * @return
     */
    User findByQqUnionIdAndUserFromType(String unionId, CommonEnum.UserFromType userFromType);

    /**
     *
     * @param unionId 用户unionid
     * @param userFromType 用户注册类型
     * @return
     */
    User findByWeixinUnionIdAndUserFromType(String unionId, CommonEnum.UserFromType userFromType);

    /**
     * 得到绑定该qq账户的用户数量
     * @param qq
     * @return
     */
    int countByQqUnionId(String qq);

    /**
     *
     *得到绑定改微信号的用户数量
     * @param weixin
     * @return
     */
    int countByWeixinUnionId(String weixin);

    /**
     * 通过qqunionid 获取用户
     * @param unionId
     * @return
     */
    User findByQqUnionId(String unionId);

    /**
     * 通过微信获取用户
     * @param unionId
     * @return
     */
    User findByWeixinUnionId(String unionId);
}
