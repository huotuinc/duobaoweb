package com.huotu.duobaoweb.service.impl;

import com.huotu.duobaoweb.service.CommonConfigService;
import com.huotu.duobaoweb.service.SecurityService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by xhk on 2016/4/9.
 */
@Service
public class SecurityServiceImpl implements SecurityService{
    private static Log log = LogFactory.getLog(SecurityServiceImpl.class);

    @Autowired
    private CommonConfigService commonConfigService;

    @Override
    public String getPaySign(Map<String, String> paramMap) throws UnsupportedEncodingException {
        Map<String, String> resultMap = new TreeMap<String, String>();
        for (Object key : paramMap.keySet()) {
            resultMap.put(key.toString(), paramMap.get(key));
        }

        StringBuilder strB = new StringBuilder();
        for (String key : resultMap.keySet()) {
            if (!"sign".equals(key) && !StringUtils.isEmpty(resultMap.get(key))) {
                strB.append("&" + key + "=" + resultMap.get(key));
            }
        }

        String toSign = (strB.toString().length() > 0 ? strB.toString().substring(1) : "") + commonConfigService.getPaySecret();
        log.info(toSign);
        return DigestUtils.md5DigestAsHex(toSign.getBytes("UTF-8")).toLowerCase();

    }
}
