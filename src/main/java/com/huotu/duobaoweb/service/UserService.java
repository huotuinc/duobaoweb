package com.huotu.duobaoweb.service;


/**
 * Created by xhk on 2016/3/25.
 */
public interface UserService {


    /**
     * 由于未登陆直接返回首页
     * @param issueId
     * @return
     */
    String getIndexUrl(Long issueId);
}
