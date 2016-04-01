package com.huotu.duobaoweb.service.impl;


import com.huotu.duobaoweb.repository.UserRepository;
import com.huotu.duobaoweb.service.UserService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by xhk on 2016/3/25.
 */
@Service
public class UserServiceImpl implements UserService {

    private static final Log log = LogFactory.getLog(UserServiceImpl.class);

    @Autowired
    UserRepository userRepository;


    @Override
    public String getIndexUrl(Long issueId) {
        return "111";
    }
}
