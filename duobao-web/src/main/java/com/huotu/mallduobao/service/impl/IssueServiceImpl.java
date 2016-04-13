package com.huotu.mallduobao.service.impl;

import com.huotu.mallduobao.entity.Issue;
import com.huotu.mallduobao.repository.IssueRepository;
import com.huotu.mallduobao.service.IssueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xhk on 2016/4/1.
 */
@Service
public class IssueServiceImpl implements IssueService {

    @Autowired
    private IssueRepository issueRepository;

    @Override
    public Issue getIssueById(String issueId) {
        return issueRepository.findOne(Long.parseLong(issueId));
    }
}
