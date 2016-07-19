package com.huotu.mallduobao.utils;

import com.huotu.common.api.ICommonEnum;

/**
 * @author CJ
 */
public enum CodeType implements ICommonEnum {
    /**
     * text(0, "文本")
     */
    text(0, "文本"),
    /**
     * voice(1, "语音")
     */
    voice(1, "语音");

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

    CodeType(int value, String name) {
        this.value = value;
        this.name = name;
    }
}
