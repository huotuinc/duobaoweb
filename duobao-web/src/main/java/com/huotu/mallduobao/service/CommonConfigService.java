/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.mallduobao.service;


/**
 * 通用变量定义 todo 没有用到？
 * Created by lgh on 2015/9/23.
 */
public interface CommonConfigService {


    /**
     * 获取当前网站的地址
     *
     * @return
     */
    String getWebUrl();


    /**
     * 商城的商家Id
     * @return
     */
    String getMallCustomerId();


    /**
     * 夺宝的apikey
     * @return
     */
    String getDuobaoApiKey();

    /**
     * 上传资源的服务地址
     * @return
     */
    String getResourcesHome();

    /**
     * 伙伴商城管理Web资源地址
     */
    String getResourceUri();


    /**
     * 夺宝的key
     * @return
     */
    String getDuobaoKey();

    /**
     * 得到商城的顶级域名
     * @return
     */
    String getMaindomain();



    /**
     * 商城对接秘钥
     * @return
     */
    String getMallKey();


    String getHuobanplusOpenApiAppid();

    String getHuobanplusOpenApiAppsecrect();

    String getHuobanplusOpenApiRoot();

}
