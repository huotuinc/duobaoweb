/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.mallduobao.service.impl;


import com.huotu.mallduobao.service.CommonConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

/**
 * 系统配置项
 * Created by lgh on 2015/9/23.
 */

@Service
public class CommonConfigServiceImpl implements CommonConfigService {

    @Autowired
    Environment env;


    @Override
    public String getWebUrl() {
        return env.getProperty("mallduobao.web.url", "http://192.168.3.86:8080/duobaoweb");
    }//http://mallduobao.51flashmall.com:8091,http://192.168.1.41:8080/duobaoweb



    @Override
    public String getResourcesHome() {
        return env.getProperty("huotu.resourcesHome", (String) null);
    }

    @Override
    public String getResourceUri() {
        return env.getProperty("huotu.resourcesUri", "http://120.24.243.104/resources/lgh/mallduobao");
    }

    @Override
    public String getDuobaoKey() {
        return env.getProperty("duobao.key", "91d214037e584213b5a1352855c502af");
    }

    @Override
    public String getMainDomain() {
        return env.getProperty("duobao.maindomain", " 51flashmall.com");
    }


    @Override
    public String getMallKey() {
        return env.getProperty("mall.key", "1165a8d240b29af3f418b8d10599d0dc");
    }

    @Override
    public String getHuobanplusOpenApiAppid() {
        return env.getProperty("com.huotu.huobanplus.open.api.appid", "_demo");
    }

    @Override
    public String getHuobanplusOpenApiAppsecrect() {
        return env.getProperty("com.huotu.huobanplus.open.api.appsecrect", "1f2f3f4f5f6f7f8f");
    }

    @Override
    public String getHuobanplusOpenApiRoot() {
        return env.getProperty("com.huotu.huobanplus.open.api.root", "http://api.open.fancat.cn:8081");
    }

}
