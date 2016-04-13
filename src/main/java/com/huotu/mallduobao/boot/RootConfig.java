package com.huotu.mallduobao.boot;

import com.huotu.huobanplus.sdk.common.CommonClientSpringConfig;
import org.luffy.lib.libspring.data.ClassicsRepositoryFactoryBean;
import org.luffy.lib.libspring.logging.LoggingConfig;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Created by lgh on 2016/3/22.
 */

@Configuration
//@EnableTransactionManagement(mode = AdviceMode.PROXY)
@ComponentScan({"com.huotu.mallduobao.service", "com.huotu.mallduobao.controller"})
@EnableJpaRepositories(value = "com.huotu.mallduobao.repository", repositoryFactoryBeanClass = ClassicsRepositoryFactoryBean.class)
@ImportResource(value = {"classpath:spring-jpa.xml"})
@Import({LoggingConfig.class, CommonClientSpringConfig.class})
public class RootConfig {


}
