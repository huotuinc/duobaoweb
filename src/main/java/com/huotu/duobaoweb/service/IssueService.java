package com.huotu.duobaoweb.service;

import com.huotu.duobaoweb.entity.Issue;

/**
 * Created by xhk on 2016/4/1.
 */
public interface IssueService {
    /**
     * 通过期号id获取期号
     * @param issueId
     * @return
     */
    Issue getIssueById(String issueId);
}
