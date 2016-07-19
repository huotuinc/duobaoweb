package com.huotu.mallduobao.utils;

import com.huotu.common.api.ICommonEnum;

/**
 * 1 注册 2 找回密码 3 绑定手机
 */
public enum VerificationType implements ICommonEnum {

    BIND_REGISTER(1, "注册"),

    BIND_LOGINPASSWORD(2, "找回密码 "),

    BIND_MOBILE(3, "绑定手机"),

    ERROR_REPORT(4, "错误汇报"),

    CHECK_PHONE(5,"验证手机"),

    LOTTERY_INFO(6, "中奖信息");


    private int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    VerificationType(int value, String name) {
        this.value = value;
        this.name = name;
    }
}
