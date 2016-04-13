package com.huotu.mallduobao.common;

/**
 * 微信支付地址
 * Created by xhk on 2016/3/12.
 */
public class WeixinPayUrl {

    /**
     * 商家二级域名
     */
    public static String subdomain = "cosytest";

    /**
     * 顶级域名
     */
    public static String maindomain = "51flashmall.com";

    /**
     * 商户编号
     */
    public static Long customerid = 3447L;

    /**
     * 完成授权后要回来的网址，需urencode
     */
    public static String returnurl = "";

    /**
     * 订单号
     */
    public static String outtradeno = "";

    /**
     * 微信用户id
     */
    public static String openid = "";

    /**
     * 支付结果通知地址
     */
    public static String notifyurl = "";

    /**
     * 标题
     */
    public static String title = "";

    /**
     * 标题
     */
    public static Long timestamp = 0L;

    /**
     * 签名
     */
    public static String sign = "";

    /**
     * 支付费用
     */
    public static Double totalfee = 0.0;


    /**
     * 伙伴商城微信授权接口地址
     */
    public static String getWeixinPayUrl() {
        String weixinAuthUrl = "http://" + subdomain + "." + maindomain
                + "/weixin/pay/payment_delegate.aspx?customerid=" + customerid
                + "&returnurl=" + returnurl
                + "&outtradeno=" + outtradeno
                + "&openid=" + openid
                + "&title=" + title
                + "&timestamp=" + timestamp
                + "&sign=" + sign
                + "&totalfee=" + totalfee
                + "&notifyurl=" + notifyurl;
        return weixinAuthUrl;
    }

}
