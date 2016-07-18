package com.huotu.mallduobao.service.impl;


import com.huotu.huobanplus.sdk.common.repository.MerchantRestRepository;
import com.huotu.mallduobao.common.AuthEntity;
import com.huotu.mallduobao.common.PublicParameterHolder;
import com.huotu.mallduobao.common.WeixinAuthUrl;
import com.huotu.mallduobao.common.thirdparty.MD5Util;
import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.model.WebPublicModel;
import com.huotu.mallduobao.repository.UserRepository;
import com.huotu.mallduobao.service.StaticResourceService;
import com.huotu.mallduobao.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by xhk on 2016/3/25.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Log log = LogFactory.getLog(UserServiceImpl.class);

    @Autowired
    private StaticResourceService staticResourceService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommonConfigServiceImpl commonConfigService;

    @Autowired
    MerchantRestRepository merchantRestRepository;

    @Autowired
    EntityManager entityManager;


    @Override
    public String getIndexUrl(HttpServletRequest request,Long customerId) throws UnsupportedEncodingException {
        String url = commonConfigService.getWebUrl() + "/user/getOpid?redirectUrl=" + URLEncoder.encode(request.getRequestURL()
                + (StringUtils.isEmpty(request.getQueryString()) ? "" : "?" + request.getQueryString()), "utf-8");
        return url+"&customerId="+customerId;
    }

    @Override
    public String getAppIndexUrl(HttpServletRequest request, Long customerId, String openId) throws UnsupportedEncodingException{
        String goodsId = request.getParameter("goodsId");
        String url = commonConfigService.getWebUrl() + "/user/appAccredit?redirectUrl=" + URLEncoder.encode(commonConfigService.getWebUrl() + "/goods/index?goodsId=" + goodsId + "&customerId=" + customerId, "utf-8");
        return url+"&customerId="+customerId+"&openId=" + openId;
    }

    @Override
    public String getReturnUrl(HttpServletRequest request, Long customerId) throws UnsupportedEncodingException {
        String goodsId = request.getParameter("goodsId");
        return URLEncoder.encode(commonConfigService.getWebUrl() + "/goods/index?goodsId=" + goodsId + "&customerId=" + customerId, "utf-8");
    }


    @Override
    public synchronized User registerUser(AuthEntity authEntity,String customerId,String ip) throws IOException, URISyntaxException {
        User user=userRepository.findByWeixinOpenId(authEntity.getOpenid());
        //User user = findByWeixinOpenId(authEntity.getOpenid());
        //如果在数据库中没有这个微信信息则注册一个新的用户
        if(user==null){
            user=new User();
            user.setRegTime(new Date());
            user.setEnabled(true);

            user.setUsername(UUID.randomUUID().toString().replace("-",""));
            user.setWeixinOpenId(authEntity.getOpenid());
            user.setRealName(Mb4Helper.utf8mb4Remove(authEntity.getNickname()));
            user.setIp(ip);

            //设置用户头像
            String head=authEntity.getHeadimgurl();
            String fileName = head.substring(head.lastIndexOf("/") + 1);
            String headPath = StaticResourceService.USER_HEAD_PATH + UUID.randomUUID().toString() + fileName;
            //String pat="http://wx.qlogo.cn/mmopen/HAVcjF9mzkWewuoejVjwuBtDgLMxYbiafyMZmk0ZmU3VeWy6micgDeiadOIug6heOmHPKibILKibZzEvqXp0NOuF73PK5UIehDNyd/0";
            if (head != null) {
                URL uri = new URL(head);
                InputStream in = uri.openStream();
                BufferedImage bufferedImage = ImageIO.read(in);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", baos);
                staticResourceService.uploadResource(headPath, new ByteArrayInputStream(baos.toByteArray()));
                user.setUserHead(headPath);
            }

            user.setWeixinBinded(true);
            if(customerId!=null&&!customerId.equals("null")){
                //如果商家id不为空并且不是null字符串在进行一下操作
                user.setMerchantId(Long.parseLong(customerId));
            }
        }else{
            user.setIp(ip);
        }

        return userRepository.saveAndFlush(user);
    }

    @Override
    public synchronized User registerAppUser(String customerId,String openId, String ip, String userId, String wxheadimg, String wxnickname) throws IOException, URISyntaxException {
        User user = userRepository.findByWeixinOpenId(openId);
        if(user == null){
            user = new User();
            user.setRegTime(new Date());
            user.setEnabled(true);

            user.setUsername(UUID.randomUUID().toString().replace("-", ""));
            user.setWeixinOpenId(openId);
            user.setRealName(Mb4Helper.utf8mb4Remove(wxnickname));

            String head = wxheadimg;
            String fileName = head.substring(head.lastIndexOf("/") + 1);
            String headPath = StaticResourceService.USER_HEAD_PATH + UUID.randomUUID().toString() + fileName;
            if (head != null) {
                URL uri = new URL(head);
                InputStream in = uri.openStream();
                BufferedImage bufferedImage = ImageIO.read(in);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "jpg", baos);
                staticResourceService.uploadResource(headPath, new ByteArrayInputStream(baos.toByteArray()));
                user.setUserHead(headPath);
            }

            user.setWeixinBinded(true);
            if(customerId!=null&&!customerId.equals("null")){
                //如果商家id不为空并且不是null字符串在进行一下操作
                user.setMerchantId(Long.parseLong(customerId));
            }
        }else{
            user.setIp(ip);
        }
        return userRepository.saveAndFlush(user);
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
        //User user = findByWeixinOpenId(openId);
        if(user!=null&&this.getSign(user).equals(sign)){
                return true;
        }
        return false;
    }

    @Override
    public String getWeixinAuthUrl(HttpServletRequest request,WebPublicModel common) throws IOException {


        //微信授权回调url，跳转到index
        WeixinAuthUrl.encode_url= java.net.URLEncoder.encode(this.getIndexUrl(request,common.getCustomerId()), "utf-8");
        WeixinAuthUrl.customerid=common.getCustomerId();
        WeixinAuthUrl.subdomain=merchantRestRepository.getOneByPK(String.valueOf(WeixinAuthUrl.customerid)).getSubDomain();
        WeixinAuthUrl.maindomain=commonConfigService.getMainDomain();
        String apiUrl = WeixinAuthUrl.getWeixinAuthUrl();
        return apiUrl;
    }

    @Override
    public User findByMobile(String mobile) {
        WebPublicModel parameters = PublicParameterHolder.getParameters();
        Long customerId = parameters.getCustomerId();
        return userRepository.findByMobileAndMerchantId(mobile, customerId);
    }

}
