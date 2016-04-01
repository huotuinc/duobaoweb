package com.huotu.duobaoweb.boot;

import com.huotu.huobanplus.sdk.base.BaseClientSpringConfig;
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
@ComponentScan({"com.huotu.duobaoweb.service","com.huotu.duobaoweb.controller"})
@EnableJpaRepositories(value = "com.huotu.duobaoweb.repository", repositoryFactoryBeanClass = ClassicsRepositoryFactoryBean.class)
@ImportResource(value = {"classpath:spring-jpa.xml"})
@Import({LoggingConfig.class, BaseClientSpringConfig.class, CommonClientSpringConfig.class})
public class RootConfig {


}
