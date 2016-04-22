/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.mallduobao.utils;

import com.alibaba.fastjson.JSON;
import com.huotu.common.api.ICommonEnum;

import java.util.ArrayList;
import java.util.List;


/**
 * 枚举处理类
 *
 * @author Administrator
 */
public class EnumHelper {

    public static String GetEnumName(Class<? extends ICommonEnum> cls, int value) {
        ICommonEnum ice = getEnumType(cls, value);
        if (ice != null) {
            return ice.getName();
        }
        return "";
    }

    public static <T extends ICommonEnum> T getEnumType(Class<T> cls, int value) {
        for (T item : cls.getEnumConstants()) {
            if (item.getValue() == value) {
                return item;
            }
        }
        return null;
    }

    public static String toJsonString(Class<? extends ICommonEnum> cls) {
        List<EnumModel> list = new ArrayList<EnumModel>();
        for (ICommonEnum e : cls.getEnumConstants()) {
            EnumModel model = new EnumModel();
            model.setValue(e.getValue());
            model.setName(e.getName());
            list.add(model);
        }

        return JSON.toJSONString(list);
    }

    public static List<EnumModel> list(Class<? extends ICommonEnum> cls) {
        List<EnumModel> list = new ArrayList<EnumModel>();
        for (ICommonEnum e : cls.getEnumConstants()) {
            EnumModel model = new EnumModel();
            model.setValue(e.getValue());
            model.setName(e.getName());
            list.add(model);
        }

        return list;
    }
}
