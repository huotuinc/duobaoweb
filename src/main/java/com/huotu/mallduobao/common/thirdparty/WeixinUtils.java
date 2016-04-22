package com.huotu.mallduobao.common.thirdparty;

import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/24.
 */
public class WeixinUtils {
    private static String key = "21b7468d09e647ecae5580a2f458e68e";//"0db0d4908a6ae6a09b0a7727878f0ca6";

    //微信分配的公众号ID（开通公众号之后可以获取到）
    private static String appID = "";

    //微信支付分配的商户号ID（开通公众号的微信支付功能之后可以获取到）
    private static String mchID = "";

    //受理模式下给子商户分配的子商户号
    private static String subMchID = "";

    //HTTPS证书的本地路径
    private static String certLocalPath = "";

    //HTTPS证书密码，默认密码等于商户号MCHID
    private static String certPassword = "";

    //是否使用异步线程的方式来上报API测速，默认为异步模式
    private static boolean useThreadToDoReport = true;

    //机器IP
    private static String ip = "";

    public static String getAppID(){
        return appID;
    }

    public static String getSign(Map<String, String> map) {
        ArrayList<String> list = new ArrayList<String>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (!"sign".equals(entry.getKey()) && !entry.getValue().equals("")) {
                list.add(entry.getKey() + "=" + entry.getValue() + "&");
            }
        }
        int size = list.size();
        String[] arrayToSort = list.toArray(new String[size]);
        Arrays.sort(arrayToSort, String.CASE_INSENSITIVE_ORDER);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < size; i++) {
            sb.append(arrayToSort[i]);
        }
        String result = sb.toString();
        result += "key=" + key;
        //Util.log("Sign Before MD5:" + result);
        result = DigestUtils.md5DigestAsHex(result.getBytes()).toUpperCase();
        //Util.log("Sign Result:" + result);
        return result;
    }

}
