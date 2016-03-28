package com.huotu.duobaoweb.common;

import com.huotu.duobaoweb.model.WebPublicModel;
import com.huotu.duobaoweb.repository.UserRepository;
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


    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        
        String weixinOpenId = "";//todo 获取微信openid
        WebPublicModel webPublicModel = initPublicParam(request, weixinOpenId);
        PublicParameterHolder.putParameters(webPublicModel);
        return true;
    }


    private WebPublicModel initPublicParam(HttpServletRequest request, String weixinOpenId) {

        WebPublicModel webPublicModel = new WebPublicModel();
        webPublicModel.setCurrentUser(userRepository.findByWeixinOpenId(weixinOpenId));
        webPublicModel.setIp(getIp(request));
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
