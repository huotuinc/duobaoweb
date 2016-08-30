package com.huotu.mallduobao.common;

import com.huotu.huobanplus.common.entity.Merchant;
import com.huotu.huobanplus.sdk.common.repository.MerchantRestRepository;
import com.huotu.mallduobao.entity.User;
import com.huotu.mallduobao.model.WebPublicModel;
import com.huotu.mallduobao.repository.UserRepository;
import com.huotu.mallduobao.service.CommonConfigService;
import com.huotu.mallduobao.service.UserService;
import com.huotu.mallduobao.utils.StringHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lgh on 2016/3/28.
 */
public class WebInterceptor implements HandlerInterceptor {

    private static Log log = LogFactory.getLog(WebInterceptor.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MerchantRestRepository merchantRestRepository;

    @Autowired
    private CommonConfigService commonConfigService;

    @Autowired
    private UserService userService;

    @Autowired
    private Environment environment;


//    private URL redirectURLForAPPInfo(Long customerId, String openId, String redirectUrl) throws IOException {
//        StringBuilder builder = new StringBuilder();
//        String customerUrl = getMerchantMainWebURL(customerId);
//        List<NameValuePair> list = new ArrayList<>();
//        String encodeUrl = URLEncoder.encode(redirectUrl, "UTF-8");
//        list.add(new BasicNameValuePair("customerid", String.valueOf(customerId)));
//        list.add(new BasicNameValuePair("redirecturl", encodeUrl));
//        list.add(new BasicNameValuePair("openid", openId));
//        list.sort(((o1, o2) -> o1.getName().compareTo(o2.getName())));
//        StringBuilder toSign = new StringBuilder(list.get(0).getName()).append("=").append(list.get(0).getValue());
//        for (int i = 1; i < list.size(); i++) {
//            NameValuePair pair = list.get(i);
//            if (pair.getValue() == null || pair.getValue().length() == 0)
//                continue;
//            toSign.append("&").append(pair.getName())
//                    .append("=").append(pair.getValue());
//        }
//        toSign.append(commonConfigService.getHuobanplusCustomerApiAppsecrect());
//        String sign = DigestUtils.md5DigestAsHex(toSign.toString().getBytes("UTF-8")).toLowerCase();
//        list.add(new BasicNameValuePair("sign", sign));
//        builder.append(customerUrl).append("/API/MemberAPI.aspx").append("?").append(list.get(0).getName()).append("=")
//                .append(list.get(0).getValue());
//
//        for (int i = 1; i < list.size(); i++) {
//            NameValuePair pair = list.get(i);
//            if (pair.getValue() == null || pair.getValue().length() == 0)
//                continue;
//            builder.append("&").append(pair.getName())
//                    .append("=").append(URLEncoder.encode(pair.getValue(), "UTF-8"));
//        }
//
//        return new URL(builder.toString());
//    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String currentRequestUrl = request.getRequestURL() + (StringUtils.isEmpty(request.getQueryString()) ? "" : "?" + request.getQueryString());
        log.info(currentRequestUrl);
        log.info("head:" + request.getHeader("User-Agent").toLowerCase());

        if (!environment.acceptsProfiles("development")) {
            if (StringUtils.isEmpty(request.getParameter("customerId") == null)) {
                log.info("customerId is null");
                response.sendRedirect("/html/error.html");
                return false;
            }
            Long customerId = Long.parseLong(request.getParameter("customerId"));

            SystemEnvironment systemEnvironment = getSystemEnvironment(request);
            WebPublicModel webPublicModel = initPublicParam(request, systemEnvironment);

            switch (systemEnvironment) {
                case WEIXIN:
                    if (webPublicModel.getOpenId() == null || webPublicModel.getSign() == null
                            || !userService.webAuth(webPublicModel.getOpenId(), webPublicModel.getSign())
                            || !customerId.equals(webPublicModel.getCustomerId())) {
                        log.info("weixin container");
                        if (!customerId.equals(webPublicModel.getCustomerId())) {
                            //如果商户号与cookie中存的不一样则以传过来的商户为准进行请求
                            webPublicModel.setCustomerId(customerId);
                        }

                        String openidUrl = userService.getWeixinAuthUrl(currentRequestUrl, webPublicModel);
                        response.sendRedirect(openidUrl);
                        return false;
                    }

                    break;
                case APP:
                    log.info("app container");
//                    request.getParameter("goodsId")
//                    String returnUrl = userService.getReturnUrl(request, customerId);
//                    String redirectUrl = userService.getAppIndexUrl(request, customerId, openId);
//                    response.sendRedirect(redirectURLForAPPInfo(customerId, openId, redirectUrl).toString());


                    boolean checkPass = false;
                    if (checkAppSign(webPublicModel)) {
                        log.info("app auth check pass");
                        if (webPublicModel.getOpenId() != null) {
                            User user = userRepository.findByWeixinOpenId(webPublicModel.getOpenId());
                            if (user == null) {
                                //注册个用户
                                user = userService.registerAppUser(webPublicModel.getOpenId(), getIp(request));
                                webPublicModel.setCurrentUser(user);
                            }
                        }

                        checkPass = true;
                    }

                    if (!checkPass) {
                        log.info("app auth check no pass,begin to login page");
                        StringBuilder url = new StringBuilder(getMerchantMainWebURL(webPublicModel.getCustomerId()));
                        url.append("/UserCenter/Login.aspx").append("?").append("customerid=").append(webPublicModel.getCustomerId()).append("redirectURL=").append(URLEncoder.encode(currentRequestUrl, "utf-8"));
                        response.sendRedirect(url.toString());
                        return false;
                    }
                    break;
                case UNKNOW:
                    throw new Exception("not support ,please use weixin or app");
            }

            PublicParameterHolder.putParameters(webPublicModel);

        } else {

            WebPublicModel webPublicModel = new WebPublicModel();
            //在测试环境下，获取所有用户中的第一个用户，如果数据库中没有用户则请求失败
            List<User> userList = userRepository.findAll();
            if (userList == null || userList.size() < 1) {
                return false;
            }
            //默认取第一个用户
            User user = userList.get(0);
            webPublicModel.setIp(getIp(request));
            if (!StringUtils.isEmpty(request.getParameter("issueId")))
                webPublicModel.setIssueId(Long.parseLong(request.getParameter("issueId")));
            webPublicModel.setOpenId(user.getWeixinOpenId());
            webPublicModel.setSign(userService.getSign(user));
            webPublicModel.setCurrentUser(user);
            webPublicModel.setCustomerId(user.getMerchantId());
            PublicParameterHolder.putParameters(webPublicModel);
        }
        return true;
    }

