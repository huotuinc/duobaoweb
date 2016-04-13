package com.huotu.duobaoweb.service.impl;


import com.huotu.duobaoweb.common.WeixinAuthUrl;
import com.huotu.duobaoweb.common.thirdparty.MD5Util;
import com.huotu.duobaoweb.entity.Issue;
import com.huotu.duobaoweb.entity.User;
import com.huotu.duobaoweb.model.WebPublicModel;
import com.huotu.duobaoweb.repository.UserRepository;
import com.huotu.duobaoweb.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.UUID;

/**
 * Created by xhk on 2016/3/25.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Log log = LogFactory.getLog(UserServiceImpl.class);

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommonConfigServiceImpl commonConfigService;


    @Override
    public String getIndexUrl(Long issueId,Long customerId) {
        String url = commonConfigService.getWebUrl()+ "/user/getOpid?issueId=";
        if(issueId!=null) {
            url =url + issueId;
        }
        return url+"&customerId="+customerId;
    }

    @Override
    public User registerUser(String openid,String customerId) {
        User user=userRepository.findByWeixinOpenId(openid);
        //如果在数据库中没有这个微信信息则注册一个新的用户
        if(user==null){
            user=new User();
            user.setRegTime(new Date());
            user.setEnabled(true);
            user.setUsername(UUID.randomUUID().toString().replace("-",""));
            user.setWeixinOpenId(openid);
            user.setWeixinBinded(true);
            if(customerId!=null&&!customerId.equals("null")){
                //如果商家id不为空并且不是null字符串在进行一下操作
                user.setMerchantId(Long.parseLong(customerId));
            }
            user=userRepository.saveAndFlush(user);
        }

        return user;
    }

    @Override
    public WebPublicModel getWebPublicModel(User user, Issue issue) {
        String sign=this.getSign(user);
        WebPublicModel webPublicModel=new WebPublicModel();
        if(issue!=null&&issue.getId()!=null) {
            webPublicModel.setCustomerId(issue.getGoods().getMerchantId());
            webPublicModel.setIssueId(issue.getId());
            webPublicModel.setOpenId(user.getWeixinOpenId());
            webPublicModel.setSign(sign);
        }
        webPublicModel.setCurrentUser(user);
        return webPublicModel;
    }

    @Override
    public String getSign(User user) {
        String secretId=user.getMerchantId()+user.getWeixinOpenId()+commonConfigService.getDuobaoKey();
        String sign=MD5Util.MD5Encode(secretId,null);
        return sign;
    }

    @Override
    public boolean checkOpenid(String openId, String sign) {
        User user=userRepository.findByWeixinOpenId(openId);
        if(user!=null&&this.getSign(user).equals(sign)){
                return true;
        }
        return false;
    }

    @Override
    public String getWeixinAuthUrl(WebPublicModel common) throws UnsupportedEncodingException {

        //微信授权回调url，跳转到index
        WeixinAuthUrl.encode_url= java.net.URLEncoder.encode(this.getIndexUrl(common.getIssueId(),common.getCustomerId()), "utf-8");
        WeixinAuthUrl.customerid=common.getCustomerId();
        String apiUrl = WeixinAuthUrl.getWeixinAuthUrl();
        return apiUrl;
    }
}
