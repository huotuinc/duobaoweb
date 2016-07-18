package com.huotu.mallduobao.common;

import com.huotu.common.base.HttpHelper;
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
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
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
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
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


    private String customerURL(Long customerId) throws IOException {
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

    private URL redirectURLForAPPInfo(Long customerId, String openId, String redirectUrl) throws IOException {
        StringBuilder builder = new StringBuilder();
        String customerUrl = customerURL(customerId);
        List<NameValuePair> list = new ArrayList<>();
        String encodeUrl = URLEncoder.encode(redirectUrl, "UTF-8");
        list.add(new BasicNameValuePair("customerid", String.valueOf(customerId)));
        list.add(new BasicNameValuePair("redirecturl", encodeUrl));
        list.add(new BasicNameValuePair("openid", openId));
        list.sort(((o1, o2) -> o1.getName().compareTo(o2.getName())));
        StringBuilder toSign = new StringBuilder(list.get(0).getName()).append("=").append(list.get(0).getValue());
        for (int i = 1; i < list.size(); i++) {
            NameValuePair pair = list.get(i);
            if (pair.getValue() == null || pair.getValue().length() == 0)
                continue;
            toSign.append("&").append(pair.getName())
                    .append("=").append(pair.getValue());
        }
        toSign.append(commonConfigService.getHuobanplusCustomerApiAppsecrect());
        String sign = DigestUtils.md5DigestAsHex(toSign.toString().getBytes("UTF-8")).toLowerCase();
        list.add(new BasicNameValuePair("sign", sign));
        builder.append(customerUrl).append("/API/MemberAPI.aspx").append("?").append(list.get(0).getName()).append("=")
                .append(list.get(0).getValue());

        for (int i = 1; i < list.size(); i++) {
            NameValuePair pair = list.get(i);
            if (pair.getValue() == null || pair.getValue().length() == 0)
                continue;
            builder.append("&").append(pair.getName())
                    .append("=").append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return new URL(builder.toString());
    }


    public boolean isWeixinEnvironment(HttpServletRequest request) {
        String header = request.getHeader("User-Agent");
        return header.contains("MicroMessenger");
    }


    /**
     * 检测是否是app请求 通过检测是否有
     */
    private boolean checkRequestIsApp(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String openId;
        String userId;
        String unionid;

        //获取请求头中的信息,用于判断是否是app发送的信息;
        String header = request.getHeader("User-Agent");
        log.info(header);
        if (isWeixinEnvironment(request)) return false;
        Long customerId = Long.parseLong(request.getParameter("customerId"));
        String[] data = StringHelper.getRequestAppInfo(header);
        String returnUrl = userService.getReturnUrl(request, customerId);
        if (data != null && data.length == 4 && StringHelper.isTrueSign(data)) {
            userId = data[1];
            unionid = data[2];
            openId = data[3];
            if (StringUtils.isEmpty(userId) || StringUtils.isEmpty(unionid) || StringUtils.isEmpty(openId)) {
                StringBuilder url = new StringBuilder(customerURL(customerId));
                url.append("/UserCenter/Login.aspx").append("?").append("customerid=").append(customerId).append("redirectURL=").append(returnUrl);
                response.sendRedirect(url.toString());
            } else {
                String redirectUrl = userService.getAppIndexUrl(request, customerId, openId);
                HttpHelper.getRequest(redirectURLForAPPInfo(customerId, openId, redirectUrl).toString());
            }
        } else {
            StringBuilder url = new StringBuilder(customerURL(customerId));
            url.append("/UserCenter/Login.aspx").append("?").append("customerid=").append(customerId).append("redirectURL=").append(returnUrl);
            response.sendRedirect(url.toString());
        }
        return true;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        WebPublicModel webPublicModel = initPublicParam(request);

        //todo 正式发布环境要进行修改
        if (!environment.acceptsProfiles("development")) {
            //在非测试环境下进行正常请求
            if (request.getParameter("customerId") == null) {
                log.info("初始化认证失败啦！！！！！！");
                String errorUrl="/html/error.html";
                //跳转到错误页面
                response.sendRedirect(errorUrl);
                return false;
            }
            if (webPublicModel.getOpenId() == null ||
                    webPublicModel.getSign() == null || !userService.checkOpenid(webPublicModel.getOpenId(), webPublicModel.getSign()) ||
                    !request.getParameter("customerId").equals(String.valueOf(webPublicModel.getCustomerId()))) {
                //校验openid是否正确 需要进行认证
                log.info("开始进行认证服务！！");
                //todo 这是认证服务，暂时注释，不允许删除
            if(!request.getParameter("customerId").equals(String.valueOf(webPublicModel.getCustomerId()))){
                //如果商户号与cookie中存的不一样则以传过来的商户为准进行请求
                webPublicModel.setCustomerId(Long.parseLong(request.getParameter("customerId")));
            }

            PublicParameterHolder.putParameters(webPublicModel);
                if (!checkRequestIsApp(request, response)) {
                    String openidUrl = userService.getWeixinAuthUrl(request, webPublicModel);
                    response.sendRedirect(openidUrl);
                    return false;
                } else {
                    return true;
                }

                //进行微信认证
            }
        }else{
            //在测试环境下，获取所有用户中的第一个用户，如果数据库中没有用户则请求失败
            List<User> userList=userRepository.findAll();
            if(userList==null||userList.size()<1){
                return false;
            }
            //默认取第一个用户
            User user=userList.get(0);
            webPublicModel.setOpenId(user.getWeixinOpenId());
            webPublicModel.setSign(userService.getSign(user));
            webPublicModel.setCurrentUser(user);
            webPublicModel.setCustomerId(user.getMerchantId());
        }
        PublicParameterHolder.putParameters(webPublicModel);
        return true;
    }

    private WebPublicModel initPublicParam(HttpServletRequest request) {

        WebPublicModel webPublicModel = new WebPublicModel();
//        if(weixinOpenId!=null) {
//            webPublicModel.setCurrentUser(userRepository.findByWeixinOpenId(weixinOpenId));
//        }
        //todo lhx 本地模拟用户
        webPublicModel.setCurrentUser(userRepository.findOne(1L));
        webPublicModel.setIp(getIp(request));

        if(request.getParameter("issueId")!=null&&request.getParameter("issueId")!="") {
            webPublicModel.setIssueId(Long.parseLong(request.getParameter("issueId")));
        }else {
            webPublicModel.setIssueId(null);
        }
        //获取cookies
        Cookie[] cookies=request.getCookies();
        Map<String,String> map=new HashMap<String,String>();

        if(cookies!=null) {
            for (Cookie cookie : cookies) {
                map.put(cookie.getName(), cookie.getValue());
            }
            if (map.get("customerId") != null) {
                webPublicModel.setCustomerId(Long.parseLong(map.get("customerId")));
            } else {
                log.info("customerId为空异常！customerId is null!");
                webPublicModel.setCustomerId(null);
            }
            if (map.get("openId") != null) {
                webPublicModel.setOpenId(map.get("openId"));
            } else {
                log.info("openId为空异常！openId is null!");
                webPublicModel.setOpenId(null);
            }
            if (webPublicModel.getOpenId() != null) {
                webPublicModel.setCurrentUser(userRepository.findByWeixinOpenId(webPublicModel.getOpenId()));
               // webPublicModel.setCurrentUser(userService.findByWeixinOpenId(webPublicModel.getOpenId()));
            }
            if (map.get("sign") != null) {
                webPublicModel.setSign(map.get("sign"));
            } else {
                log.info("sign为空异常！sign is null!");
                webPublicModel.setSign(null);
            }
        }
        return webPublicModel;
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
}
