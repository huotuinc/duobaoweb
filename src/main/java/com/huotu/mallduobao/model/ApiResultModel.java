package com.huotu.mallduobao.model;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by lgh on 2016/4/7.
 */
@Getter
@Setter
public class ApiResultModel {
    /**
     * 编码
     * 1成功 其他失败
     */
    private String code;
    /**
     * 提示信息
     */
    private String message;
    /**
     * 数据
     */
    private Object data;
}
