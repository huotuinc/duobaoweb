package com.huotu.duobaoweb.controller;

import com.huotu.duobaoweb.base.BaseTest;
import com.huotu.duobaoweb.boot.RootConfig;
import com.huotu.huobanplus.sdk.base.BaseClientSpringConfig;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

/**
 * Created by lgh on 2016/3/30.
 */
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {RootConfig.class, BaseClientSpringConfig.class})
@ActiveProfiles("development")
@Transactional
public class PersonalControllerTest extends BaseTest {

    @Test
    public void testGetMyInvolvedRecord() throws Exception {

    }

    @Test
    public void testGetMyRaiderNumbers() throws Exception {
        driver.get("/personal/getMyRaiderNumbers");
    }

    @Test
    public void testGetMyLotteryList() throws Exception {

    }

    @Test
    public void testGetOneLotteryInfo() throws Exception {

    }
}