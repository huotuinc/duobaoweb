package com.huotu.mallduobao.service.impl;

import com.huotu.mallduobao.entity.SystemConfig;
import com.huotu.mallduobao.model.CommonVersion;
import com.huotu.mallduobao.repository.SystemConfigRepository;
import com.huotu.mallduobao.service.JdbcService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Statement;


/**
 * Created by lgh on 2016/4/19.
 */
@Service
public class AppService implements ApplicationListener<ContextRefreshedEvent> {
    private static Log log = LogFactory.getLog(AppService.class);

    @Autowired
    private SystemConfigRepository systemConfigRepository;


    @Autowired
    private JdbcService jdbcService;

    @Autowired
    private BaseService baseService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

        if (event.getApplicationContext().getParent() == null) {
            SystemConfig systemConfig = systemConfigRepository.findOne("DatabaseVersion");
            if (systemConfig == null) {
                SystemConfig databaseVession = new SystemConfig();
                databaseVession.setCode("DatabaseVersion");
                databaseVession.setValueForCode(String.valueOf(CommonVersion.initVersion.ordinal()));
                systemConfigRepository.save(databaseVession);
            }

            //系统升级
            baseService.systemUpgrade("DatabaseVersion", CommonVersion.class, CommonVersion.Version101, (upgrade) -> {
                switch (upgrade) {
                    case Version101:
                        //系统初始化
                        try {
                            jdbcService.runJdbcWork(connection -> {
                                Statement statement = connection.getConnection().createStatement();
                                String hql = "alter table ISSUE AUTO_INCREMENT=100000001";
                                statement.execute(hql);

                                InputStream is = getClass().getClassLoader().getResourceAsStream("data/city.sql");
                                InputStreamReader isr = new InputStreamReader(is, "utf-8");
                                BufferedReader bis = new BufferedReader(isr);
                                String valueString = null;
                                while ((valueString = bis.readLine()) != null) {
                                    statement.execute(valueString);
                                }
                                isr.close();
                                is.close();
                            });

                        } catch (Exception e) {
                            log.info("upgrade to " + CommonVersion.Version101.ordinal() + " error", e);
                        }

                        break;

                }
            });

        }
    }
}
