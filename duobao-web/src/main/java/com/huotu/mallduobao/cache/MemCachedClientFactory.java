package com.huotu.mallduobao.cache;

import com.whalin.MemCached.MemCachedClient;
import com.whalin.MemCached.SockIOPool;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by lgh on 2015/12/21.
 */
public class MemCachedClientFactory {
    private static Log log = LogFactory.getLog(MemCachedClientFactory.class);

    protected static Properties properties = new Properties();
    private volatile static MemCachedClient client = null;

    static {

        String propertiesFileName = "memcached-client.properties";
        InputStream inputStream = MemCachedClientFactory.class.getClassLoader().getResourceAsStream(propertiesFileName);
        try {
            properties.load(inputStream);

            SockIOPool pool = SockIOPool.getInstance();
            String[] servers = properties.getProperty("servers").split(",");
            pool.setServers(servers);

            String[] strWeights = properties.getProperty("weights").split(",");
            List<Integer> weights = new ArrayList<>();
            for (String weight : strWeights) {
                weights.add(Integer.parseInt(weight));
            }
            pool.setWeights(weights.toArray(new Integer[weights.size()]));

            pool.setInitConn(Integer.parseInt(properties.getProperty("initConn")));
            pool.setMinConn(Integer.parseInt(properties.getProperty("minConn")));
            pool.setMaxConn(Integer.parseInt(properties.getProperty("maxConn")));

            pool.setHashingAlg(SockIOPool.CONSISTENT_HASH);
            pool.setMaxIdle(1000 * 60 * 60 * 6);
            pool.setMaintSleep(30);//设置维护线程时间
            pool.setNagle(false);//设置是否使用Nagle算法，因为我们的通讯数据量通常都比较大（相对TCP控制数据）而且要求响应及时，因此该值需要设置为false（默认是true）
            pool.setSocketTO(3000);//设置socket的读取等待超时值
            pool.setSocketConnectTO(0);//设置socket的连接等待超时值
            pool.initialize();
        } catch (IOException e) {
            log.error("load " + propertiesFileName + " error");
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("close " + propertiesFileName + " error");
                }
            }
        }
    }

    public static MemCachedClient getInstance() {
        if (client == null) {
            synchronized (MemCachedClient.class) {
                if (client == null)
                    client = new MemCachedClient();
            }
        }
        return client;
    }
}
