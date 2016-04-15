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
        return env.getProperty("duobao.web.url", "http://mallduobao.51flashmall.com:8091");
    }//http://mallduobao.51flashmall.com:8091,http://192.168.1.41:8080/duobaoweb

    @Override
    public String getMallCustomerId() {
        return env.getProperty("mall.customerid", "4471");//3447
    }

    @Override
    public String getDuobaoApiKey() {
        return env.getProperty("duobao.apikey", "f7b88579e3b948bf8658d103329dd75d");
    }

    @Override
    public String getHuoBanPlusManagerResourceUrl() {
        return env.getProperty("huobanplusmanager.resource.url", "http://192.168.3.86:8088/huobanplusmanager");
    }

    @Override
    public String getNetMallResourceUrl() {
        return env.getProperty("mall.resource.url", "http://192.168.3.86:8088/huobanplusmanager");
    }


    @Override
    public String getDuobaoKey() {
        return env.getProperty("duobao.key", "91d214037e584213b5a1352855c502af");
    }

    @Override
    public String getMaindomain() {
        return env.getProperty("duobao.maindomain", " 51flashmall.com");
    }


    @Override
    public String getMallKey() {
        return env.getProperty("mall.key", "1165a8d240b29af3f418b8d10599d0dc");
    }


}
