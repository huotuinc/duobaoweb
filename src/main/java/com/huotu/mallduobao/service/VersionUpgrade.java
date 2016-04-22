package com.huotu.mallduobao.service;

/**
 * Created by lgh on 2016/2/27.
 */
@FunctionalInterface
public interface VersionUpgrade<T> {

    /**
     * 从最近版本升级到step版本.
     *
     * @param version 要升级的版本
     */
    void upgradeToVersion(T version) throws Exception;
}
