package com.huotu.duobaoweb.common;

import com.huotu.duobaoweb.model.WebPublicModel;
import com.huotu.duobaoweb.repository.UserRepository;
import com.huotu.duobaoweb.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lgh on 2016/3/28.
 */
public class WebInterceptor implements HandlerInterceptor {

    private static Log log = LogFactory.getLog(WebInterceptor.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        
        String weixinOpenId = "2";//todo 获取微信openid
        WebPublicModel webPublicModel = initPublicParam(request, weixinOpenId);
        if(weixinOpenId==null||webPublicModel.getCustomerId()==null||
                webPublicModel.getIssueId()==null||webPublicModel.getOpenId()==null||
                webPublicModel.getSign()==null||!userService.checkOpenid(webPublicModel.getOpenId(),webPublicModel.getSign())){
            //校验openid是否正确
            log.info("初始化认证失败啦！！！！！！");
            //todo 跳转到错误页面
        }
        PublicParameterHolder.putParameters(webPublicModel);
        return true;
    }


    private WebPublicModel initPublicParam(HttpServletRequest request, String weixinOpenId) {

        WebPublicModel webPublicModel = new WebPublicModel();
//        if(weixinOpenId!=null) {
//            webPublicModel.setCurrentUser(userRepository.findByWeixinOpenId(weixinOpenId));
//        }
        webPublicModel.setIp(getIp(request));
        if(request.getParameter("issueId")!=null) {
            webPublicModel.setIssueId(Long.parseLong(request.getParameter("issueId")));
        }else {
            log.info("issueId为空异常！issueId is null!");
            webPublicModel.setIssueId(null);
        }
        if(request.getParameter("customerId")!=null) {
            webPublicModel.setCustomerId(Long.parseLong(request.getParameter("customerId")));
        }else{
            log.info("customerId为空异常！customerId is null!");
            webPublicModel.setCustomerId(null);
        }
        if(request.getParameter("openId")!=null) {
            webPublicModel.setOpenId(request.getParameter("openId"));
        }else{
            log.info("openId为空异常！openId is null!");
            webPublicModel.setOpenId(null);
        }
        if(webPublicModel.getOpenId()!=null) {
            webPublicModel.setCurrentUser(userRepository.findByWeixinOpenId(webPublicModel.getOpenId()));
        }
        if(request.getParameter("sign")!=null) {
            webPublicModel.setSign(request.getParameter("sign"));
        }else{
            log.info("sign为空异常！sign is null!");
            webPublicModel.setSign(null);
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
