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
     * 上传资源的物理路径
     *
     * @return
     */
    String getResourcesHome();

    /**
     * 网站上传资源URI地址
     *
     * @return
     */
    String getResourceUri();


    /**
     * 夺宝的key
     * 用于用户登录的openId和customerId的安全加密
     *
     * @return
     */
    String getDuobaoKey();

    /**
     * 商城的顶级域名
     * 用于提交订单
     *
     * @return
     */
    String getMainDomain();


    /**
     * 商城对接秘钥
     *
     * @return
     */
    String getMallKey();


    String getHuobanplusOpenApiAppid();

    String getHuobanplusOpenApiAppsecrect();

    String getHuobanplusOpenApiRoot();

}
