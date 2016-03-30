/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.duobaoweb.service;


/**
 * 通用变量定义
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
     * 静态资源域名地址
     *
     * @return
     */
    String getResourcesUri();

    /**
     * 上传资源的服务地址
     *
     * @return
     */
    String getResourcesHome();


    /**
     * 接受被提供错误信息的手机号
     * 比如抓取异常会发送到此手机
     *
     * @return
     */
    String getErrorPrividedMobile();


    /**
     * 商城的商家Id
     * @return
     */
    String getMallCustomerId();

    /**
     * 商城mallapi地址
     *
     * @return
     */
    String getMallApiUrl();

    /**
     * 商城安全keysecret
     *
     * @return
     */
    String getMallAuthKeySecret();

    /**
     * 商城地址
     *
     * @return
     */
    String getMallUrl();


}
