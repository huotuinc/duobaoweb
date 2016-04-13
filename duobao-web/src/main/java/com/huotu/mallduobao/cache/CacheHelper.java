package com.huotu.mallduobao.cache;


import com.huotu.mallduobao.model.AppWinningInfoModel;
import com.whalin.MemCached.MemCachedClient;

import java.util.Date;
import java.util.List;

public class CacheHelper {

    private static MemCachedClient client = MemCachedClientFactory.getInstance();

    /**
     * 设置缓存
     *
     * @param key
     * @param value
     * @return
     */
    public static boolean set(String key, Object value) {
        return client.set(key, value);
    }

    public static boolean set(String key, Object value, Date expiry) {
        return client.set(key, value, expiry);
    }

    public static Object get(String key) {
        return client.get(key);
    }

    public static boolean delete(String key) {
        return client.delete(key);
    }

    public static boolean replace(String key,List<AppWinningInfoModel> value,Date expiry){
        return client.replace(key,value,expiry);
    }

}
