/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2015. All rights reserved.
 */

package com.huotu.mallduobao.service.impl;


import com.huotu.mallduobao.service.JdbcService;
import com.huotu.mallduobao.service.jdbc.SimpleConnectionProvider;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author CJ
 */
@Service
public class JdbcServiceImpl implements JdbcService {

    private static final Log log = LogFactory.getLog(JdbcServiceImpl.class);

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private EntityManager entityManager;

    @Override
    public void runJdbcWork(ConnectionConsumer connectionConsumer) throws SQLException, IOException {
        log.debug("Prepare to run jdbc");
        Connection connection = entityManager.unwrap(Connection.class);
        if (connection == null) {
            throw new IllegalStateException("@Transactional did not work check DataSupportConfig for details.");
        }
        connectionConsumer.workWithConnection(new SimpleConnectionProvider(connection));
        log.debug("End jdbc");
    }

    @Override
    public void runStandaloneJdbcWork(ConnectionConsumer connectionConsumer) throws SQLException, IOException {
        log.debug("Prepare to run jdbc");
        Connection connection = entityManager.unwrap(Connection.class);
        if (connection==null){
            connectionConsumer.workWithConnection(null);
        }else{
            connectionConsumer.workWithConnection(new SimpleConnectionProvider(connection));
        }
        log.debug("End jdbc");
    }
}
