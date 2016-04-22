package com.huotu.mallduobao.common;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by lgh on 2016/3/28.
 */
public class WebHandlerExceptionResolver implements HandlerExceptionResolver {

    private static Log log = LogFactory.getLog(WebHandlerExceptionResolver.class);

    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        String requestURI = request.getRequestURI().substring(request.getContextPath().length());
        if (requestURI.startsWith("/personal/") || requestURI.startsWith("/goods/")
                || requestURI.startsWith("/user/") || requestURI.startsWith("/shopping/")
                || requestURI.startsWith("/pay/") || requestURI.startsWith("/api/")) {
                try {
                    throw ex;
                } catch (Exception e) {
                    log.error("web request error", e);
                }

            ModelAndView modelAndView=new ModelAndView();
            modelAndView.addObject("message",ex.getMessage());
            modelAndView.setViewName("redirect:/html/error.html");
            return modelAndView;
        }
        return null;
    }
}
