package com.huotu.duobaoweb.common;

/**
 * 微信支付地址
 * Created by xhk on 2016/3/12.
 */
public class WeixinPayUrl {

    /**
     * 请求支付的商家域名
     */
    public static String customerdomain="cosytest.51flashmall.com";

    /**
     * 商户编号
     */
    public static Long customerid=3447L;

    /**
     * 完成授权后要回来的网址，需urencode
     */
    public static String encode_url= "";

    /**
     * 授权方式
     */
    public static int scope=0;

    /**
     * 返回
     */
    public static int retuinfo=0;

    /**
     * 伙伴商城微信授权接口地址
     */
    public static String getWeixinPayUrl()
    {
       String  weixinAuthUrl = "http://" + customerdomain
                + "/OAuth2/WeixinAuthorize.aspx?customerid=" + customerid
                + "&redirecturl=" + encode_url
                + "&scope=" + scope
                + "&retuinfo=" + retuinfo;
        return weixinAuthUrl;
    }

}
