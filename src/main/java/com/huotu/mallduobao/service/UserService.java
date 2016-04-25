package com.huotu.mallduobao.service;


import com.huotu.mallduobao.common.AuthEntity;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.model.WebPublicModel;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;

/**
 * Created by xhk on 2016/3/25.
 */
public interface UserService {


    /**
     * 由于未登陆直接返回首页
     * @param customerId
     * @return
     */
    String getIndexUrl(HttpServletRequest request,Long customerId) throws UnsupportedEncodingException;

    /**
     * 通过openid注册一个用户
     * @param authEntity
     * @return
     */
    User registerUser(AuthEntity authEntity,String customerId,String ip) throws IOException, URISyntaxException;

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
    String getWeixinAuthUrl(HttpServletRequest request,WebPublicModel common) throws IOException;
}
