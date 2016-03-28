package com.huotu.duobaoweb.common;

import com.huotu.duobaoweb.model.WebPublicModel;

/**
 * 公共参数持有者
 *
 * @author CJ
 */
public class PublicParameterHolder {

    private static final ThreadLocal<WebPublicModel> models = new ThreadLocal<>();

    /**
     * 获取当前公共参数
     *
     * @return 在controller级别操作 返回总不会为空
     */
    public static WebPublicModel getParameters() {
        return models.get();
    }

    public static void putParameters(WebPublicModel model) {
        models.set(model);
    }
}