    /**
     * 获取系统环境
     *
     * @return
     */
    private SystemEnvironment getSystemEnvironment(HttpServletRequest request) {
        String header = request.getHeader("User-Agent").toLowerCase();
        if (header.indexOf("micromessenger") > 0) {
            return SystemEnvironment.WEIXIN;
        } else if (header.contains("mobile")) {
            return SystemEnvironment.APP;
        }
        return SystemEnvironment.UNKNOW;
    }


    private WebPublicModel initPublicParam(HttpServletRequest request, SystemEnvironment systemEnvironment) {
        WebPublicModel webPublicModel = new WebPublicModel();
        webPublicModel.setIp(getIp(request));
        if (!StringUtils.isEmpty(request.getParameter("issueId")))
            webPublicModel.setIssueId(Long.parseLong(request.getParameter("issueId")));

        if (systemEnvironment == SystemEnvironment.WEIXIN) {
            //获取cookies
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                Map<String, String> map = new HashMap<String, String>();
                for (Cookie cookie : cookies) {
                    map.put(cookie.getName(), cookie.getValue());
                }

                if (map.get("customerId") != null) webPublicModel.setCustomerId(Long.parseLong(map.get("customerId")));
                if (map.get("openId") != null) webPublicModel.setOpenId(map.get("openId"));
                if (webPublicModel.getOpenId() != null)
                    webPublicModel.setCurrentUser(userRepository.findByWeixinOpenId(webPublicModel.getOpenId()));
                if (map.get("sign") != null) webPublicModel.setSign(map.get("sign"));
            }
        } else if (systemEnvironment == SystemEnvironment.APP) {
            String[] data = StringHelper.getAppHeaderInfo(request.getHeader("User-Agent"));
            if (data != null && data.length == 4) {
//                    String userId = data[1];
//                    String unionid = data[2];
//                    String openId = data[3];
                webPublicModel.setMallUserId(Long.parseLong(data[1]));
                webPublicModel.setUnionId(data[2]);
                webPublicModel.setOpenId(data[3]);
                webPublicModel.setSign(data[0]);
                if (webPublicModel.getOpenId() != null)
                    webPublicModel.setCurrentUser(userRepository.findByWeixinOpenId(webPublicModel.getOpenId()));
            }
            if (!StringUtils.isEmpty(request.getParameter("customerId")))
                webPublicModel.setCustomerId(Long.parseLong(request.getParameter("customerId")));
        }
        return webPublicModel;
    }


    private String getMerchantMainWebURL(Long customerId) throws IOException {
        Merchant merchant = merchantRestRepository.getOneByPK(customerId);
        String commonURL = getMerchantSubDomain(merchant.getSubDomain());
        return commonURL;
    }

    private String getMerchantSubDomain(String subDomain) {
        if (subDomain == null) {
            subDomain = "";
        }
        return "http://" + subDomain + "." + commonConfigService.getMainDomain();
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (!StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            int index = ip.indexOf(",");
            if (index != -1) {
                return ip.substring(0, index);
            } else {
                return ip;
            }
        }
        ip = request.getHeader("X-Real-IP");
        if (!StringUtils.isEmpty(ip) && !"unKnown".equalsIgnoreCase(ip)) {
            return ip;
        }
        return request.getRemoteAddr();
    }

    /**
     * 判断签名是否正确
     *
     * @param webPublicModel
     * @return
     */
    public boolean checkAppSign(WebPublicModel webPublicModel) throws UnsupportedEncodingException {
        //                    String userId = data[1];
//                    String unionid = data[2];
//                    String openId = data[3];
        String s = webPublicModel.getMallUserId() + webPublicModel.getUnionId() + webPublicModel.getOpenId() + commonConfigService.getAppSecret();
        String sign = DigestUtils.md5DigestAsHex(s.getBytes("UTF-8")).toLowerCase();
        return sign.equals(webPublicModel.getSign());
    }

    private enum SystemEnvironment {
        WEIXIN,
        APP,
        UNKNOW
    }
}


