package com.huotu.mallduobao.service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by xhk on 2016/4/9.
 */
public interface SecurityService {

    /**
     * 支付时的Sign构建
     * @param paramMap
     * @return
     */
    String getPaySign(Map<String, String> paramMap) throws UnsupportedEncodingException;
}
