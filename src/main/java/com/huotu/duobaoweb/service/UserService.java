package com.huotu.duobaoweb.service;


import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.model.WebPublicModel;

import java.io.UnsupportedEncodingException;

/**
 * Created by xhk on 2016/3/25.
 */
public interface UserService {


    /**
     * 由于未登陆直接返回首页
     * @param issueId
     * @return
     */
    String getIndexUrl(Long issueId,Long customerId);

    /**
     * 通过openid注册一个用户
     * @param openid
     * @return
     */
    User registerUser(String openid);

    /**
     * 获取公用model
     * @param user
     * @param issue
     * @return
     */
    WebPublicModel getWebPublicModel(User user, Issue issue);

    /**
     * 通过sign与openid进行校验是否是合法用户
     * @param user
     * @return
     */
    String getSign(User user);

    /**
     * 校验openid是否是正确的
     * @param openId
     * @param sign
     * @return
     */
    boolean checkOpenid(String openId, String sign);

    /**
     * 得到用户认证的地址
     * @param common
     * @return
     */
    String getWeixinAuthUrl(WebPublicModel common) throws UnsupportedEncodingException;
}
