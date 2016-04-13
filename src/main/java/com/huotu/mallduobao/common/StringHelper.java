/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.mallduobao.common;


import net.htmlparser.jericho.Source;
import org.springframework.util.StringUtils;

import java.util.Random;

public class StringHelper extends com.huotu.common.base.StringHelper {

    public static String RandomNum(Random ran, int xLen) {
        String[] char_array = new String[9];
        char_array[0] = "1";
        char_array[1] = "2";
        char_array[2] = "3";
        char_array[3] = "4";
        char_array[4] = "5";
        char_array[5] = "6";
        char_array[6] = "7";
        char_array[7] = "8";
        char_array[8] = "9";

        String output = "";
        double tmp = 0;
        while (output.length() < xLen) {
            tmp = ran.nextFloat();
            output = output + char_array[(int) (tmp * 9)];
        }
        return output;
    }


    /**
     * 获得网页文字内容
     * @param html html内容
     * @param length 文字长度
     * @return
     */
    public static String getText(String html, Integer length) {
        Source source = new Source(html);
        String result = source.getTextExtractor().toString();
        if (!StringUtils.isEmpty(result) && result.length() > length) {
            return result.substring(0, length);
        }
        return result;
    }


}
